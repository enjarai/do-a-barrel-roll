package nl.enjarai.doabarrelroll.net;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

public interface SyncableConfig<T, L> {
    Integer getSyncTimeout();

    Text getSyncTimeoutMessage();

    L getLimited(ServerPlayNetworkHandler handler);
}
