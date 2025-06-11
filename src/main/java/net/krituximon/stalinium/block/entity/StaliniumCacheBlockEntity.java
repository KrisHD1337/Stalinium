// StaliniumCacheBlockEntity.java

package net.krituximon.stalinium.block.entity;

import net.krituximon.stalinium.event.ComradeHandler;
import net.krituximon.stalinium.util.StaliniumPartyCacheData;
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

    public StaliniumCacheBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STALINIUM_CACHE_BE.get(), pos, state);
    }

    /**
     * Obtain – and create if necessary – the inventory that belongs to the player’s party.
     */
    public static SimpleContainer getPartyInventory(@Nullable Player who) {
        UUID key;
        if (who == null) {
            key = new UUID(0L, 0L);                // world-wide default dump
        } else {
            Optional<ComradeHandler.Party> opt = ComradeHandler.findPartyOf(who.getUUID());
            key = opt.map(p -> p.leader).orElse(who.getUUID());
        }

        // We keep the containers in the world-persistent SavedData
        Level lvl = who != null ? who.level() : null;
        if (lvl == null || lvl.isClientSide)           // should never happen on the server
            return new SimpleContainer(SIZE);

        StaliniumPartyCacheData data = StaliniumPartyCacheData.get(lvl);
        return data.getOrCreate(key);
    }

    /* ---------- helper to mark the SavedData dirty whenever the container changes ---------- */
    public static void markDirty(Level level, UUID key) {
        if (level.isClientSide) return;
        StaliniumPartyCacheData.get(level).setDirty();
    }

    // drop-logic unchanged …
    public void drops() {
        SimpleContainer inv = getPartyInventory(null);
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