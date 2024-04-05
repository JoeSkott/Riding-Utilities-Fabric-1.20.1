package net.joeskott.ridingutils;

import net.fabricmc.api.ModInitializer;

import net.joeskott.ridingutils.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RidingUtilities implements ModInitializer {
	public static final String MOD_ID = "ridingutils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Initializing Riding Utilities");
		ModItems.registerModItems();
	}
}