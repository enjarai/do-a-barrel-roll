package nl.enjarai.doabarrelroll.net;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.ClientEvents;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.*;

public class ClientNetworking {
    public static final HandshakeClient<ConfigResponseC2SPacket> HANDSHAKE_CLIENT = new HandshakeClient<>(
            ConfigResponseC2SPacket::new,
            ClientEvents::updateServerConfig
    );
    public static final ServerConfigUpdateClient<ConfigUpdateC2SPacket> CONFIG_UPDATE_CLIENT = new ServerConfigUpdateClient<>(
            ConfigUpdateC2SPacket::new
    );

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPacket.PACKET_ID, (payload, context) -> {
            var response = HANDSHAKE_CLIENT.handleConfigSync(payload);
            context.responseSender().sendPacket(response);

            if (HANDSHAKE_CLIENT.hasConnected()) {
                // Initialize roll sync
                ClientPlayNetworking.registerReceiver(RollSyncS2CPacket.PACKET_ID, (payload1, context1) -> {
                    var client = MinecraftClient.getInstance();
                    if (client.world == null) {
                        return;
                    }

                    var entity = client.world.getEntityById(payload1.entityId());
                    if (entity == null) {
                        return;
                    }
                    var rollEntity = (RollEntity) entity;

                    rollEntity.doABarrelRoll$setRolling(payload1.rolling());
                    rollEntity.doABarrelRoll$setRoll(MathHelper.wrapDegrees(payload1.roll()));
                });

                // Initialize config update ack listener
                ClientPlayNetworking.registerReceiver(ConfigUpdateAckS2CPacket.PACKET_ID, (payload1, context1) -> {
                     CONFIG_UPDATE_CLIENT.updateAcknowledged(payload1);
                });
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> HANDSHAKE_CLIENT.reset());
    }

    public static void sendRollUpdate(RollEntity entity) {
        if (HANDSHAKE_CLIENT.hasConnected()) {
            boolean rolling = entity.doABarrelRoll$isRolling();
            float roll = entity.doABarrelRoll$getRoll();

            ClientPlayNetworking.send(new RollSyncC2SPacket(rolling, roll));
        }
    }

    public static void sendConfigUpdatePacket(ModConfigServer config) {
        ClientPlayNetworking.send(CONFIG_UPDATE_CLIENT.prepUpdatePacket(config));
    }
}
