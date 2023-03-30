package net.aspw.client.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public final class WorldUtils {

    private static final Minecraft MC = Minecraft.getMinecraft();

    /**
     * @param blockPos blockPos
     * @return Whether the given BlockPos location is Air.
     * @author Eternal
     */
    public static boolean isAir(BlockPos blockPos) {
        return MC.theWorld.getBlockState(blockPos).getBlock() == Blocks.air;
    }

    /**
     * @return The distance between the player's feet position and the ground
     * @author Eternal
     */
    public static double distanceToGround() {
        double playerY = MC.thePlayer.posY;
        return playerY - getBlockBellow().getY() - 1;
    }

    public static double distanceToGround(Vec3 vec3) {
        double playerY = vec3.yCoord;
        return playerY - getBlockBellow(vec3).getY() - 1;
    }

    /**
     * @return The closest {@link BlockPos} to the entities y position.<br>
     * If no {@link Block} is found it returns {@link BlockPos#ORIGIN}
     * @author Eternal
     */
    public static BlockPos getBlockBellow(Vec3 playerPos) {
        for (; playerPos.yCoord > 0; playerPos = playerPos.addVector(0, -1, 0)) {
            final BlockPos blockPos = new BlockPos(playerPos);
            if (!(isAir(blockPos))) return blockPos;
        }
        return BlockPos.ORIGIN;
    }

    /**
     * @return The closest blockPos to the player's y position
     * or if the Player is above the void it returns `BlockPos.ORIGIN`
     * @author Eternal
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
     * @param blockPos
     * @return Whether the Player can stand at the given BlockPos.
     * @author Eternal
     */
    public static boolean isStandAble(BlockPos blockPos) {
        return !canCollide(blockPos)
                && !canCollide(blockPos.add(0, 1, 0));
    }

    /**
     * @param blockPos
     * @return Whether the given BlockPos location can be collided with.
     * @author Eternal
     */
    public static boolean canCollide(BlockPos blockPos) {
        IBlockState blockState = MC.theWorld.getBlockState(blockPos);
        return blockState.getBlock().getCollisionBoundingBox(MC.theWorld, blockPos, blockState) != null;
    }

}