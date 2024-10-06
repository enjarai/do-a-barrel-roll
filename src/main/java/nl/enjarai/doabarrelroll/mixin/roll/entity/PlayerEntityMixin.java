package nl.enjarai.doabarrelroll.mixin.roll.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    @Unique
    protected boolean isRolling;
    @Unique
    protected float prevRoll;
    @Unique
    protected float roll;

    @Override
    protected void doABarrelRoll$baseTickTail(CallbackInfo ci) {
        doABarrelRoll$baseTickTail2();

        prevRoll = doABarrelRoll$getRoll();

        if (!doABarrelRoll$isRolling()) {
            doABarrelRoll$setRoll(0.0f);
        }
    }

    @Unique
    protected void doABarrelRoll$baseTickTail2() {
    }

    @Override
    public boolean doABarrelRoll$isRolling() {
        return isRolling;
    }

    @Override
    public void doABarrelRoll$setRolling(boolean rolling) {
        isRolling = rolling;
    }

    @Override
    public float doABarrelRoll$getRoll() {
        return roll;
    }

    @Override
    public float doABarrelRoll$getRoll(float tickDelta) {
        if (tickDelta == 1.0f) {
            return doABarrelRoll$getRoll();
        }
        return MathHelper.lerp(tickDelta, prevRoll, doABarrelRoll$getRoll());
    }

    @Override
    public void doABarrelRoll$setRoll(float roll) {
        if (!Float.isFinite(roll)) {
            Util.error("Invalid entity rotation: " + roll + ", discarding.");
            return;
        }
        var lastRoll = doABarrelRoll$getRoll();
        this.roll = roll;

        if (roll < -90 && lastRoll > 90) {
            prevRoll -= 360;
        } else if (roll > 90 && lastRoll < -90) {
            prevRoll += 360;
        }
    }
}
