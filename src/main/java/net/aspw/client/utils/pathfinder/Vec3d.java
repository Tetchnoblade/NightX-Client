package net.aspw.client.utils.pathfinder;

import net.minecraft.util.Vec3i;

public class Vec3d {
    public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);
    public final double xCoord;
    public final double yCoord;
    public final double zCoord;

    public Vec3d(double x, double y2, double z) {
        if (x == -0.0) {
            x = 0.0;
        }

        if (y2 == -0.0) {
            y2 = 0.0;
        }

        if (z == -0.0) {
            z = 0.0;
        }

        this.xCoord = x;
        this.yCoord = y2;
        this.zCoord = z;
    }

    public Vec3d(final Vec3i vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vec3d normalize() {
        final double d0 = Math.sqrt(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return d0 < 1.0E-4 ? ZERO : new Vec3d(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
    }

    public Vec3d subtract(final Vec3d vec) {
        return this.subtract(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vec3d subtract(final double x, final double y2, final double z) {
        return this.addVector(-x, -y2, -z);
    }

    public Vec3d add(final Vec3d vec) {
        return this.addVector(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vec3d addVector(final double x, final double y2, final double z) {
        return new Vec3d(this.xCoord + x, this.yCoord + y2, this.zCoord + z);
    }

    public double squareDistanceTo(final Vec3d vec) {
        final double d0 = vec.xCoord - this.xCoord;
        final double d2 = vec.yCoord - this.yCoord;
        final double d3 = vec.zCoord - this.zCoord;
        return d0 * d0 + d2 * d2 + d3 * d3;
    }

    public double squareDistanceTo(final double xIn, final double yIn, final double zIn) {
        final double d0 = xIn - this.xCoord;
        final double d2 = yIn - this.yCoord;
        final double d3 = zIn - this.zCoord;
        return d0 * d0 + d2 * d2 + d3 * d3;
    }

    public Vec3d scale(final double p_186678_1_) {
        return new Vec3d(this.xCoord * p_186678_1_, this.yCoord * p_186678_1_, this.zCoord * p_186678_1_);
    }

    @Override
    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof Vec3d)) {
            return false;
        }
        final Vec3d vec3d = (Vec3d) p_equals_1_;
        return Double.compare(vec3d.xCoord, this.xCoord) == 0 && Double.compare(vec3d.yCoord, this.yCoord) == 0
                && Double.compare(vec3d.zCoord, this.zCoord) == 0;
    }

    @Override
    public int hashCode() {
        long j = Double.doubleToLongBits(this.xCoord);
        int i = (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.yCoord);
        i = 31 * i + (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.zCoord);
        i = 31 * i + (int) (j ^ j >>> 32);
        return i;
    }

    @Override
    public String toString() {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    public Vec3d mul(final float mul) {
        return new Vec3d(xCoord * mul, yCoord * mul, zCoord * mul);
    }
}