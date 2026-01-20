package net.krituximon.stalinium.item;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.item.custom.*;
import net.krituximon.stalinium.sound.ModSounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Stalinium.MODID);

    public static final DeferredItem<Item> STALINIUM_INGOT = ITEMS.registerItem("stalinium_ingot",
            Item::new);

    public static final DeferredItem<Item> STALINIUM_NUGGET = ITEMS.registerItem("stalinium_nugget",
            Item::new);

    public static final DeferredItem<Item> SOVIET_ANTHEM_MUSIC_DISC = ITEMS.registerItem("soviet_anthem_music_disc",
            Item::new, new Item.Properties().jukeboxPlayable(ModSounds.SOVIET_ANTHEM_KEY).stacksTo(1));

    public static final DeferredItem<SwordItem> STALINIUM_SWORD = ITEMS.registerItem("stalinium_sword",
            (properties) -> new StaliniumSwordItem(ModTiers.STALINIUM, 3f, -2.4f, properties
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))));

    public static final DeferredItem<AxeItem> STALINIUM_AXE = ITEMS.registerItem("stalinium_axe",
            (properties) -> new StaliniumAxeItem(ModTiers.STALINIUM, 5f, -3.0f, properties
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))));

    public static final DeferredItem<PickaxeItem> STALINIUM_PICKAXE = ITEMS.registerItem("stalinium_pickaxe",
            (properties) -> new StaliniumPickaxeItem(ModTiers.STALINIUM, 1.0f, -2.8f, properties
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))));

    public static final DeferredItem<ShovelItem> STALINIUM_SHOVEL = ITEMS.registerItem("stalinium_shovel",
            (properties) -> new StaliniumShovelItem(ModTiers.STALINIUM, 1.5f, -3.0f, properties
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))));

    public static final DeferredItem<HoeItem> STALINIUM_HOE = ITEMS.registerItem("stalinium_hoe",
            (properties) -> new StaliniumHoeItem(ModTiers.STALINIUM, 1.0f, -3.0f, properties
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))));

    public static final DeferredItem<ArmorItem> STALINIUM_HELMET = ITEMS.registerItem("stalinium_helmet",
            (properties) -> new StaliniumHelmetItem(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL, ArmorType.HELMET,
                    properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1)));

    public static final DeferredItem<ArmorItem> STALINIUM_CHESTPLATE = ITEMS.registerItem("stalinium_chestplate",
            (properties) -> new StaliniumChestplateLeggingsItem(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL, ArmorType.CHESTPLATE,
                    properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1)));

    public static final DeferredItem<ArmorItem> STALINIUM_LEGGINGS = ITEMS.registerItem("stalinium_leggings",
            (properties) -> new StaliniumChestplateLeggingsItem(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL, ArmorType.LEGGINGS,
                    properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1)));

    public static final DeferredItem<ArmorItem> STALINIUM_BOOTS = ITEMS.registerItem("stalinium_boots",
            (properties) -> new StaliniumBootsItem(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL, ArmorType.BOOTS,
                    properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1)));

    public static final DeferredItem<MaceItem> STALINIUM_MACE = ITEMS.registerItem("stalinium_mace",
            StaliniumMaceItem::new, new Item.Properties()
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                    .attributes(StaliniumMaceItem.createAttributes()));

    public static final DeferredItem<Item> STALINIUM_SMITHING_TEMPLATE = ITEMS.registerItem("stalinium_smithing_template",
            Item::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
