//? if fabric {
package nl.enjarai.doabarrelroll.compat.modmenu;

import nl.enjarai.cicada.api.compat.CompatMixinPlugin;

import java.util.Set;

public class ModMenuPlugin implements CompatMixinPlugin {
    @Override
    public Set<String> getRequiredMods() {
        return Set.of("modmenu");
    }
}
//?}
