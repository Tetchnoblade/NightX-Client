package net.aspw.client.utils.pathfinder;

public class Vector {
    private final double x;
    private final double y;
    private final double z;

    public Vector(final double x, final double y2, final double z) {
        this.x = x;
        y = y2;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector addVector(final double x, final double y2, final double z) {
        return new Vector(this.x + x, y + y2, this.z + z);
    }

    public Vector floor() {
        return new Vector(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    public double squareDistanceTo(final Vector v) {
        return Math.pow(v.x - x, 2.0) + Math.pow(v.y - y, 2.0) + Math.pow(v.z - z, 2.0);
    }

    public Vector add(final Vector v) {
        return addVector(v.getX(), v.getY(), v.getZ());
    }

    public net.minecraft.util.Vec3 mc() {
        return new net.minecraft.util.Vec3(x, y, z);
    }

    @Override
    public String toString() {
        return "[" + x + ";" + y + ";" + z + "]";
    }
}