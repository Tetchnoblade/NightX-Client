package dev.tr7zw.waveycapes.util;

public final class Quaternion {
    public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);

    private float i;

    private float j;

    private float k;

    private float r;

    public Quaternion(float f, float g, float h, float i) {
        this.i = f;
        this.j = g;
        this.k = h;
        this.r = i;
    }

    public Quaternion(Vector3f vector3f, float f, boolean bl) {
        if (bl)
            f *= 0.017453292F;
        float g = sin(f / 2.0F);
        this.i = vector3f.x() * g;
        this.j = vector3f.y() * g;
        this.k = vector3f.z() * g;
        this.r = cos(f / 2.0F);
    }

    public Quaternion(float f, float g, float h, boolean bl) {
        if (bl) {
            f *= 0.017453292F;
            g *= 0.017453292F;
            h *= 0.017453292F;
        }
        float i = sin(0.5F * f);
        float j = cos(0.5F * f);
        float k = sin(0.5F * g);
        float l = cos(0.5F * g);
        float m = sin(0.5F * h);
        float n = cos(0.5F * h);
        this.i = i * l * n + j * k * m;
        this.j = j * k * n - i * l * m;
        this.k = i * k * n + j * l * m;
        this.r = j * l * n - i * k * m;
    }

    public Quaternion(Quaternion quaternion) {
        this.i = quaternion.i;
        this.j = quaternion.j;
        this.k = quaternion.k;
        this.r = quaternion.r;
    }

    public static Quaternion fromYXZ(float f, float g, float h) {
        Quaternion quaternion = ONE.copy();
        quaternion.mul(new Quaternion(0.0F, (float) Math.sin((f / 2.0F)), 0.0F, (float) Math.cos((f / 2.0F))));
        quaternion.mul(new Quaternion((float) Math.sin((g / 2.0F)), 0.0F, 0.0F, (float) Math.cos((g / 2.0F))));
        quaternion.mul(new Quaternion(0.0F, 0.0F, (float) Math.sin((h / 2.0F)), (float) Math.cos((h / 2.0F))));
        return quaternion;
    }

    public static Quaternion fromXYZDegrees(Vector3f vector3f) {
        return fromXYZ((float) Math.toRadians(vector3f.x()), (float) Math.toRadians(vector3f.y()),
                (float) Math.toRadians(vector3f.z()));
    }

    public static Quaternion fromXYZ(Vector3f vector3f) {
        return fromXYZ(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public static Quaternion fromXYZ(float f, float g, float h) {
        Quaternion quaternion = ONE.copy();
        quaternion.mul(new Quaternion((float) Math.sin((f / 2.0F)), 0.0F, 0.0F, (float) Math.cos((f / 2.0F))));
        quaternion.mul(new Quaternion(0.0F, (float) Math.sin((g / 2.0F)), 0.0F, (float) Math.cos((g / 2.0F))));
        quaternion.mul(new Quaternion(0.0F, 0.0F, (float) Math.sin((h / 2.0F)), (float) Math.cos((h / 2.0F))));
        return quaternion;
    }

    public Vector3f toXYZ() {
        float f = r() * r();
        float g = i() * i();
        float h = j() * j();
        float i = k() * k();
        float j = f + g + h + i;
        float k = 2.0F * r() * i() - 2.0F * j() * k();
        float l = (float) Math.asin((k / j));
        if (Math.abs(k) > 0.999F * j)
            return new Vector3f(2.0F * (float) Math.atan2(i(), r()), l, 0.0F);
        return new Vector3f((float) Math.atan2((2.0F * j() * k() + 2.0F * i() * r()), (f - g - h + i)), l,

                (float) Math.atan2((2.0F * i() * j() + 2.0F * r() * k()), (f + g - h - i)));
    }

    public Vector3f toXYZDegrees() {
        Vector3f vector3f = toXYZ();
        return new Vector3f((float) Math.toDegrees(vector3f.x()), (float) Math.toDegrees(vector3f.y()),
                (float) Math.toDegrees(vector3f.z()));
    }

    public Vector3f toYXZ() {
        float f = r() * r();
        float g = i() * i();
        float h = j() * j();
        float i = k() * k();
        float j = f + g + h + i;
        float k = 2.0F * r() * i() - 2.0F * j() * k();
        float l = (float) Math.asin((k / j));
        if (Math.abs(k) > 0.999F * j)
            return new Vector3f(l, 2.0F *

                    (float) Math.atan2(j(), r()), 0.0F);
        return new Vector3f(l,

                (float) Math.atan2((2.0F * i() * k() + 2.0F * j() * r()), (f - g - h + i)),
                (float) Math.atan2((2.0F * i() * j() + 2.0F * r() * k()), (f - g + h - i)));
    }

    public Vector3f toYXZDegrees() {
        Vector3f vector3f = toYXZ();
        return new Vector3f((float) Math.toDegrees(vector3f.x()), (float) Math.toDegrees(vector3f.y()),
                (float) Math.toDegrees(vector3f.z()));
    }

    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Quaternion quaternion = (Quaternion) object;
        if (Float.compare(quaternion.i, this.i) != 0)
            return false;
        if (Float.compare(quaternion.j, this.j) != 0)
            return false;
        if (Float.compare(quaternion.k, this.k) != 0)
            return false;
        return (Float.compare(quaternion.r, this.r) == 0);
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.i);
        i = 31 * i + Float.floatToIntBits(this.j);
        i = 31 * i + Float.floatToIntBits(this.k);
        i = 31 * i + Float.floatToIntBits(this.r);
        return i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Quaternion[").append(r()).append(" + ");
        stringBuilder.append(i()).append("i + ");
        stringBuilder.append(j()).append("j + ");
        stringBuilder.append(k()).append("k]");
        return stringBuilder.toString();
    }

    public float i() {
        return this.i;
    }

    public float j() {
        return this.j;
    }

    public float k() {
        return this.k;
    }

    public float r() {
        return this.r;
    }

    public void mul(Quaternion quaternion) {
        float f = i();
        float g = j();
        float h = k();
        float i = r();
        float j = quaternion.i();
        float k = quaternion.j();
        float l = quaternion.k();
        float m = quaternion.r();
        this.i = i * j + f * m + g * l - h * k;
        this.j = i * k - f * l + g * m + h * j;
        this.k = i * l + f * k - g * j + h * m;
        this.r = i * m - f * j - g * k - h * l;
    }

    public void mul(float f) {
        this.i *= f;
        this.j *= f;
        this.k *= f;
        this.r *= f;
    }

    public void conj() {
        this.i = -this.i;
        this.j = -this.j;
        this.k = -this.k;
    }

    public void set(float f, float g, float h, float i) {
        this.i = f;
        this.j = g;
        this.k = h;
        this.r = i;
    }

    private static float cos(float f) {
        return (float) Math.cos(f);
    }

    private static float sin(float f) {
        return (float) Math.sin(f);
    }

    public void normalize() {
        float f = i() * i() + j() * j() + k() * k() + r() * r();
        if (f > 1.0E-6F) {
            float g = Mth.fastInvSqrt(f);
            this.i *= g;
            this.j *= g;
            this.k *= g;
            this.r *= g;
        } else {
            this.i = 0.0F;
            this.j = 0.0F;
            this.k = 0.0F;
            this.r = 0.0F;
        }
    }

    public void slerp(Quaternion quaternion, float f) {
        throw new UnsupportedOperationException();
    }

    public Quaternion copy() {
        return new Quaternion(this);
    }
}