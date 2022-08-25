package nl.enjarai.doabarrelroll;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;

public class MomentumCrosshairWidget {

    public static void render(MatrixStack matrices, int scaledWidth, int scaledHeight, Vec2f mouseTurnVec) {
        int color = 0xffffffff;
        int centerX = scaledWidth / 2;
        int centerY = scaledHeight / 2 - 1;
        var turnVec = mouseTurnVec.multiply(50);
        var lineVec = turnVec.add(turnVec.negate().normalize().multiply(Math.min(turnVec.length(), 10f)));

        if (!lineVec.equals(Vec2f.ZERO)) {

            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            ModMath.forBresenhamLine(centerX, centerY, centerX + (int) lineVec.x, centerY + (int) lineVec.y, (x, y) -> {

                var matrix = matrices.peek().getPositionMatrix();
                var bufferBuilder = Tessellator.getInstance().getBuffer();

                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix, (float) x, (float) y + 1, 0.0F).color(color).next();
                bufferBuilder.vertex(matrix, (float) x + 1, (float) y + 1, 0.0F).color(color).next();
                bufferBuilder.vertex(matrix, (float) x + 1, (float) y, 0.0F).color(color).next();
                bufferBuilder.vertex(matrix, (float) x, (float) y, 0.0F).color(color).next();
                BufferRenderer.drawWithShader(bufferBuilder.end());

            });
            RenderSystem.enableTexture();
        }

        // change the position of the crosshair, which is rendered up the stack
        matrices.translate((int) turnVec.x, (int) turnVec.y, 0);
    }
}
