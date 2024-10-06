package nl.enjarai.doabarrelroll.mixin.client.key;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import nl.enjarai.doabarrelroll.api.key.InputContext;
import nl.enjarai.doabarrelroll.impl.key.InputContextImpl;
import nl.enjarai.doabarrelroll.util.key.ContextualKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements ContextualKeyBinding {
    @Unique
    private final ArrayList<InputContext> contexts = new ArrayList<>();

    @Override
    public List<InputContext> doABarrelRoll$getContexts() {
        return contexts;
    }

    @Override
    public void doABarrelRoll$addToContext(InputContext context) {
        contexts.add(context);
    }

    private static KeyBinding getContextKeyBinding(InputUtil.Key key) {
        for (var context : InputContextImpl.getContexts()) {
            var binding = context.getKeyBinding(key);
            if (binding != null) {
                if (context.isActive()) {
                    return binding;
                } else {
                    binding.setPressed(false);
                }
            }
        }

        return null;
    }

    //? if fabric {
    
    @WrapOperation(
            method = "onKeyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0 // We let all these mixins fail if they need to as a temporary workaround to be compatible with Connector.
    )
    private static Object doABarrelRoll$applyKeybindContext(Map<InputUtil.Key, KeyBinding> map, Object key, Operation<KeyBinding> original) {
        var binding = getContextKeyBinding((InputUtil.Key) key);
        if (binding != null) return binding;

        return original.call(map, key);
    }

    @WrapOperation(
            method = "setKeyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private static Object doABarrelRoll$applyKeybindContext2(Map<InputUtil.Key, KeyBinding> map, Object key, Operation<KeyBinding> original) {
        var binding = getContextKeyBinding((InputUtil.Key) key);
        var originalBinding = original.call(map, key);
        if (binding != null) {
            if (originalBinding != null) {
                originalBinding.setPressed(false);
            }
            return binding;
        }

        return originalBinding;
    }

    @Inject(
            method = "updateKeysByCode",
            at = @At("HEAD"),
            require = 0
    )
    private static void doABarrelRoll$updateContextualKeys(CallbackInfo ci) {
        for (var context : InputContextImpl.getContexts()) {
            context.updateKeysByCode();
        }
    }

    @WrapWithCondition(
            method = "updateKeysByCode",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private static boolean doABarrelRoll$skipAddingContextualKeys(Map<InputUtil.Key, KeyBinding> map, Object key, Object keyBinding) {
        return !InputContextImpl.contextsContain((KeyBinding) keyBinding);
    }
    //?}
}
