//? if fabric {
package nl.enjarai.doabarrelroll.compat.modmenu.mixin;

import com.terraformersmc.modmenu.gui.ModsScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import nl.enjarai.doabarrelroll.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(ModsScreen.class)
public abstract class ModsScreenMixin extends Screen {
    @Unique
    private double lastTime;
    @Unique
    private double rollSecs;

    protected ModsScreenMixin(Text title) {
        super(title);
    }

    @Inject(
            method = "lambda$init$0",
            at = @At("HEAD"),
            remap = false
    )
    private void onUpdateSearch(String string, CallbackInfo ci) {
        if (ModConfig.INSTANCE.getEnableEasterEggs() && string.toLowerCase(Locale.ROOT).equals("do a barrel roll")) {
            rollSecs += 1;
        }
    }

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        double time = GlfwUtil.getTime();
        double secsDelta = time - lastTime;
        lastTime = time;

        if (rollSecs > 0) {
            // Draw a nice background to prevent previous frames pixels from peeking through
            context.fill(0, 0, width, height, 0xff000000);

            var matrices = context.getMatrices();
            rollSecs -= Math.max(0, secsDelta);
            float roll = (float) (rollSecs * Math.PI * 2);

            // Rotate around center ofc
            matrices.translate(width / 2f, height / 2f, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(roll));
            matrices.translate(-width / 2f, -height / 2f, 0);
        }
    }
}
//?}