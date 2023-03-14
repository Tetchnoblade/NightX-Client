package net.aspw.client.utils.render;

public class AnimationUtils {

    /**
     * In-out-easing function
     * https://github.com/jesusgollonet/processing-penner-easing
     *
     * @param t Current iteration
     * @param d Total iterations
     * @return Eased value
     */
    public static float easeOut(float t, float d) {
        return (t = t / d - 1) * t * t + 1;
    }
}
