package net.krituximon.stalinium.block.entity;

import net.krituximon.stalinium.event.ComradeHandler;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StaliniumPressBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOT  = 0;
    public static final int OUTPUT_SLOT = 1;

    private int progress    = 0;
    private int maxProgress = 600;

    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    protected final ContainerData data = new ContainerData() {
        @Override public int get(int index)      { return index == 0 ? progress : (index == 1 ? maxProgress : 0); }
        @Override public void set(int index,int v){ if(index==0) progress=v; if(index==1) maxProgress=v; }
        @Override public int getCount()          { return 2; }
    };

    public StaliniumPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STALINIUM_PRESS_BE.get(), pos, state);
    }

    @Override public Component getDisplayName() {
        return Component.translatable("block.stalinium.stalinium_press");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id,
                                            net.minecraft.world.entity.player.Inventory inv,
                                            Player p) {
        return new StaliniumPressMenu(id, inv, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        tag.put("inventory", itemHandler.serializeNBT(regs));
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        super.saveAdditional(tag, regs);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        itemHandler.deserializeNBT(regs, tag.getCompound("inventory"));
        progress    = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
    }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider regs) {
        return saveWithoutMetadata(regs);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, StaliniumPressBlockEntity be) {
        if (be.hasRecipe()) {
            int bonus = 0;
            AABB search = new AABB(pos).inflate(10);
            for (Player p : level.getEntitiesOfClass(Player.class, search, _ignore->true)) {
                if (ComradeHandler.findPartyOf(p.getUUID()).isPresent()) bonus++;
            }

            be.progress += 1 + bonus;
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
        ItemStack in = itemHandler.getStackInSlot(INPUT_SLOT);
        return in.getItem() == ModItems.STALINIUM_NUGGET.get() && in.getCount() >= 9
                && canInsertItemIntoOutputSlot(ModItems.STALINIUM_INGOT.get().getDefaultInstance());
    }
    
    private void craftItem() {
        itemHandler.extractItem(INPUT_SLOT, 9, false);
        ItemStack out = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (out.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(ModItems.STALINIUM_INGOT.get()));
        } else {
            out.grow(1);
        }
    }
    private void resetProgress() {
        this.progress    = 0;
        this.maxProgress = 600;
    }
    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        ItemStack cur = itemHandler.getStackInSlot(OUTPUT_SLOT);
        return cur.isEmpty() || cur.getItem() == output.getItem();
    }
    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inv);
    }
}