package nl.enjarai.doabarrelroll.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import nl.enjarai.doabarrelroll.EventCallbacksClient;
import nl.enjarai.doabarrelroll.util.StarFoxUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "HEAD"
            )
    )
    private void doABarrelRoll$captureTickDelta(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.getMatrices().push();
        EventCallbacksClient.onRenderCrosshair(context, tickCounter, context.getScaledWindowWidth(), context.getScaledWindowHeight());
    }

    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "RETURN"
            )
    )
    private void doABarrelRoll$renderCrosshairReturn(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.getMatrices().pop();
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    //? if fabric {
                    target = "Lnet/minecraft/client/gui/LayeredDrawer;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
                     //?} else
                    /*target = "Lnet/neoforged/neoforge/client/gui/GuiLayerManager;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"*/
            )
    )
    private void doABarrelRoll$renderPeppy(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        StarFoxUtil.renderPeppy(context, tickCounter.getTickDelta(false), context.getScaledWindowWidth(), context.getScaledWindowHeight());
    }
}
