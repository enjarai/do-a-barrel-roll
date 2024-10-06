package nl.enjarai.doabarrelroll.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import nl.enjarai.doabarrelroll.ModMath;
import org.joml.Vector2d;

public class MomentumCrosshairWidget extends RenderHelper {

    public static void render(MatrixStack matrices, int scaledWidth, int scaledHeight, Vector2d mouseTurnVec) {
        int centerX = scaledWidth / 2;
        int centerY = scaledHeight / 2 - 1;
        mouseTurnVec.mul(50);
        var lineVec = new Vector2d(mouseTurnVec).add(
                new Vector2d(mouseTurnVec).negate().normalize().mul(Math.min(mouseTurnVec.length(), 10f)));

        if (!lineVec.equals(new Vector2d()) && mouseTurnVec.lengthSquared() > 10f * 10f) {

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            ModMath.forBresenhamLine(
                    centerX, centerY,
                    centerX + (int) lineVec.x, centerY + (int) lineVec.y,
                    blankPixel(matrices)
            );
            RenderSystem.defaultBlendFunc();
        }

        // change the position of the crosshair, which is rendered up the stack
        matrices.translate((int) mouseTurnVec.x, (int) mouseTurnVec.y, 0);
    }
}
