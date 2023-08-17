package net.aspw.client.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

/**
 * The type World utils.
 */
public final class WorldUtils {

    private static final Minecraft MC = Minecraft.getMinecraft();

    /**
     * Is air boolean.
     *
     * @param blockPos the block pos
     * @return the boolean
     */
    public static boolean isAir(BlockPos blockPos) {
        return MC.theWorld.getBlockState(blockPos).getBlock() == Blocks.air;
    }

    /**
     * Distance to ground double.
     *
     * @return the double
     */
    public static double distanceToGround() {
        double playerY = MC.thePlayer.posY;
        return playerY - getBlockBellow().getY() - 1;
    }

    /**
     * Distance to ground double.
     *
     * @param vec3 the vec 3
     * @return the double
     */
    public static double distanceToGround(Vec3 vec3) {
        double playerY = vec3.yCoord;
        return playerY - getBlockBellow(vec3).getY() - 1;
    }

    /**
     * Gets block bellow.
     *
     * @param playerPos the player pos
     * @return the block bellow
     */
    public static BlockPos getBlockBellow(Vec3 playerPos) {
        for (; playerPos.yCoord > 0; playerPos = playerPos.addVector(0, -1, 0)) {
            final BlockPos blockPos = new BlockPos(playerPos);
            if (!(isAir(blockPos))) return blockPos;
        }
        return BlockPos.ORIGIN;
    }

    /**
     * Gets block bellow.
     *
     * @return the block bellow
     */
    public static BlockPos getBlockBellow() {
        Vec3 playerPos = MC.thePlayer.getPositionEyes(1.0F);
        for (; playerPos.yCoord > 0; playerPos = playerPos.addVector(0, -1, 0)) {
            final BlockPos blockPos = new BlockPos(playerPos);
            if (!isAir(blockPos)) return blockPos;
        }
        return BlockPos.ORIGIN;
    }

    /**
     * Is stand able boolean.
     *
     * @param blockPos the block pos
     * @return the boolean
     */
    public static boolean isStandAble(BlockPos blockPos) {
        return !canCollide(blockPos)
                && !canCollide(blockPos.add(0, 1, 0));
    }

    /**
     * Can collide boolean.
     *
     * @param blockPos the block pos
     * @return the boolean
     */
    public static boolean canCollide(BlockPos blockPos) {
        IBlockState blockState = MC.theWorld.getBlockState(blockPos);
        return blockState.getBlock().getCollisionBoundingBox(MC.theWorld, blockPos, blockState) != null;
    }

}