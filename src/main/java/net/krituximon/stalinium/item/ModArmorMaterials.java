package net.krituximon.stalinium.item;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.util.ModTags;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final ArmorMaterial STALINIUM_ARMOR_MATERIAL = new ArmorMaterial(8096,
            Util.make(new EnumMap<>(ArmorType.class), attribute -> {
                attribute.put(ArmorType.BOOTS, 6);
                attribute.put(ArmorType.LEGGINGS, 8);
                attribute.put(ArmorType.CHESTPLATE, 10);
                attribute.put(ArmorType.HELMET, 6);
                attribute.put(ArmorType.BODY, 12);
            }), 25, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5f, 0.15f,
            ModTags.Items.STALINIUM_REPAIRABLE, ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "stalinium"));
}
