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
        withExistingParent("compressed_bedrock", modLoc("block/compressed_bedrock"));
        withExistingParent("stalinium_ore", modLoc("block/stalinium_ore"));
        withExistingParent("stalinium_block", modLoc("block/stalinium_block"));
        singleTexture("stalinium_ingot",
                mcLoc("item/generated"),
                "layer0",
                modLoc("item/stalinium_ingot"));
        singleTexture("stalinium_nugget", mcLoc("item/generated"), "layer0", modLoc("item/stalinium_nugget"));
    }
}
