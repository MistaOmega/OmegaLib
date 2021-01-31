package mistaomega.lib.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.items.ItemStackHandler;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.function.Function;

/**
 * Just some good old common utility functions :D
 */
public class utils
{
    public static final Random RANDOM = new Random();

    /**
     * format strings based on leading zeros
     *
     * @param v value
     * @return formatted string
     */
    public static String longFormatter(long v)
    {
        if (v <= 0) return "0";
        final String[] units = new String[]{"P", "KP", "MP", "GP", "TP", "PP", "EP",}; // lol pp.
        long loggedV = (long) Math.log10(v);
        int digitGroups = (int) (Math.log10(v) / Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(v / Math.pow(1000, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * This is a check to see if two ItemStacks hold the same item (exactly the same) this includes not dealing with stack size
     *
     * @param itemStack1 First ItemStack to check
     * @param itemStack2 Second ItemStack to check
     * @return true if ItemStack are equal
     * Redone for 1.16.5 from a version by <strong>Pahimar</strong> {@link "https://github.com/pahimar" }
     */
    public static boolean isSameStackIgnoreSize(ItemStack itemStack1, ItemStack itemStack2)
    {
        if (itemStack1 != null && itemStack2 != null)
        {
            // ItemID's first
            if (Item.getIdFromItem(itemStack1.getItem()) - Item.getIdFromItem(itemStack2.getItem()) == 0)
            {
                //The two items actually have to be the same, we check that here
                if (itemStack1.getItem() == itemStack2.getItem())
                {
                    //Metadata
                    if (itemStack1.getDamage() == itemStack2.getDamage())
                    {
                        // Do they have NBT stuff going on
                        if (itemStack1.hasTag() && itemStack2.hasTag())
                        {
                            //Check the NBT stuff is the same
                            return ItemStack.areItemStackTagsEqual(itemStack1, itemStack2);
                            //There NBT stuff can return here, on the basis it's the same NBT Tag
                        }
                        else return !itemStack1.hasTag() && !itemStack2.hasTag();
                    }
                }
            }
        }
        return false;
    }


    public static Item getItemFromResource(String resourceName)
    {
        return getItemFromResource(new ResourceLocation(resourceName));
    }

    public static Item getItemFromResource(ResourceLocation resourceLocation)
    {
        return (Registry.ITEM.getOptional(resourceLocation).orElseThrow(() -> new IllegalStateException("Item does not exist")));
    }

    /**
     * @param world This is the world we're checking
     * @return true when the world is server side
     */
    public static boolean isServer(World world)
    {
        return !world.isRemote;
    }

    /**
     * @param world This is the world we're checking
     * @return true when the world is client side
     */
    public static boolean isClient(World world)
    {
        return world.isRemote;
    }

    /**
     * Teleport entity to location, dimension compatable
     *
     * @param entity      Entity to move
     * @param destination Destination dimension
     * @param pos         position to move entity to
     */
    public static void teleport(ServerPlayerEntity entity, ServerWorld destination, BlockPos pos)
    {
        entity.changeDimension(destination, new ITeleporter()
        {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                entity = repositionEntity.apply(false);
                entity.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
                return entity;
            }
        });
    }

    public static boolean isSlotFull(ItemStackHandler slot, int recipeOutputCount) {
        System.out.println(slot.getStackInSlot(0));
        System.out.println(slot.getStackInSlot(0).getCount() + recipeOutputCount);
        return slot.getStackInSlot(0).getCount() + recipeOutputCount > slot.getStackInSlot(0).getMaxStackSize();
    }


}


