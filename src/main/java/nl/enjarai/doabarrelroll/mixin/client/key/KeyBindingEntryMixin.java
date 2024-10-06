package nl.enjarai.doabarrelroll.mixin.client.key;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import nl.enjarai.doabarrelroll.util.key.ContextualKeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class KeyBindingEntryMixin {
    @Shadow @Final private KeyBinding binding;

    @ModifyExpressionValue(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;equals(Lnet/minecraft/client/option/KeyBinding;)Z"
            )
    )
    private boolean doABarrelRoll$ignoreCertainKeyBindingConflicts(boolean original, @Local KeyBinding otherBinding) {
        var firstContexts = ((ContextualKeyBinding) binding).doABarrelRoll$getContexts();
        var secondContexts = ((ContextualKeyBinding) otherBinding).doABarrelRoll$getContexts();

        // none + none -> original
        // none + has -> false
        // has + none -> false
        // has + has ->
        //   same context -> original
        //   different context -> false

        if (firstContexts.isEmpty() && secondContexts.isEmpty()) return original;
        if (firstContexts.isEmpty() || secondContexts.isEmpty()) return false;
        if (firstContexts.stream().anyMatch(secondContexts::contains)) return original;
        return false;
    }
}
