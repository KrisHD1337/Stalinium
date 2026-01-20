package net.krituximon.stalinium.item;

import net.krituximon.stalinium.util.ModTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModTiers {
    public static final ToolMaterial STALINIUM = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 4096,
            11f, 6f, 25, ModTags.Items.STALINIUM_REPAIRABLE);
}
