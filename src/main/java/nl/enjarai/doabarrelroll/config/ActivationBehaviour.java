package nl.enjarai.doabarrelroll.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum ActivationBehaviour implements NameableEnum {
    VANILLA,
    TRIPLE_JUMP,
    HYBRID,
    HYBRID_TOGGLE;

    @Override
    public Text getDisplayName() {
        return Text.translatable("config.do_a_barrel_roll.controls.activation_behaviour." + this.name().toLowerCase());
    }
}
