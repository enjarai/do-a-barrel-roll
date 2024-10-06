package nl.enjarai.doabarrelroll.render;

import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.BiConsumer;

public class RenderHelper {
    public static BiConsumer<Integer, Integer> blankPixel(MatrixStack matrices) {
        return (x, y) -> {
            int color = 0xffffffff;
            var matrix = matrices.peek().getPositionMatrix();
            var bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            //TODO: Figure out if I can just delete the nexts here?
            bufferBuilder.vertex(matrix, (float) x, (float) y + 1, 0.0F).color(color);
            bufferBuilder.vertex(matrix, (float) x + 1, (float) y + 1, 0.0F).color(color);
            bufferBuilder.vertex(matrix, (float) x + 1, (float) y, 0.0F).color(color);
            bufferBuilder.vertex(matrix, (float) x, (float) y, 0.0F).color(color);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        };
    }
}
