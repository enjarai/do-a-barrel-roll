package nl.enjarai.doabarrelroll.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

public class Compat {
    public static final String YACL_MIN_VERSION = "3.1.0";

    public static boolean isYACLLoaded() {
        return checkModLoaded("yet_another_config_lib_v3");
    }

    public static boolean isYACLUpToDate() {
        return isModVersionAtLeast("yet_another_config_lib_v3", YACL_MIN_VERSION);
    }

    public static boolean checkModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean isModVersionAtLeast(String modId, String version) {
        try {
            var parsed = Version.parse(version);
            return FabricLoader.getInstance().getModContainer("yet_another_config_lib_v3")
                    .filter(modContainer -> modContainer.getMetadata().getVersion().compareTo(parsed) >= 0)
                    .isPresent();
        } catch (VersionParsingException e) {
            throw new RuntimeException("Skill issue, bad version");
        }
    }
}
