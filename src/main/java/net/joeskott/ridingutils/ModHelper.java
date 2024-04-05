package net.joeskott.ridingutils;

import net.joeskott.ridingutils.config.ModConfigModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
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

    public static boolean hasSpeedEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasStatusEffect(StatusEffects.SPEED);
        }
        return false;
    }

    public static boolean hasHasteEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasStatusEffect(StatusEffects.HASTE);
        }
        return false;
    }

    public static boolean hasLuckEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasStatusEffect(StatusEffects.LUCK);
        }
        return false;
    }

    public static boolean hasFrenziedEffect(Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasStatusEffect(StatusEffects.STRENGTH);
        }
        return false;
    }


    public static int getWhipState(Entity entity) {
        boolean lockSpeedState = ModConfigModel.disabledSpeedStates;
        // If we're only doing one state, keep it at one state at all times
        if(lockSpeedState) {
            return -1;
        }
        boolean speedEffect = hasSpeedEffect(entity);
        boolean hasteEffect = hasHasteEffect(entity);
        boolean luckEffect = hasLuckEffect(entity);

        if (speedEffect && !hasteEffect && !luckEffect) {
            return 0;
        } else if (speedEffect && hasteEffect && !luckEffect) {
            return 1;
        } else if (speedEffect && hasteEffect && luckEffect) {
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
}
