package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public record ConfigResponseC2SPacket(int protocolVersion, boolean success) implements CustomPayload {
    public static final Id<ConfigResponseC2SPacket> PACKET_ID = new Id<>(DoABarrelRoll.id("config_response"));
    public static final PacketCodec<ByteBuf, ConfigResponseC2SPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ConfigResponseC2SPacket::protocolVersion,
            PacketCodecs.BOOL, ConfigResponseC2SPacket::success,
            ConfigResponseC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
