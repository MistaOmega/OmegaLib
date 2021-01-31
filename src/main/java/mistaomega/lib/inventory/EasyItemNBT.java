package mistaomega.lib.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * @author MistaOmega
 * Common NBT methods to make my code a little tidier
 * Are these functions kinda just saving my code a bit of room? yes
 * Do I care? no. <3
 * <p>
 * This will help with NBT tags for items as opposed to blocks
 * SuppressWarnings because the isPresent handles a null check which IntelliJ keeps shouting at me about (lol watch it not work)
 */

@SuppressWarnings("ConstantConditions")
public class EasyItemNBT
{

    public static boolean isPresent(ItemStack itemStack, String nbtTag)
    {
        CompoundNBT compoundNBT = itemStack.getTag();
        if (compoundNBT == null)
        {
            return false;
        }
        else return itemStack.getTag().contains(nbtTag);
    }

    //region Setters
    public static CompoundNBT getCompound(ItemStack itemStack)
    {
        if (itemStack.getTag() == null) itemStack.setTag(new CompoundNBT());
        return itemStack.getTag();
    }

    public static ItemStack setByte(ItemStack itemStack, String nbtTag, byte b)
    {
        getCompound(itemStack).putByte(nbtTag, b);
        return itemStack;
    }

    public static ItemStack setByteArray(ItemStack itemStack, String nbtTag, byte[] bytes)
    {
        getCompound(itemStack).putByteArray(nbtTag, bytes);
        return itemStack;
    }

    public static ItemStack setBoolean(ItemStack itemStack, String nbtTag, boolean flag)
    {
        getCompound(itemStack).putBoolean(nbtTag, flag);
        return itemStack;
    }

    public static ItemStack setFloat(ItemStack itemStack, String nbtTag, float val)
    {
        getCompound(itemStack).putFloat(nbtTag, val);
        return itemStack;
    }

    public static ItemStack setDouble(ItemStack itemStack, String nbtTag, double val)
    {
        getCompound(itemStack).putDouble(nbtTag, val);
        return itemStack;
    }

    public static ItemStack setShort(ItemStack itemStack, String nbtTag, short val)
    {
        getCompound(itemStack).putShort(nbtTag, val);
        return itemStack;
    }

    public static ItemStack setInteger(ItemStack itemStack, String nbtTag, int val)
    {
        getCompound(itemStack).putInt(nbtTag, val);
        return itemStack;
    }

    public static ItemStack setLong(ItemStack itemStack, String nbtTag, long val)
    {
        getCompound(itemStack).putLong(nbtTag, val);
        return itemStack;
    }

    public static ItemStack setString(ItemStack itemStack, String nbtTag, String str)
    {
        getCompound(itemStack).putString(nbtTag, str);
        return itemStack;
    }

    public static ItemStack setIntArray(ItemStack itemStack, String nbtTag, int[] values)
    {
        getCompound(itemStack).putIntArray(nbtTag, values);
        return itemStack;
    }

    public static ItemStack setLongArray(ItemStack itemStack, String nbtTag, long[] values)
    {
        getCompound(itemStack).putLongArray(nbtTag, values);
        return itemStack;
    }

    //endregion

    //region Getters
    public static byte getByte(ItemStack itemStack, String nbtTag, int defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getByte(nbtTag) : (byte) defaultExpected;
    }

    public static byte[] getByteArray(ItemStack itemStack, String nbtTag, byte[] defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getByteArray(nbtTag) : defaultExpected;
    }

    public static boolean getBoolean(ItemStack itemStack, String nbtTag, boolean defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getBoolean(nbtTag) : defaultExpected;
    }

    public static short getShort(ItemStack itemStack, String nbtTag, short defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getShort(nbtTag) : defaultExpected;
    }

    public static int getInteger(ItemStack itemStack, String nbtTag, int defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getInt(nbtTag) : defaultExpected;
    }

    public static long getLong(ItemStack itemStack, String nbtTag, long defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getLong(nbtTag) : defaultExpected;
    }

    public static float getFloat(ItemStack itemStack, String nbtTag, float defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getFloat(nbtTag) : defaultExpected;
    }

    public static double getDouble(ItemStack itemStack, String nbtTag, double defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getDouble(nbtTag) : defaultExpected;
    }

    public static String getString(ItemStack itemStack, String nbtTag, String defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getString(nbtTag) : defaultExpected;
    }

    public static int[] getDouble(ItemStack itemStack, String nbtTag, int[] defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getIntArray(nbtTag) : defaultExpected;
    }

    public static long[] getString(ItemStack itemStack, String nbtTag, long[] defaultExpected)
    {
        return isPresent(itemStack, nbtTag) ? itemStack.getTag().getLongArray(nbtTag) : defaultExpected;
    }

    //endregion

}
