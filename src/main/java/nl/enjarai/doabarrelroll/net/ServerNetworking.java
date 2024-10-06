package nl.enjarai.doabarrelroll.net;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.ServerEvents;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.*;

public class ServerNetworking {
    public static final ServerConfigHolder<ConfigUpdateAckS2CPacket> CONFIG_HOLDER = new ServerConfigHolder<>(
            FabricLoader.getInstance().getConfigDir().resolve(DoABarrelRoll.MODID + "-server.json"),
            ModConfigServer.CODEC, ConfigUpdateAckS2CPacket::new, ServerEvents::updateServerConfig
    );
    public static final HandshakeServer<ConfigSyncS2CPacket> HANDSHAKE_SERVER = new HandshakeServer<>(
            ConfigSyncS2CPacket::new, CONFIG_HOLDER, player -> !ModConfigServer.canModify(player));

    public static void init() {
        CONFIG_HOLDER.setHandshakeServer(HANDSHAKE_SERVER);

        PayloadTypeRegistry.playC2S().register(ConfigResponseC2SPacket.PACKET_ID, ConfigResponseC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ConfigUpdateC2SPacket.PACKET_ID, ConfigUpdateC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RollSyncC2SPacket.PACKET_ID, RollSyncC2SPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(ConfigSyncS2CPacket.PACKET_ID, ConfigSyncS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigUpdateAckS2CPacket.PACKET_ID, ConfigUpdateAckS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RollSyncS2CPacket.PACKET_ID, RollSyncS2CPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ConfigResponseC2SPacket.PACKET_ID, (payload, context) -> {
            var reply = HANDSHAKE_SERVER.clientReplied(context.player().networkHandler, payload);
            if (reply == HandshakeServer.HandshakeState.RESEND) {
                // Resending can happen when the client has a different protocol version than expected.
                sendHandshake(context.player());
            } else if (reply == HandshakeServer.HandshakeState.ACCEPTED) {
                // Init roll syncing
                ServerPlayNetworking.registerReceiver(context.player().networkHandler, RollSyncC2SPacket.PACKET_ID, (payload1, context1) -> {
                    var rollPlayer = (RollEntity) context1.player();

                    var isRolling = payload1.rolling();
                    var roll = payload1.roll();

                    rollPlayer.doABarrelRoll$setRolling(isRolling);
                    rollPlayer.doABarrelRoll$setRoll(isRolling ? MathHelper.wrapDegrees(roll) : 0);
                });

                // Init client -> server config update
                ServerPlayNetworking.registerReceiver(context.player().networkHandler, ConfigUpdateC2SPacket.PACKET_ID, (payload1, context1) -> {
                    context1.responseSender().sendPacket(CONFIG_HOLDER.clientSendsUpdate(context1.player(), payload1));
                });
            }
        });
        // The initial handshake is sent in the CommandManagerMixin.

        ServerEvents.SERVER_CONFIG_UPDATE.register((server, config) -> {
            for (var player : server.getPlayerManager().getPlayerList()) {
                sendHandshake(player);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            HANDSHAKE_SERVER.playerDisconnected(handler);
        });
        ServerTickEvents.END_SERVER_TICK.register(HANDSHAKE_SERVER::tick);
    }

    public static void sendHandshake(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, HANDSHAKE_SERVER.initiateConfigSync(player.networkHandler));
        HANDSHAKE_SERVER.configSentToClient(player.networkHandler);
    }

    public static void sendRollUpdates(Entity entity) {
        var rollEntity = (RollEntity) entity;
        var isRolling = rollEntity.doABarrelRoll$isRolling();
        var roll = rollEntity.doABarrelRoll$getRoll();

        var payload = new RollSyncS2CPacket(entity.getId(), isRolling, roll);

        PlayerLookup.tracking(entity).stream()
                .filter(player -> player != entity)
                .filter(player -> HANDSHAKE_SERVER.getHandshakeState(player).state == HandshakeServer.HandshakeState.ACCEPTED)
                .forEach(player -> ServerPlayNetworking.send(player, payload));
    }
}
