package nl.enjarai.doabarrelroll.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import nl.enjarai.doabarrelroll.compat.Compat;
import nl.enjarai.doabarrelroll.compat.yacl.YACLImplementation;

import java.net.URI;

public class ModConfigScreen {
    public static Screen create(Screen parent) {
        if (!Compat.isYACLLoaded()) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl/versions"));
                }
                MinecraftClient.getInstance().setScreen(parent);
            }, getText("missing"), getText("missing.message"), ScreenTexts.YES, ScreenTexts.NO);
        } else if (!Compat.isYACLUpToDate()) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl/versions"));
                }
                MinecraftClient.getInstance().setScreen(parent);
            }, getText("outdated"), getText("outdated.message"), ScreenTexts.YES, ScreenTexts.NO);
        } else {
            return YACLImplementation.generateConfigScreen(parent);
        }
    }

    private static Text getText(String key) {
        return Text.translatable("config.do_a_barrel_roll.yacl." + key);
    }
}
