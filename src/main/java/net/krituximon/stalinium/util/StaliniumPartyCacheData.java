package net.krituximon.stalinium.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * World-persistent store for every party’s cache inventory.
 * Saved/loaded automatically by the game.
 */
public class StaliniumPartyCacheData extends SavedData {
    private static final String DATA_NAME = "stalinium_party_caches";
    private static final int SIZE = 27;

    private final Map<UUID, SimpleContainer> inventories = new HashMap<>();

    /* ------------------------------------------------- */
    /* helper that produces an inventory wired to setDirty */
    /* ------------------------------------------------- */

    private SimpleContainer newContainer() {
        /* Every time the container changes, flag the data object. */
        return new SimpleContainer(SIZE) {
            @Override
            public void setChanged() {
                super.setChanged();
                StaliniumPartyCacheData.this.setDirty();
            }
        };
    }

    /* ---------------- public API ---------------- */

    /** Get (or lazily create) the inventory for the given party / player. */
    public SimpleContainer getOrCreate(UUID id) {
        return inventories.computeIfAbsent(id, __ -> newContainer());
    }

    /* ---------------- persistence ---------------- */

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();

        inventories.forEach((uuid, container) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Id", uuid);

            NonNullList<ItemStack> tmp =
                    NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < container.getContainerSize(); ++i) {
                tmp.set(i, container.getItem(i));
            }
            ContainerHelper.saveAllItems(entry, tmp, provider);
            list.add(entry);
        });

        tag.put("Inventories", list);
        return tag;
    }

    /** Deserializer – now also uses {@link #newContainer()} so the hook survives loading. */
    public static StaliniumPartyCacheData load(CompoundTag tag, HolderLookup.Provider provider) {
        StaliniumPartyCacheData data = new StaliniumPartyCacheData();
        ListTag list = tag.getList("Inventories", Tag.TAG_COMPOUND);

        list.forEach(t -> {
            CompoundTag entry = (CompoundTag) t;
            UUID id = entry.getUUID("Id");

            SimpleContainer cont = data.newContainer();
            NonNullList<ItemStack> tmp = NonNullList.withSize(SIZE, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(entry, tmp, provider);
            for (int i = 0; i < SIZE; ++i) cont.setItem(i, tmp.get(i));

            data.inventories.put(id, cont);
        });

        return data;
    }

    /* ---------------- factory / access ---------------- */

    private static final SavedData.Factory<StaliniumPartyCacheData> FACTORY =
            new SavedData.Factory<>(StaliniumPartyCacheData::new, StaliniumPartyCacheData::load);

    public static StaliniumPartyCacheData get(Level lvl) {
        if (lvl.isClientSide) throw new IllegalStateException("Called on client");
        return ((ServerLevel) lvl).getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }
}