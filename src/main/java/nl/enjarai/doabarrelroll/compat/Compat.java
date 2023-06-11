package nl.enjarai.doabarrelroll.compat;

import net.fabricmc.loader.api.FabricLoader;

public class Compat {
    public static boolean isYACLLoaded() {
         return FabricLoader.getInstance().isModLoaded("yet_another_config_lib");
    }
}
