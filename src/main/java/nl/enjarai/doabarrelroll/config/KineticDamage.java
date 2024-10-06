package nl.enjarai.doabarrelroll.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public enum KineticDamage {
    VANILLA,
    HIGH_SPEED,
    NONE,
    INSTANT_KILL;

    public static final Codec<KineticDamage> CODEC = Codec.STRING.comapFlatMap(name -> {
        try {
            return DataResult.success(valueOf(name));
        } catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Unknown kinetic damage type: " + name);
        }
    }, KineticDamage::name);
    public static final PacketCodec<ByteBuf, KineticDamage> PACKET_CODEC =
            PacketCodecs.STRING.xmap(KineticDamage::valueOf, KineticDamage::name);
}
