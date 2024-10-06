package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public record ConfigUpdateAckS2CPacket(int protocolVersion, boolean success) implements CustomPayload {
    public static final Id<ConfigUpdateAckS2CPacket> PACKET_ID = new Id<>(DoABarrelRoll.id("config_update_ack"));
    public static final PacketCodec<ByteBuf, ConfigUpdateAckS2CPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ConfigUpdateAckS2CPacket::protocolVersion,
            PacketCodecs.BOOL, ConfigUpdateAckS2CPacket::success,
            ConfigUpdateAckS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
