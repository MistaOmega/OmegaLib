package mistaomega.lib.power;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author MistaOmega
 *
 * This is soon to be the base of my power based systems
 * It handles long based input and output for huge power management and production
 * Needs to be compatable with standard FE, so if you wanna use this when it's done, you need a new capability for it that works with FE's one too xo
 */
public class LongBasedFEStorage implements ILongBasedFEStorage, INBTSerializable<CompoundNBT>
{
    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;
    private long energy;
    private final boolean isAllowReceive = true; //todo allow modify
    private final boolean isAllowExtract = true;

    //region Long based energy
    public LongBasedFEStorage(long capacity, long maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public LongBasedFEStorage(long capacity, long maxReceive, long maxExtract)
    {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public LongBasedFEStorage(long capacity, long maxReceive, long maxExtract, long initialEnergy)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = initialEnergy;
    }

    @Override
    public long extractLong(long extract, boolean sim)
    {
        if (!canExtract())
        {
            return 0;
        }

        long energyExtracted = Math.min(energy, maxExtract);
        if (!sim)
        {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public long receiveLong(long maxReceive, boolean sim)
    {
        if (!canReceive())
        {
            return 0;
        }

        long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!sim)
        {
            energy += energyReceived;
        }
        return energyReceived;
    }


    @Override
    public long getLongStored()
    {
        return energy;
    }

    @Override
    public long getMaxLongStored()
    {
        return capacity;
    }

    public void setLongEnergy(long energy)
    {
        this.energy = energy;
    }

    //endregion

    //region int based energy overwrites
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
        return (int) Math.min(getLongStored(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored()
    {
        return (int) Math.min(getMaxLongStored(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract()
    {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive()
    {
        return maxReceive > 0;
    }
    //endregion

    //region NBT
    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT compound = new CompoundNBT();
        writeWithChecks("longEnergy", energy, compound);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        energy = readWithChecks("longEnergy", nbt);
    }

    /**
     * This and it's brother readWithChecks handle the long based writing making sure that integers don't stop us saving the power
     *
     * @param name     nbt tag name
     * @param value    what to write
     * @param compound NBT Compound to do the writing
     */
    private void writeWithChecks(String name, long value, CompoundNBT compound)
    {
        if (value > Integer.MAX_VALUE)
        {
            compound.putLong(name, value);
        }
        else
        {
            compound.putInt(name, (int) value);
        }
    }

    private long readWithChecks(String name, CompoundNBT compound)
    {
        INBT tag = compound.get(name);
        if (tag instanceof NumberNBT)
        {
            return ((NumberNBT) tag).getLong();
        }
        return 0;
    }
    //endregion

}
