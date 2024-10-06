package nl.enjarai.doabarrelroll.api.event;

import net.minecraft.server.MinecraftServer;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.impl.event.EventImpl;

public interface ServerEvents {
    Event<ServerConfigUpdateEvent> SERVER_CONFIG_UPDATE = new EventImpl<>();

    interface ServerConfigUpdateEvent {
        void updateServerConfig(MinecraftServer server, ModConfigServer config);
    }

    static void updateServerConfig(MinecraftServer server, ModConfigServer config) {
        for (var listener : SERVER_CONFIG_UPDATE.getListeners()) {
            listener.updateServerConfig(server, config);
        }
    }
}
