package dev.tr7zw.waveycapes.util;

public class Vector4f {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4f() {
    }

    public Vector4f(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Vector4f vector4f = (Vector4f) object;
        if (Float.compare(vector4f.x, this.x) != 0)
            return false;
        if (Float.compare(vector4f.y, this.y) != 0)
            return false;
        if (Float.compare(vector4f.z, this.z) != 0)
            return false;
        return (Float.compare(vector4f.w, this.w) == 0);
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        i = 31 * i + Float.floatToIntBits(this.w);
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

    public float w() {
        return this.w;
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
    }

    public void set(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public void add(float f, float g, float h, float i) {
        this.x += f;
        this.y += g;
        this.z += h;
        this.w += i;
    }

    public float dot(Vector4f vector4f) {
        return this.x * vector4f.x + this.y * vector4f.y + this.z * vector4f.z + this.w * vector4f.w;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if (f < 1.0E-5D)
            return false;
        float g = Mth.fastInvSqrt(f);
        this.x *= g;
        this.y *= g;
        this.z *= g;
        this.w *= g;
        return true;
    }

    public void transform(Matrix4f matrix4f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = this.w;
        this.x = matrix4f.m00 * f + matrix4f.m01 * g + matrix4f.m02 * h + matrix4f.m03 * i;
        this.y = matrix4f.m10 * f + matrix4f.m11 * g + matrix4f.m12 * h + matrix4f.m13 * i;
        this.z = matrix4f.m20 * f + matrix4f.m21 * g + matrix4f.m22 * h + matrix4f.m23 * i;
        this.w = matrix4f.m30 * f + matrix4f.m31 * g + matrix4f.m32 * h + matrix4f.m33 * i;
    }

    public void transform(Quaternion quaternion) {
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.mul(new Quaternion(x(), y(), z(), 0.0F));
        Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.conj();
        quaternion2.mul(quaternion3);
        set(quaternion2.i(), quaternion2.j(), quaternion2.k(), w());
    }

    public void perspectiveDivide() {
        this.x /= this.w;
        this.y /= this.w;
        this.z /= this.w;
        this.w = 1.0F;
    }

    public void lerp(Vector4f vector4f, float f) {
        float g = 1.0F - f;
        this.x = this.x * g + vector4f.x * f;
        this.y = this.y * g + vector4f.y * f;
        this.z = this.z * g + vector4f.z * f;
        this.w = this.w * g + vector4f.w * f;
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }
}