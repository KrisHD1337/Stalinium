package net.krituximon.stalinium.block.entity;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.block.entity.StaliniumPressBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Stalinium.MODID);
    
    
    public static final Supplier<BlockEntityType<StaliniumPressBlockEntity>> STALINIUM_PRESS_BE =
            BLOCK_ENTITIES.register("stalinium_press_be", () -> BlockEntityType.Builder.of(
                    StaliniumPressBlockEntity::new, ModBlocks.STALINIUM_PRESS.get()).build(null));

    public static final Supplier<BlockEntityType<StaliniumCacheBlockEntity>> STALINIUM_CACHE_BE =
            BLOCK_ENTITIES.register("stalinium_cache", () ->
                    BlockEntityType.Builder.of(
                            StaliniumCacheBlockEntity::new,
                            ModBlocks.STALINIUM_CACHE.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}