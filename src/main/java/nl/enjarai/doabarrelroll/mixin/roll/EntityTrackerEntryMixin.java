package nl.enjarai.doabarrelroll.mixin.roll;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.net.ServerNetworking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {
    @Shadow @Final private Entity entity;

    @Unique
    private boolean lastIsRolling;
    @Unique
    private float lastRoll;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void doABarrelRoll$syncRollS2C(CallbackInfo ci) {
        var rollEntity = (RollEntity) entity;
        var isRolling = rollEntity.doABarrelRoll$isRolling();
        var roll = rollEntity.doABarrelRoll$getRoll();

        if (isRolling != lastIsRolling || roll != lastRoll) {
            ServerNetworking.sendRollUpdates(entity);

            lastIsRolling = isRolling;
            lastRoll = roll;
        }
    }
}
