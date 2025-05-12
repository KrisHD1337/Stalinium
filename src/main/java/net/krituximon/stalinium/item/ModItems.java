package net.krituximon.stalinium.item;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.sound.ModSounds;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Stalinium.MODID);

    public static final DeferredItem<Item> STALINIUM_INGOT = ITEMS.register("stalinium_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SOVIET_ANTHEM_MUSIC_DISC = ITEMS.register("soviet_anthem_music_disc",
            () -> new Item(new Item.Properties().jukeboxPlayable(ModSounds.SOVIET_ANTHEM_KEY).stacksTo(1)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
