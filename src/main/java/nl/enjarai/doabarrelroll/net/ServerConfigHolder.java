package nl.enjarai.doabarrelroll.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateAckS2CPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateC2SPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.BiConsumer;

public class ServerConfigHolder<P extends ConfigUpdateAckS2CPacket> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public final Path configFile;
    public final Codec<ModConfigServer> codec;
    private final PacketConstructor<P> packetConstructor;
    private final BiConsumer<MinecraftServer, ModConfigServer> updateCallback;
    private HandshakeServer<?> handshakeServer;
    public ModConfigServer instance;

    public ServerConfigHolder(Path configFile, Codec<ModConfigServer> codec, PacketConstructor<P> packetConstructor, BiConsumer<MinecraftServer, ModConfigServer> updateCallback) {
        this.configFile = configFile;
        this.codec = codec;
        this.packetConstructor = packetConstructor;
        this.updateCallback = updateCallback;

        load();
    }

    public void load() {
        ModConfigServer config = null;

        if (Files.exists(configFile)) {
            // An existing config is present, we should use its values
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(configFile), StandardCharsets.UTF_8)
            )) {
                // Parses the config file and puts the values into config object
                config = codec.decode(JsonOps.INSTANCE, JsonParser.parseReader(fileReader))
                        .getOrThrow(e -> {
                            throw new RuntimeException(e);
                        })
                        .getFirst();
            } catch (IOException | RuntimeException e) {
                DoABarrelRoll.LOGGER.error("Failed to parse server config file, regenerating: ", e);
            }
        }
        // config will be null if the file doesn't exist or if it failed to parse
        if (config == null || !config.isValid()) {
            config = ModConfigServer.DEFAULT;
        }

        instance = config;
        // Saves the file in order to write new fields if they were added
        save();
    }

    public void save() {
        try {
            // Creates the file if it doesn't exist
            Files.createDirectories(configFile.getParent());
            // Writes the config to the file
            Files.writeString(configFile, GSON.toJson(codec.encodeStart(JsonOps.INSTANCE, instance)
                    .getOrThrow(e -> {
                        throw new RuntimeException(e);
                    })), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException | RuntimeException e) {
            DoABarrelRoll.LOGGER.error("Failed to save server config file: ", e);
        }
    }

    public P clientSendsUpdate(ServerPlayerEntity player, ConfigUpdateC2SPacket packet) {
        var info = handshakeServer.getHandshakeState(player);
        var accepted = info.state == HandshakeServer.HandshakeState.ACCEPTED;
        var hasPermission = ModConfigServer.canModify(player.networkHandler);

        // Only players that have accepted the handshake and have permission can update the config
        if (!accepted || !hasPermission) {
            DoABarrelRoll.LOGGER.warn(
                    "Client of {} tried to update the server config, but is not allowed to. Rejecting.",
                    player.getName().getString()
            );
            return packetConstructor.construct(HandshakeServer.PROTOCOL_VERSION, false);
        }

        try {
            var protocolVersion = packet.protocolVersion();
            if (protocolVersion != HandshakeServer.PROTOCOL_VERSION) {
                DoABarrelRoll.LOGGER.warn(
                        "Client of {} sent unknown protocol version for server config update, expected {}, got {}. Will attempt to proceed anyway.",
                        player.getName().getString(),
                        HandshakeServer.PROTOCOL_VERSION,
                        protocolVersion
                );
            }

            var newConfig = packet.config();

            if (!newConfig.isValid()) {
                throw new RuntimeException("Config arrived, but contains invalid values");
            }

            DoABarrelRoll.LOGGER.info(
                    "{} updated the server config.",
                    player.getName().getString()
            );

            // Only set our instance if everything else succeeds
            instance = newConfig;
            updateCallback.accept(player.getServer(), instance);
            save();
            return packetConstructor.construct(HandshakeServer.PROTOCOL_VERSION, true);
        } catch (RuntimeException e) {
            DoABarrelRoll.LOGGER.warn(
                    "Client of {} sent invalid server config update, rejecting.",
                    player.getName().getString(),
                    e
            );
            return packetConstructor.construct(HandshakeServer.PROTOCOL_VERSION, false);
        }
    }

    public void setHandshakeServer(HandshakeServer<?> handshakeServer) {
        this.handshakeServer = handshakeServer;
    }

    public interface PacketConstructor<P extends ConfigUpdateAckS2CPacket> {
        P construct(int protocolVersion, boolean success);
    }
}
