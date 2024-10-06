package nl.enjarai.doabarrelroll.net;

import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateAckS2CPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateC2SPacket;
import nl.enjarai.doabarrelroll.util.ToastUtil;

public class ServerConfigUpdateClient<P extends ConfigUpdateC2SPacket> {
    private final PacketConstructor<P> packetConstructor;
    private boolean waitingForAck = false;

    public ServerConfigUpdateClient(PacketConstructor<P> packetConstructor) {
        this.packetConstructor = packetConstructor;
    }

    public P prepUpdatePacket(ModConfigServer config) {
        waitingForAck = true;
        return packetConstructor.construct(HandshakeServer.PROTOCOL_VERSION, config);
    }

    public void updateAcknowledged(ConfigUpdateAckS2CPacket packet) {
        if (waitingForAck) {
            waitingForAck = false;

            var protocolVersion = packet.protocolVersion();
            if (protocolVersion != HandshakeServer.PROTOCOL_VERSION) {
                DoABarrelRoll.LOGGER.warn("Received config update ack with unknown protocol version: {}, will attempt to read anyway", protocolVersion);
            }

            var success = packet.success();
            if (success) {
                // I don't think we need this
//                    var data = ModConfigServer.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(buf.readString()))
//                            .getOrThrow(false, DoABarrelRoll.LOGGER::warn).getFirst();
//                    DoABarrelRollClient.HANDSHAKE_CLIENT.setConfig(data);

                ToastUtil.toasty("server_config_updated");
            } else {
                ToastUtil.toasty("server_config_update_failed");
            }
        }
    }

    public interface PacketConstructor<P extends ConfigUpdateC2SPacket> {
        P construct(int protocolVersion, ModConfigServer config);
    }
}
