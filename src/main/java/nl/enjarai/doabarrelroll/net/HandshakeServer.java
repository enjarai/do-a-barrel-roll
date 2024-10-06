package nl.enjarai.doabarrelroll.net;

import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.ConfigResponseC2SPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigSyncS2CPacket;
import nl.enjarai.doabarrelroll.util.DelayedRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public class HandshakeServer<P extends ConfigSyncS2CPacket> {
    public static final int PROTOCOL_VERSION = 4;

    private final PacketConstructor<P> packetConstructor;
    private final ServerConfigHolder configHolder;
    private final Map<ServerPlayNetworkHandler, ClientInfo> syncStates = new WeakHashMap<>();
    private final Map<ServerPlayNetworkHandler, DelayedRunnable> scheduledKicks = new WeakHashMap<>();
    private final Function<ServerPlayNetworkHandler, Boolean> getsLimitedCheck;
    private final Codec<ModConfigServer> transferCodec = ModConfigServer.CODEC;
    private final Codec<LimitedModConfigServer> limitedTransferCodec = LimitedModConfigServer.getCodec();

    public HandshakeServer(PacketConstructor<P> packetConstructor, ServerConfigHolder configHolder, Function<ServerPlayNetworkHandler, Boolean> getsLimitedCheck) {
        this.packetConstructor = packetConstructor;
        this.configHolder = configHolder;
        this.getsLimitedCheck = getsLimitedCheck;
    }

    public void tick(MinecraftServer server) {
        var it = scheduledKicks.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            if (entry.getValue().isDone()) {
                it.remove();
            } else {
                entry.getValue().tick();
            }
        }
    }

    public ClientInfo getHandshakeState(ServerPlayerEntity player) {
        return getHandshakeState(player.networkHandler);
    }

    public ClientInfo getHandshakeState(ServerPlayNetworkHandler handler) {
        return syncStates.computeIfAbsent(handler, key -> new ClientInfo(HandshakeState.NOT_SENT, PROTOCOL_VERSION, true));
    }

    public P initiateConfigSync(ServerPlayNetworkHandler handler) {
        var isLimited = getsLimitedCheck.apply(handler);
        getHandshakeState(handler).isLimited = isLimited;
        var config = configHolder.instance;

        return packetConstructor.construct(PROTOCOL_VERSION, config, isLimited, isLimited ? ModConfigServer.DEFAULT : config);
    }

    public void configSentToClient(ServerPlayNetworkHandler handler) {
        getHandshakeState(handler).state = HandshakeState.SENT;

        var config = configHolder.instance;
        if (config.getSyncTimeout() != null) {
            scheduledKicks.put(handler, new DelayedRunnable(config.getSyncTimeout(), () -> {
                if (getHandshakeState(handler).state != HandshakeState.ACCEPTED) {
                    DoABarrelRoll.LOGGER.warn(
                            "{} did not accept config syncing, config indicates we kick them.",
                            handler.getPlayer().getName().getString()
                    );
                    handler.disconnect(config.getSyncTimeoutMessage());
                }
            }));
        }
    }

    public HandshakeState clientReplied(ServerPlayNetworkHandler handler, ConfigResponseC2SPacket packet) {
        var info = getHandshakeState(handler);
        var player = handler.getPlayer();

        if (info.state == HandshakeState.SENT) {
            var protocolVersion = packet.protocolVersion();
            if (protocolVersion < 1 || protocolVersion > PROTOCOL_VERSION) {
                DoABarrelRoll.LOGGER.warn(
                        "Client of {} sent unknown protocol version, expected range 1-{}, got {}. Will attempt to proceed anyway.",
                        player.getName().getString(),
                        PROTOCOL_VERSION,
                        protocolVersion
                );
            }

            if (protocolVersion == 2 && info.protocolVersion != 2) {
                DoABarrelRoll.LOGGER.info("Client of {} is using an older protocol version, resending.", player.getName().getString());
                info.state = HandshakeState.RESEND;
            } else if (packet.success()) {
                DoABarrelRoll.LOGGER.info("Client of {} accepted server config.", player.getName().getString());
                info.state = HandshakeState.ACCEPTED;
            } else {
                DoABarrelRoll.LOGGER.warn(
                        "Client of {} failed to process server config, check client logs find what went wrong.",
                        player.getName().getString());
                info.state = HandshakeState.FAILED;
            }
            info.protocolVersion = protocolVersion;
        }

        return info.state;
    }

    public void playerDisconnected(ServerPlayNetworkHandler handler) {
        syncStates.remove(handler);
    }

    public static class ClientInfo {
        public HandshakeState state;
        public int protocolVersion;
        public boolean isLimited;

        public ClientInfo(HandshakeState state, int protocolVersion, boolean isLimited) {
            this.state = state;
            this.protocolVersion = protocolVersion;
            this.isLimited = isLimited;
        }
    }

    public interface PacketConstructor<P extends ConfigSyncS2CPacket> {
        P construct(int protocolVersion, LimitedModConfigServer applicableConfig, boolean isLimited, ModConfigServer fullConfig);
    }

    public enum HandshakeState {
        NOT_SENT,
        SENT,
        ACCEPTED,
        FAILED,
        RESEND
    }
}
