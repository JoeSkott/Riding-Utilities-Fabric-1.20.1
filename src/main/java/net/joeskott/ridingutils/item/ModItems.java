package net.joeskott.ridingutils.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.joeskott.ridingutils.RidingUtilities;
import net.joeskott.ridingutils.item.custom.LassoItem;
import net.joeskott.ridingutils.item.custom.WhipItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item LASSO = registerItem("lasso", new LassoItem(new FabricItemSettings()
            .maxDamage(64)));
    public static final Item WHIP = registerItem("whip", new WhipItem(new FabricItemSettings()
            .maxDamage(128)));

    private static void addItemsToToolsItemGroup(FabricItemGroupEntries entries) {
        entries.add(LASSO);
        entries.add(WHIP);
    }


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(RidingUtilities.MOD_ID, name), item);
    }

    public static void registerModItems() {
        RidingUtilities.LOGGER.info("Registering Mod Items");
        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::addItemsToToolsItemGroup);
    }
}
