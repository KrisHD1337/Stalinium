package net.krituximon.stalinium.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * Listens for block place & break events to mark / un-mark player placed logs.
 * Register this class during mod-initialisation: MinecraftForge.EVENT_BUS.register(new PlayerLogEvents());
 */
public class PlayerLogEvents {

    /* Player (or entity) places a block */
    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent e) {
        if (!(e.getLevel() instanceof ServerLevel level)) return;
        if (!e.getPlacedBlock().is(BlockTags.LOGS)) return;

        PlacedLogStorage.get(level).add(e.getPos());
    }

    /* Any time the block is removed (player break, piston, explosion …) */
    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent e) {
        if (!(e.getLevel() instanceof ServerLevel level)) return;
        BlockPos pos = e.getPos();

        PlacedLogStorage.get(level).remove(pos);
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(new PlayerLogEvents());
    }

}