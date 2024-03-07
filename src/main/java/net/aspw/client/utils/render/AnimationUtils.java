package net.aspw.client.utils.render;

/**
 * The type Animation utils.
 */
public class AnimationUtils {

    /**
     * Ease out float.
     *
     * @param t the t
     * @param d the d
     * @return the float
     */
    public static float easeOut(float t, float d) {
        return (t = t / d - 1) * t * t + 1;
    }
}
