package mistaomega.lib.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author MistaOmega
 * Base class for when I eventually do multiblocks lol
 */
public class MultiBlockBase
{
    public BlockPos invalidBlock;
    public BlockPos validBlock;

    public boolean isValidBlock(String blockName, BlockPos pos, World world)
    {
        BlockState state = world.getBlockState(pos);
        if (blockName.isEmpty())
        { // blank block name shouldn't really occur, but I will check anyway
            return true;
        }
        else if (blockName.equals("air"))
        {
            return state.getBlock().isAir(state, world, pos);
        }
        else
        {
            return ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString().equals(blockName);
        }
    }


    public void setBlock(String blockName, World world, BlockPos pos)
    {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if (block != null)
        {
            world.setBlockState(pos, block.getDefaultState());
        }
        else
        {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
