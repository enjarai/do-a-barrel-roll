package nl.enjarai.doabarrelroll.mixin.roll.entity;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Inject(
            method = "baseTick",
            at = @At("TAIL")
    )
    protected void doABarrelRoll$baseTickTail(CallbackInfo ci) {
    }
}
