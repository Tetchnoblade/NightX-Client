package net.aspw.client.utils.pathfinder;

import net.minecraft.block.Block;

public class PathFinderBlockInfo {

    private final double height;
    private final Block block;

    public PathFinderBlockInfo(final double height, final Block block) {
        this.height = height;
        this.block = block;
    }

    public double getHeight() {
        return height;
    }

    public Block getBlock() {
        return block;
    }
}