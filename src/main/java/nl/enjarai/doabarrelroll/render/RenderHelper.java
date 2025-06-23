package nl.enjarai.doabarrelroll.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;

import java.util.function.BiConsumer;

public class RenderHelper {
    public static final RenderPipeline INVERTED = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation("pipeline/crosshair")
                    .withBlend(new BlendFunction(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO))
                    .build()
    );

    public static BiConsumer<Integer, Integer> blankPixel(DrawContext drawContext) {
        return (x, y) -> {
            drawContext.fill(INVERTED, x, y, x + 1, y + 1, 0xffffffff);
        };
    }
}
