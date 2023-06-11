package nl.enjarai.doabarrelroll.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.flight.util.RotationInstant;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ModConfig {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public static final Path CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("do_a_barrel_roll-client.json");
    public static ModConfig INSTANCE = loadConfigFile(CONFIG_FILE.toFile());

    public static void touch() {
        // touch the grass
    }

    General general = new General();
    static class General {
        boolean mod_enabled = true;

        Controls controls = new Controls();
        static class Controls {
            boolean switch_roll_and_yaw = false;
            boolean invert_pitch = false;
            boolean momentum_based_mouse = false;
            boolean show_momentum_widget = true;
            ActivationBehaviour activation_behaviour = ActivationBehaviour.VANILLA;
        }

        Hud hud = new Hud();
        static class Hud {
            boolean show_horizon = false;
        }

        Banking banking = new Banking();
        static class Banking {
            boolean enable_banking = true;
            double banking_strength = 20.0;
        }

        Thrust thrust = new Thrust();
        static class Thrust {
            boolean enable_thrust = false;
            double max_thrust = 2.0;
            double thrust_acceleration = 0.1;
            boolean thrust_particles = true;
        }
    }

    SensitivityConfig sensitivity = new SensitivityConfig();
    static class SensitivityConfig {
        Smoothing smoothing = new Smoothing();
        static class Smoothing {
            boolean smoothing_enabled = true;
            double smoothing_pitch = 1.0;
            double smoothing_yaw = 0.4;
            double smoothing_roll = 1.0;
        }

        Sensitivity desktop = new Sensitivity();
        Sensitivity controller = new Sensitivity();
    }

    public boolean getModEnabled() {
        return general.mod_enabled;
    }

    public boolean getSwitchRollAndYaw() {
        return general.controls.switch_roll_and_yaw;
    } //= false;

    public boolean getMomentumBasedMouse() {
        return general.controls.momentum_based_mouse;
    } //= false;

    public boolean getShowMomentumWidget() {
        return general.controls.show_momentum_widget;
    } //= true;

    public boolean getInvertPitch() {
        return general.controls.invert_pitch;
    } //= false;

    public ActivationBehaviour getActivationBehaviour() {
        return general.controls.activation_behaviour;
    } //= ActivationBehaviour.VANILLA;

    public boolean getShowHorizon() {
        return general.hud.show_horizon;
    }

    public boolean getEnableBanking() {
        return general.banking.enable_banking;
    }// = true;

    public double getBankingStrength() {
        return general.banking.banking_strength;
    }// = 20;

    public boolean getEnableThrust() {
        return general.thrust.enable_thrust && DoABarrelRollClient.HANDSHAKE_CLIENT
                .getConfig().map(SyncedModConfig::allowThrusting).orElse(false);
    }

    public double getMaxThrust() {
        return general.thrust.max_thrust;
    }

    public double getThrustAcceleration() {
        return general.thrust.thrust_acceleration;
    }

    public boolean getThrustParticles() {
        return general.thrust.thrust_particles;
    }

    public boolean getSmoothingEnabled() {
        return sensitivity.smoothing.smoothing_enabled;
    }

    public double getSmoothingPitch() {
        return sensitivity.smoothing.smoothing_pitch;
    }

    public double getSmoothingYaw() {
        return sensitivity.smoothing.smoothing_yaw;
    }

    public double getSmoothingRoll() {
        return sensitivity.smoothing.smoothing_roll;
    }

    public Sensitivity getSmoothing() {
        return new Sensitivity(
                sensitivity.smoothing.smoothing_pitch,
                sensitivity.smoothing.smoothing_yaw,
                sensitivity.smoothing.smoothing_roll
        );
    }

    public Sensitivity getDesktopSensitivity() {
        return sensitivity.desktop;
    }

    public double getDesktopPitch() {
        return sensitivity.desktop.pitch;
    }

    public double getDesktopYaw() {
        return sensitivity.desktop.yaw;
    }

    public double getDesktopRoll() {
        return sensitivity.desktop.roll;
    }

    public Sensitivity getControllerSensitivity() {
        return sensitivity.controller;
    }

    public double getControllerPitch() {
        return sensitivity.controller.pitch;
    }

    public double getControllerYaw() {
        return sensitivity.controller.yaw;
    }

    public double getControllerRoll() {
        return sensitivity.controller.roll;
    }

    public void setModEnabled(boolean enabled) {
        general.mod_enabled = enabled;
    }

    public void setSwitchRollAndYaw(boolean enabled) {
        general.controls.switch_roll_and_yaw = enabled;
    }

    public void setMomentumBasedMouse(boolean enabled) {
        general.controls.momentum_based_mouse = enabled;
    }

    public void setShowMomentumWidget(boolean enabled) {
        general.controls.show_momentum_widget = enabled;
    }

    public void setInvertPitch(boolean enabled) {
        general.controls.invert_pitch = enabled;
    }

    public void setActivationBehaviour(ActivationBehaviour behaviour) {
        general.controls.activation_behaviour = behaviour;
    }

    public void setShowHorizon(boolean enabled) {
        general.hud.show_horizon = enabled;
    }

    public void setEnableBanking(boolean enabled) {
        general.banking.enable_banking = enabled;
    }

    public void setBankingStrength(double strength) {
        general.banking.banking_strength = strength;
    }

    public void setEnableThrust(boolean enabled) {
        general.thrust.enable_thrust = enabled;
    }

    public void setMaxThrust(double thrust) {
        general.thrust.max_thrust = thrust;
    }

    public void setThrustAcceleration(double acceleration) {
        general.thrust.thrust_acceleration = acceleration;
    }

    public void setThrustParticles(boolean enabled) {
        general.thrust.thrust_particles = enabled;
    }

    public void setSmoothingEnabled(boolean enabled) {
        sensitivity.smoothing.smoothing_enabled = enabled;
    }

    public void setSmoothingPitch(double pitch) {
        sensitivity.smoothing.smoothing_pitch = pitch;
    }

    public void setSmoothingYaw(double yaw) {
        sensitivity.smoothing.smoothing_yaw = yaw;
    }

    public void setSmoothingRoll(double roll) {
        sensitivity.smoothing.smoothing_roll = roll;
    }

    public void setDesktopSensitivity(Sensitivity sensitivity) {
        this.sensitivity.desktop = sensitivity;
    }

    public void setDesktopPitch(double pitch) {
        sensitivity.desktop.pitch = pitch;
    }

    public void setDesktopYaw(double yaw) {
        sensitivity.desktop.yaw = yaw;
    }

    public void setDesktopRoll(double roll) {
        sensitivity.desktop.roll = roll;
    }

    public void setControllerSensitivity(Sensitivity sensitivity) {
        this.sensitivity.controller = sensitivity;
    }

    public void setControllerPitch(double pitch) {
        sensitivity.controller.pitch = pitch;
    }

    public void setControllerYaw(double yaw) {
        sensitivity.controller.yaw = yaw;
    }

    public void setControllerRoll(double roll) {
        sensitivity.controller.roll = roll;
    }

    public void save() {
        saveConfigFile(CONFIG_FILE.toFile());
    }

    public RotationInstant configureRotation(RotationInstant rotationInstant) {
        var pitch = rotationInstant.getPitch();
        var yaw = rotationInstant.getYaw();
        var roll = rotationInstant.getRoll();

        if (!getSwitchRollAndYaw()) {
            var temp = yaw;
            yaw = roll;
            roll = temp;
        }
        if (getInvertPitch()) {
            pitch = -pitch;
        }

        return new RotationInstant(pitch, yaw, roll, rotationInstant.getRenderDelta());
    }

    public void notifyPlayerOfServerConfig(SyncedModConfig serverConfig) {
        if (!serverConfig.allowThrusting() && general.thrust.enable_thrust) {
            MinecraftClient.getInstance().getToastManager().add(SystemToast.create(
                    MinecraftClient.getInstance(),
                    SystemToast.Type.TUTORIAL_HINT,
                    Text.translatable("toast.do_a_barrel_roll"),
                    Text.translatable("toast.do_a_barrel_roll.thrusting_disabled_by_server")
            ));
        }
        if (serverConfig.forceEnabled() && !general.mod_enabled) {
            MinecraftClient.getInstance().getToastManager().add(SystemToast.create(
                    MinecraftClient.getInstance(),
                    SystemToast.Type.TUTORIAL_HINT,
                    Text.translatable("toast.do_a_barrel_roll"),
                    Text.translatable("toast.do_a_barrel_roll.mod_forced_enabled_by_server")
            ));
        }
    }

    /**
     * Loads config file.
     *
     * @param file file to load the config file from.
     * @return ConfigManager object
     */
    private static ModConfig loadConfigFile(File file) {
        ModConfig config = null;

        if (file.exists()) {
            // An existing config is present, we should use its values
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                // Parses the config file and puts the values into config object
                config = GSON.fromJson(fileReader, ModConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Problem occurred when trying to load config: ", e);
            }
        }
        // gson.fromJson() can return null if file is empty
        if (config == null) {
            config = new ModConfig();
        }

        // Saves the file in order to write new fields if they were added
        config.saveConfigFile(file);
        return config;
    }

    /**
     * Saves the config to the given file.
     *
     * @param file file to save config to
     */
    private void saveConfigFile(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
