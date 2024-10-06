package nl.enjarai.doabarrelroll.api.event;

import nl.enjarai.doabarrelroll.impl.event.EventImpl;

public interface ThrustEvents {
    /**
     * Use this event to register inputs that modify thrust.
     */
    Event<ModifyThrustInputEvent> MODIFY_THRUST_INPUT = new EventImpl<>();

    interface ModifyThrustInputEvent {
        double modify(double input);
    }

    static double modifyThrustInput(double input) {
        for (var listener : MODIFY_THRUST_INPUT.getListeners()) {
            input = listener.modify(input);
        }

        return input;
    }
}
