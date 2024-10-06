package nl.enjarai.doabarrelroll;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

//? if fabric {
public class DoABarrelRollInitializer implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DoABarrelRollClient.init();
    }

    @Override
    public void onInitialize() {
        DoABarrelRoll.init();
    }
}
//?} else {
/*import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import nl.enjarai.doabarrelroll.config.ModConfigScreen;

@Mod(DoABarrelRoll.MODID)
public class DoABarrelRollInitializer {
    public DoABarrelRollInitializer() {
        DoABarrelRoll.init();

        if (FMLLoader.getDist().isClient()) {
            DoABarrelRollClient.init();
        }

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (mod, parent) -> ModConfigScreen.create(parent)
        );
    }
}
*///?}