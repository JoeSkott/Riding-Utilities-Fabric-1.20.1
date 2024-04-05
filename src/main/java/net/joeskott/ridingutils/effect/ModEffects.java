package net.joeskott.ridingutils.effect;

import net.joeskott.ridingutils.RidingUtilities;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static StatusEffect WHIP_SPEED;


    public static StatusEffect registerStatusEffect(String name, StatusEffect effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(RidingUtilities.MOD_ID, name),
                effect);
    }

    public static void registerEffects() {
        WHIP_SPEED = registerStatusEffect("whip_speed", (new ModStatusEffect(StatusEffectCategory.NEUTRAL, 16262179))
                .addAttributeModifier(
                        EntityAttributes.GENERIC_MOVEMENT_SPEED,
                        "235fbd4d-cd81-4d51-be79-fd4781b1e842",
                        0.3d,
                        EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        RidingUtilities.LOGGER.info("Registering Status Effects");
    }
}
