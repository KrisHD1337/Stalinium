package net.krituximon.stalinium.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        private static TagKey<Block> commonTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
        public static final TagKey<Block> STALINIUM = commonTag("storage_blocks/stalinium");
    }

    public static class Items {
        private static TagKey<Item> commonTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
        public static final TagKey<Item> STALINIUM = commonTag("ingots/stalinium");
        public static final TagKey<Item> STALINIUM_REPAIRABLE = commonTag("stalinium_repairable");
    }
}
