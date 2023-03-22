package net.aspw.client.utils;

import kotlin.jvm.internal.Intrinsics;
import net.aspw.client.event.StrafeEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Rotation {
    private float yaw;
    private float pitch;

    public Rotation(final float yaw, final float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public final float getYaw() {
        return this.yaw;
    }

    public final float getPitch() {
        return this.pitch;
    }

    public final void toPlayer(@NotNull final EntityPlayer player) {
        Intrinsics.checkNotNullParameter(player, "player");
        if (Float.isNaN(this.yaw) || Float.isNaN(this.pitch)) {
            return;
        }
        this.fixedSensitivity(MinecraftInstance.mc.gameSettings.mouseSensitivity);
        player.rotationYaw = this.yaw;
        player.rotationPitch = this.pitch;
    }

    public final void fixedSensitivity(final float sensitivity) {
        final float f = sensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        this.yaw -= this.yaw % gcd;
        this.pitch -= this.pitch % gcd;
    }

    public final void applyStrafeToPlayer(@NotNull final StrafeEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        final EntityPlayerSP player = MinecraftInstance.mc.thePlayer;
        final int dif = (int) ((MathHelper.wrapAngleTo180_float(player.rotationYaw - this.yaw - 23.5f - 135) + 180) / 45);
        final float yaw = this.yaw;
        final float strafe = event.getStrafe();
        final float forward = event.getForward();
        final float friction = event.getFriction();
        float calcForward = 0.0f;
        float calcStrafe = 0.0f;
        switch (dif) {
            case 0: {
                calcForward = forward;
                calcStrafe = strafe;
                break;
            }
            case 1: {
                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
                break;
            }
            case 2: {
                calcForward = strafe;
                calcStrafe = -forward;
                break;
            }
            case 3: {
                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
                break;
            }
            case 4: {
                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            }
            case 5: {
                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
                break;
            }
            case 6: {
                calcForward = -strafe;
                calcStrafe = forward;
                break;
            }
            case 7: {
                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
                break;
            }
        }
        if (calcForward > 1.0f || (calcForward < 0.9f && calcForward > 0.3f) || calcForward < -1.0f || (calcForward > -0.9f && calcForward < -0.3f)) {
            calcForward *= 0.5f;
        }
        if (calcStrafe > 1.0f || (calcStrafe < 0.9f && calcStrafe > 0.3f) || calcStrafe < -1.0f || (calcStrafe > -0.9f && calcStrafe < -0.3f)) {
            calcStrafe *= 0.5f;
        }
        float d = calcStrafe * calcStrafe + calcForward * calcForward;
        if (d >= 1.0E-4f) {
            d = MathHelper.sqrt_float(d);
            if (d < 1.0f) {
                d = 1.0f;
            }
            d = friction / d;
            calcStrafe *= d;
            calcForward *= d;
            final float yawSin = MathHelper.sin((float) (yaw * 3.141592653589793 / 180.0f));
            final float yawCos = MathHelper.cos((float) (yaw * 3.141592653589793 / 180.0f));
            player.motionX += calcStrafe * yawCos - calcForward * (double) yawSin;
            player.motionZ += calcForward * yawCos + calcStrafe * (double) yawSin;
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "Rotation(yaw=" + this.yaw + ", pitch=" + this.pitch + ')';
    }

    public final float component1() {
        return this.yaw;
    }

    public final float component2() {
        return this.pitch;
    }

    @NotNull
    public final Rotation copy(final float yaw, final float pitch) {
        return new Rotation(yaw, pitch);
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(this.yaw);
        result = result * 31 + Float.hashCode(this.pitch);
        return result;
    }

    @Override
    public boolean equals(@Nullable final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Rotation)) {
            return false;
        }
        final Rotation rotation = (Rotation) other;
        return Intrinsics.areEqual(this.yaw, (Object) rotation.getYaw()) && Intrinsics.areEqual(this.pitch, (Object) rotation.getPitch());
    }
}