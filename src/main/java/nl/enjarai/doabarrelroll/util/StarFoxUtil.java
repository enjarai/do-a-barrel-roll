package nl.enjarai.doabarrelroll.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.api.event.RollEvents;
import nl.enjarai.doabarrelroll.api.event.StarFox64Events;
import nl.enjarai.doabarrelroll.api.rotation.RotationInstant;
import nl.enjarai.doabarrelroll.config.ModConfig;

public class StarFoxUtil {
    private static final Random random = Random.create();
    private static final Identifier barrelRollSoundId = DoABarrelRoll.id("do_a_barrel_roll");
    private static final SoundEvent barrelRollSound = SoundEvent.of(barrelRollSoundId);
    private static final Identifier barrelRollTexture1 = DoABarrelRoll.id("textures/gui/barrel_roll_1.png");
    private static final Identifier barrelRollTexture2 = DoABarrelRoll.id("textures/gui/barrel_roll_2.png");
    private static final double rollTol = 90.0;

    // Tracks the roll direction. 0 = no roll, 1 = right, -1 = left
    private static double rollTracker = 0;
    private static int barrelRollTimer = 0;

    public static void register() {
//        Registry.register(Registries.SOUND_EVENT, barrelRollSoundId, barrelRollSound);

        StarFox64Events.DOES_A_BARREL_ROLL.register(StarFoxUtil::playBarrelRollSound);
        StarFox64Events.DOES_A_BARREL_ROLL.register(player -> barrelRollTimer = 30);

        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> {
            trackRoll(context.getRotationDelta(), context.getCurrentRotation());
        }, 999999);
    }

    public static void clientTick(MinecraftClient client) {
        if (barrelRollTimer > 0) {
            barrelRollTimer--;
        }
    }

    public static void renderPeppy(DrawContext context, float tickDelta, int scaledWidth, int scaledHeight) {
        if (barrelRollTimer > 0) {
            int x = scaledWidth / 2 - 75;
            int y = scaledHeight - 90;
            int texture = barrelRollTimer % 2 == 0 ? 1 : 2;
            context.drawTexture(texture == 1 ? barrelRollTexture1 : barrelRollTexture2,
                    x, y, 0, 0, 160, 160, 160, 160);
        }
    }

    private static void trackRoll(RotationInstant rotationDelta, RotationInstant currentRotation) {
        var player = MinecraftClient.getInstance().player;
        if (player != null && isFoxMcCloud(player)) {
            double cRoll = currentRotation.roll();
            double dRoll = rotationDelta.roll();

            // If the player crosses the threshold in any direction, set the roll tracker to that direction
            if (cRoll < rollTol && cRoll + dRoll >= rollTol) {
                rollTracker = 1;
            } else if (cRoll > -rollTol && cRoll + dRoll <= -rollTol) {
                rollTracker = -1;
            } else if (rollTracker != 0) {
                // Reset roll tracker if the player starts turning the other way
                if (rollTracker * dRoll < 0) {
                    rollTracker = 0;
                }

                // If the player has the roll tracker set, and crosses the opposite
                // threshold in the direction of the roll tracker, do a barrel roll
                if ((rollTracker > 0 && cRoll <= -rollTol) || (rollTracker < 0 && cRoll >= rollTol)) {
                    StarFox64Events.doesABarrelRoll(player);
                    rollTracker = 0;
                }
            }
        } else {
            rollTracker = 0;
        }
    }

    public static boolean isFoxMcCloud(PlayerEntity player) {
        if (!ModConfig.INSTANCE.getEnableEasterEggs()) return false;

        ItemStack chestStack = player.getEquippedStack(EquipmentSlot.CHEST);
        String name = chestStack.getName().getString();
        return chestStack.isOf(Items.ELYTRA) && (name.equals("Arwing") || name.equals("Star Fox 64") || name.contains("Do a Barrel Roll"));
    }

    public static void playBarrelRollSound(PlayerEntity player) {
        player.getWorld().playSoundFromEntity(
                player, player, barrelRollSound, SoundCategory.PLAYERS,
                1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F
        );
    }
}
