package net.krituximon.stalinium.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;

public class ModTiers {
    public static final Tier STALINIUM = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 4096, 1f, 1f, 25, () -> Ingredient.of(ModItems.STALINIUM_INGOT.get(), ModItems.STALINIUM_NUGGET.get()));
}
