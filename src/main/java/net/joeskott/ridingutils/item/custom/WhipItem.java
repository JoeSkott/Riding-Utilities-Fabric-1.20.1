package net.joeskott.ridingutils.item.custom;

import net.joeskott.ridingutils.ModHelper;
import net.joeskott.ridingutils.RidingUtilities;
import net.joeskott.ridingutils.config.ModConfigModel;
import net.joeskott.ridingutils.effect.ModEffects;
import net.joeskott.ridingutils.item.ModItems;
import net.joeskott.ridingutils.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WhipItem extends Item {
    public WhipItem(Settings settings) {
        super(settings);
    }

    boolean ejectPlayer = false;
    int damageOnUse = 1;
    int cooldownTicks = 20;
    int waterCooldownTicks = 100;
    int frenziedCooldownTicks = 80;
    int damageCheck = 32;
    int durationOfEffect = 120;
    int durationOfCompoundEffect = 75;
    boolean doBuckPlayer = true;
    boolean showDamage = false;
    boolean displayState = true;
    int fastAmplifier = 2;
    int ultraFastAmplifier = 3;
    int frenzyAmplifier = 7;

    double motionBoost = 0.4d;
    double waterMotionBoost = 0.4d;


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        // Cancel if we're client side, or if player doesn't have a vehicle
        if(world.isClient() || !player.hasVehicle()) {
            return super.use(world, player, hand);
        }

        // Cancel if it's a physical vehicle
        if(ModHelper.isPhysicalVehicle(player.getVehicle())) {
            return super.use(world, player, hand);
        }

        updateValuesFromConfig();

        Entity mount = player.getVehicle();

        ItemStack itemSelf = player.getStackInHand(hand);
        ItemStack itemOffhand = player.getOffHandStack();

        boolean offhandIsLasso = itemOffhand.isOf(ModItems.LASSO);

        // Cancel if is player
        if(mount instanceof PlayerEntity) {
            return super.use(world, player, hand);
        }


        int maxDamage = itemSelf.getMaxDamage();
        int currentDamage = itemSelf.getDamage();
        int chanceRange = (maxDamage - currentDamage + 1)/2;

        boolean onGround = mount.isOnGround();
        boolean inWater = mount.isTouchingWater();

        // Add motion
        if(onGround) {
            addMotion(mount);
        } else if (inWater) {
            addWaterMotion(mount);
        }


        // Activate
        if(onGround || inWater) {
            if(ModHelper.getWhipState(mount) > 0) {
                activateWhipFrenzySound(mount);
            } else {
                activateWhipSound(mount);
            }

            // Do cooldowns
            if(onGround) {
                player.getItemCooldownManager().set(this, cooldownTicks);
            } else {
                player.getItemCooldownManager().set(this, waterCooldownTicks);
            }

            // Handle damage
            ModHelper.addItemDamage(player, itemSelf, damageOnUse);
            addVariableEffect(mount, player, durationOfEffect);
            rollForHPDamage(player, mount, chanceRange, currentDamage, maxDamage);

        }

        // Do buck
        if(ejectPlayer && doBuckPlayer) {
            buckPlayer(player, mount);
            ejectPlayer = false;
        } else if (ejectPlayer) {
            ejectPlayer = false;
        }

        return super.use(world, player, hand);
    }

    private void updateValuesFromConfig() {
        cooldownTicks = RidingUtilities.CONFIG.whipCooldownTicks();
        frenziedCooldownTicks = RidingUtilities.CONFIG.frenziedCooldownTicks();
        waterCooldownTicks = RidingUtilities.CONFIG.whipWaterCooldownTicks();
        damageCheck = RidingUtilities.CONFIG.whipDangerStart();
        durationOfEffect = RidingUtilities.CONFIG.whipEffectDuration();
        durationOfCompoundEffect = RidingUtilities.CONFIG.whipCompoundEffectDuration();
        doBuckPlayer = RidingUtilities.CONFIG.whipBuck();
        fastAmplifier = RidingUtilities.CONFIG.whipFastSpeedAmplifier();
        ultraFastAmplifier = RidingUtilities.CONFIG.whipUltraFastSpeedAmplifier();
        frenzyAmplifier = RidingUtilities.CONFIG.whipFrenzySpeedAmplifier();
        displayState = RidingUtilities.CONFIG.displayState();
    }

    private void buckPlayer(PlayerEntity player, Entity mount) {
        mount.removeAllPassengers();
        if(player.hasVehicle()) {
            return;
        }
        player.stopFallFlying();
    }

    private void addVariableEffect(Entity mount, PlayerEntity player, int duration) {
        int state = ModHelper.getWhipState(mount);
        removeEffects(mount);

        switch (state) {
            case -1:
                addWhipSpeed(mount, fastAmplifier, duration);
                if(displayState) {
                    ModHelper.displayActionBarMessage(player, "Fast", Style.EMPTY.withColor(Formatting.GREEN));
                }
                break;
            case 0:
                addWhipSpeed(mount, ultraFastAmplifier, duration);
                addCompoundSpeed(mount, 1, durationOfCompoundEffect);
                doBuckChance(mount,  player, 80, 40, false);
                if(displayState) {
                    ModHelper.displayActionBarMessage(player, "Ultra Fast", Style.EMPTY.withColor(Formatting.YELLOW));
                }

                break;
            case 1:
                addWhipSpeed(mount, frenzyAmplifier, duration);
                addCompoundSpeed(mount, 1, durationOfCompoundEffect);
                doBuckChance(mount,  player, 10, 5, false);
                if(displayState) {
                    ModHelper.displayActionBarMessage(player, "Frenzy", Style.EMPTY.withColor(Formatting.DARK_RED));
                }

                break;
            case 2:
                addWhipSpeed(mount, frenzyAmplifier, duration);
                addCompoundSpeed(mount, 1, duration);
                // Add particle effects only if it's not a horse
                if(!(mount instanceof HorseEntity)) {
                    ModHelper.addHorseEjectEffect(mount, 1, duration);
                }
                doBuckChance(mount, player, 3, 4, true);
                if(displayState) {
                    ModHelper.displayActionBarMessage(player, "Frenzy", Style.EMPTY.withColor(Formatting.RED));
                }
                break;
        }

    }

    private void doBuckChance(Entity mount, PlayerEntity player, int bound, int fauxBound, boolean fauxDamage) {
        int randInt = ModHelper.random.nextInt(bound);
        int randInt2 = 0;
        if (randInt == 0) {
            buckPlayer(player, mount);
            if(mount instanceof HorseEntity) {
                ((HorseEntity) mount).setAngry(true);
            }
            addCompoundSpeed(mount, 1, frenziedCooldownTicks);
            ModHelper.addHorseEjectEffect(mount, 1, frenziedCooldownTicks);
        } else if(fauxDamage) {
            randInt2 = ModHelper.random.nextInt(fauxBound);
            if(randInt2 == 0) {

                doHurt(mount, player, 0.0f);
            } else if(randInt2 == 1 && mount instanceof HorseEntity) {
                ((HorseEntity) mount).setAngry(true);
            }
        }
    }

    private void doHurt(Entity mount, PlayerEntity player, float hurtAmount) {
        if(!mount.isOnGround()) {
            return;
        }

        if(mount instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) mount);
            boolean isHorse = mount instanceof HorseEntity;

            // Activate
            livingEntity.damage(player.getDamageSources().generic(), hurtAmount);

            //Sounds
            if(isHorse) {
                int bound = 3;
                if(!showDamage) {
                    bound = 2;
                }
                int choose = ModHelper.random.nextInt(bound);
                float pitch = getVariablePitch(0.3f);

                switch (choose) {
                    case 0 -> mount.playSound(SoundEvents.ENTITY_HORSE_ANGRY, 1.0f, pitch);
                    case 1 -> mount.playSound(SoundEvents.ENTITY_HORSE_BREATHE, 1.0f, pitch);
                    case 2 -> mount.playSound(SoundEvents.ENTITY_HORSE_HURT, 1.0f, pitch);
                }
            }

        }

    }

    private float getVariablePitch(float maxVariance) {
        float pitchAdjust = ModHelper.random.nextFloat(maxVariance) - ModHelper.random.nextFloat(maxVariance);
        return 1.2f + pitchAdjust;
    }


    private void addWhipSpeed(Entity entity, int amplifier, int duration) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            StatusEffectInstance whipSpeedEffect = new StatusEffectInstance(
                    ModEffects.WHIP_SPEED,
                    duration,
                    amplifier,
                    false,
                    false,
                    false);
            livingEntity.addStatusEffect(whipSpeedEffect);
        }
    }

    private void addCompoundSpeed(Entity entity, int amplifier, int duration) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            StatusEffectInstance compoundSpeedEffect = new StatusEffectInstance(
                    ModEffects.COMPOUND_SPEED,
                    duration,
                    amplifier,
                    false,
                    false,
                    false);
            livingEntity.addStatusEffect(compoundSpeedEffect);
        }
    }

    private void rollForHPDamage(PlayerEntity player, Entity mount, int chanceRange, int currentDamage, int maxDamage) {
        int roll = ModHelper.random.nextInt(chanceRange);
        if(currentDamage >= damageCheck && roll == 0) {
            doRealDamageAndSideEffects(player, mount);
        }
    }

    private void doRealDamageAndSideEffects(PlayerEntity pPlayer, Entity entity) {
        ejectPlayer = ModHelper.random.nextBoolean();
        float hurtAmount = ModHelper.random.nextFloat(2.0f);
        doHurt(entity, pPlayer, hurtAmount);
    }

    private void activateWhipSound(Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ModSounds.WHIP_CRACKED, SoundCategory.BLOCKS, 1f, getVariablePitch(0.4f) - 0.4f);
    }

    private void activateWhipFrenzySound(Entity entity) {
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ModSounds.WHIP_FRENZY, SoundCategory.BLOCKS, 1f, 1f);
    }

    private void addWaterMotion(Entity entity) {
        Vec3d lookAngle = ModHelper.getLookAngle(entity);
        Vec3d newMotion = new Vec3d(lookAngle.x, waterMotionBoost, lookAngle.z);
        moveEntity(entity, newMotion);
    }

    private void addMotion(Entity entity) {
        Vec3d lookAngle = ModHelper.getLookAngle(entity);
        Vec3d lastMotion =  entity.getVelocity();
        Vec3d newMotion = new Vec3d(
                lastMotion.x + lookAngle.x,
                lastMotion.y + lookAngle.y + motionBoost,
                lastMotion.z + lookAngle.z);

        moveEntity(entity, newMotion);
    }

    private void removeEffects(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            livingEntity.clearStatusEffects();
        }
    }

    private void moveEntity(Entity entity, Vec3d motion) {
        entity.setVelocity(motion);
    }
}
