package net.joeskott.ridingutils.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.RestartRequired;
import io.wispforest.owo.config.annotation.Sync;


@Config(name = "riding-utils-config", wrapperName = "RidingUtilitiesConfig")
public class ModConfigModel {

    //How high do mobs jump when using the lasso? (Defaults to 0.5)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 0.1d, max = 2.0d)
    public double lassoJumpHeight = 0.5d;



    //Speed multiplier for when using the lasso with applied whip speed at stage 0 (Defaults to 1.2)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 0.1d, max = 3.0d)
    public double lassoWhipFastSpeedBoost = 1.2d;

    //Speed multiplier for when using the lasso with applied whip speed at stage 1 (Defaults to 1.5)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 0.1d, max = 3.0d)
    public double lassoWhipUltraFastSpeedBoost = 1.5d;

    //Speed multiplier for when using the lasso with applied whip speed at stage 2 (Defaults to 2.0)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 0.1d, max = 3.0d)
    public double lassoWhipFrenzySpeedBoost = 1.8d;

    //How long does the speed boost last? (Defaults to 160 ticks or 8 seconds)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 1, max = 99999999)
    public int whipEffectDuration = 160;

    //This is the period after using the whip that repeat usage will increase speed (Defaults to 95 ticks or 4.75 seconds)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 1, max = 99999999)
    public int whipCompoundEffectDuration = 95;

    //Fast speed amplifier for default controllable mobs (Defaults to 2)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 0, max = 99999999)
    public int whipFastSpeedAmplifier = 2;

    //Ultra fast speed amplifier for default controllable mobs (Defaults to 3)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 0, max = 99999999)
    public int whipUltraFastSpeedAmplifier = 3;

    //Frenzy speed amplifier for default controllable mobs (Defaults to 5)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 0, max = 99999999)
    public int whipFrenzySpeedAmplifier = 5;

    //How many ticks before the whip can be used again? (Defaults to 80 or 4 seconds)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 1, max = 99999999)
    public int whipCooldownTicks = 80;

    //How many ticks, after being bucked, before you can ride an entity again? (Defaults to 80 or 4 seconds)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 1, max = 99999999)
    public int frenziedCooldownTicks = 80;

    //How many ticks for whip cooldown when it's used in water? (Defaults to 70 or 3.5 seconds)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 1, max = 99999999)
    public int whipWaterCooldownTicks = 70;

    //In frenzy mode, does it cause erratic movement? (Defaults to true)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public boolean whipFrenzyErratic = true;

    //Display the current speed state in the action bar? (Defaults to false)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public boolean displayState = false;

    //Display the cooldown message when the entity doesn't want to be ridden, in the action bar? (Defaults to true)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public boolean displayEntityCooldownMessage = true;

    //Does the whip have a chance to buck off the rider? (Defaults to true)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public boolean whipBuck = true;

    //When does the risk of side effects begin (at what damage value, higher number = lower durability)? (Defaults to 32)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @RangeConstraint(min = 1, max = 2048)
    public int whipDangerStart = 32;

    //Do horses naturally swim in water (even lava) when they have a rider? (Defaults to true)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public boolean horsesSwimNaturally = true;

    //If true, locks the speed states to only one state (Defaults to false)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    public boolean disabledSpeedStates = false;

    //If true, displays particles when you can't mount an entity when it's riled up (Defaults to false)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableRiledUpParticles = false;

}
