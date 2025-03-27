package nl.enjarai.doabarrelroll.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.BiConsumer;

public class RenderHelper {
    public static final RenderLayer INVERTED = RenderLayer.of(
            "crosshair",
            786432,
            RenderPipelines.register(
                    RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                            .withLocation("pipeline/crosshair")
                            .withBlend(new BlendFunction(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO))
                            .build()
            ),
            RenderLayer.MultiPhaseParameters.builder().build(false)
    );

    public static BiConsumer<Integer, Integer> blankPixel(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        return (x, y) -> {
            int color = 0xffffffff;
            var matrix = matrices.peek().getPositionMatrix();

            var bufferBuilder = vertexConsumers.getBuffer(INVERTED);
            bufferBuilder.vertex(matrix, (float) x, (float) y + 1, 0.0F).color(color);
            bufferBuilder.vertex(matrix, (float) x + 1, (float) y + 1, 0.0F).color(color);
            bufferBuilder.vertex(matrix, (float) x + 1, (float) y, 0.0F).color(color);
            bufferBuilder.vertex(matrix, (float) x, (float) y, 0.0F).color(color);
        };
    }
}
