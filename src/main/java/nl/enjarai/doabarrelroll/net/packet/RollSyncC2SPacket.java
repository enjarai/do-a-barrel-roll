package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public record RollSyncC2SPacket(boolean rolling, float roll) implements CustomPayload {
    public static final Id<RollSyncC2SPacket> PACKET_ID = new Id<>(DoABarrelRoll.id("roll_sync"));
    public static final PacketCodec<ByteBuf, RollSyncC2SPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, RollSyncC2SPacket::rolling,
            PacketCodecs.FLOAT, RollSyncC2SPacket::roll,
            RollSyncC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
