package dev.tr7zw.waveycapes.util;

import java.nio.FloatBuffer;

public final class Matrix3f {

    protected float m00;

    protected float m01;

    protected float m02;

    protected float m10;

    protected float m11;

    protected float m12;

    protected float m20;

    protected float m21;

    protected float m22;

    public Matrix3f() {
    }

    public Matrix3f(Quaternion quaternion) {
        float f = quaternion.i();
        float g = quaternion.j();
        float h = quaternion.k();
        float i = quaternion.r();
        float j = 2.0F * f * f;
        float k = 2.0F * g * g;
        float l = 2.0F * h * h;
        this.m00 = 1.0F - k - l;
        this.m11 = 1.0F - l - j;
        this.m22 = 1.0F - j - k;
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
        this.m10 = 2.0F * (m + r);
        this.m01 = 2.0F * (m - r);
        this.m20 = 2.0F * (o - q);
        this.m02 = 2.0F * (o + q);
        this.m21 = 2.0F * (n + p);
        this.m12 = 2.0F * (n - p);
    }

    public static Matrix3f createScaleMatrix(float f, float g, float h) {
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.m00 = f;
        matrix3f.m11 = g;
        matrix3f.m22 = h;
        return matrix3f;
    }

    public Matrix3f(Matrix4f matrix4f) {
        this.m00 = matrix4f.m00;
        this.m01 = matrix4f.m01;
        this.m02 = matrix4f.m02;
        this.m10 = matrix4f.m10;
        this.m11 = matrix4f.m11;
        this.m12 = matrix4f.m12;
        this.m20 = matrix4f.m20;
        this.m21 = matrix4f.m21;
        this.m22 = matrix4f.m22;
    }

    public Matrix3f(Matrix3f matrix3f) {
        this.m00 = matrix3f.m00;
        this.m01 = matrix3f.m01;
        this.m02 = matrix3f.m02;
        this.m10 = matrix3f.m10;
        this.m11 = matrix3f.m11;
        this.m12 = matrix3f.m12;
        this.m20 = matrix3f.m20;
        this.m21 = matrix3f.m21;
        this.m22 = matrix3f.m22;
    }

    public void transpose() {
        float f = this.m01;
        this.m01 = this.m10;
        this.m10 = f;
        f = this.m02;
        this.m02 = this.m20;
        this.m20 = f;
        f = this.m12;
        this.m12 = this.m21;
        this.m21 = f;
    }

    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Matrix3f matrix3f = (Matrix3f) object;
        return (Float.compare(matrix3f.m00, this.m00) == 0 && Float.compare(matrix3f.m01, this.m01) == 0
                && Float.compare(matrix3f.m02, this.m02) == 0 && Float.compare(matrix3f.m10, this.m10) == 0
                && Float.compare(matrix3f.m11, this.m11) == 0 && Float.compare(matrix3f.m12, this.m12) == 0
                && Float.compare(matrix3f.m20, this.m20) == 0 && Float.compare(matrix3f.m21, this.m21) == 0
                && Float.compare(matrix3f.m22, this.m22) == 0);
    }

    public int hashCode() {
        int i = (this.m00 != 0.0F) ? Float.floatToIntBits(this.m00) : 0;
        i = 31 * i + ((this.m01 != 0.0F) ? Float.floatToIntBits(this.m01) : 0);
        i = 31 * i + ((this.m02 != 0.0F) ? Float.floatToIntBits(this.m02) : 0);
        i = 31 * i + ((this.m10 != 0.0F) ? Float.floatToIntBits(this.m10) : 0);
        i = 31 * i + ((this.m11 != 0.0F) ? Float.floatToIntBits(this.m11) : 0);
        i = 31 * i + ((this.m12 != 0.0F) ? Float.floatToIntBits(this.m12) : 0);
        i = 31 * i + ((this.m20 != 0.0F) ? Float.floatToIntBits(this.m20) : 0);
        i = 31 * i + ((this.m21 != 0.0F) ? Float.floatToIntBits(this.m21) : 0);
        i = 31 * i + ((this.m22 != 0.0F) ? Float.floatToIntBits(this.m22) : 0);
        return i;
    }

    private static int bufferIndex(int i, int j) {
        return j * 3 + i;
    }

    public void load(FloatBuffer floatBuffer) {
        this.m00 = floatBuffer.get(bufferIndex(0, 0));
        this.m01 = floatBuffer.get(bufferIndex(0, 1));
        this.m02 = floatBuffer.get(bufferIndex(0, 2));
        this.m10 = floatBuffer.get(bufferIndex(1, 0));
        this.m11 = floatBuffer.get(bufferIndex(1, 1));
        this.m12 = floatBuffer.get(bufferIndex(1, 2));
        this.m20 = floatBuffer.get(bufferIndex(2, 0));
        this.m21 = floatBuffer.get(bufferIndex(2, 1));
        this.m22 = floatBuffer.get(bufferIndex(2, 2));
    }

    public void loadTransposed(FloatBuffer floatBuffer) {
        this.m00 = floatBuffer.get(bufferIndex(0, 0));
        this.m01 = floatBuffer.get(bufferIndex(1, 0));
        this.m02 = floatBuffer.get(bufferIndex(2, 0));
        this.m10 = floatBuffer.get(bufferIndex(0, 1));
        this.m11 = floatBuffer.get(bufferIndex(1, 1));
        this.m12 = floatBuffer.get(bufferIndex(2, 1));
        this.m20 = floatBuffer.get(bufferIndex(0, 2));
        this.m21 = floatBuffer.get(bufferIndex(1, 2));
        this.m22 = floatBuffer.get(bufferIndex(2, 2));
    }

    public void load(FloatBuffer floatBuffer, boolean bl) {
        if (bl) {
            loadTransposed(floatBuffer);
        } else {
            load(floatBuffer);
        }
    }

    public void load(Matrix3f matrix3f) {
        this.m00 = matrix3f.m00;
        this.m01 = matrix3f.m01;
        this.m02 = matrix3f.m02;
        this.m10 = matrix3f.m10;
        this.m11 = matrix3f.m11;
        this.m12 = matrix3f.m12;
        this.m20 = matrix3f.m20;
        this.m21 = matrix3f.m21;
        this.m22 = matrix3f.m22;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Matrix3f:\n");
        stringBuilder.append(this.m00);
        stringBuilder.append(" ");
        stringBuilder.append(this.m01);
        stringBuilder.append(" ");
        stringBuilder.append(this.m02);
        stringBuilder.append("\n");
        stringBuilder.append(this.m10);
        stringBuilder.append(" ");
        stringBuilder.append(this.m11);
        stringBuilder.append(" ");
        stringBuilder.append(this.m12);
        stringBuilder.append("\n");
        stringBuilder.append(this.m20);
        stringBuilder.append(" ");
        stringBuilder.append(this.m21);
        stringBuilder.append(" ");
        stringBuilder.append(this.m22);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public void store(FloatBuffer floatBuffer) {
        floatBuffer.put(bufferIndex(0, 0), this.m00);
        floatBuffer.put(bufferIndex(0, 1), this.m01);
        floatBuffer.put(bufferIndex(0, 2), this.m02);
        floatBuffer.put(bufferIndex(1, 0), this.m10);
        floatBuffer.put(bufferIndex(1, 1), this.m11);
        floatBuffer.put(bufferIndex(1, 2), this.m12);
        floatBuffer.put(bufferIndex(2, 0), this.m20);
        floatBuffer.put(bufferIndex(2, 1), this.m21);
        floatBuffer.put(bufferIndex(2, 2), this.m22);
    }

    public void storeTransposed(FloatBuffer floatBuffer) {
        floatBuffer.put(bufferIndex(0, 0), this.m00);
        floatBuffer.put(bufferIndex(1, 0), this.m01);
        floatBuffer.put(bufferIndex(2, 0), this.m02);
        floatBuffer.put(bufferIndex(0, 1), this.m10);
        floatBuffer.put(bufferIndex(1, 1), this.m11);
        floatBuffer.put(bufferIndex(2, 1), this.m12);
        floatBuffer.put(bufferIndex(0, 2), this.m20);
        floatBuffer.put(bufferIndex(1, 2), this.m21);
        floatBuffer.put(bufferIndex(2, 2), this.m22);
    }

    public void store(FloatBuffer floatBuffer, boolean bl) {
        if (bl) {
            storeTransposed(floatBuffer);
        } else {
            store(floatBuffer);
        }
    }

    public void setIdentity() {
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
    }

    public float adjugateAndDet() {
        float f = this.m11 * this.m22 - this.m12 * this.m21;
        float g = -(this.m10 * this.m22 - this.m12 * this.m20);
        float h = this.m10 * this.m21 - this.m11 * this.m20;
        float i = -(this.m01 * this.m22 - this.m02 * this.m21);
        float j = this.m00 * this.m22 - this.m02 * this.m20;
        float k = -(this.m00 * this.m21 - this.m01 * this.m20);
        float l = this.m01 * this.m12 - this.m02 * this.m11;
        float m = -(this.m00 * this.m12 - this.m02 * this.m10);
        float n = this.m00 * this.m11 - this.m01 * this.m10;
        float o = this.m00 * f + this.m01 * g + this.m02 * h;
        this.m00 = f;
        this.m10 = g;
        this.m20 = h;
        this.m01 = i;
        this.m11 = j;
        this.m21 = k;
        this.m02 = l;
        this.m12 = m;
        this.m22 = n;
        return o;
    }

    public float determinant() {
        float f = this.m11 * this.m22 - this.m12 * this.m21;
        float g = -(this.m10 * this.m22 - this.m12 * this.m20);
        float h = this.m10 * this.m21 - this.m11 * this.m20;
        return this.m00 * f + this.m01 * g + this.m02 * h;
    }

    public boolean invert() {
        float f = adjugateAndDet();
        if (Math.abs(f) > 1.0E-6F) {
            mul(f);
            return true;
        }
        return false;
    }

    public void set(int i, int j, float f) {
        if (i == 0) {
            if (j == 0) {
                this.m00 = f;
            } else if (j == 1) {
                this.m01 = f;
            } else {
                this.m02 = f;
            }
        } else if (i == 1) {
            if (j == 0) {
                this.m10 = f;
            } else if (j == 1) {
                this.m11 = f;
            } else {
                this.m12 = f;
            }
        } else if (j == 0) {
            this.m20 = f;
        } else if (j == 1) {
            this.m21 = f;
        } else {
            this.m22 = f;
        }
    }

    public void mul(Matrix3f matrix3f) {
        float f = this.m00 * matrix3f.m00 + this.m01 * matrix3f.m10 + this.m02 * matrix3f.m20;
        float g = this.m00 * matrix3f.m01 + this.m01 * matrix3f.m11 + this.m02 * matrix3f.m21;
        float h = this.m00 * matrix3f.m02 + this.m01 * matrix3f.m12 + this.m02 * matrix3f.m22;
        float i = this.m10 * matrix3f.m00 + this.m11 * matrix3f.m10 + this.m12 * matrix3f.m20;
        float j = this.m10 * matrix3f.m01 + this.m11 * matrix3f.m11 + this.m12 * matrix3f.m21;
        float k = this.m10 * matrix3f.m02 + this.m11 * matrix3f.m12 + this.m12 * matrix3f.m22;
        float l = this.m20 * matrix3f.m00 + this.m21 * matrix3f.m10 + this.m22 * matrix3f.m20;
        float m = this.m20 * matrix3f.m01 + this.m21 * matrix3f.m11 + this.m22 * matrix3f.m21;
        float n = this.m20 * matrix3f.m02 + this.m21 * matrix3f.m12 + this.m22 * matrix3f.m22;
        this.m00 = f;
        this.m01 = g;
        this.m02 = h;
        this.m10 = i;
        this.m11 = j;
        this.m12 = k;
        this.m20 = l;
        this.m21 = m;
        this.m22 = n;
    }

    public void mul(Quaternion quaternion) {
        mul(new Matrix3f(quaternion));
    }

    public void mul(float f) {
        this.m00 *= f;
        this.m01 *= f;
        this.m02 *= f;
        this.m10 *= f;
        this.m11 *= f;
        this.m12 *= f;
        this.m20 *= f;
        this.m21 *= f;
        this.m22 *= f;
    }

    public void add(Matrix3f matrix3f) {
        this.m00 += matrix3f.m00;
        this.m01 += matrix3f.m01;
        this.m02 += matrix3f.m02;
        this.m10 += matrix3f.m10;
        this.m11 += matrix3f.m11;
        this.m12 += matrix3f.m12;
        this.m20 += matrix3f.m20;
        this.m21 += matrix3f.m21;
        this.m22 += matrix3f.m22;
    }

    public void sub(Matrix3f matrix3f) {
        this.m00 -= matrix3f.m00;
        this.m01 -= matrix3f.m01;
        this.m02 -= matrix3f.m02;
        this.m10 -= matrix3f.m10;
        this.m11 -= matrix3f.m11;
        this.m12 -= matrix3f.m12;
        this.m20 -= matrix3f.m20;
        this.m21 -= matrix3f.m21;
        this.m22 -= matrix3f.m22;
    }

    public float trace() {
        return this.m00 + this.m11 + this.m22;
    }

    public Matrix3f copy() {
        return new Matrix3f(this);
    }
}