package mistaomega.lib.items;

import mistaomega.lib.capability.MultiCapability;
import mistaomega.lib.inventory.EasyItemNBT;
import mistaomega.lib.power.ILongBasedFEStorage;
import mistaomega.lib.utility.utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class EnergyStorageItemBase extends Item
{
    private final String energyNBTTag = "opes:energy";
    private long capacity;
    private long receive;
    private long extract;

    public EnergyStorageItemBase(Properties properties)
    {
        super(properties);
    }

    //region Basic item methods

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return oldStack.getItem() != newStack.getItem();
    }


    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        return new MultiCapability(LazyOptional.of(() -> new LongEnergyItem(stack)), null); // This *should* work?
    }
    //endregion

    //region Energy stuffs
    public void setItemEnergyStats(long capacity, long receive, long extract)
    {
        this.capacity = capacity;
        this.receive = receive;
        this.extract = extract;
    }

    /**
     * adds charged and empty version of each item
     *
     * @param group
     * @param items
     */
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            ItemStack stack = new ItemStack(this);
            setEnergy(stack, getCapacity(stack));
            //charged
            items.add(stack);
            //not charged
            items.add(new ItemStack(this));
        }
    }

    protected long getCapacity(ItemStack itemStack)
    {
        return capacity;
    }


    protected long getMaxReceive(ItemStack itemStack)
    {
        return receive;
    }


    protected long getMaxExtract(ItemStack itemStack)
    {
        return extract;
    }

    protected long receiveEnergy(ItemStack itemStack, long maxReceive, boolean simulate)
    {
        if (getMaxReceive(itemStack) > 0)
        {
            long energy = EasyItemNBT.getLong(itemStack, energyNBTTag, 0);
            long energyReceived = Math.min(getCapacity(itemStack) - energy, Math.min(getMaxReceive(itemStack), maxReceive));

            if (!simulate)
            {
                energy += energyReceived;
                EasyItemNBT.setLong(itemStack, energyNBTTag, energy);
            }
            return energyReceived;
        }

        return 0;
    }

    protected long extractEnergy(ItemStack itemStack, long maxExtract, boolean simulate)
    {
        if (getMaxExtract(itemStack) > 0)
        {
            long energy = EasyItemNBT.getLong(itemStack, energyNBTTag, 0);
            long energyExtracted = Math.min(energy, Math.min(getMaxExtract(itemStack), maxExtract));

            if (!simulate)
            {
                energy -= energyExtracted;
                EasyItemNBT.setLong(itemStack, energyNBTTag, energy);
            }
            return energyExtracted;
        }

        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        String energy = utils.longFormatter(getEnergyStored(stack));
        String maxEnergy = utils.longFormatter(capacity);
        tooltip.add(new StringTextComponent("Energy stored: " + energy + " / " + maxEnergy));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public long getEnergyStored(ItemStack stack)
    {
        return EasyItemNBT.getLong(stack, energyNBTTag, 0);
    }

    protected long getEnergyStored(ItemStack stack, boolean isOPAsking)
    {
        return getEnergyStored(stack);
    }

    public void setEnergy(ItemStack stack, long energy)
    {
        EasyItemNBT.setLong(stack, energyNBTTag, MathHelper.clamp(energy, 0, getCapacity(stack)));
    }

    public void modifyEnergy(ItemStack stack, long modifyValue)
    {
        long energy = EasyItemNBT.getLong(stack, energyNBTTag, 0);
        EasyItemNBT.setLong(stack, energyNBTTag, MathHelper.clamp(energy + modifyValue, 0, getCapacity(stack)));
    }
    //endregion

    //region Display

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return !(getEnergyStored(stack) == getCapacity(stack));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        long es = getEnergyStored(stack, true);
        long mes = getCapacity(stack);
        return 1D - ((double) es / (double) mes);
    }
    //endregion

    //region Energy Item Interface Implementation
    private class LongEnergyItem implements ILongBasedFEStorage
    {
        private final ItemStack stack;

        public LongEnergyItem(ItemStack stack)
        {
            this.stack = stack;
        }

        @Override
        public long receiveLong(long maxReceive, boolean simulate)
        {
            return EnergyStorageItemBase.this.receiveEnergy(stack, maxReceive, simulate);
        }

        @Override
        public long extractLong(long maxExtract, boolean simulate)
        {
            return EnergyStorageItemBase.this.extractEnergy(stack, maxExtract, simulate);
        }

        @Override
        public long getLongStored()
        {
            return EnergyStorageItemBase.this.getEnergyStored(stack, true);
        }

        @Override
        public long getMaxLongStored()
        {
            return EnergyStorageItemBase.this.getCapacity(stack);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate)
        {
            return (int) receiveLong(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate)
        {
            return (int) extractLong(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored()
        {
            return (int) Math.min(Integer.MAX_VALUE, EnergyStorageItemBase.this.getEnergyStored(stack, false));
        }

        @Override
        public int getMaxEnergyStored()
        {
            return (int) Math.min(Integer.MAX_VALUE, getMaxLongStored());
        }

        @Override
        public boolean canExtract()
        {
            return getMaxExtract(stack) > 0;
        }

        @Override
        public boolean canReceive()
        {
            return getMaxReceive(stack) > 0;
        }
    }
    //endregion
}
