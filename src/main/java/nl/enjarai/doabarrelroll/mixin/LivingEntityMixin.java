package nl.enjarai.doabarrelroll.mixin;

import net.minecraft.entity.LivingEntity;
import nl.enjarai.doabarrelroll.net.ServerNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyVariable(
            method = "travel",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/util/math/Vec3d;horizontalLength()D",
                            ordinal = 1
                    )
            ),
            at = @At("STORE"),
            index = 21,
            require = 0 // We let this mixin fail if it needs to as a temporary workaround to be compatible with Connector.
    )
    private float doABarrelRoll$modifyKineticDamage(float original) {
        var damageType = ServerNetworking.CONFIG_HOLDER.instance.kineticDamage();

        return switch (damageType) {
            case VANILLA -> original;
            case HIGH_SPEED -> original - 2.0f;
            case NONE -> 0.0f;
            case INSTANT_KILL -> Float.MAX_VALUE;
        };
    }
}
