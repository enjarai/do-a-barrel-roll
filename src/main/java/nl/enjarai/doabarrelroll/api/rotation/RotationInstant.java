package nl.enjarai.doabarrelroll.api.rotation;

import nl.enjarai.doabarrelroll.config.Sensitivity;
import nl.enjarai.doabarrelroll.impl.rotation.RotationInstantImpl;

import java.util.function.BooleanSupplier;

public interface RotationInstant {
    static RotationInstant of(double pitch, double yaw, double roll) {
        return new RotationInstantImpl(pitch, yaw, roll);
    }

    double pitch();

    double yaw();

    double roll();

    RotationInstant add(double pitch, double yaw, double roll);

    RotationInstant multiply(double pitch, double yaw, double roll);

    /**
     * Add absolute upright rotation to this rolled rotation, taking roll into account.
     */
    RotationInstant addAbsolute(double x, double y, double currentRoll);

    RotationInstant applySensitivity(Sensitivity sensitivity);

    RotationInstant useModifier(ConfiguresRotation modifier, BooleanSupplier condition);

    RotationInstant useModifier(ConfiguresRotation modifier);

    @FunctionalInterface
    interface ConfiguresRotation {
        RotationInstant apply(RotationInstant rotationInstant);
    }
}
