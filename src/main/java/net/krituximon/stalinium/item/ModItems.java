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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Stalinium.MODID);

    public static final DeferredItem<Item> STALINIUM_INGOT = ITEMS.register("stalinium_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> STALINIUM_NUGGET = ITEMS.register("stalinium_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SOVIET_ANTHEM_MUSIC_DISC = ITEMS.register("soviet_anthem_music_disc",
            () -> new Item(new Item.Properties().jukeboxPlayable(ModSounds.SOVIET_ANTHEM_KEY).stacksTo(1)));

    public static final DeferredItem<SwordItem> STALINIUM_SWORD = ITEMS.register("stalinium_sword",
            () -> new StaliniumSwordItem(ModTiers.STALINIUM, new Item.Properties()
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                    .attributes(SwordItem.createAttributes(ModTiers.STALINIUM, 3f, -2.4f))));

    public static final DeferredItem<AxeItem> STALINIUM_AXE = ITEMS.register("stalinium_axe",
            () -> new StaliniumAxeItem(ModTiers.STALINIUM, new Item.Properties()
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                    .attributes(AxeItem.createAttributes(ModTiers.STALINIUM, 5f, -3.0f))));

    public static final DeferredItem<PickaxeItem> STALINIUM_PICKAXE = ITEMS.register("stalinium_pickaxe",
            () -> new StaliniumPickaxeItem(ModTiers.STALINIUM, new Item.Properties()
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                    .attributes(PickaxeItem.createAttributes(ModTiers.STALINIUM, 1.0f, -2.8f))));

    public static final DeferredItem<ShovelItem> STALINIUM_SHOVEL = ITEMS.register("stalinium_shovel",
            () -> new StaliniumShovelItem(ModTiers.STALINIUM, new Item.Properties()
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                    .attributes(ShovelItem.createAttributes(ModTiers.STALINIUM, 1.5f, -3.0f))));

    public static final DeferredItem<HoeItem> STALINIUM_HOE = ITEMS.register("stalinium_hoe",
            () -> new StaliniumHoeItem(ModTiers.STALINIUM, new Item.Properties()
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                    .attributes(HoeItem.createAttributes(ModTiers.STALINIUM, 1.0f, -3.0f))));

    public static final DeferredItem<ArmorItem> STALINIUM_HELMET = ITEMS.register("stalinium_helmet",
            () -> new StaliniumHelmetItem(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1)));

    public static final DeferredItem<ArmorItem> STALINIUM_CHESTPLATE = ITEMS.register("stalinium_chestplate",
            () -> new StaliniumChestplateLeggingsItem(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1)));

    public static final DeferredItem<ArmorItem> STALINIUM_LEGGINGS = ITEMS.register("stalinium_leggings",
            () -> new StaliniumChestplateLeggingsItem(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1)));

    public static final DeferredItem<ArmorItem> STALINIUM_BOOTS = ITEMS.register("stalinium_boots",
            () -> new StaliniumBootsItem(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1)));

    public static final DeferredItem<MaceItem> STALINIUM_MACE = ITEMS.register("stalinium_mace",
            () -> new StaliniumMaceItem(new Item.Properties()
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                    .attributes(StaliniumMaceItem.createAttributes())));

    public static final DeferredItem<Item> STALINIUM_SMITHING_TEMPLATE = ITEMS.register("stalinium_smithing_template",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
