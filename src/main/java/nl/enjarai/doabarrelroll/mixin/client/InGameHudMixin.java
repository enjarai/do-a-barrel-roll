package nl.enjarai.doabarrelroll.mixin.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import nl.enjarai.doabarrelroll.EventCallbacksClient;
import nl.enjarai.doabarrelroll.util.StarFoxUtil;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "HEAD"
            )
    )
    private void doABarrelRoll$renderAdditionalCrosshairComponents(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Share("crosshair_offset") LocalRef<Vector2i> crosshairOffset) {
        crosshairOffset.set(EventCallbacksClient.onRenderCrosshair(context, tickCounter, context.getScaledWindowWidth(), context.getScaledWindowHeight()));
    }

    @ModifyArgs(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"
            )
    )
    private void doABarrelRoll$moveCrosshair(Args args, @Share("crosshair_offset") LocalRef<Vector2i> crosshairOffset) {
        var offset = crosshairOffset.get();
        if (offset != null) {
            args.set(2, (int) args.get(2) + offset.x);
            args.set(3, (int) args.get(3) + offset.y);
        }
    }

    @ModifyArgs(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIIIIII)V"
            )
    )
    private void doABarrelRoll$moveCrosshair2(Args args, @Share("crosshair_offset") LocalRef<Vector2i> crosshairOffset) {
        var offset = crosshairOffset.get();
        if (offset != null) {
            args.set(6, (int) args.get(6) + offset.x);
            args.set(7, (int) args.get(7) + offset.y);
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    //? if fabric {
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderBossBarHud(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
                     //?} else
                    /*target = "Lnet/neoforged/neoforge/client/gui/GuiLayerManager;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"*/
            )
    )
    private void doABarrelRoll$renderPeppy(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        StarFoxUtil.renderPeppy(context, tickCounter.getFixedDeltaTicks(), context.getScaledWindowWidth(), context.getScaledWindowHeight());
    }
}
