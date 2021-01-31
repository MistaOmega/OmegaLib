package mistaomega.lib.utility;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * @author MistaOmega
 * Class to handle functions that a VoxelShape manipulation may require such as shape combination and rotation
 * Created to make my v stressful life just a little less stressful
 * @since 02/06/20
 */
public class VoxelUtils
{
    private static final Vec3d vecFromOrigin = new Vec3d(-0.5, -0.5, -0.5);

    /**
     * This is for rotating the bounding box (NOT THE VOXEL SHAPE) to a specific side, using the Direction class
     *
     * @param axisAlignedBBToRotate the bounding box to rotate
     * @param side                  what side is the bounding box rotating towards
     * @return rotated bounding box.
     */
    public static AxisAlignedBB bbSideRotate(AxisAlignedBB axisAlignedBBToRotate, Direction side)
    {
        switch (side)
        {
            case NORTH:
                return new AxisAlignedBB(axisAlignedBBToRotate.minX, -axisAlignedBBToRotate.minZ, axisAlignedBBToRotate.minY, axisAlignedBBToRotate.maxX, -axisAlignedBBToRotate.maxZ, axisAlignedBBToRotate.maxY);
            case EAST:
                return new AxisAlignedBB(-axisAlignedBBToRotate.minY, axisAlignedBBToRotate.minZ, axisAlignedBBToRotate.minX, -axisAlignedBBToRotate.maxY, axisAlignedBBToRotate.maxZ, axisAlignedBBToRotate.maxX);
            case SOUTH:
                return new AxisAlignedBB(-axisAlignedBBToRotate.minX, axisAlignedBBToRotate.minZ, -axisAlignedBBToRotate.minY, -axisAlignedBBToRotate.maxX, axisAlignedBBToRotate.maxZ, -axisAlignedBBToRotate.maxY);
            case WEST:
                return new AxisAlignedBB(axisAlignedBBToRotate.minY, -axisAlignedBBToRotate.minZ, -axisAlignedBBToRotate.minX, axisAlignedBBToRotate.maxY, -axisAlignedBBToRotate.maxZ, -axisAlignedBBToRotate.maxX);
            case DOWN:
                return axisAlignedBBToRotate;
            case UP:
                return new AxisAlignedBB(axisAlignedBBToRotate.minX, -axisAlignedBBToRotate.minY, -axisAlignedBBToRotate.minZ, axisAlignedBBToRotate.maxX, -axisAlignedBBToRotate.maxY, -axisAlignedBBToRotate.maxZ);
        }
        return axisAlignedBBToRotate;
    }

    /**
     * This is for rotating a bounding axisAlignedBBToRotate through a specific rotation as opposed to block side
     *
     * @param axisAlignedBBToRotate bounding box to rotate {@link AxisAlignedBB}
     * @param rotation              Rotation ENUM that the bounding box will be rotated around
     * @return rotated BB {@link AxisAlignedBB}
     */
    public static AxisAlignedBB bbRotationRotate(AxisAlignedBB axisAlignedBBToRotate, Rotation rotation)
    {
        switch (rotation)
        {
            case NONE:
                return axisAlignedBBToRotate;
            case CLOCKWISE_90:
                //Positive x rotation, negative z rotation
                return new AxisAlignedBB(-axisAlignedBBToRotate.minZ, axisAlignedBBToRotate.minY, axisAlignedBBToRotate.minX, -axisAlignedBBToRotate.maxZ, axisAlignedBBToRotate.maxY, axisAlignedBBToRotate.maxX);
            case CLOCKWISE_180:
                //Negative z and x rotation
                return new AxisAlignedBB(-axisAlignedBBToRotate.minX, axisAlignedBBToRotate.minY, -axisAlignedBBToRotate.minZ, -axisAlignedBBToRotate.maxX, axisAlignedBBToRotate.maxY, -axisAlignedBBToRotate.maxZ);
            case COUNTERCLOCKWISE_90:
                //Positive z rotation, negative z rotation
                return new AxisAlignedBB(axisAlignedBBToRotate.minZ, axisAlignedBBToRotate.minY, -axisAlignedBBToRotate.minX, axisAlignedBBToRotate.maxZ, axisAlignedBBToRotate.maxY, -axisAlignedBBToRotate.maxX);
            //if you're reading this and you're about to say "Where's COUNTERCLOCKWISE_180", brother, I understand, I'll do anything I can to help <3
        }
        return axisAlignedBBToRotate;
    }

    public static VoxelShape shapeRotateSide(VoxelShape voxelShape, Direction side)
    {
        return rotateShapeByFunction(voxelShape, axisAlignedBB -> bbSideRotate(axisAlignedBB, side));
    }

    public static VoxelShape rotate(VoxelShape voxelShape, Rotation rotation)
    {
        return rotateShapeByFunction(voxelShape, axisAlignedBB -> bbRotationRotate(axisAlignedBB, rotation));
    }


    public static VoxelShape rotateShapeByFunction(VoxelShape voxelShape, UnaryOperator<AxisAlignedBB> alignedBBUnaryOperator)
    {
        List<VoxelShape> rotatedShapes = new ArrayList<>();

        List<AxisAlignedBB> boundingBoxes = voxelShape.toBoundingBoxList();
        for (AxisAlignedBB boundingBox : boundingBoxes)
        {
            rotatedShapes.add(VoxelShapes.create(alignedBBUnaryOperator.apply(boundingBox.offset(vecFromOrigin.x, vecFromOrigin.y, vecFromOrigin.z))
                    .offset(-vecFromOrigin.x, -vecFromOrigin.y, -vecFromOrigin.z)));
        }

        return combine(rotatedShapes);
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        int times = (to.getHorizontalIndex() - from.getHorizontalIndex() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(buffer[1], VoxelShapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape combine(VoxelShape... shapes)
    {
        return batchCombine(VoxelShapes.empty(), IBooleanFunction.OR, true, shapes);
    }

    public static VoxelShape combine(Collection<VoxelShape> shapes)
    {
        return combine(shapes, true);
    }

    public static VoxelShape combine(Collection<VoxelShape> shapes, boolean simplify)
    {
        return batchCombine(VoxelShapes.empty(), IBooleanFunction.OR, simplify, shapes);
    }

    /**
     * Does the batch combining operation on a voxelshape collection
     *
     * @param initial  inital shape
     * @param function Function to execute on rotation
     * @param simplify Simplify shape
     * @param shapes   A collection of VoxelShapes to combine
     * @return all shapes combined into one voxel shape
     */
    public static VoxelShape batchCombine(VoxelShape initial, IBooleanFunction function, boolean simplify, Collection<VoxelShape> shapes)
    {
        VoxelShape combinedShape = initial;
        for (VoxelShape shape : shapes)
        {
            combinedShape = VoxelShapes.combine(combinedShape, shape, function);
        }
        return simplify ? combinedShape.simplify() : combinedShape;
    }

    /**
     * Does the batch combining operation on a voxelshape vararg
     *
     * @param initial
     * @param function
     * @param simplify
     * @param shapes
     * @return
     */
    public static VoxelShape batchCombine(VoxelShape initial, IBooleanFunction function, boolean simplify, VoxelShape... shapes)
    {
        VoxelShape combinedShape = initial;
        for (VoxelShape shape : shapes)
        {
            combinedShape = VoxelShapes.combine(combinedShape, shape, function);
        }
        return simplify ? combinedShape.simplify() : combinedShape;
    }


    //TODO Add some toString statements for output clarification and debugging
    //TODO Implement sloping blocks for variable shapes
}
