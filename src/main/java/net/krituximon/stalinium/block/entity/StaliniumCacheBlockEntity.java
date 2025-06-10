// StaliniumCacheBlockEntity.java

package net.krituximon.stalinium.block.entity;

import net.krituximon.stalinium.event.ComradeHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaliniumCacheBlockEntity extends BlockEntity {
    private static final int SIZE = 27;

    // each party → shared 27‐slot inventory
    private static final Map<UUID, SimpleContainer> PARTY_INVENTORIES = new ConcurrentHashMap<>();

    public StaliniumCacheBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STALINIUM_CACHE_BE.get(), pos, state);
    }

    /**
     * Grab (or create) the shared SimpleContainer for this player's party  
     */
    public static SimpleContainer getPartyInventory(Player who) {
        Optional<ComradeHandler.Party> opt = ComradeHandler.findPartyOf(who.getUUID());
        UUID key = opt.map(p -> p.leader).orElse(who.getUUID());
        return PARTY_INVENTORIES.computeIfAbsent(key, __ -> new SimpleContainer(SIZE));
    }

    /** Drop everything, using that same shared container */
    public void drops() {
        SimpleContainer inv = getPartyInventory((ServerPlayer) null);
        // actually, we don't know "who" here—just drop all slots
        SimpleContainer tmp = new SimpleContainer(SIZE);
        for (int i = 0; i < SIZE; i++) {
            tmp.setItem(i, inv.getItem(i));
        }
        Containers.dropContents(level, worldPosition, tmp);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider prov) {
        super.saveAdditional(tag, prov);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
