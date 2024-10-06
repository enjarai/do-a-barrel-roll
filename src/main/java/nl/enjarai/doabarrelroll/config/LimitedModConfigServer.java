package nl.enjarai.doabarrelroll.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface LimitedModConfigServer {
    LimitedModConfigServer OPERATOR = new Impl(true, false);

    static Codec<LimitedModConfigServer> getCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("allowThrusting", ModConfigServer.DEFAULT.allowThrusting()).forGetter(LimitedModConfigServer::allowThrusting),
                Codec.BOOL.optionalFieldOf("forceEnabled", ModConfigServer.DEFAULT.forceEnabled()).forGetter(LimitedModConfigServer::forceEnabled)
        ).apply(instance, Impl::new));
    }

    static PacketCodec<PacketByteBuf, LimitedModConfigServer> getPacketCodec() {
        return PacketCodec.tuple(
                PacketCodecs.BOOL, LimitedModConfigServer::allowThrusting,
                PacketCodecs.BOOL, LimitedModConfigServer::forceEnabled,
                Impl::new
        );
    }

    boolean allowThrusting();

    boolean forceEnabled();

    record Impl(boolean allowThrusting, boolean forceEnabled) implements LimitedModConfigServer {
    }
}
