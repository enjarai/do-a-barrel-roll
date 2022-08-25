package nl.enjarai.doabarrelroll.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    // redirect mouse handling to our own code
    @Redirect(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
            )
    )
    private void changeLookDirection(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
        DoABarrelRollClient.updateMouse(player, cursorDeltaX, cursorDeltaY);
    }
}
