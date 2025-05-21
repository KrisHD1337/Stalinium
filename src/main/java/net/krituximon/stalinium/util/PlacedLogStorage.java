package net.krituximon.stalinium.util;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Set;

public class PlacedLogStorage extends SavedData {

    /* ------------------------------------------------------------------ */
    /*  Data                                                              */
    /* ------------------------------------------------------------------ */

    private final Set<BlockPos> placedLogs = new ObjectOpenHashSet<>();

    /* ------------------------------------------------------------------ */
    /*  Constructors required by DataStorage.computeIfAbsent              */
    /* ------------------------------------------------------------------ */

    /**
     * Creates a brand-new, empty storage object (used when no save-file exists yet).
     */
    public PlacedLogStorage() {
    }

    public PlacedLogStorage(CompoundTag compoundTag, HolderLookup.Provider provider) {
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        long[] packed = placedLogs.stream().mapToLong(BlockPos::asLong).toArray();
        compoundTag.putLongArray("logs", packed);
        return compoundTag;
    }

    /**
     * Re-hydrates the storage from NBT (used when the save-file already exists).
     */
    public PlacedLogStorage(CompoundTag tag) {
        for (long packed : tag.getLongArray("logs")) {
            placedLogs.add(BlockPos.of(packed));
        }
    }

    /* ------------------------------------------------------------------ */
    /*  SavedData implementation                                          */
    /* ------------------------------------------------------------------ */


    /* ------------------------------------------------------------------ */
    /*  Public helpers (used by PlayerLogEvents, etc.)                    */
    /* ------------------------------------------------------------------ */

    public void add(BlockPos pos) {
        if (placedLogs.add(pos)) setDirty();
    }

    public void remove(BlockPos pos) {
        if (placedLogs.remove(pos)) setDirty();
    }

    public boolean contains(BlockPos pos) {
        return placedLogs.contains(pos);
    }

    /* ------------------------------------------------------------------ */
    /*  Accessor                                                          */
    /* ------------------------------------------------------------------ */

    private static final String DATA_NAME = "stalinium_placed_logs";

    public static PlacedLogStorage get(ServerLevel level) {
        SavedData.Factory<PlacedLogStorage> factory =
                new SavedData.Factory<>(            // <T extends SavedData>
                        PlacedLogStorage::new,      // Function<CompoundTag, PlacedLogStorage>
                        PlacedLogStorage::new);     // Supplier<PlacedLogStorage>

        return level.getDataStorage().computeIfAbsent(factory, DATA_NAME);
    }

}