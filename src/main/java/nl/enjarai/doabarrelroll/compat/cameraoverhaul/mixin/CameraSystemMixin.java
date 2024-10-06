package nl.enjarai.doabarrelroll.compat.cameraoverhaul.mixin;

import net.minecraft.client.MinecraftClient;
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "mirsario.cameraoverhaul.common.systems.CameraSystem")
public abstract class CameraSystemMixin {
    @Unique
    private boolean allowModifications() {
        return !(MinecraftClient.getInstance().getCameraEntity() instanceof RollEntity rollEntity && rollEntity.doABarrelRoll$isRolling());
    }

    @Dynamic
    @ModifyArg(
            method = "OnCameraUpdate(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Camera;Lmirsario/cameraoverhaul/core/structures/Transform;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmirsario/cameraoverhaul/common/systems/CameraSystem;VerticalVelocityPitchOffset(Lmirsario/cameraoverhaul/core/structures/Transform;Lmirsario/cameraoverhaul/core/structures/Transform;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;DFF)V"
            ),
            index = 5
    )
    private float doABarrelRoll$cancelVerticalVelocityPitchOffset(float original) {
        return allowModifications() ? original : 0f;
    }

    @Dynamic
    @ModifyArg(
            method = "OnCameraUpdate(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Camera;Lmirsario/cameraoverhaul/core/structures/Transform;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmirsario/cameraoverhaul/common/systems/CameraSystem;ForwardVelocityPitchOffset(Lmirsario/cameraoverhaul/core/structures/Transform;Lmirsario/cameraoverhaul/core/structures/Transform;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;DFF)V"
            ),
            index = 5
    )
    private float doABarrelRoll$cancelForwardVelocityPitchOffset(float original) {
        return allowModifications() ? original : 0f;
    }

    @Dynamic
    @ModifyArg(
            method = "OnCameraUpdate(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Camera;Lmirsario/cameraoverhaul/core/structures/Transform;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmirsario/cameraoverhaul/common/systems/CameraSystem;YawDeltaRollOffset(Lmirsario/cameraoverhaul/core/structures/Transform;Lmirsario/cameraoverhaul/core/structures/Transform;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;DFFF)V"
            ),
            index = 5
    )
    private float doABarrelRoll$cancelYawDeltaRollOffset(float original) {
        return allowModifications() ? original : 0f;
    }

    @Dynamic
    @ModifyArg(
            method = "OnCameraUpdate(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/Camera;Lmirsario/cameraoverhaul/core/structures/Transform;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmirsario/cameraoverhaul/common/systems/CameraSystem;StrafingRollOffset(Lmirsario/cameraoverhaul/core/structures/Transform;Lmirsario/cameraoverhaul/core/structures/Transform;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;DFF)V"
            ),
            index = 5
    )
    private float doABarrelRoll$cancelStrafingRollOffset(float original) {
        return allowModifications() ? original : 0f;
    }
}
