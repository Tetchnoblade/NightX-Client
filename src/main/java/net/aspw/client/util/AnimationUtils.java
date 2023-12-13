package net.aspw.client.util;

/**
 * The type Animation utils.
 */
public final class AnimationUtils {
    /**
     * Animate double.
     *
     * @param target  the target
     * @param current the current
     * @param speed   the speed
     * @return the double
     */
    public static double animate(double target, double current, double speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += factor;
            if (current >= target) current = target;
        } else {
            current -= factor;
            if (current <= target) current = target;
        }

        return current;
    }

    /**
     * Animate float.
     *
     * @param target  the target
     * @param current the current
     * @param speed   the speed
     * @return the float
     */
    public static float animate(float target, float current, float speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0F) {
            speed = 0.0F;
        } else if (speed > 1.0F) {
            speed = 1.0F;
        }

        double dif = Math.max(target, (double) current) - Math.min(target, (double) current);
        double factor = dif * (double) speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += (float) factor;
            if (current >= target) current = target;
        } else {
            current -= (float) factor;
            if (current <= target) current = target;
        }

        return current;
    }
}
