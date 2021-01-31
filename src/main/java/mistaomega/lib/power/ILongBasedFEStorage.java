package mistaomega.lib.power;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author MistaOmega
 * This is a class that expands on Forge's energy system to allow 64 bit integers to be used
 * <p>
 * Will have to add two capabilities, one for this, one for any other FE based power, as FE based power usually uses 32 bit
 */
public interface ILongBasedFEStorage extends IEnergyStorage
{
    /**
     * default method for power extraction using long based energy
     *
     * @param extract how much to extract
     * @param sim     if true, only simulates the power extraction, no actual movement of power will occur
     * @return amount of energy extracted from the storage
     */
    default long extractLong(long extract, boolean sim)
    {
        return extractEnergy((int) Math.min(extract, Integer.MAX_VALUE), sim);
    }

    /**
     * Default method for the insertion of long based energy
     *
     * @param maxReceive Maximum power to be inserted
     * @param sim        this if true will only simulate the insertion, no actual movement of power will occur
     * @return amount of energy accepted into storage
     */
    default long receiveLong(long maxReceive, boolean sim)
    {
        return receiveEnergy((int) Math.min(maxReceive, Long.MAX_VALUE), sim);
    }


    default long getLongStored()
    {
        return getEnergyStored();
    }

    default long getMaxLongStored()
    {
        return getMaxEnergyStored();
    }

    @Override
    boolean canExtract();

    @Override
    boolean canReceive();
}
