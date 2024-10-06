package nl.enjarai.doabarrelroll.mixin.client.roll;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    @Shadow @Final private MinecraftClient client;

    // Not using ModifyArg**s** here to be compatible with Forge
    @ModifyArg(
            method = "getLeftText",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
                    ordinal = 7
            ),
            index = 1,
            require = 0
    )
    private String doABarrelRoll$modifyDebugHudText(String format) {
        var cameraEntity = client.getCameraEntity();
        if (cameraEntity == null) return null;

        // Carefully insert a new number format specifier into the facing string
        var firstHalf = format.substring(0, format.length() - 1);
        var secondHalf = format.substring(format.length() - 1);
        return firstHalf + " / %.1f" + secondHalf;
    }

    @ModifyArg(
            method = "getLeftText",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
                    ordinal = 7
            ),
            index = 2,
            require = 0
    )
    private Object[] doABarrelRoll$modifyDebugHudText2(Object[] args) {
        var cameraEntity = client.getCameraEntity();
        if (cameraEntity == null) return args;

        // Add the roll value to the format arguments
        var roll = ((RollEntity) client.getCameraEntity()).doABarrelRoll$getRoll();
        var newFmtArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, newFmtArgs, 0, args.length);
        newFmtArgs[args.length] = roll;
        return newFmtArgs;
    }
}
