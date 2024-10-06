package nl.enjarai.doabarrelroll.api;

import net.minecraft.client.network.ClientPlayerEntity;
import org.joml.Vector2d;

public interface RollMouse {
    boolean doABarrelRoll$updateMouse(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY, double mouseDelta);

    Vector2d doABarrelRoll$getMouseTurnVec();
}
