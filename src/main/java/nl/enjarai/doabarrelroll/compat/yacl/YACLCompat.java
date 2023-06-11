package nl.enjarai.doabarrelroll.compat.yacl;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nl.enjarai.doabarrelroll.config.ActivationBehaviour;
import nl.enjarai.doabarrelroll.config.ModConfig;

public class YACLCompat {
    public static Screen generateConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(getText("title"))
                .category(ConfigCategory.createBuilder()
                        .name(getText("ModConfig.INSTANCE.general"))
                        .option(getBooleanOption("ModConfig.INSTANCE.general", "mod_enabled", false)
                                .binding(true, () -> ModConfig.INSTANCE.getModEnabled(), value -> ModConfig.INSTANCE.setModEnabled(value))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("controls"))
                                .option(getBooleanOption("controls", "switch_roll_and_yaw", true)
                                        .binding(false, () -> ModConfig.INSTANCE.getSwitchRollAndYaw(), value -> ModConfig.INSTANCE.setSwitchRollAndYaw(value))
                                        .build())
                                .option(getBooleanOption("controls", "invert_pitch", false)
                                        .binding(false, () -> ModConfig.INSTANCE.getInvertPitch(), value -> ModConfig.INSTANCE.setInvertPitch(value))
                                        .build())
                                .option(getBooleanOption("controls", "momentum_based_mouse", true)
                                        .binding(false, () -> ModConfig.INSTANCE.getMomentumBasedMouse(), value -> ModConfig.INSTANCE.setMomentumBasedMouse(value))
                                        .build())
                                .option(getOption(ActivationBehaviour.class, "controls", "activation_behaviour", true)
                                        .controller(EnumController::new)
                                        .binding(ActivationBehaviour.VANILLA, () -> ModConfig.INSTANCE.getActivationBehaviour(), value -> ModConfig.INSTANCE.setActivationBehaviour(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("hud"))
                                .option(getBooleanOption("hud", "show_horizon", true)
                                        .binding(false, () -> ModConfig.INSTANCE.getShowHorizon(), value -> ModConfig.INSTANCE.setShowHorizon(value))
                                        .build())
                                .option(getBooleanOption("controls", "show_momentum_widget", true)
                                        .binding(true, () -> ModConfig.INSTANCE.getShowMomentumWidget(), value -> ModConfig.INSTANCE.setShowMomentumWidget(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("banking"))
                                .option(getBooleanOption("banking", "enable_banking", true)
                                        .binding(true, () -> ModConfig.INSTANCE.getEnableBanking(), value -> ModConfig.INSTANCE.setEnableBanking(value))
                                        .build())
                                .option(getOption(Double.class, "banking", "banking_strength", false)
                                        .controller(option -> new DoubleSliderController(option, 0.0, 100.0, 1.0))
                                        .binding(20.0, () -> ModConfig.INSTANCE.getBankingStrength(), value -> ModConfig.INSTANCE.setBankingStrength(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("thrust"))
                                .collapsed(true)
                                .option(getBooleanOption("thrust", "enable_thrust", true)
                                        .binding(false, () -> ModConfig.INSTANCE.getEnableThrust(), value -> ModConfig.INSTANCE.setEnableThrust(value))
                                        .build())
                                .option(getOption(Double.class, "thrust", "max_thrust", true)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 10.0, 0.1))
                                        .binding(2.0, () -> ModConfig.INSTANCE.getMaxThrust(), value -> ModConfig.INSTANCE.setMaxThrust(value))
                                        .build())
                                .option(getOption(Double.class, "thrust", "thrust_acceleration", true)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 1.0, 0.1))
                                        .binding(0.1, () -> ModConfig.INSTANCE.getThrustAcceleration(), value -> ModConfig.INSTANCE.setThrustAcceleration(value))
                                        .build())
                                .option(getBooleanOption("thrust", "thrust_particles", false)
                                        .binding(true, () -> ModConfig.INSTANCE.getThrustParticles(), value -> ModConfig.INSTANCE.setThrustParticles(value))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(getText("sensitivity"))
                        .group(OptionGroup.createBuilder()
                                .name(getText("smoothing"))
                                .option(getBooleanOption("smoothing", "smoothing_enabled", false)
                                        .binding(true, () -> ModConfig.INSTANCE.getSmoothingEnabled(), value -> ModConfig.INSTANCE.setSmoothingEnabled(value))
                                        .build())
                                .option(getOption(Double.class, "smoothing", "smoothing_pitch", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 5.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getSmoothingPitch(), value -> ModConfig.INSTANCE.setSmoothingPitch(value))
                                        .build())
                                .option(getOption(Double.class, "smoothing", "smoothing_yaw", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 5.0, 0.1))
                                        .binding(0.4, () -> ModConfig.INSTANCE.getSmoothingYaw(), value -> ModConfig.INSTANCE.setSmoothingYaw(value))
                                        .build())
                                .option(getOption(Double.class, "smoothing", "smoothing_roll", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 5.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getSmoothingRoll(), value -> ModConfig.INSTANCE.setSmoothingRoll(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("desktop"))
                                .option(getOption(Double.class, "desktop", "pitch", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 10.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getDesktopPitch(), value -> ModConfig.INSTANCE.setDesktopPitch(value))
                                        .build())
                                .option(getOption(Double.class, "desktop", "yaw", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 10.0, 0.1))
                                        .binding(0.4, () -> ModConfig.INSTANCE.getDesktopYaw(), value -> ModConfig.INSTANCE.setDesktopYaw(value))
                                        .build())
                                .option(getOption(Double.class, "desktop", "roll", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 10.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getDesktopRoll(), value -> ModConfig.INSTANCE.setDesktopRoll(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("controller"))
                                .collapsed(!(FabricLoader.getInstance().isModLoaded("controlify") || FabricLoader.getInstance().isModLoaded("midnightcontrols")))
                                .tooltip(getText("controller.description"))
                                .option(getOption(Double.class, "controller", "pitch", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 10.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getControllerPitch(), value -> ModConfig.INSTANCE.setControllerPitch(value))
                                        .build())
                                .option(getOption(Double.class, "controller", "yaw", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 10.0, 0.1))
                                        .binding(0.4, () -> ModConfig.INSTANCE.getControllerYaw(), value -> ModConfig.INSTANCE.setControllerYaw(value))
                                        .build())
                                .option(getOption(Double.class, "controller", "roll", false)
                                        .controller(option -> new DoubleSliderController(option, 0.1, 10.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getControllerRoll(), value -> ModConfig.INSTANCE.setControllerRoll(value))
                                        .build())
                                .build())
                        .build())
                .save(ModConfig.INSTANCE::save)
                .build()
                .generateScreen(parent);
    }

    private static <T> Option.Builder<T> getOption(Class<T> clazz, String category, String key, boolean description) {
        Option.Builder<T> builder = Option.createBuilder(clazz)
                .name(getText(category, key));
        if (description) {
            builder.tooltip(getText(category, key + ".description"));
        }
        return builder;
    }
    
    private static Option.Builder<Boolean> getBooleanOption(String category, String key, boolean description) {
        return getOption(Boolean.class, category, key, description)
                .controller(TickBoxController::new);
    }

    private static Text getText(String category, String key) {
        return Text.translatable("config.do_a_barrel_roll." + category + "." + key);
    }

    private static Text getText(String key) {
        return Text.translatable("config.do_a_barrel_roll." + key);
    }
}
