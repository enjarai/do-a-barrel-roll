package nl.enjarai.doabarrelroll;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.config.Sensitivity;

public class DoABarrelRollClient implements ClientModInitializer {
	public static final String MODID = "do-a-barrel-roll";

	public static final SmoothUtil pitchSmoother = new SmoothUtil();
	public static final SmoothUtil yawSmoother = new SmoothUtil();
	public static final SmoothUtil rollSmoother = new SmoothUtil();
	private static double lastLookUpdate;
	private static double lastLerpUpdate;
	public static double landingLerp = 1;
	public static Vec3d left;
	public static Vec2f mouseTurnVec = Vec2f.ZERO;
	

	@Override
    public void onInitializeClient() { // TODO triple jump to activate???
		ModConfig.init();
    }

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
	

	public static void updateMouse(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
		
		double time = GlfwUtil.getTime();
		double lerpDelta = time - lastLerpUpdate;
		lastLerpUpdate = time;

		// smoothly lerp left vector to the assumed upright left if not in flight
		if (!player.isFallFlying()) {

			landingLerp = MathHelper.lerp(MathHelper.clamp(lerpDelta * 2, 0, 1), landingLerp, 1);

			// round the lerp off when done to hopefully avoid world flickering
			if (landingLerp > 0.9) landingLerp = 1;
			
			clearValues();
			player.changeLookDirection(cursorDeltaX, cursorDeltaY);
			left = left.lerp(ElytraMath.getAssumedLeft(player.getYaw()), landingLerp);

			return;
		}


		// reset the landing animation when flying
		landingLerp = 0;

		if (ModConfig.INSTANCE.momentumBasedMouse) {

			// add the mouse movement to the current vector and normalize if needed
			var turnVec = mouseTurnVec.add(new Vec2f((float) cursorDeltaX, (float) cursorDeltaY).multiply(1f / 300));
			if (turnVec.lengthSquared() > 1) {
				turnVec = turnVec.normalize();
			}
			mouseTurnVec = turnVec;

			// enlarge the vector and apply it to the camera
			var delta = getDelta();
			var readyTurnVec = mouseTurnVec.multiply(1200 * (float) delta);
			changeElytraLook(readyTurnVec.y, 0, readyTurnVec.x, ModConfig.INSTANCE.desktopSensitivity, delta, true);

		} else {

			// if we are not using a momentum based mouse, we can reset it and apply the values directly
			mouseTurnVec = Vec2f.ZERO;
			changeElytraLook(cursorDeltaY, 0, cursorDeltaX, ModConfig.INSTANCE.desktopSensitivity);
		}
	}
	
	public static void onWorldRender(MinecraftClient client, float tickDelta, long limitTime, MatrixStack matrix) {

		if (client.player == null || !client.player.isFallFlying()) {

			clearValues();

		} else {

			if (client.isPaused()) {

				// keep updating the last look update time when paused to prevent large jumps after unpausing
				lastLookUpdate = GlfwUtil.getTime();

			} else {
				//This is floating point number
				var yawDelta = 10f * (ModConfig.INSTANCE.switchRollAndYaw ? ModConfig.INSTANCE.responsiveness.roll : ModConfig.INSTANCE.responsiveness.yaw);
				double yaw = 0;

				// Strafe buttons
				if (client.options.leftKey.isPressed()) {
					yaw -= yawDelta;
				}
				if (client.options.rightKey.isPressed()) {
					yaw += yawDelta;
				}

				//For banking, we swap yaw-roll manually here
				double roll = 0;
				if (ModConfig.INSTANCE.switchRollAndYaw) {
					roll = yaw;
					yaw = 0;
				}

				//Realistic roll keep adding to yaw.
				if (ModConfig.INSTANCE.enableBanking && !client.isPaused()) {
					double currentRoll = -Math.acos(left.dotProduct(ElytraMath.getAssumedLeft(client.player.getYaw())));
					if (left.getY() < 0) currentRoll *= -1;
					var change = Math.sin(currentRoll);

					var scalar = 10 * ElytraMath.sigmoid(client.player.getVelocity().length() * 2 - 2) * ModConfig.INSTANCE.bankingStrength;
					change *= scalar;

					yaw += change;
				}
				//Change with identity sensitivity
				changeElytraLook(0, yaw, roll, Sensitivity.identity(), getDelta(), false);

			}
		}

		if (landingLerp < 1) {

			// calculate the camera angle and apply it
			double angle = -Math.acos(left.dotProduct(ElytraMath.getAssumedLeft(client.player.getYaw()))) * ElytraMath.TODEG;
			if (left.getY() < 0) angle *= -1;
			matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) angle));

		}
	}

	public static void onRenderCrosshair(MatrixStack matrices, int scaledWidth, int scaledHeight) {
		if (!DoABarrelRollClient.isFallFlying() || !ModConfig.INSTANCE.momentumBasedMouse || !ModConfig.INSTANCE.showMomentumWidget) return;

		MomentumCrosshairWidget.render(matrices, scaledWidth, scaledHeight, mouseTurnVec);
	}


	private static void clearValues() {
		pitchSmoother.clear();
		yawSmoother.clear();
		rollSmoother.clear();
		mouseTurnVec = Vec2f.ZERO;
	}

	/**
	 * Returns the time since the last look update.
	 *
	 * <p>
	 * <b>Only call if you're going to call
	 * {@link DoABarrelRollClient#changeElytraLook(double, double, double, Sensitivity, double, boolean)}
	 * right after this using the returned value.</b>
	 * Neglecting to do this will disrupt the smoothness of the camera.
	 * </p>
	 */
	private static double getDelta() {
		double time = GlfwUtil.getTime();
		double delta = time - lastLookUpdate;
		lastLookUpdate = time;
		return delta;
	}

	/**
	 * Only use if you <b>haven't</b> called {@link DoABarrelRollClient#getDelta()} before this.
	 */
	public static void changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity) {
		changeElytraLook(pitch, yaw, roll, sensitivity, getDelta(), true);
	}

	public static void changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double delta, boolean allowRollYawSwitch) {
		// apply the look changes
		if (allowRollYawSwitch && ModConfig.INSTANCE.switchRollAndYaw) {
			changeElytraLookSmoothed(pitch, roll, yaw, sensitivity, delta);
		} else {
			changeElytraLookSmoothed(pitch, yaw, roll, sensitivity, delta);
		}
	}

	public static void changeElytraLookSmoothed(double pitch, double yaw, double roll, Sensitivity sensitivity, double delta) {
		var player = MinecraftClient.getInstance().player;
		if (player == null) return;

		// smooth the look changes
		pitch *= sensitivity.pitch;
		yaw *= sensitivity.yaw;
		roll *= sensitivity.roll;
		pitch = pitchSmoother.smooth(pitch, ModConfig.INSTANCE.responsiveness.pitch * delta);
		yaw = yawSmoother.smooth(yaw, ModConfig.INSTANCE.responsiveness.yaw * delta);
		roll = rollSmoother.smooth(roll, ModConfig.INSTANCE.responsiveness.roll * delta);
		ElytraMath.changeElytraLookDirectly(player, pitch, yaw, roll);
	}

	public static boolean isFallFlying() {
		var player = MinecraftClient.getInstance().player;
		return player != null && player.isFallFlying();
	}
}
