package net.krituximon.stalinium.item;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Stalinium.MODID);

    public static final Supplier<CreativeModeTab> STALINIUM_TAB = CREATIVE_MODE_TAB.register("stalinium_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.STALINIUM_INGOT.get()))
                    .title(Component.translatable("creativetab.stalinium.stalinium"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.STALINIUM_NUGGET.get());
                        output.accept(ModItems.STALINIUM_INGOT.get());
                        output.accept(ModItems.SOVIET_ANTHEM_MUSIC_DISC.get());
                        output.accept(ModBlocks.STALINIUM_BLOCK.get());
                        output.accept(ModBlocks.STALINIUM_ORE.get());
                        output.accept(ModBlocks.COMPRESSED_BEDROCK.get());
                        output.accept(ModBlocks.STALINIUM_PRESS.get());
                        output.accept(ModItems.STALINIUM_SWORD.get());
                        output.accept(ModItems.STALINIUM_AXE.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
