package nl.enjarai.doabarrelroll.net.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.config.ModConfigServer;

public record ConfigSyncS2CPacket(int protocolVersion, LimitedModConfigServer applicableConfig, boolean isLimited, ModConfigServer fullConfig) implements CustomPayload {
    public static final Id<ConfigSyncS2CPacket> PACKET_ID = new Id<>(DoABarrelRoll.id("config_sync"));
    public static final PacketCodec<PacketByteBuf, ConfigSyncS2CPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ConfigSyncS2CPacket::protocolVersion,
            LimitedModConfigServer.getPacketCodec(), ConfigSyncS2CPacket::applicableConfig,
            PacketCodecs.BOOL, ConfigSyncS2CPacket::isLimited,
            ModConfigServer.PACKET_CODEC, ConfigSyncS2CPacket::fullConfig,
            ConfigSyncS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
