package net.krituximon.stalinium.block.entity;

import net.krituximon.stalinium.item.ModItems;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.plaf.basic.BasicComboBoxUI;

public class StaliniumPressBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public static final int INPUT_SLOT          = 0;
    public static final int REDSTONE_FUEL_SLOT  = 1;
    public static final int LAVA_FUEL_SLOT      = 2;
    public static final int OUTPUT_SLOT         = 3;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public StaliniumPressBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.STALINIUM_PRESS_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> net.krituximon.stalinium.block.entity.StaliniumPressBlockEntity.this.progress;
                    case 1 -> net.krituximon.stalinium.block.entity.StaliniumPressBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        net.krituximon.stalinium.block.entity.StaliniumPressBlockEntity.this.progress = value;
                    case 1:
                        net.krituximon.stalinium.block.entity.StaliniumPressBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.stalinium.stalinium_press");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new StaliniumPressMenu(i, inventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("growth_chamber.progress", progress);
        pTag.putInt("growth_chamber.max_progress", maxProgress);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("growth_chamber.progress");
        maxProgress = pTag.getInt("growth_chamber.max_progress");
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (hasRecipe()) {
            increaseCraftingProgress();
            setChanged(level, pos, state);
            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void craftItem() {
        itemHandler.extractItem(INPUT_SLOT,         9, false);
        itemHandler.extractItem(REDSTONE_FUEL_SLOT, 1, false);
        itemHandler.extractItem(LAVA_FUEL_SLOT,     1, false);
        itemHandler.setStackInSlot(LAVA_FUEL_SLOT, new ItemStack(Items.BUCKET, 1));
        ItemStack out = new ItemStack(ModItems.STALINIUM_INGOT.get(), 1);
        itemHandler.setStackInSlot(
                OUTPUT_SLOT,
                new ItemStack(
                        out.getItem(),
                        itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + 1
                )
        );
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 600;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        ItemStack in   = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack red  = itemHandler.getStackInSlot(REDSTONE_FUEL_SLOT);
        ItemStack lava = itemHandler.getStackInSlot(LAVA_FUEL_SLOT);

        if (in.getItem()  != ModItems.STALINIUM_NUGGET.get() || in.getCount()  < 9)   return false;
        if (red.getItem() != Items.REDSTONE_BLOCK   || red.getCount() < 1)   return false;
        if (lava.getItem()!= Items.LAVA_BUCKET      || lava.getCount()< 1)   return false;
        ItemStack out  = new ItemStack(ModItems.STALINIUM_INGOT.get(), 1);
        return canInsertItemIntoOutputSlot(out) && canInsertAmountIntoOutputSlot(out.getCount());
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}