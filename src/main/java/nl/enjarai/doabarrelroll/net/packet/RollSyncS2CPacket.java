package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public record RollSyncS2CPacket(int entityId, boolean rolling, float roll) implements CustomPayload {
    public static final Id<RollSyncS2CPacket> PACKET_ID = new Id<>(DoABarrelRoll.id("roll_sync"));
    public static final PacketCodec<ByteBuf, RollSyncS2CPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RollSyncS2CPacket::entityId,
            PacketCodecs.BOOL, RollSyncS2CPacket::rolling,
            PacketCodecs.FLOAT, RollSyncS2CPacket::roll,
            RollSyncS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
