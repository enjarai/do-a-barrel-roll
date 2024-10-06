package nl.enjarai.doabarrelroll.api.event;

import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.impl.event.EventImpl;

public interface ClientEvents {
    Event<ServerConfigUpdateEvent> SERVER_CONFIG_UPDATE = new EventImpl<>();

    interface ServerConfigUpdateEvent {
        void updateServerConfig(LimitedModConfigServer config);
    }

    static void updateServerConfig(LimitedModConfigServer config) {
        for (var listener : SERVER_CONFIG_UPDATE.getListeners()) {
            listener.updateServerConfig(config);
        }
    }
}
