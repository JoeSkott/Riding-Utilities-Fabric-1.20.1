package net.joeskott.ridingutils.config;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "riding-utils-config", wrapperName = "RidingUtilitiesConfig")
public class ModConfigModel {
    public double lassoJumpHeight;
    public double lassoWhipFastSpeedBoost;
    public double lassoWhipUltraFastSpeedBoost;
    public double lassoWhipFrenzySpeedBoost;
    public int whipEffectDuration;
    public int whipCompoundEffectDuration;
    public int whipFastSpeedAmplifier;
    public int whipUltraFastSpeedAmplifier;
    public int whipFrenzySpeedAmplifier;
    public int whipCooldownTicks;
    public int frenziedCooldownTicks;
    public int whipWaterCooldownTicks;
    public boolean whipFrenzyErratic;
    public boolean displayState;
    public boolean displayEntityCooldownMessage;
    public boolean whipBuck;
    public int whipDangerStart;
    public boolean horsesSwimNaturally;
    public boolean disabledSpeedStates;

}
