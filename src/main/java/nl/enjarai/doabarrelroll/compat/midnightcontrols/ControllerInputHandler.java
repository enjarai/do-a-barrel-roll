package nl.enjarai.doabarrelroll.compat.midnightcontrols;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.client.MinecraftClient;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.ElytraMath;
import nl.enjarai.doabarrelroll.config.ModConfig;

import static org.lwjgl.glfw.GLFW.*;

public class ControllerInputHandler {

    public static boolean onControllerInput(MinecraftClient client, int axis, float value, int state) {

        if (DoABarrelRollClient.isFallFlying()) {

            // some math to process the raw input
            var powValue = Math.pow(value, 2.0d);
            var rotationAmount = MidnightControlsConfig.rotationSpeed * powValue * 1.5d;
            var rotationDelta = state == 1 ? rotationAmount : -rotationAmount;

            // calculate the smoothing and apply the rotation
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_X) {
                DoABarrelRollClient.changeElytraLook(0, 0, rotationDelta * MidnightControlsConfig.getRightXAxisSign(),
                        ModConfig.INSTANCE.controllerSensitivity);

            } else if (axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                DoABarrelRollClient.changeElytraLook(rotationDelta * MidnightControlsConfig.getRightYAxisSign(), 0, 0,
                        ModConfig.INSTANCE.controllerSensitivity);

            } else if (axis == GLFW_GAMEPAD_AXIS_LEFT_X) {
                DoABarrelRollClient.changeElytraLook(0, rotationDelta, 0,
                        ModConfig.INSTANCE.controllerSensitivity);

            }

            return true;
        }

        return false;
    }

    public static void afterLookUpdate(MinecraftClient client) {
        if (client.player != null && DoABarrelRollClient.landingLerp >= 1) {
            DoABarrelRollClient.left = ElytraMath.getAssumedLeft(client.player.getYaw());
        }
    }
}
