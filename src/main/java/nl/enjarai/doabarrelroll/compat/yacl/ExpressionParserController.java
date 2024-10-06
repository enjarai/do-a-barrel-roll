package nl.enjarai.doabarrelroll.compat.yacl;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import net.minecraft.text.Text;
import nl.enjarai.doabarrelroll.math.ExpressionParser;
import nl.enjarai.doabarrelroll.math.SyntaxHighlighter;

public record ExpressionParserController(Option<ExpressionParser> option) implements IStringController<ExpressionParser> {
    @Override
    public String getString() {
        return option.pendingValue().getString();
    }

    @Override
    public void setFromString(String value) {
        option.requestSet(new ExpressionParser(value));
    }
    
    @Override
    public Text formatValue() {
        return SyntaxHighlighter.highlightText(getString());
    }
    
    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new StringControllerElement(this, screen, widgetDimension, true) {
            @Override
            protected int getValueColor() {
                return option.pendingValue().hasError() ? 0xFF5555 : super.getValueColor();
            }
        };
    }
}