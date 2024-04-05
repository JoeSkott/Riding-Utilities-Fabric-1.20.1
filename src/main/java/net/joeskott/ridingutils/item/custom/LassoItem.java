package net.joeskott.ridingutils.item.custom;

import net.joeskott.ridingutils.ModHelper;
import net.joeskott.ridingutils.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static java.lang.Math.round;

public class LassoItem extends Item {
    public LassoItem(Settings settings) {
        super(settings);
    }

    int damageChance = 10;
    int damageOnUse = 1;
    double jumpHeight = 0.5d;
    double fastSpeedEffectMultiplier = 1.2d;
    double ultraSpeedEffectMultiplier = 1.4d;
    double frenzyEffectMultiplier = 1.8d;
    float flightMotionMultiplier = 1.3f;
    double waterMobBoost = 0.01d;
    boolean displayEntityCooldownMessage = true;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        // Cancel if we're on client
        if(world.isClient()) {
            return super.use(world, player, hand);
        }

        // Cancel if we're not on a vehicle
        if(!player.hasVehicle()) {
            return super.use(world, player, hand);
        }

        //updateValuesFromConfig();

        Entity mount = player.getVehicle();
        ItemStack itemSelf = player.getStackInHand(hand);
        ItemStack itemOffHand = player.getOffHandStack();

        boolean offhandIsWhip = itemOffHand.isOf(ModItems.WHIP);
        boolean isVanillaControllable = mount instanceof Saddleable;
        boolean cancelMotion = (!itemSelf.isOf(ModItems.LASSO) ||
                offhandIsWhip ||
                ModHelper.isPhysicalVehicle(mount) ||
                isVanillaControllable);

        if(cancelMotion) {
            return super.use(world, player, hand);
        }


        // Random damage chance
        if(ModHelper.random.nextInt(damageChance) == 0) {
            ModHelper.addItemDamage(player, itemSelf, damageOnUse);
            player.playSound(SoundEvents.ENTITY_LEASH_KNOT_BREAK, 0.2f, 1f);
        }

        // Add Motion
        if(mount.isTouchingWater()) {
            addWaterMotion(player, mount);
        } else {
            addMotion(player, mount);
        }

        return super.use(world, player, hand);
    }

    private void addWaterMotion(PlayerEntity player, Entity mount) {
        boolean offGround = (!mount.isOnGround() && !mount.isTouchingWater());
        boolean canFly = (mount instanceof FlyingEntity);
        boolean waterMob = (mount instanceof WaterCreatureEntity);

        // If we're not in water and can't fly, cancel
        if(offGround && !canFly) {
            return;
        }

        Vec3d lookAngle = ModHelper.getLookAngle(player);
        Vec3d lastMotion = mount.getVelocity();

        double boost = (waterMob) ? waterMobBoost : lastMotion.y;

        Vec3d newMotion = new Vec3d(lastMotion.x + (lookAngle.x/4), boost, lastMotion.z + (lookAngle.z/4));
        mount.setVelocity(newMotion);
        setLookAngle(mount, player);
    }

    private void addMotion(PlayerEntity player, Entity mount) {
        //boolean climbingMob = mount instanceof SpiderEntity;

        if(getBlockCollision(mount)) {
            //addJumpMotion(player, mount, climbingMob);
            addJumpMotion(player, mount);
        }

        Vec3d lookAngle = ModHelper.getLookAngle(player);
        Vec3d lastMotion = mount.getVelocity();

        boolean offGround = !mount.isOnGround() && lastMotion.y < -0.1f;
        boolean inWater = mount.isTouchingWater();
        boolean canFly = (mount instanceof FlyingEntity);

        // If we're not in the ground or in water (and not a flying or climbing mod)
        // we cancel movement
        if((offGround || inWater) && !canFly) {//&& !climbingMob) {
            return;
        }

        // Motion Definitions
        Vec3d newMotion = new Vec3d(lastMotion.x + (lookAngle.x/2), lastMotion.y, lastMotion.z + (lookAngle.z/2));
        Vec3d newFastMotion = new Vec3d(lastMotion.x + (lookAngle.x * fastSpeedEffectMultiplier), lastMotion.y, lastMotion.z + (lookAngle.z * fastSpeedEffectMultiplier));
        Vec3d newUltraFastMotion = new Vec3d(lastMotion.x + (lookAngle.x * ultraSpeedEffectMultiplier), lastMotion.y, lastMotion.z + (lookAngle.z * ultraSpeedEffectMultiplier));
        Vec3d newFrenzyMotion = new Vec3d(lastMotion.x + (lookAngle.x * frenzyEffectMultiplier), lastMotion.y, lastMotion.z + (lookAngle.z * frenzyEffectMultiplier));

        Vec3d newJumpMotion = new Vec3d(lookAngle.x/4, lastMotion.y, lookAngle.z/4);
        Vec3d newFlightMotion = new Vec3d(lastMotion.x + (lookAngle.x * flightMotionMultiplier), lastMotion.y + (lookAngle.y * flightMotionMultiplier), lastMotion.z + (lookAngle.z * flightMotionMultiplier));

        setLookAngle(mount, player);

        if(canFly) {
            mount.setVelocity(newFlightMotion);
        } else if (!mount.isOnGround()) {
            mount.setVelocity(newJumpMotion);
        } else {
            int state = ModHelper.getWhipState(mount);
            switch (state){
                case 0 -> mount.setVelocity(newFastMotion);
                case 1 -> mount.setVelocity(newUltraFastMotion);
                case 2 -> mount.setVelocity(newFrenzyMotion);
                default -> mount.setVelocity(newMotion);
            }
        }


    }

    //private void addJumpMotion(PlayerEntity player, Entity mount, boolean climbingMob) {
    private void addJumpMotion(PlayerEntity player, Entity mount) {
        boolean isOnGround = mount.isOnGround();
        //if((!isOnGround && !climbingMob) || getBlockCeilingCollision(player)) {
        if((!isOnGround) || getBlockCeilingCollision(player)) {
            return;
        }

        mount.fallDistance = 0.0F;

        Vec3d lastMotion = mount.getVelocity();
        setLookAngle(mount, player);

        Vec3d newMotion = new Vec3d(lastMotion.x, jumpHeight, lastMotion.z);
        mount.setVelocity(newMotion);



    }

    private void setLookAngle(Entity to_entity, Entity from_entity) {
        float yRot = from_entity.getYaw();
        to_entity.setYaw(yRot);
    }

    private boolean getBlockCeilingCollision(Entity entity) {
        BlockPos collidePos = entity.getBlockPos().up();
        BlockState blockState = entity.getWorld().getBlockState(collidePos);
        return blockState.isSolid();
    }

    private boolean getBlockCollision(Entity entity) {
        Vec3d lookAngle = ModHelper.getLookAngle(entity);
        double offsetY = 0.1f;
        int posX = (int)round(lookAngle.x + entity.getX());
        int posZ = (int)round(lookAngle.z + entity.getZ());
        int posY = (int)round(entity.getY() + offsetY);

        BlockPos collidePos = new BlockPos(posX, posY, posZ);

        BlockState blockState = entity.getWorld().getBlockState(collidePos);

        return blockState.isSolid();
    }


    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        // Exit if in client or has vehicle
        if(player.getWorld().isClient() || player.hasVehicle()) {
            return super.useOnEntity(stack, player, entity, hand);
        }

        // Frenzy active
        if(ModHelper.hasFrenziedEffect(entity)) {
            if(displayEntityCooldownMessage) {
                Style style = Style.EMPTY.withColor(Formatting.GOLD);
                ModHelper.displayCantRideActionBarMessage(entity, player, style);
            }
            return super.useOnEntity(stack, player, entity, hand);
        }

        boolean isAdult = !entity.isBaby();

        if(isAdult) {
            player.startRiding(entity);
            entity.playSound(SoundEvents.ENTITY_PIG_SADDLE, 1.0f, 1);
        }

        return super.useOnEntity(stack, player, entity, hand);
    }
}
