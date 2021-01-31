package mistaomega.lib.power;


import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class CustomIntFEStorage extends EnergyStorage implements INBTSerializable<CompoundNBT>
{


    public CustomIntFEStorage(int capacity)
    {
        super(capacity);
    }

    public CustomIntFEStorage(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer);
    }

    public CustomIntFEStorage(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract);
    }

    public void setEnergy(int energy)
    {
        this.energy = energy;
    }

    public void addEnergy(int energy)
    {
        this.energy += energy;
        if (this.energy > getMaxEnergyStored())
        {
            this.energy = getMaxEnergyStored();
        }
    }

    public void useEnergy(int energy)
    {
        this.energy -= energy;
        if (this.energy < 0)
        {
            this.energy = 0;
        }
    }

    @Override
    public boolean canReceive()
    {
        return super.canReceive();
    }

    @Override
    public boolean canExtract()
    {
        return super.canExtract();
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("energy", getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        setEnergy(nbt.getInt("energy"));
    }
}