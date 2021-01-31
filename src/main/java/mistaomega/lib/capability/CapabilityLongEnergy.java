package mistaomega.lib.capability;

import mistaomega.lib.power.ILongBasedFEStorage;
import mistaomega.lib.power.LongBasedFEStorage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * @author MistaOmega
 * big up intelliJ for the help fam
 * Capability for big energy yes yes, pls still use FE checks too, otherwise big error.
 */
public class CapabilityLongEnergy
{
    @CapabilityInject(ILongBasedFEStorage.class)
    public static Capability<ILongBasedFEStorage> LONGENERGY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ILongBasedFEStorage.class, new Capability.IStorage<ILongBasedFEStorage>()
        {
            @Nullable
            @Override
            public INBT writeNBT(Capability<ILongBasedFEStorage> capability, ILongBasedFEStorage instance, Direction side)
            {
                if (instance != null)
                {
                    return ((INBTSerializable<?>) instance).serializeNBT();
                }
                else
                {
                    throw new IllegalArgumentException("Instance cannot be serialised to");
                }
            }

            @Override
            public void readNBT(Capability<ILongBasedFEStorage> capability, ILongBasedFEStorage instance, Direction side, INBT nbt)
            {
                if (instance != null)
                {
                    ((LongBasedFEStorage) instance).deserializeNBT((CompoundNBT) nbt);
                }
                else
                {
                    throw new IllegalArgumentException("Instance cannot be serialised to");
                }
            }
        }, () -> new LongBasedFEStorage(1000, 1000));
    }
}
