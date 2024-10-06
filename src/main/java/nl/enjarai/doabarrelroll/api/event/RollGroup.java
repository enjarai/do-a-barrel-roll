package nl.enjarai.doabarrelroll.api.event;

import net.minecraft.util.Identifier;
import nl.enjarai.doabarrelroll.impl.event.RollGroupImpl;

import java.util.function.Supplier;

/**
 * A group of conditions that determine whether the camera should be rolling and what effects should be applied.
 * Instances can be directly used as conditions for registered events.
 *
 * <p>Usually, you'll want to use {@link RollGroup#of(Identifier)} to get an instance of this class.
 * You can then use {@link RollGroup#trueIf(Supplier)}, {@link RollGroup#falseUnless(Supplier)} or
 * {@link Event#register(Object)} to add conditions.
 *
 * <p>It is usually recommended to store your instance in a static final field, so you can easily use it in your
 * event registrations.
 *
 * <p>A basic example:
 * <pre>{@code
 * class ModClass {
 *     public static final RollGroup ROLL_GROUP = RollGroup.of(new Identifier("my_mod", "my_roll_group"));
 *
 *     public static void onInitialize() {
 *         ROLL_GROUP.trueIf(() -> yourCondition);
 *
 *         RollEvents.EARLY_CAMERA_MODIFIERS.register((delta, current) -> delta
 *                 .useModifier(Your::modifier),
 *                 10, ROLL_GROUP);
 *     }
 * }
 * }</pre>
 *
 * <p>The conditions are checked in order of priority, and the first condition that returns {@link TriState#TRUE}
 * will cause the camera to roll. If no condition returns {@link TriState#TRUE}, the camera will not roll.
 * If a condition returns {@link TriState#FALSE}, the camera will not roll and no further conditions will be
 * checked. If a condition returns {@link TriState#PASS}, the next condition will be checked.
 */
public interface RollGroup extends Supplier<Boolean>, Event<RollGroup.RollCondition> {
    /**
     * Gets the RollGroup instance for the given identifier.
     * If no instance exists, a new one is created and registered to {@link RollEvents#SHOULD_ROLL_CHECK}
     */
    static RollGroup of(Identifier id) {
        return RollGroupImpl.instances.computeIfAbsent(id, id2 -> new RollGroupImpl());
    }

    void trueIf(Supplier<Boolean> condition, int priority);

    void trueIf(Supplier<Boolean> condition);

    void falseUnless(Supplier<Boolean> condition, int priority);

    void falseUnless(Supplier<Boolean> condition);

    interface RollCondition {
        TriState shouldRoll();
    }
}
