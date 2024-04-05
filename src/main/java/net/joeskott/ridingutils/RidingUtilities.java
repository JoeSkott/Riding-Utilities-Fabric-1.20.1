package net.joeskott.ridingutils;

import net.fabricmc.api.ModInitializer;

import net.joeskott.ridingutils.config.RidingUtilitiesConfig;
import net.joeskott.ridingutils.item.ModItemGroups;
import net.joeskott.ridingutils.item.ModItems;
import net.joeskott.ridingutils.sound.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RidingUtilities implements ModInitializer {
	public static final String MOD_ID = "ridingutils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final RidingUtilitiesConfig CONFIG = RidingUtilitiesConfig.createAndLoad();

	@Override
	public void onInitialize() {
		LOGGER.info("Initialized Configs");
		LOGGER.info("Initializing...");
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModSounds.registerSounds();
	}
}