package net.joeskott.ridingutils;

import net.joeskott.ridingutils.config.ModConfigModel;
import net.joeskott.ridingutils.config.RidingUtilitiesConfig;
import net.joeskott.ridingutils.effect.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class ModHelper {
    public static Random random = new Random();
    public static final int erraticChance = 20;

    public static boolean isPhysicalVehicle(Entity entity) {
        if(entity instanceof BoatEntity || entity instanceof MinecartEntity) {
            return true;
        }
        return false;
    }

    public static void addItemDamage(PlayerEntity player, ItemStack item, int damageOnUse) {
        item.damage(
                damageOnUse,
                player,
                playerEntity -> playerEntity.sendToolBreakStatus(playerEntity.getActiveHand())
        );
    }

    public static Vec3d getLookAngle(Entity entity) {
        return entity.getRotationVector(); //forge is getLookAngle
    }


    public static int hasWhipSpeedEffectLevel(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if(!livingEntity.hasStatusEffect(ModEffects.WHIP_SPEED)) {
                return -1;
            }
            return livingEntity.getStatusEffect(ModEffects.WHIP_SPEED).getAmplifier();
        }
        return -1;
    }

    public static boolean hasCompoundSpeedEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasStatusEffect(ModEffects.COMPOUND_SPEED);
        }
        return false;
    }

    public static boolean hasHorseEjectEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasStatusEffect(ModEffects.HORSE_EJECT);
        }
        return false;
    }

    public static int getWhipState(Entity entity) {
        //boolean lockSpeedState = RidingUtilities.CONFIG.disabledSpeedStates;
        boolean lockSpeedState = RidingUtilities.CONFIG.disabledSpeedStates();
        // If we're only doing one state, keep it at one state at all times
        if(lockSpeedState) {
            return -1;
        }

        int effectLevel = hasWhipSpeedEffectLevel(entity);

        boolean levelNone = effectLevel <= -1;
        boolean level0 = effectLevel <= RidingUtilities.CONFIG.whipFastSpeedAmplifier();
        boolean level1 = effectLevel <= RidingUtilities.CONFIG.whipUltraFastSpeedAmplifier();
        boolean level2 = effectLevel > RidingUtilities.CONFIG.whipUltraFastSpeedAmplifier();

        boolean compoundedSpeed = hasCompoundSpeedEffect(entity);


        if (levelNone) {
            return -1;
        }

        if (level0 && compoundedSpeed) {
            return 0;
        }

        if (level1) {
            if(!compoundedSpeed) {
                return 0;
            }
            return 1;
        }

        if (level2) {
            if(!compoundedSpeed) {
                return 1;
            }
            return 2;
        }

        return -1;
    }

    public static void displayCantRideActionBarMessage(Entity mount, PlayerEntity player, Style style) {
        EntityType<?> entityType = mount.getType();
        String translationKey = entityType.getTranslationKey();
        Text translatedText = Text.translatable(translationKey);
        String name = translatedText.getString();
        String text = "Cannot Ride " + name + ". " + name + " is Riled Up!";
        player.sendMessage(Text.literal(text).setStyle(style), true);
    }

    public static void displayActionBarMessage(PlayerEntity player, String text, Style style) {
        player.sendMessage(Text.literal(text).setStyle(style), true);
    }

    public static void applyErraticFrenzy(Entity entity) {
        int roll = random.nextInt(erraticChance);
        if(roll != 0) {
            return;
        }

        double movementMultiplier = 0.15D;

        Vec3d currentVelocity = entity.getVelocity();
        Vec3d lookAngle = getLookAngle(entity);
        double addX = (lookAngle.x + random.nextDouble(4.0D) - 2.0D) * movementMultiplier;
        double addY = (lookAngle.y + random.nextDouble(1.0D) - 0.5D) * movementMultiplier;
        double addZ = (lookAngle.z + random.nextDouble(4.0D) - 2.0D) * movementMultiplier;

        // on ground check for y so behavior isn't crazy in the air
        if(!entity.isOnGround()) {
            addY = 0.0D;
        }

        Vec3d newVelocity = new Vec3d(currentVelocity.x + addX, currentVelocity.y + addY, currentVelocity.z + addZ);
        entity.setVelocity(newVelocity);
    }

    public static void addHorseEjectEffect(Entity entity, int amplifier, int duration) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            StatusEffectInstance horseEjectEffect = new StatusEffectInstance(
                    ModEffects.HORSE_EJECT,
                    duration,
                    amplifier,
                    false,
                    RidingUtilities.CONFIG.enableRiledUpParticles(),
                    false);
            livingEntity.addStatusEffect(horseEjectEffect);
        }
    }
}
