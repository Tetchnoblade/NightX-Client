package net.aspw.client.utils.pathfinder;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;

public class MainPathFinder {
    private final Vec3 startVec3;
    private final Vec3 endVec3;
    private ArrayList<Vec3> path = new ArrayList();
    private final ArrayList<PathHub> pathHubs = new ArrayList();
    private final ArrayList<PathHub> workingPathHubList = new ArrayList();
    private static final Vec3[] directions = new Vec3[]{new Vec3(1.0, 0.0, 0.0), new Vec3(-1.0, 0.0, 0.0),
            new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 0.0, -1.0)};

    public MainPathFinder(final Vec3 startVec3, final Vec3 endVec3) {
        this.startVec3 = startVec3.addVector(0.0, 0.0, 0.0).floor();
        this.endVec3 = endVec3.addVector(0.0, 0.0, 0.0).floor();
    }

    public ArrayList<Vec3> getPath() {
        return this.path;
    }

    public void compute(final int loops, final int depth) {
        this.path.clear();
        this.workingPathHubList.clear();

        final ArrayList<Vec3> initPath = new ArrayList<>();
        initPath.add(this.startVec3);

        this.workingPathHubList
                .add(new PathHub(this.startVec3, null, initPath, this.startVec3.squareDistanceTo(this.endVec3), 0.0, 0.0));

        block0:
        for (int i = 0; i < loops; ++i) {
            this.workingPathHubList.sort(new CompareHub());
            int j = 0;

            if (this.workingPathHubList.size() == 0) {
                break;
            }

            for (final PathHub pathHub : new ArrayList<>(this.workingPathHubList)) {
                final Vec3 loc2;

                if (++j > depth) {
                    continue block0;
                }

                this.workingPathHubList.remove(pathHub);
                this.pathHubs.add(pathHub);

                for (final Vec3 direction : directions) {
                    final Vec3 loc = pathHub.getLoc().add(direction).floor();
                    if (isValid(loc, false) && this.putHub(pathHub, loc, 0.0)) {
                        break block0;
                    }
                }

                final Vec3 loc1 = pathHub.getLoc().addVector(0.0, 1.0, 0.0).floor();
                if (isValid(loc1, false) && this.putHub(pathHub, loc1, 0.0)
                        || isValid(loc2 = pathHub.getLoc().addVector(0.0, -1.0, 0.0).floor(), false)
                        && this.putHub(pathHub, loc2, 0.0)) {
                    break block0;
                }
            }
        }

        this.pathHubs.sort(new CompareHub());
        this.path = this.pathHubs.get(0).getPathway();
    }

    public static boolean isValid(final Vec3 loc, final boolean checkGround) {
        return isValid((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(),
                checkGround);
    }

    public static boolean isValid(final int x, final int y, final int z, final boolean checkGround) {
        final BlockPos block1 = new BlockPos(x, y, z);
        final BlockPos block2 = new BlockPos(x, y + 1, z);
        final BlockPos block3 = new BlockPos(x, y - 1, z);
        return !isNotPassable(block1) && !isNotPassable(block2)
                && (isNotPassable(block3) || !checkGround)
                && canWalkOn(block3);
    }

    private static boolean isNotPassable(final BlockPos block) {
        final Block b = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock();

        return b.isFullBlock()
                || b instanceof BlockSlab
                || b instanceof BlockStairs
                || b instanceof BlockCactus
                || b instanceof BlockChest
                || b instanceof BlockEnderChest
                || b instanceof BlockSkull
                || b instanceof BlockPane
                || b instanceof BlockFence
                || b instanceof BlockWall
                || b instanceof BlockGlass
                || b instanceof BlockPistonBase
                || b instanceof BlockPistonExtension
                || b instanceof BlockPistonMoving
                || b instanceof BlockStainedGlass
                || b instanceof BlockTrapDoor
                || b instanceof BlockEndPortalFrame
                || b instanceof BlockEndPortal
                || b instanceof BlockBed
                || b instanceof BlockWeb
                || b instanceof BlockBarrier
                || b instanceof BlockLadder
                || b instanceof BlockCarpet;
    }

    private static boolean canWalkOn(final BlockPos block) {
        return !(Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(block.getX(), block.getY(),
                block.getZ())).getBlock() instanceof BlockFence)
                && !(Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(block.getX(), block.getY(),
                block.getZ())).getBlock() instanceof BlockWall);
    }

    public PathHub doesHubExistAt(final Vec3 loc) {
        for (final PathHub pathHub : this.pathHubs) {
            if (pathHub.getLoc().getX() != loc.getX() || pathHub.getLoc().getY() != loc.getY()
                    || pathHub.getLoc().getZ() != loc.getZ()) {
                continue;
            }
            return pathHub;
        }

        for (final PathHub pathHub : this.workingPathHubList) {
            if (pathHub.getLoc().getX() != loc.getX() || pathHub.getLoc().getY() != loc.getY()
                    || pathHub.getLoc().getZ() != loc.getZ()) {
                continue;
            }
            return pathHub;
        }
        return null;
    }

    public boolean putHub(final PathHub parent, final Vec3 loc, final double cost) {
        final PathHub existingPathHub = this.doesHubExistAt(loc);
        double totalCost = cost;

        if (parent != null) {
            totalCost += parent.getMaxCost();
        }

        if (existingPathHub == null) {
            final double minDistanceSquared = 9.5;
            if (loc.getX() == this.endVec3.getX() && loc.getY() == this.endVec3.getY() && loc.getZ() == this.endVec3.getZ() || loc.squareDistanceTo(this.endVec3) <= minDistanceSquared) {
                this.path.clear();
                this.path = parent.getPathway();
                this.path.add(loc);
                return true;
            }

            final ArrayList<Vec3> path = new ArrayList<>(parent.getPathway());
            path.add(loc);

            this.workingPathHubList.add(new PathHub(loc, parent, path, loc.squareDistanceTo(this.endVec3), cost, totalCost));
        } else if (existingPathHub.getCurrentCost() > cost) {
            final ArrayList<Vec3> path = new ArrayList<>(parent.getPathway());
            path.add(loc);
            existingPathHub.setLoc(loc);
            existingPathHub.setParentPathHub(parent);
            existingPathHub.setPathway(path);
            existingPathHub.setSqDist(loc.squareDistanceTo(this.endVec3));
            existingPathHub.setCurrentCost(cost);
            existingPathHub.setMaxCost(totalCost);
        }
        return false;
    }

    public static boolean canPassThrough(final BlockPos pos) {
        final Block block = Minecraft.getMinecraft().theWorld
                .getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants
                || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water
                || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    public static ArrayList<Vec3> computePath(Vec3 topFrom, final Vec3 to) {
        if (!canPassThrough(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0.0, 1.0, 0.0);
        }

        final PathFinder pathfinder = new PathFinder(topFrom, to);
        pathfinder.compute();
        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        final ArrayList<Vec3> path = new ArrayList<>();
        final ArrayList<Vec3> pathFinderPath = pathfinder.getPath();

        for (final Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0.0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > 5 * 5) {
                    canContinue = false;
                } else {
                    final double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    final double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    final double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    final double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    final double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    final double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    int x = (int) smallX;
                    block1:
                    while (x <= bigX) {
                        int y2 = (int) smallY;
                        while (y2 <= bigY) {
                            int z = (int) smallZ;
                            while (z <= bigZ) {
                                if (!isValid(x, y2, z, false)) {
                                    canContinue = false;
                                    break block1;
                                }
                                ++z;
                            }
                            ++y2;
                        }
                        ++x;
                    }
                }

                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            ++i;
        }
        return path;
    }

    public static class CompareHub implements Comparator<PathHub> {
        @Override
        public int compare(final PathHub o1, final PathHub o2) {
            return (int) (o1.getSqDist() + o1.getMaxCost()
                    - (o2.getSqDist() + o2.getMaxCost()));
        }
    }
}