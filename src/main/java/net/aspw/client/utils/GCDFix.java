package net.aspw.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public final class GCDFix {
    public float pitch;

    public float yaw;

    public int hashCode() {
        return Float.hashCode(this.yaw) * 31 + Float.hashCode(this.pitch);
    }

    public boolean equals(Object paramObject) {
        if (this != paramObject) {
            if (paramObject instanceof GCDFix) {
                GCDFix gCDFix = (GCDFix) paramObject;
                return (Float.compare(this.yaw, gCDFix.yaw) == 0 && Float.compare(this.pitch, gCDFix.pitch) == 0);
            }
            return false;
        }
        return true;
    }

    public static float getDeltaMouse(float paramFloat) {
        return Math.round(paramFloat / getGCDValue());
    }

    public float getYaw() {
        return this.yaw;
    }

    public GCDFix copy(float paramFloat1, float paramFloat2) {
        return new GCDFix(paramFloat1, paramFloat2);
    }

    public GCDFix(float paramFloat1, float paramFloat2) {
        this.yaw = paramFloat1;
        this.pitch = paramFloat2;
    }

    public static float getGCDValue() {
        return (float) (getGCD() * 0.15D);
    }

    public void toPlayer(EntityPlayer paramEntityPlayer) {
        float f = this.yaw;
        if (!Float.isNaN(f)) {
            f = this.pitch;
            if (!Float.isNaN(f)) {
                fixedSensitivity((Minecraft.getMinecraft()).gameSettings.mouseSensitivity);
                paramEntityPlayer.rotationYaw = this.yaw;
                paramEntityPlayer.rotationPitch = this.pitch;
            }
        }
    }

    public float getPitch() {
        return this.pitch;
    }

    public void fixedSensitivity(float paramFloat) {
        float f1 = paramFloat * 0.6F + 0.2F;
        float f2 = f1 * f1 * f1 * 1.2F;
        this.yaw -= this.yaw % f2;
        this.pitch -= this.pitch % f2;
    }

    public void setYaw(float paramFloat) {
        this.yaw = paramFloat;
    }

    public float component1() {
        return this.yaw;
    }

    public static GCDFix copy$default(GCDFix paramGCDFix, float paramFloat1, float paramFloat2, int paramInt) {
        if ((paramInt & 0x1) != 0)
            paramFloat1 = paramGCDFix.yaw;
        if ((paramInt & 0x2) != 0)
            paramFloat2 = paramGCDFix.pitch;
        return paramGCDFix.copy(paramFloat1, paramFloat2);
    }

    public String toString() {
        return String.valueOf((new StringBuilder("Rotation(yaw=")).append(this.yaw).append(", pitch=").append(this.pitch).append(")"));
    }

    public static float getFixedRotation(float paramFloat) {
        return getDeltaMouse(paramFloat) * getGCDValue();
    }

    public void setPitch(float paramFloat) {
        this.pitch = paramFloat;
    }

    public float component2() {
        return this.pitch;
    }

    public static float getGCD() {
        float f;
        return (f = (float) (Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6D + 0.2D)) * f * f * 8.0F;
    }
}