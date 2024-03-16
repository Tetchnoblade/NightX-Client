package net.aspw.client.utils

object AnimationUtils {

    @JvmStatic
    fun animate(target: Float, current: Float, speed: Float): Float {
        var current = current
        var speed = speed
        if (current == target) return current
        val larger = target > current
        if (speed < 0.0f) {
            speed = 0.0f
        } else if (speed > 1.0f) {
            speed = 1.0f
        }
        val dif = target.coerceAtLeast(current) - target.coerceAtMost(current)
        var factor = dif * speed
        if (factor < 0.01) {
            factor = 0.01f
        }
        if (larger) {
            current += factor
            if (current >= target) current = target
        } else {
            current -= factor
            if (current <= target) current = target
        }
        return current
    }
}