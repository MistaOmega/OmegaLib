package mistaomega.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This is a container that containers can extend, containing methods for shift click operation and inventory layout
 */
public class CommonContainer extends Container
{
    protected final int inventorySize;
    int PLAYER_INVENTORY_X_MARGIN;
    int PLAYER_INVENTORY_Y_MARGIN;

    protected CommonContainer(ContainerType<?> type, int id, int size)
    {
        super(type, id);
        inventorySize = size;
        PLAYER_INVENTORY_X_MARGIN = 10;
        PLAYER_INVENTORY_Y_MARGIN = 70;
    }

    public int getPLAYER_INVENTORY_X_MARGIN()
    {
        return PLAYER_INVENTORY_X_MARGIN;
    }

    public void setPLAYER_INVENTORY_X_MARGIN(int PLAYER_INVENTORY_X_MARGIN)
    {
        this.PLAYER_INVENTORY_X_MARGIN = PLAYER_INVENTORY_X_MARGIN;
    }

    public int getPLAYER_INVENTORY_Y_MARGIN()
    {
        return PLAYER_INVENTORY_Y_MARGIN;
    }

    public void setPLAYER_INVENTORY_Y_MARGIN(int PLAYER_INVENTORY_Y_MARGIN)
    {
        this.PLAYER_INVENTORY_Y_MARGIN = PLAYER_INVENTORY_Y_MARGIN;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }

    /**
     * Bind slots to the players inventory
     *
     * @param playerInventory
     */
    protected void bindSlotsToPlayerInv(PlayerInventory playerInventory)
    {
        //Main inventory, 3 deep, 9 across
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, PLAYER_INVENTORY_X_MARGIN + j * 18, PLAYER_INVENTORY_Y_MARGIN + i * 18));
            }
        }

        //Hotbar
        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(playerInventory, k, PLAYER_INVENTORY_X_MARGIN + k * 18, PLAYER_INVENTORY_Y_MARGIN + (3 * 18) + 4));
        }
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack transferStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            // Stack already in slot
            ItemStack currentHeldStack = slot.getStack();
            transferStack = currentHeldStack.copy();

            if (index < inventorySize)
            {
                if (!this.mergeItemStack(currentHeldStack, inventorySize, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(currentHeldStack, 0, inventorySize, false))
            {
                return ItemStack.EMPTY;
            }

            if (currentHeldStack.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return transferStack;
    }

}
