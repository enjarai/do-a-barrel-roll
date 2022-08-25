package nl.enjarai.doabarrelroll.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.ElytraMath;
import nl.enjarai.doabarrelroll.ModMath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Shadow abstract void renderCrosshair(MatrixStack matrices);

    @Redirect(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V"
            )
    )
    private void onRenderCrosshair(InGameHud inGameHud, MatrixStack matrixStack) {
        matrixStack.push();
        DoABarrelRollClient.onRenderCrosshair(matrixStack, scaledWidth, scaledHeight);
        renderCrosshair(matrixStack);
        matrixStack.pop();
    }
}
