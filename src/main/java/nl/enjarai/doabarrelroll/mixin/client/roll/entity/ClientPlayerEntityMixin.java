package nl.enjarai.doabarrelroll.mixin.client.roll.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import nl.enjarai.doabarrelroll.api.event.RollContext;
import nl.enjarai.doabarrelroll.api.event.RollEvents;
import nl.enjarai.doabarrelroll.api.rotation.RotationInstant;
import nl.enjarai.doabarrelroll.config.Sensitivity;
import nl.enjarai.doabarrelroll.flight.RotationModifiers;
import nl.enjarai.doabarrelroll.math.MagicNumbers;
import nl.enjarai.doabarrelroll.mixin.roll.entity.PlayerEntityMixin;
import nl.enjarai.doabarrelroll.net.ClientNetworking;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {
	@Shadow public float renderYaw;
	@Shadow public float lastRenderYaw;

	@Unique
	private boolean lastSentIsRolling;
	@Unique
	private float lastSentRoll;

	@Inject(
			method = "sendMovementPackets",
			at = @At("TAIL")
	)
	private void doABarrelRoll$sendRollPacket(CallbackInfo ci) {
		var isRolling = doABarrelRoll$isRolling();
		var rollDiff = doABarrelRoll$getRoll() - lastSentRoll;
		if (isRolling != lastSentIsRolling || rollDiff != 0.0f) {
			ClientNetworking.sendRollUpdate(this);

			lastSentIsRolling = isRolling;
			lastSentRoll = doABarrelRoll$getRoll();
		}
	}

	@Override
	@Unique
	protected void doABarrelRoll$baseTickTail2() {
		// Update rolling status
		doABarrelRoll$setRolling(RollEvents.shouldRoll());
	}

	@Override
	public void doABarrelRoll$changeElytraLook(double pitch, double yaw, double roll, Sensitivity sensitivity, double mouseDelta) {
		var rotDelta = RotationInstant.of(pitch, yaw, roll);
		var currentRoll = doABarrelRoll$getRoll();
		var currentRotation = RotationInstant.of(
				getPitch(),
				getYaw(),
				currentRoll
		);
		var context = RollContext.of(currentRotation, rotDelta, mouseDelta);

		context.useModifier(RotationModifiers.fixNaN("INPUT"));
		RollEvents.earlyCameraModifiers(context);
		context.useModifier(RotationModifiers.fixNaN("EARLY_CAMERA_MODIFIERS"));
		context.useModifier((rotation, ctx) -> rotation.applySensitivity(sensitivity));
		context.useModifier(RotationModifiers.fixNaN("SENSITIVITY"));
		RollEvents.lateCameraModifiers(context);
		context.useModifier(RotationModifiers.fixNaN("LATE_CAMERA_MODIFIERS"));

		rotDelta = context.getRotationDelta();

		doABarrelRoll$changeElytraLook((float) rotDelta.pitch(), (float) rotDelta.yaw(), (float) rotDelta.roll());
	}

	@Override
	public void doABarrelRoll$changeElytraLook(float pitch, float yaw, float roll) {
		var currentPitch = getPitch();
		var currentYaw = getYaw();
		var currentRoll = doABarrelRoll$getRoll();

		// Convert pitch, yaw, and roll to a facing and left vector
		var facing = new Vector3d(getRotationVecClient().toVector3f());
		var left = new Vector3d(1, 0, 0);
		left.rotateZ(-currentRoll * MagicNumbers.TORAD);
		left.rotateX(-currentPitch * MagicNumbers.TORAD);
		left.rotateY(-(currentYaw + 180) * MagicNumbers.TORAD);


		// Apply pitch
		facing.rotateAxis(-0.15 * pitch * MagicNumbers.TORAD, left.x, left.y, left.z);

		// Apply yaw
		var up = facing.cross(left, new Vector3d());
		facing.rotateAxis(0.15 * yaw * MagicNumbers.TORAD, up.x, up.y, up.z);
		left.rotateAxis(0.15 * yaw * MagicNumbers.TORAD, up.x, up.y, up.z);

		// Apply roll
		left.rotateAxis(0.15 * roll * MagicNumbers.TORAD, facing.x, facing.y, facing.z);


		// Extract new pitch, yaw, and roll
		double newPitch = -Math.asin(facing.y) * MagicNumbers.TODEG;
		double newYaw = -Math.atan2(facing.x, facing.z) * MagicNumbers.TODEG;

		var normalLeft = new Vector3d(1, 0, 0).rotateY(-(newYaw + 180) * MagicNumbers.TORAD);
		double newRoll = -Math.atan2(left.cross(normalLeft, new Vector3d()).dot(facing), left.dot(normalLeft)) * MagicNumbers.TODEG;

		// Calculate deltas
		double deltaY = newPitch - currentPitch;
		double deltaX = newYaw - currentYaw;
		double deltaRoll = newRoll - currentRoll;

		// Apply vanilla pitch and yaw
		changeLookDirection(deltaX / 0.15, deltaY / 0.15);

		// Apply roll
		this.roll += (float) deltaRoll;
		this.prevRoll += (float) deltaRoll;

		// fix hand spasm when wrapping yaw value
		if (getYaw() < -90 && renderYaw > 90) {
			renderYaw -= 360;
			lastRenderYaw -= 360;
		} else if (getYaw() > 90 && renderYaw < -90) {
			renderYaw += 360;
			lastRenderYaw += 360;
		}
	}
}