package mistaomega.lib.capability;

import mistaomega.lib.power.ILongBasedFEStorage;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author MistaOmega
 * Forge Energy and Long Forge Energy Storage Capability
 * Hopefully it works, idrk yet.
 */

public class MultiCapability implements ICapabilityProvider
{
    public final LazyOptional<ILongBasedFEStorage> instance;
    public final Direction facing;

    public MultiCapability(LazyOptional<ILongBasedFEStorage> instance, Direction facing)
    {
        this.instance = instance;
        this.facing = facing;
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (facing == null || facing == side)
        {
            if (cap == CapabilityLongEnergy.LONGENERGY)
            {
                return instance.cast();
            }
            else if (cap == CapabilityEnergy.ENERGY)
            {
                return instance.cast();
            }
        }
        return LazyOptional.empty();
    }
}
