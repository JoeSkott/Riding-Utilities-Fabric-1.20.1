package net.joeskott.ridingutils.mixin;

import net.joeskott.ridingutils.ModHelper;
import net.joeskott.ridingutils.config.ModConfigModel;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseEntity.class)
public abstract class HorseMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	private void injectTick(CallbackInfo info) {
		AbstractHorseEntity horse = (AbstractHorseEntity) (Object) this;

		boolean erraticFrenzy = ModConfigModel.whipFrenzyErratic;
		boolean horsesSwim = ModConfigModel.horsesSwimNaturally;
		int ejectChance = 250;



		// Exit if no passengers
		if(horse.hasPassengers()) {

			int state = ModHelper.getWhipState(horse);
			if (erraticFrenzy && state > 1) {
				ModHelper.applyErraticFrenzy(horse);
			}

			// Exit if we can't swim
			if(!horsesSwim) {
				return;
			}

			// Eject
			if(!horse.getWorld().isClient()) {
				if(ModHelper.hasHorseEjectEffect(horse)) {
					horse.removeAllPassengers();
					horse.playAngrySound();
				} else if (horse.isAngry()) {
					horse.setAngry(false);
				}

				if((ModHelper.getWhipState(horse) >= 2 && ModHelper.random.nextInt(ejectChance) == 0)) {
					horse.setAngry(true);
					ModHelper.addHorseEjectEffect(horse, 1, ModConfigModel.whipCompoundEffectDuration);
					return;
				}
			}


			float chance = 0.4f;
			float roll = ModHelper.random.nextFloat(1.0f);

			if(horse.isTouchingWater() && shouldSwim(horse) && !getLiquidBelow(horse)) {
				Vec3d currentVelocity = horse.getVelocity();

				double upVelocity = 0.3d;
				Vec3d newVelocity = new Vec3d(currentVelocity.x, upVelocity, currentVelocity.z);
				horse.setVelocity(newVelocity);
				return;
			}

			if(!getLiquidBelow(horse)) {
				return;
			}

			if(shouldSwim(horse)) {
				Vec3d currentVelocity = horse.getVelocity();

				double upVelocity = currentVelocity.y;
				double sine = getSine(horse.getWorld().getTime(), 1.0D);

				if (currentVelocity.y < 0.0D) {
					if(sine < 0.9D && roll < chance) { // designed to prevent jumping too much
						upVelocity = currentVelocity.y + 0.1D + ModHelper.random.nextDouble(0.1D);
					} else {
						upVelocity = currentVelocity.y + 0.03D + ModHelper.random.nextDouble(0.1D);
					}
				}

				Vec3d newVelocity = new Vec3d(currentVelocity.x, upVelocity, currentVelocity.z);
				horse.setVelocity(newVelocity);
			}

		}
	}



	private static boolean shouldSwim(Entity entity) {
		double boostHeight = 0.5D;
		return entity.isTouchingWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getSwimHeight() + boostHeight;
	}

	private static double getSine(long time, double range) {
		double factor = 0.5D;
		double result = Math.sin((float) (time * factor)) * range;
		result += range;
		result /= 2.0D;

		return result;
	}

	private static boolean getLiquidBelow(Entity entity) {
		if(entity.isOnGround()) {
			return false;
		}

		int posX = entity.getBlockX();
		int posY = entity.getBlockY();
		int posZ = entity.getBlockZ();

		BlockPos blockPos = new BlockPos(posX, posY, posZ);

		BlockState blockStateBelow = entity.getWorld().getBlockState(blockPos.down());
		BlockState blockStateBelow2 = entity.getWorld().getBlockState(blockPos.down().down());

		return blockStateBelow.isLiquid() && blockStateBelow2.isLiquid();
	}
}