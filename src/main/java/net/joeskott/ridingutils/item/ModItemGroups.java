package net.joeskott.ridingutils.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.joeskott.ridingutils.RidingUtilities;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup RIDING_UTILITIES_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(RidingUtilities.MOD_ID, "riding_utilities"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.riding_utilities"))
                    .icon(() -> new ItemStack(ModItems.LASSO)).entries((displayContext, entries) -> {
                        entries.add(ModItems.LASSO);
                        entries.add(ModItems.WHIP);
                    }).build());

    public static void registerItemGroups() {
        RidingUtilities.LOGGER.info("Registering Item Group");
    }
}
