package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.item.ModItems;
import net.krituximon.stalinium.Stalinium;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Stalinium.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.STALINIUM_INGOT.get());
        basicItem(ModItems.SOVIET_ANTHEM_MUSIC_DISC.get());
        basicItem(ModItems.STALINIUM_NUGGET.get());
        singleTexture(
                "stalinium_sword",
                mcLoc("item/handheld"),
                "layer0",
                modLoc("item/stalinium_sword")
        );
    }
}
