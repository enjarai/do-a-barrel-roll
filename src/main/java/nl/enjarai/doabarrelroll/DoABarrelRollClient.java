package nl.enjarai.doabarrelroll;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
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
			
			clearSmoothers();
			player.changeLookDirection(cursorDeltaX, cursorDeltaY);
			left = left.lerp(ElytraMath.getAssumedLeft(player.getYaw()), landingLerp);
			return;
		}

		landingLerp = 0;

		changeElytraLook(cursorDeltaY, 0, cursorDeltaX, ModConfig.INSTANCE.desktopSensitivity);
	}
	
	public static void onWorldRender(MinecraftClient client, float tickDelta, long limitTime, MatrixStack matrix) {

		if (client.player == null || !client.player.isFallFlying()) {

			clearSmoothers();

		} else {


			var yawDelta = 25f * (ModConfig.INSTANCE.switchRollAndYaw ? ModConfig.INSTANCE.responsiveness.roll : ModConfig.INSTANCE.responsiveness.yaw);
			double yaw = 0;

			// Strafe buttons
			if (client.options.leftKey.isPressed()) {
				yaw -= yawDelta;
			}
			if (client.options.rightKey.isPressed()) {
				yaw += yawDelta;
			}

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

				var scalar = 10 * ElytraMath.sigmoid(client.player.getVelocity().length()*2-2) * ModConfig.INSTANCE.bankingStrength;
				change *= scalar;

				yaw += change;
			}
			//Change with identity sensitivity
			changeElytraLook(0, yaw, roll, Sensitivity.identity(), false);

		}

		if (landingLerp < 1) {

			double angle = -Math.acos(left.dotProduct(ElytraMath.getAssumedLeft(client.player.getYaw()))) * ElytraMath.TODEG;
			if (left.getY() < 0) angle *= -1;

			matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) angle));

		}
	}


	private static void clearSmoothers() {
		pitchSmoother.clear();
		yawSmoother.clear();
		rollSmoother.clear();
	}

	public static void changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, boolean allowRollYawSwitch) {
		// apply the look changes
		if (allowRollYawSwitch && ModConfig.INSTANCE.switchRollAndYaw) {
			changeElytraLookSmoothed(pitch, roll, yaw, sensitivity);
		} else {
			changeElytraLookSmoothed(pitch, yaw, roll, sensitivity);
		}
	}

	//Default parameter function
	public static void changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity) {
		changeElytraLook(pitch, yaw, roll, sensitivity, true);
	}

	// with this the sensitivity won't be swapped by the option
	public static void changeElytraLookSmoothed(double pitch, double yaw, double roll, Sensitivity sensitivity) {
		var player = MinecraftClient.getInstance().player;
		if (player == null) return;

		// calculate time since last update
		double time = GlfwUtil.getTime();
		double delta = time - lastLookUpdate;
		lastLookUpdate = time;

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
