package net.joeskott.ridingutils.sound;

import net.joeskott.ridingutils.RidingUtilities;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent WHIP_CRACKED = registerSoundEvent("whip_cracked");
    public static final SoundEvent WHIP_FRENZY = registerSoundEvent("whip_frenzy");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(RidingUtilities.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }


    public static void registerSounds() {
        RidingUtilities.LOGGER.info("Registering Sounds");
    }
}
