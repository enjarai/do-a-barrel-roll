package nl.enjarai.doabarrelroll.impl.event;

import net.minecraft.util.Identifier;
import nl.enjarai.doabarrelroll.api.event.RollEvents;
import nl.enjarai.doabarrelroll.api.event.RollGroup;
import nl.enjarai.doabarrelroll.api.event.TriState;

import java.util.HashMap;
import java.util.function.Supplier;

public class RollGroupImpl extends EventImpl<RollGroup.RollCondition> implements RollGroup {
    public static final HashMap<Identifier, RollGroup> instances = new HashMap<>();

    public RollGroupImpl() {
        RollEvents.SHOULD_ROLL_CHECK.register(this::get);
    }

    @Override
    public void trueIf(Supplier<Boolean> condition, int priority) {
        register(() -> condition.get() ? TriState.TRUE : TriState.PASS, priority);
    }

    @Override
    public void trueIf(Supplier<Boolean> condition) {
        trueIf(condition, 0);
    }

    @Override
    public void falseUnless(Supplier<Boolean> condition, int priority) {
        register(() -> condition.get() ? TriState.PASS : TriState.FALSE, priority);
    }

    @Override
    public void falseUnless(Supplier<Boolean> condition) {
        falseUnless(condition, 0);
    }

    @Override
    public Boolean get() {
        for (var condition : getListeners()) {
            var result = condition.shouldRoll();
            if (result != TriState.PASS) {
                return result == TriState.TRUE;
            }
        }
        return false;
    }
}
