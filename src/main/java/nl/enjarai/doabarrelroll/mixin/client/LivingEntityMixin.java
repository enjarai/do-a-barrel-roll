package nl.enjarai.doabarrelroll.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.enjarai.doabarrelroll.ModKeybindings;
import nl.enjarai.doabarrelroll.api.event.ThrustEvents;
import nl.enjarai.doabarrelroll.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// High priority to ensure compat with mods like Elytra Aeronautics.
@Mixin(value = LivingEntity.class, priority = 1200)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 6
            )
    )
    private Vec3d doABarrelRoll$wrapElytraVelocity(Vec3d original) {
        if (!((Object) this instanceof ClientPlayerEntity) || !ModConfig.INSTANCE.getEnableThrust()) return original;

        Vec3d rotation = getRotationVector();
        Vec3d velocity = getVelocity();

        double throttleSign = ModKeybindings.THRUST_FORWARD.isPressed() ? 1 : ModKeybindings.THRUST_BACKWARD.isPressed() ? -1 : 0;
        throttleSign = ThrustEvents.modifyThrustInput(throttleSign);

        if (ModConfig.INSTANCE.getThrustParticles()) {
            int particleDensity = (int) MathHelper.clamp(throttleSign * 10, 0, 10);
            if (throttleSign > 0.1 && getWorld().getTime() % (11 - particleDensity) == 0) {
                var pPos = getPos().add(velocity.multiply(0.5).negate());
                getWorld().addParticle(
                        ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                        pPos.getX(), pPos.getY(), pPos.getZ(),
                        0, 0, 0
                );
            }
        }

        double maxSpeed = ModConfig.INSTANCE.getMaxThrust();
        double speedIncrease = Math.max(maxSpeed - velocity.length(), 0) / maxSpeed * throttleSign;
        double acceleration = ModConfig.INSTANCE.getThrustAcceleration() * speedIncrease;

        return original.add(
                rotation.x * acceleration,
                rotation.y * acceleration,
                rotation.z * acceleration
        );
    }
}
