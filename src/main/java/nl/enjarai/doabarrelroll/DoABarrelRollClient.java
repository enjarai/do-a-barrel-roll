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

		changeElytraLook(cursorDeltaY, 0, cursorDeltaX, ModConfig.INSTANCE.desktopSensitivity, ModConfig.INSTANCE.smoothing);
	}
	
	public static void onWorldRender(MinecraftClient client, float tickDelta, long limitTime, MatrixStack matrix) {

		if (client.player == null || !client.player.isFallFlying()) {

			clearSmoothers();

		} else {

			var yawDelta = 25f;
			var yaw = 0f;

			// Strafe buttons
			if (client.options.leftKey.isPressed()) {
				yaw -= yawDelta;
			}
			if (client.options.rightKey.isPressed()) {
				yaw += yawDelta;
			}

			//Realistic roll keep adding to yaw.
			if (!client.isPaused()) {
				double roll = -Math.acos(left.dotProduct(ElytraMath.getAssumedLeft(client.player.getYaw())));
				if (left.getY() < 0) roll *= -1;
				var change = Math.sin(roll);

				var scalar = 10 * ElytraMath.sigmoid(client.player.getVelocity().length()*2-2);
				change *= scalar;

				yaw += change;
			}
			changeElytraLook(0, yaw, 0, ModConfig.INSTANCE.desktopSensitivity, ModConfig.INSTANCE.smoothing);

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

	public static void changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, Sensitivity smoothing) {
		var player = MinecraftClient.getInstance().player;
		if (player == null) return;

		// calculate time since last update
		double time = GlfwUtil.getTime();
		double delta = time - lastLookUpdate;
		lastLookUpdate = time;

		// smooth the look changes
		pitch = pitchSmoother.smooth(pitch, smoothing.pitch * delta);
		yaw = yawSmoother.smooth(yaw, smoothing.yaw * delta);
		roll = rollSmoother.smooth(roll, smoothing.roll * delta);

		// apply the look changes
		if (ModConfig.INSTANCE.switchRollAndYaw) {
			ElytraMath.changeElytraLookDirectly(player, pitch, roll, yaw, sensitivity);
		} else {
			ElytraMath.changeElytraLookDirectly(player, pitch, yaw, roll, sensitivity);
		}
	}
	
	public static boolean isFallFlying() {
		var player = MinecraftClient.getInstance().player;
		return player != null && player.isFallFlying();
	}
}
