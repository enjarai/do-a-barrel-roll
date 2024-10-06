package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.ModConfigServer;

public record ConfigUpdateC2SPacket(int protocolVersion, ModConfigServer config) implements CustomPayload {
    public static final Id<ConfigUpdateC2SPacket> PACKET_ID = new Id<>(DoABarrelRoll.id("config_update"));
    public static final PacketCodec<ByteBuf, ConfigUpdateC2SPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ConfigUpdateC2SPacket::protocolVersion,
            ModConfigServer.PACKET_CODEC, ConfigUpdateC2SPacket::config,
            ConfigUpdateC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
