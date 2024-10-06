package nl.enjarai.doabarrelroll;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import com.bawnorton.mixinsquared.tools.MixinAnnotationReader;

import java.util.List;

public class DABRMixinCanceller implements MixinCanceller {
//    public static final String GAME_RENDERER_CLASSNAME = FabricLoader.getInstance().getMappingResolver()
//                .mapClassName("intermediary", "net.minecraft.class_757").replace('.', '/');

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.equals("com.anthonyhilyard.equipmentcompare.mixin.KeyMappingMixin") && MixinAnnotationReader.getPriority(mixinClassName) == 1000) {
            DoABarrelRoll.LOGGER.warn("Equipment Compare detected, disabling their overly invasive keybinding mixin. Report any relevant issues to them.");
            DoABarrelRoll.LOGGER.warn("If the author of Equipment Compare is reading this: see #31 on your github. Once the issue is resolved, you can set the priority of this mixin to anything other than 1000 to stop it being disabled.");
            return true;
        }
//        if (targetClassNames.contains(GAME_RENDERER_CLASSNAME) && !mixinClassName.contains("doabarrelroll")) {
//            DoABarrelRoll.LOGGER.warn("Found mixin targeting GameRenderer. If this is done to modify matrices, it might create incompatibilities. Mixin classname: {}", mixinClassName);
//        }
        return false;
    }
}
