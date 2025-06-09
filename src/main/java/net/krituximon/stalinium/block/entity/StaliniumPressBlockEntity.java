package net.krituximon.stalinium.block.entity;

import net.krituximon.stalinium.event.ComradeHandler;
import net.krituximon.stalinium.item.ModItems;
import net.krituximon.stalinium.recipe.StaliniumPressRecipe;
import net.krituximon.stalinium.recipe.StaliniumPressRecipeInput;
import net.krituximon.stalinium.recipe.ModRecipes;
import net.krituximon.stalinium.screen.custom.StaliniumPressMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StaliniumPressBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOT         = 0;
    public static final int REDSTONE_FUEL_SLOT = 1;
    public static final int LAVA_FUEL_SLOT     = 2;
    public static final int OUTPUT_SLOT        = 3;

    private int progress    = 0;
    private int maxProgress = 600; // base

    public final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? progress : (index == 1 ? maxProgress : 0);
        }
        @Override
        public void set(int index, int value) {
            if (index == 0) progress = value;
            if (index == 1) maxProgress = value;
        }
        @Override
        public int getCount() {
            return 2;
        }
    };

    public StaliniumPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STALINIUM_PRESS_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.stalinium.stalinium_press");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, Player player) {
        return new StaliniumPressMenu(id, inv, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress    = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StaliniumPressBlockEntity be) {
        if (be.hasRecipe()) {
            // count nearby party‐members within 10 blocks
            AABB box = new AABB(pos).inflate(10);
            int nearbyComrades = 0;
            for (Player p : level.getEntitiesOfClass(Player.class, box, _ignored -> true)) {
                if (ComradeHandler.findPartyOf(p.getUUID()).isPresent()) {
                    nearbyComrades++;
                }
            }
            // base + bonus per comrade
            int speed = 1 + nearbyComrades;
            for (int i = 0; i < speed; i++) {
                be.progress++;
            }
            be.setChanged(level, pos, state);
            if (be.progress >= be.maxProgress) {
                be.craftItem();
                be.resetProgress();
            }
        } else {
            be.resetProgress();
        }
    }

    private boolean hasRecipe() {
        // require ≥9 nuggets
        ItemStack in = itemHandler.getStackInSlot(INPUT_SLOT);
        if (in.getItem() != ModItems.STALINIUM_NUGGET.get() || in.getCount() < 9) {
            return false;
        }
        Optional<RecipeHolder<StaliniumPressRecipe>> o = getCurrentRecipe();
        if (o.isEmpty()) return false;
        ItemStack out = o.get().value().output();
        return canInsertItemIntoOutputSlot(out) &&
                canInsertAmountIntoOutputSlot(out.getCount());
    }

    private Optional<RecipeHolder<StaliniumPressRecipe>> getCurrentRecipe() {
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.STALINIUM_PRESS_TYPE.get(),
                        new StaliniumPressRecipeInput(
                                itemHandler.getStackInSlot(REDSTONE_FUEL_SLOT),
                                itemHandler.getStackInSlot(INPUT_SLOT),
                                itemHandler.getStackInSlot(LAVA_FUEL_SLOT)
                        ),
                        level);
    }

    private void craftItem() {
        Optional<RecipeHolder<StaliniumPressRecipe>> o = getCurrentRecipe();
        if (o.isEmpty()) return;
        itemHandler.extractItem(INPUT_SLOT, 9, false);
        itemHandler.extractItem(REDSTONE_FUEL_SLOT, 1, false);
        itemHandler.extractItem(LAVA_FUEL_SLOT, 1, false);
        itemHandler.setStackInSlot(LAVA_FUEL_SLOT, new ItemStack(Items.BUCKET));
        ItemStack out = o.get().value().output();
        ItemStack current = itemHandler.getStackInSlot(OUTPUT_SLOT);
        itemHandler.setStackInSlot(
                OUTPUT_SLOT,
                new ItemStack(out.getItem(), current.getCount() + 1)
        );
    }

    private void resetProgress() {
        this.progress    = 0;
        this.maxProgress = 600;
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack out) {
        ItemStack current = itemHandler.getStackInSlot(OUTPUT_SLOT);
        return current.isEmpty() || current.getItem() == out.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        ItemStack current = itemHandler.getStackInSlot(OUTPUT_SLOT);
        int max = current.isEmpty() ? 64 : current.getMaxStackSize();
        return max >= current.getCount() + count;
    }
}