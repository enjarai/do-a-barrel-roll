package nl.enjarai.doabarrelroll.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import nl.enjarai.doabarrelroll.compat.Compat;
import nl.enjarai.doabarrelroll.compat.yacl.YACLImplementation;

import java.net.URI;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if(Compat.isYACLLoaded()) {
            return YACLImplementation::generateConfigScreen;
        } else {
            return parent -> new ConfirmScreen((result) -> {
                if (result) {
                    Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl/versions"));
                }
                MinecraftClient.getInstance().setScreen(parent);
            }, getText("yacl.missing"), getText("yacl.missing.message"), ScreenTexts.YES, ScreenTexts.NO);
        }
    }

    private static Text getText(String key) {
        return Text.translatable("config.do_a_barrel_roll." + key);
    }
}
