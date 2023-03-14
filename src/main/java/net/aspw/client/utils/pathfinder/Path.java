package net.aspw.client.utils.pathfinder;

public class Path {

    private final double startX, startY, startZ;
    private final double endX, endY, endZ;

    public Path(final double startX, final double startY, final double startZ, final double endX, final double endY, final double endZ) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartZ() {
        return startZ;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public double getEndZ() {
        return endZ;
    }
}