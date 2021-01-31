package mistaomega.lib.items;

import com.google.common.collect.ImmutableSet;
import mistaomega.lib.utility.ToolUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Base class for all power using pickaxes, this rebuilds most of the "PickaxeItem" class which isn't ideal, but is required
 * Should world well enough, it seems to behave well enough anyhow
 */
public class PowerPickaxeBase extends PowerToolBase
{

    private static final Set<Block> EFFECTIVE_PICKAXE = ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX);
    private int range; // Range from the center block in each direction, 1 will do a 3x3, 2 will do a 5x5
    private int depth; // how many blocks deep
    private int rangeY; // height of mine
    private int perBlockPowerReq; // power to break 1 block, this is multiplied by each block hardness

    protected PowerPickaxeBase(float attackDamageIn, float attackSpeedIn, IItemTier tier, Properties builder)
    {
        super(attackDamageIn, attackSpeedIn, tier, EFFECTIVE_PICKAXE, builder);
        setItemEnergyStats(100000, 100);
    }

    public void setStats(int range, int perBlockPowerReq)
    {
        this.range = range;
        this.depth = 2 * range;
        this.rangeY = Math.max(1, range);
        this.perBlockPowerReq = perBlockPowerReq;
    }

    //region Basic Pickaxe business

    /**
     * Check whether this Item can harvest the given Block
     */
    public boolean canHarvestBlock(BlockState blockIn)
    {

        Block block = blockIn.getBlock();
        int i = this.getTier().getHarvestLevel();
        if (blockIn.getHarvestTool() == net.minecraftforge.common.ToolType.PICKAXE)
        {
            return i >= blockIn.getHarvestLevel();
        }
        Material material = blockIn.getMaterial();

        return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;

    }


    public float getDestroySpeed(ItemStack stack, BlockState state)
    {
        Material material = state.getMaterial();
        return material != Material.IRON && material != Material.ANVIL && material != Material.ROCK ? super.getDestroySpeed(stack, state) : this.efficiency;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
    {
        return 0;
    }


    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player)
    {
        if (!powerRequirement(itemstack, pos, player.world))
        {
            return true;
        }
        World world = player.world;
        BlockRayTraceResult rayTraceResult = ToolUtilities.raytraceFromEntity(player, 5, false);
        if (!player.world.isRemote && rayTraceResult.getType() == RayTraceResult.Type.BLOCK)
        { // we be breaking blocks in the piece
            Direction blockFace = rayTraceResult.getFace(); // Got the direction of where I'm looking, saves subscribing to the player interact event
            multiBlockBreak(player, itemstack, pos, blockFace, world);
        }
        return false;
    }

    //endregion

    /**
     * Handles the breaking of other blocks
     *
     * @param player Player who initated break
     * @param stack  Item used
     * @param pos    Block position
     * @param side   What face is the block broken from
     */
    public void multiBlockBreak(PlayerEntity player, ItemStack stack, BlockPos pos, Direction side, World world)
    {
        if (world.isAirBlock(pos))
        { // we need to make sure it's not air
            return;
        }

        if (world.isAirBlock(pos))
        {
            return;
        }

        boolean doX = side.getXOffset() == 0;
        boolean doY = side.getYOffset() == 0;
        boolean doZ = side.getZOffset() == 0;


        //deciding whether to handle as range or block depth depending on the faces hit
        Vec3i beginDiff = new Vec3i(doX ? -range : -depth, doY ? -1 : -depth, doZ ? -range : -depth);
        Vec3i endDiff = new Vec3i(doX ? range : depth, doY ? rangeY * 2 - 1 : depth, doZ ? range : depth);

        int total = calculateBreakPowerCost(pos, beginDiff, endDiff, world);
        if (getEnergyStored(stack) >= total)
        {
            extractEnergy(stack, total, false);
            System.out.println(getEnergyStored(stack));
            ToolUtilities.iterateAndRemove(player, stack, world, pos, beginDiff, endDiff, false);
        }
    }

    /**
     * Does the item have enough power to break block pos
     *
     * @param itemStack Item to check
     * @param pos       block position
     * @param world     world the check is occuring in
     * @return true if there's enough power in the tool
     */
    private boolean powerRequirement(ItemStack itemStack, BlockPos pos, World world)
    {
        return !(getEnergyStored(itemStack) < world.getBlockState(pos).getBlockHardness(world, pos) * perBlockPowerReq);
    }

    public int calculateBreakPowerCost(BlockPos centerBlockPos, Vec3i startVector, Vec3i endVector, World world)
    {
        int total = 0;
        for (BlockPos currentBlockPos : BlockPos.getAllInBoxMutable(centerBlockPos.add(startVector), centerBlockPos.add(endVector)))
        {
            if (currentBlockPos.equals(centerBlockPos))
            {
                continue;
            }
            int hardness = (int) world.getBlockState(currentBlockPos).getBlockHardness(world, currentBlockPos);
            total += (hardness * perBlockPowerReq);
        }
        System.out.println(total);
        return total;
    }

}
