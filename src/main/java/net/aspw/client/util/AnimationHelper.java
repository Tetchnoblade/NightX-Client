package net.aspw.client.util;

import net.minecraft.client.Minecraft;

public class AnimationHelper {
    public int alpha;

    public int getAlpha() {
        return this.alpha;
    }

    public AnimationHelper() {
        this.alpha = 0;
    }

    public static float calculateCompensation(float target, float current, long delta, int speed) {
        float diff = current - target;
        if (delta < 1L) {
            delta = 1L;
        }

        double xD;
        if (diff > (float) speed) {
            xD = (double) ((long) speed * delta / 16L) < 0.25D ? 0.5D : (double) ((long) speed * delta / 16L);
            current = (float) ((double) current - xD);
            if (current < target) {
                current = target;
            }
        } else if (diff < (float) (-speed)) {
            xD = (double) ((long) speed * delta / 16L) < 0.25D ? 0.5D : (double) ((long) speed * delta / 16L);
            current = (float) ((double) current + xD);
            if (current > target) {
                current = target;
            }
        } else {
            current = target;
        }

        return current;
    }

    public static int deltaTime;
    public static float speedTarget = 0.125f;

    public static float animation(float current, float targetAnimation, float speed) {
        return AnimationHelper.animation(current, targetAnimation, speedTarget, speed);
    }

    public static float animation(float animation, float target, float poxyi, float speedTarget) {
        float da = (target - animation) / Math.max((float) Minecraft.getDebugFPS(), 5.0f) * 15.0f;
        if (da > 0.0f) {
            da = Math.max(speedTarget, da);
            da = Math.min(target - animation, da);
        } else if (da < 0.0f) {
            da = Math.min(-speedTarget, da);
            da = Math.max(target - animation, da);
        }
        return animation + da;
    }
}