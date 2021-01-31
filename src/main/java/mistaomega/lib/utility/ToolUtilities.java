package mistaomega.lib.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ToolUtilities
{

    /**
     * A basic ray tracer for any entity because I couldn't find the default one
     *
     * @param e        entity
     * @param distance distance of the raytrace
     * @param fluids   need to know if fluids are involved in the trace
     * @return a ray trace result based on inputs
     */
    public static BlockRayTraceResult raytraceFromEntity(Entity e, double distance, boolean fluids)
    {
        Vec3d lookVec = e.getLook(1); // Where the player is looking
        Vec3d eyeVec = e.getEyePosition(1); // This is the player's eye, the origin for the vector cross origin (Thanks Draco)
        Vec3d eyeVecToLookVec = eyeVec.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
        return e.world.rayTraceBlocks(new RayTraceContext(eyeVec, eyeVecToLookVec,
                RayTraceContext.BlockMode.OUTLINE, fluids ?
                RayTraceContext.FluidMode.ANY :
                RayTraceContext.FluidMode.NONE, e)); //Ternary operators are a gift from god no cap.-
    }

    /**
     * Handles the iteration through all the blocks added
     *
     * @param player         Player who broke center block
     * @param stack          Item the player has
     * @param world          World the action is happening in
     * @param centerBlockPos The center block that was broken by the player
     * @param startVector    Start vector
     * @param endVector      end vector
     * @param doDrops        doDrops of items
     */
    public static void iterateAndRemove(PlayerEntity player, ItemStack stack, World world, BlockPos centerBlockPos, Vec3i startVector, Vec3i endVector, boolean doDrops)
    {
        for (BlockPos currentBlockPos : BlockPos.getAllInBoxMutable(centerBlockPos.add(startVector), centerBlockPos.add(endVector)))
        {

            // Don't want to deal with the center block, we've already got that
            if (currentBlockPos.equals(centerBlockPos))
            {
                continue;
            }
            blockRemovalDroppable(player, stack, world, currentBlockPos, doDrops);
        }
    }

    @SuppressWarnings("deprecation") // Don't @ me about this fam, I'll do me tyvm.
    public static void blockRemovalDroppable(PlayerEntity player, ItemStack stack, World world, BlockPos pos, boolean doDrops)
    {
        // @depreciated --> is useful for these purposes, we need the block to be loaded
        if (!world.isBlockLoaded(pos))
        {
            return;
        }

        BlockState state = world.getBlockState(pos); // Blockstate
        Block block = state.getBlock(); // current block we're at
        if (!block.isAir(state, world, pos) && state.getPlayerRelativeBlockHardness(player, world, pos) > 0 && state.canHarvestBlock(player.world, pos, player))
        {
            int exp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);
            if (exp == -1)
            {
                return;
            }

            if (!player.abilities.isCreativeMode)
            {
                TileEntity tile = world.getTileEntity(pos);

                if (block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos)))
                {
                    //stack.damageItem(1, player, (playerEntity) -> playerEntity.sendBreakAnimation(EquipmentSlotType.MAINHAND)); // calls the item to be damaged after each block break

                    if (!doDrops)
                    {
                        block.harvestBlock(world, player, pos, state, tile, stack);
                        block.dropXpOnBlockBreak(world, pos, exp);
                    }
                }
            }
            else
            {
                world.removeBlock(pos, false);
            }
            world.playEvent(2001, pos, Block.getStateId(state)); // play the block breaking event
        }
    }
}
