package mistaomega.lib.items;

import mistaomega.lib.capability.MultiCapability;
import mistaomega.lib.inventory.EasyItemNBT;
import mistaomega.lib.power.ILongBasedFEStorage;
import mistaomega.lib.utility.utils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
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
import java.util.Set;

public class PowerToolBase extends ToolItem
{
    private final String energyNBTTag = "opes:energy";
    private long capacity;
    private long receive;

    protected PowerToolBase(float attackDamageIn, float attackSpeedIn, IItemTier tier, Set<Block> effectiveBlocksIn, Properties builder)
    {
        super(attackDamageIn, attackSpeedIn, tier, effectiveBlocksIn, builder);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return oldStack.getItem() != newStack.getItem();
    }


    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        return new MultiCapability(LazyOptional.of(() -> new PowerToolBase.LongEnergyItem(stack)), null); // This *should* work?
    }
    //endregion

    //region Energy stuffs
    public void setItemEnergyStats(long capacity, long chargeRate)
    {
        this.capacity = capacity;
        this.receive = chargeRate;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        String energy = utils.longFormatter(getEnergyStored(stack));
        String maxEnergy = utils.longFormatter(capacity);
        tooltip.add(new StringTextComponent("Energy stored: " + energy + " / " + maxEnergy));
        super.addInformation(stack, worldIn, tooltip, flagIn);
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
            setEnergy(stack, getCapacity());
            //charged
            items.add(stack);
            //not charged
            items.add(new ItemStack(this));
        }
    }

    protected long getCapacity()
    {
        return capacity;
    }


    protected long getMaxReceive()
    {
        return receive;
    }


    protected long getMaxExtract()
    {
        return Long.MAX_VALUE;
    }

    protected long receiveEnergy(ItemStack itemStack, long maxReceive, boolean simulate)
    {
        if (getMaxReceive() > 0)
        {
            long energy = EasyItemNBT.getLong(itemStack, energyNBTTag, 0);

            if (!simulate)
            {
                energy += maxReceive;
                EasyItemNBT.setLong(itemStack, energyNBTTag, energy);
            }
            return maxReceive;
        }

        return 0;
    }

    protected long extractEnergy(ItemStack itemStack, long maxExtract, boolean simulate)
    {
        if (getMaxExtract() > 0)
        {
            long energy = EasyItemNBT.getLong(itemStack, energyNBTTag, 0);
            long energyExtracted = Math.min(energy, Math.min(getMaxExtract(), maxExtract));

            if (!simulate)
            {
                energy -= energyExtracted;
                EasyItemNBT.setLong(itemStack, energyNBTTag, energy);
            }
            return energyExtracted;
        }

        return 0;
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
        EasyItemNBT.setLong(stack, energyNBTTag, MathHelper.clamp(energy, 0, getCapacity()));
    }

    public void modifyEnergy(ItemStack stack, long modifyValue)
    {
        long energy = EasyItemNBT.getLong(stack, energyNBTTag, 0);
        EasyItemNBT.setLong(stack, energyNBTTag, MathHelper.clamp(energy + modifyValue, 0, getCapacity()));
    }
    //endregion

    //region Display

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return !(getEnergyStored(stack) == getCapacity());
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        long es = getEnergyStored(stack, true);
        long mes = getCapacity();
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
            return PowerToolBase.this.receiveEnergy(stack, maxReceive, simulate);
        }

        @Override
        public long extractLong(long maxExtract, boolean simulate)
        {
            return PowerToolBase.this.extractEnergy(stack, maxExtract, simulate);
        }

        @Override
        public long getLongStored()
        {
            return PowerToolBase.this.getEnergyStored(stack, true);
        }

        @Override
        public long getMaxLongStored()
        {
            return PowerToolBase.this.getCapacity();
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
            return (int) Math.min(Integer.MAX_VALUE, PowerToolBase.this.getEnergyStored(stack, false));
        }

        @Override
        public int getMaxEnergyStored()
        {
            return (int) Math.min(Integer.MAX_VALUE, getMaxLongStored());
        }

        @Override
        public boolean canExtract()
        {
            return getMaxExtract() > 0;
        }

        @Override
        public boolean canReceive()
        {
            return getMaxReceive() > 0;
        }
    }
    //endregion
}
