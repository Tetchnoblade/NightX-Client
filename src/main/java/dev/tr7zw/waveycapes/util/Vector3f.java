package dev.tr7zw.waveycapes.util;

import net.minecraft.util.MathHelper;

public final class Vector3f {

    public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);

    public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);

    public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);

    public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);

    public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);

    public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);

    public static Vector3f ZERO = new Vector3f(0.0F, 0.0F, 0.0F);

    private float x;

    private float y;

    private float z;

    public Vector3f() {
    }

    public Vector3f(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public Vector3f(Vector4f vector4f) {
        this(vector4f.x(), vector4f.y(), vector4f.z());
    }

    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Vector3f vector3f = (Vector3f) object;
        if (Float.compare(vector3f.x, this.x) != 0)
            return false;
        if (Float.compare(vector3f.y, this.y) != 0)
            return false;
        return (Float.compare(vector3f.z, this.z) == 0);
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        return i;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
    }

    public void mul(float f, float g, float h) {
        this.x *= f;
        this.y *= g;
        this.z *= h;
    }

    public void clamp(Vector3f vector3f, Vector3f vector3f2) {
        this.x = MathHelper.clamp_float(this.x, vector3f.x(), vector3f2.x());
        this.y = MathHelper.clamp_float(this.y, vector3f.x(), vector3f2.y());
        this.z = MathHelper.clamp_float(this.z, vector3f.z(), vector3f2.z());
    }

    public void clamp(float f, float g) {
        this.x = MathHelper.clamp_float(this.x, f, g);
        this.y = MathHelper.clamp_float(this.y, f, g);
        this.z = MathHelper.clamp_float(this.z, f, g);
    }

    public void set(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public void load(Vector3f vector3f) {
        this.x = vector3f.x;
        this.y = vector3f.y;
        this.z = vector3f.z;
    }

    public void add(float f, float g, float h) {
        this.x += f;
        this.y += g;
        this.z += h;
    }

    public void add(Vector3f vector3f) {
        this.x += vector3f.x;
        this.y += vector3f.y;
        this.z += vector3f.z;
    }

    public void sub(Vector3f vector3f) {
        this.x -= vector3f.x;
        this.y -= vector3f.y;
        this.z -= vector3f.z;
    }

    public float dot(Vector3f vector3f) {
        return this.x * vector3f.x + this.y * vector3f.y + this.z * vector3f.z;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if (f < 1.0E-5D)
            return false;
        float g = Mth.fastInvSqrt(f);
        this.x *= g;
        this.y *= g;
        this.z *= g;
        return true;
    }

    public void cross(Vector3f vector3f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = vector3f.x();
        float j = vector3f.y();
        float k = vector3f.z();
        this.x = g * k - h * j;
        this.y = h * i - f * k;
        this.z = f * j - g * i;
    }

//    public void transform(Matrix3f matrix3f) {
//        float f = this.x;
//        float g = this.y;
//        float h = this.z;
//        this.x = matrix3f.m00 * f + matrix3f.m01 * g + matrix3f.m02 * h;
//        this.y = matrix3f.m10 * f + matrix3f.m11 * g + matrix3f.m12 * h;
//        this.z = matrix3f.m20 * f + matrix3f.m21 * g + matrix3f.m22 * h;
//    }

    public void transform(Quaternion quaternion) {
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.mul(new Quaternion(x(), y(), z(), 0.0F));
        Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.conj();
        quaternion2.mul(quaternion3);
        set(quaternion2.i(), quaternion2.j(), quaternion2.k());
    }

    public void lerp(Vector3f vector3f, float f) {
        float g = 1.0F - f;
        this.x = this.x * g + vector3f.x * f;
        this.y = this.y * g + vector3f.y * f;
        this.z = this.z * g + vector3f.z * f;
    }

    public Quaternion rotation(float f) {
        return new Quaternion(this, f, false);
    }

    public Quaternion rotationDegrees(float f) {
        return new Quaternion(this, f, true);
    }

    public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }
}