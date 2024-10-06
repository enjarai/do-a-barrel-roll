package nl.enjarai.doabarrelroll.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.api.event.RollContext;
import nl.enjarai.doabarrelroll.api.rotation.RotationInstant;
import nl.enjarai.doabarrelroll.config.serialization.ExpressionParserTypeAdapter;
import nl.enjarai.doabarrelroll.math.ExpressionParser;
import nl.enjarai.doabarrelroll.net.ClientNetworking;
import nl.enjarai.doabarrelroll.util.ToastUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ModConfig {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ExpressionParser.class, new ExpressionParserTypeAdapter())
            .addSerializationExclusionStrategy(new MigrationValue.SerializationStrategy())
            .setPrettyPrinting()
            .create();
    public static final ModConfig DEFAULT = new ModConfig();
    public static final Path CONFIG_FILE = Path.of("config").resolve("do_a_barrel_roll-client.json");
    public static ModConfig INSTANCE = loadConfigFile(CONFIG_FILE.toFile());

    public static void touch() {
        // touch the grass
    }


    private void performMigrations() {
        if (format_version_do_not_edit <= 1) { // configs below format version 1 use old smoothing values
            var enabled = sensitivity.smoothing.smoothing_enabled;
            sensitivity.camera_smoothing.pitch = enabled ? 1 / sensitivity.smoothing.smoothing_pitch : 0;
            sensitivity.camera_smoothing.yaw = enabled ? 1 / sensitivity.smoothing.smoothing_yaw : 0;
            sensitivity.camera_smoothing.roll = enabled ? 1 / sensitivity.smoothing.smoothing_roll : 0;
        }

        format_version_do_not_edit = 2; // update format version
    }


    int format_version_do_not_edit = 2;

    General general = new General();
    static class General {
        boolean mod_enabled = true;

        Controls controls = new Controls();
        static class Controls {
            boolean switch_roll_and_yaw = false;
            boolean invert_pitch = false;
            boolean momentum_based_mouse = false;
            double momentum_mouse_deadzone = 0.2;
            boolean show_momentum_widget = true;
            ActivationBehaviour activation_behaviour = ActivationBehaviour.VANILLA;
            boolean disable_when_submerged = true;
        }

        Hud hud = new Hud();
        static class Hud {
            boolean show_horizon = false;
        }

        Banking banking = new Banking();
        static class Banking {
            boolean enable_banking = true;
            double banking_strength = 20.0;
            boolean simulate_control_surface_efficacy = false;
            boolean automatic_righting = false;
            double righting_strength = 50.0;
        }

        Thrust thrust = new Thrust();
        static class Thrust {
            boolean enable_thrust = false;
            double max_thrust = 2.0;
            double thrust_acceleration = 0.1;
            boolean thrust_particles = true;
        }

        Misc misc = new Misc();
        static class Misc {
            boolean enable_easter_eggs = true;
        }
    }

    SensitivityConfig sensitivity = new SensitivityConfig();
    static class SensitivityConfig {
        @MigrationValue
        Smoothing smoothing = new Smoothing();
        static class Smoothing {
            boolean smoothing_enabled = true;
            double smoothing_pitch = 1.0;
            double smoothing_yaw = 0.4;
            double smoothing_roll = 1.0;
        }

        Sensitivity camera_smoothing = new Sensitivity(1.0, 2.5, 1.0);
        Sensitivity desktop = new Sensitivity();
        Sensitivity controller = new Sensitivity();
    }

    AdvancedConfig advanced = new AdvancedConfig();
    static class AdvancedConfig {
        ExpressionParser banking_x_formula = new ExpressionParser("sin($roll * TO_RAD) * cos($pitch * TO_RAD) * 10 * $banking_strength");
        ExpressionParser banking_y_formula = new ExpressionParser("(-1 + cos($roll * TO_RAD)) * cos($pitch * TO_RAD) * 10 * $banking_strength");

        ExpressionParser elevator_efficacy_formula = new ExpressionParser("$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");
        ExpressionParser aileron_efficacy_formula = new ExpressionParser("$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");
        ExpressionParser rudder_efficacy_formula = new ExpressionParser("$velocity_x * $look_x + $velocity_y * $look_y + $velocity_z * $look_z");
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

    public double getMomentumMouseDeadzone() {
        return general.controls.momentum_mouse_deadzone;
    }

    public boolean getShowMomentumWidget() {
        return general.controls.show_momentum_widget;
    } //= true;

    public boolean getInvertPitch() {
        return general.controls.invert_pitch;
    } //= false;

    public ActivationBehaviour getActivationBehaviour() {
        return general.controls.activation_behaviour;
    } //= ActivationBehaviour.VANILLA;

    public boolean getDisableWhenSubmerged() {
        return general.controls.disable_when_submerged;
    } //= true;

    public boolean getShowHorizon() {
        return general.hud.show_horizon;
    }

    public boolean getEnableBanking() {
        return general.banking.enable_banking;
    }// = true;

    public double getBankingStrength() {
        return general.banking.banking_strength;
    }// = 20;

    public boolean getSimulateControlSurfaceEfficacy() {
        return general.banking.simulate_control_surface_efficacy;
    }// = false;

    public boolean getAutomaticRighting() {
        return general.banking.automatic_righting;
    }

    public double getRightingStrength() {
        return general.banking.righting_strength;
    }

    public boolean getEnableThrust() {
        if (general.thrust.enable_thrust) {
            ClientPlayerEntity player;
            if (DoABarrelRollClient.isConnectedToRealms() &&
                    (player = MinecraftClient.getInstance().player) != null && player.hasPermissionLevel(2)) {
                return true;
            }

            return ClientNetworking.HANDSHAKE_CLIENT.getConfig()
                    .map(LimitedModConfigServer::allowThrusting)
                    .orElse(false);
        }

        return false;
    }

    public boolean getEnableThrustClient() {
        return general.thrust.enable_thrust;
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

    public boolean getEnableEasterEggs() {
        return general.misc.enable_easter_eggs;
    }

    public double getSmoothingPitch() {
        return sensitivity.camera_smoothing.pitch;
    }

    public double getSmoothingYaw() {
        return sensitivity.camera_smoothing.yaw;
    }

    public double getSmoothingRoll() {
        return sensitivity.camera_smoothing.roll;
    }

    public Sensitivity getSmoothing() {
        return sensitivity.camera_smoothing;
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

    public ExpressionParser getBankingXFormula() {
        return advanced.banking_x_formula;
    }

    public ExpressionParser getBankingYFormula() {
        return advanced.banking_y_formula;
    }

    public ExpressionParser getElevatorEfficacyFormula() {
        return advanced.elevator_efficacy_formula;
    }

    public ExpressionParser getAileronEfficacyFormula() {
        return advanced.aileron_efficacy_formula;
    }

    public ExpressionParser getRudderEfficacyFormula() {
        return advanced.rudder_efficacy_formula;
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

    public void setMomentumMouseDeadzone(double deadzone) {
        general.controls.momentum_mouse_deadzone = deadzone;
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

    public void setDisableWhenSubmerged(boolean enabled) {
        general.controls.disable_when_submerged = enabled;
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

    public void setSimulateControlSurfaceEfficacy(boolean enabled) {
        general.banking.simulate_control_surface_efficacy = enabled;
    }

    public void setAutomaticRighting(boolean enabled) {
        general.banking.automatic_righting = enabled;
    }

    public void setRightingStrength(double strength) {
        general.banking.righting_strength = strength;
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

    public void setEnableEasterEggs(boolean enabled) {
        general.misc.enable_easter_eggs = enabled;
    }

    public void setSmoothingPitch(double pitch) {
        sensitivity.camera_smoothing.pitch = pitch;
    }

    public void setSmoothingYaw(double yaw) {
        sensitivity.camera_smoothing.yaw = yaw;
    }

    public void setSmoothingRoll(double roll) {
        sensitivity.camera_smoothing.roll = roll;
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

    public void setBankingXFormula(ExpressionParser formula) {
        advanced.banking_x_formula = formula;
    }

    public void setBankingYFormula(ExpressionParser formula) {
        advanced.banking_y_formula = formula;
    }

    public void setElevatorEfficacyFormula(ExpressionParser formula) {
        advanced.elevator_efficacy_formula = formula;
    }

    public void setAileronEfficacyFormula(ExpressionParser formula) {
        advanced.aileron_efficacy_formula = formula;
    }

    public void setRudderEfficacyFormula(ExpressionParser formula) {
        advanced.rudder_efficacy_formula = formula;
    }

    public void save() {
        saveConfigFile(CONFIG_FILE.toFile());
    }

    public RotationInstant configureRotation(RotationInstant rotationInstant, RollContext context) {
        var pitch = rotationInstant.pitch();
        var yaw = rotationInstant.yaw();
        var roll = rotationInstant.roll();

        if (!getSwitchRollAndYaw()) {
            var temp = yaw;
            yaw = roll;
            roll = temp;
        }
        if (getInvertPitch()) {
            pitch = -pitch;
        }

        return RotationInstant.of(pitch, yaw, roll);
    }

    public void notifyPlayerOfServerConfig(LimitedModConfigServer serverConfig) {
        if (!serverConfig.allowThrusting() && general.thrust.enable_thrust) {
            ToastUtil.toasty("thrusting_disabled_by_server");
        }
        if (serverConfig.forceEnabled() && !general.mod_enabled) {
            ToastUtil.toasty("mod_forced_enabled_by_server");
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
        config.performMigrations();
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
