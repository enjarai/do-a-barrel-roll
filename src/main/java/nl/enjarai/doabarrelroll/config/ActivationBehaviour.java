package nl.enjarai.doabarrelroll.config;

import net.minecraft.text.Text;
public enum ActivationBehaviour {
    VANILLA,
    TRIPLE_JUMP,
    HYBRID,
    HYBRID_TOGGLE;

    public int getId() {
        return this.ordinal();
    }

    public String getTranslationKey() {
        return "config.do_a_barrel_roll.controls.activation_behaviour." + this.name().toLowerCase();
    }

    public Text getText() {
        return Text.translatable(getTranslationKey());
    }
}
