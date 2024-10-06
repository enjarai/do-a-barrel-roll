package nl.enjarai.doabarrelroll.config;

import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;

public enum ActivationBehaviour implements TranslatableOption {
    VANILLA,
    TRIPLE_JUMP,
    HYBRID,
    HYBRID_TOGGLE;

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getTranslationKey() {
        return "config.do_a_barrel_roll.controls.activation_behaviour." + this.name().toLowerCase();
    }

    @Override
    public Text getText() {
        return Text.translatable(getTranslationKey());
    }
}
