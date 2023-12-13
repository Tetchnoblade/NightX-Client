package net.aspw.client.util;

import net.aspw.client.util.block.BlockUtils;
import net.minecraft.block.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

/**
 * The type A star custom pathfinder
 */
public class AStarCustomPathFinder {
    private static final Vec3[] flatCardinalDirections = {
            new Vec3(1, 0, 0),
            new Vec3(-1, 0, 0),
            new Vec3(0, 0, 1),
            new Vec3(0, 0, -1)
    };
    private final Vec3 startVec3;
    private final Vec3 endVec3;
    private final ArrayList<Hub> hubs = new ArrayList<>();
    private final ArrayList<Hub> hubsToWork = new ArrayList<>();
    private ArrayList<Vec3> path = new ArrayList<>();

    /**
     * Instantiates a new A star custom pathfinder.
     *
     * @param startVec3 the start vec 3
     * @param endVec3   the end vec 3
     */
    public AStarCustomPathFinder(Vec3 startVec3, Vec3 endVec3) {
        this.startVec3 = BlockUtils.floorVec3(startVec3.addVector(0, 0, 0));
        this.endVec3 = BlockUtils.floorVec3(endVec3.addVector(0, 0, 0));
    }

    /**
     * Check position validity boolean.
     *
     * @param loc         the loc
     * @param checkGround the check ground
     * @return the boolean
     */
    public static boolean checkPositionValidity(Vec3 loc, boolean checkGround) {
        return checkPositionValidity((int) loc.xCoord, (int) loc.yCoord, (int) loc.zCoord, checkGround);
    }

    /**
     * Check position validity boolean.
     *
     * @param x           the x
     * @param y           the y
     * @param z           the z
     * @param checkGround the check ground
     * @return the boolean
     */
    public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);
        return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
    }

    private static boolean isBlockSolid(BlockPos blockPos) {
        Block block = BlockUtils.getBlock(blockPos);
        if (block == null) return false;

        return block.isFullBlock() ||
                (block instanceof BlockSlab) ||
                (block instanceof BlockStairs) ||
                (block instanceof BlockCactus) ||
                (block instanceof BlockChest) ||
                (block instanceof BlockEnderChest) ||
                (block instanceof BlockSkull) ||
                (block instanceof BlockPane) ||
                (block instanceof BlockFence) ||
                (block instanceof BlockWall) ||
                (block instanceof BlockGlass) ||
                (block instanceof BlockPistonBase) ||
                (block instanceof BlockPistonExtension) ||
                (block instanceof BlockPistonMoving) ||
                (block instanceof BlockStainedGlass) ||
                (block instanceof BlockTrapDoor);
    }

    private static boolean isSafeToWalkOn(BlockPos blockPos) {
        Block block = BlockUtils.getBlock(blockPos);
        if (block == null) return false;

        return !(block instanceof BlockFence) &&
                !(block instanceof BlockWall);
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public ArrayList<Vec3> getPath() {
        return path;
    }

    /**
     * Compute.
     */
    public void compute() {
        compute(1000, 4);
    }

    /**
     * Compute.
     *
     * @param loops the loops
     * @param depth the depth
     */
    public void compute(int loops, int depth) {
        path.clear();
        hubsToWork.clear();
        ArrayList<Vec3> initPath = new ArrayList<>();
        initPath.add(startVec3);
        hubsToWork.add(new Hub(startVec3, initPath, startVec3.squareDistanceTo(endVec3), 0, 0));
        search:
        for (int i = 0; i < loops; i++) {
            hubsToWork.sort(new CompareHub());
            int j = 0;
            if (hubsToWork.size() == 0) {
                break;
            }
            for (Hub hub : new ArrayList<>(hubsToWork)) {
                j++;
                if (j > depth) {
                    break;
                } else {
                    hubsToWork.remove(hub);
                    hubs.add(hub);

                    for (Vec3 direction : flatCardinalDirections) {
                        Vec3 loc = BlockUtils.floorVec3(hub.getLoc().add(direction));
                        if (checkPositionValidity(loc, false)) {
                            if (addHub(hub, loc, 0)) {
                                break search;
                            }
                        }
                    }

                    Vec3 loc1 = BlockUtils.floorVec3(hub.getLoc().addVector(0, 1, 0));
                    if (checkPositionValidity(loc1, false)) {
                        if (addHub(hub, loc1, 0)) {
                            break search;
                        }
                    }

                    Vec3 loc2 = BlockUtils.floorVec3(hub.getLoc().addVector(0, -1, 0));
                    if (checkPositionValidity(loc2, false)) {
                        if (addHub(hub, loc2, 0)) {
                            break search;
                        }
                    }
                }
            }
        }
        hubs.sort(new CompareHub());
        path = hubs.get(0).getPath();
    }

    /**
     * Is hub existing hub.
     *
     * @param loc the loc
     * @return the hub
     */
    public Hub isHubExisting(Vec3 loc) {
        for (Hub hub : hubs) {
            if (hub.getLoc().xCoord == loc.xCoord && hub.getLoc().yCoord == loc.yCoord && hub.getLoc().zCoord == loc.zCoord) {
                return hub;
            }
        }
        for (Hub hub : hubsToWork) {
            if (hub.getLoc().xCoord == loc.xCoord && hub.getLoc().yCoord == loc.yCoord && hub.getLoc().zCoord == loc.zCoord) {
                return hub;
            }
        }
        return null;
    }

    /**
     * Add hub boolean.
     *
     * @param parent the parent
     * @param loc    the loc
     * @param cost   the cost
     * @return the boolean
     */
    public boolean addHub(Hub parent, Vec3 loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            double minDistanceSquared = 9;
            if (loc.xCoord == endVec3.xCoord && loc.yCoord == endVec3.yCoord && loc.zCoord == endVec3.zCoord || loc.squareDistanceTo(endVec3) <= minDistanceSquared) {
                path.clear();
                path = Objects.requireNonNull(parent).getPath();
                path.add(loc);
                return true;
            } else {
                ArrayList<Vec3> path = new ArrayList<>(Objects.requireNonNull(parent).getPath());
                path.add(loc);
                hubsToWork.add(new Hub(loc, path, loc.squareDistanceTo(endVec3), cost, totalCost));
            }
        } else if (existingHub.getCost() > cost) {
            ArrayList<Vec3> path = new ArrayList<>(Objects.requireNonNull(parent).getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent();
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    private static class Hub {
        private Vec3 loc;
        private ArrayList<Vec3> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        /**
         * Instantiates a new Hub.
         *
         * @param loc                        the loc
         * @param path                       the path
         * @param squareDistanceToFromTarget the square distance to from target
         * @param cost                       the cost
         * @param totalCost                  the total cost
         */
        public Hub(Vec3 loc, ArrayList<Vec3> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        /**
         * Gets loc.
         *
         * @return the loc
         */
        public Vec3 getLoc() {
            return loc;
        }

        /**
         * Sets loc.
         *
         * @param loc the loc
         */
        public void setLoc(Vec3 loc) {
            this.loc = loc;
        }

        /**
         * Sets parent.
         */
        public void setParent() {
        }

        /**
         * Gets path.
         *
         * @return the path
         */
        public ArrayList<Vec3> getPath() {
            return path;
        }

        /**
         * Sets path.
         *
         * @param path the path
         */
        public void setPath(ArrayList<Vec3> path) {
            this.path = path;
        }

        /**
         * Gets square distance to from target.
         *
         * @return the square distance to from target
         */
        public double getSquareDistanceToFromTarget() {
            return squareDistanceToFromTarget;
        }

        /**
         * Sets square distance to from target.
         *
         * @param squareDistanceToFromTarget the square distance to from target
         */
        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        /**
         * Gets cost.
         *
         * @return the cost
         */
        public double getCost() {
            return cost;
        }

        /**
         * Sets cost.
         *
         * @param cost the cost
         */
        public void setCost(double cost) {
            this.cost = cost;
        }

        /**
         * Gets total cost.
         *
         * @return the total cost
         */
        public double getTotalCost() {
            return totalCost;
        }

        /**
         * Sets total cost.
         *
         * @param totalCost the total cost
         */
        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }

    /**
     * The type Compare hub.
     */
    public static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int) (
                    (o1.getSquareDistanceToFromTarget() + o1.getTotalCost()) - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost())
            );
        }
    }
}

