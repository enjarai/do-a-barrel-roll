package nl.enjarai.doabarrelroll.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.net.SyncableConfig;
import nl.enjarai.doabarrelroll.net.ValidatableConfig;

public record ModConfigServer(boolean allowThrusting,
                              boolean forceEnabled,
                              boolean forceInstalled,
                              int installedTimeout,
                              KineticDamage kineticDamage) implements SyncableConfig<ModConfigServer, LimitedModConfigServer>, LimitedModConfigServer, ValidatableConfig {
    public static final ModConfigServer DEFAULT = new ModConfigServer(
            false, false, false, 40, KineticDamage.VANILLA);

    public static final Codec<ModConfigServer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("allowThrusting", DEFAULT.allowThrusting()).forGetter(ModConfigServer::allowThrusting),
            Codec.BOOL.optionalFieldOf("forceEnabled", DEFAULT.forceEnabled()).forGetter(ModConfigServer::forceEnabled),
            Codec.BOOL.optionalFieldOf("forceInstalled", DEFAULT.forceInstalled()).forGetter(ModConfigServer::forceInstalled),
            Codec.INT.optionalFieldOf("installedTimeout", DEFAULT.installedTimeout()).forGetter(ModConfigServer::installedTimeout),
            KineticDamage.CODEC.optionalFieldOf("kineticDamage", DEFAULT.kineticDamage()).forGetter(ModConfigServer::kineticDamage)
    ).apply(instance, ModConfigServer::new));
    public static final PacketCodec<ByteBuf, ModConfigServer> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ModConfigServer::allowThrusting,
            PacketCodecs.BOOL, ModConfigServer::forceEnabled,
            PacketCodecs.BOOL, ModConfigServer::forceInstalled,
            PacketCodecs.INTEGER, ModConfigServer::installedTimeout,
            KineticDamage.PACKET_CODEC, ModConfigServer::kineticDamage,
            ModConfigServer::new
    );

    @Override
    public Integer getSyncTimeout() {
        return forceInstalled ? installedTimeout : null;
    }

    @Override
    public Text getSyncTimeoutMessage() {
        return Text.of("Please install Do a Barrel Roll 2.4.0 or later to play on this server.");
    }

    @Override
    public LimitedModConfigServer getLimited(ServerPlayNetworkHandler handler) {
        return DoABarrelRoll.checkPermission(handler, DoABarrelRoll.MODID + ".ignore_config", 2)
                ? LimitedModConfigServer.OPERATOR : this;
    }

    public static boolean canModify(ServerPlayNetworkHandler net) {
        return DoABarrelRoll.checkPermission(net, DoABarrelRoll.MODID + ".configure", 3);
    }

    @Override
    public boolean isValid() {
        return installedTimeout > 0;
    }
}
