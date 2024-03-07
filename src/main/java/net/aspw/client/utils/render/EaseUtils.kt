package net.aspw.client.utils.render

import kotlin.math.pow

/***
 * Thanks https://github.com/ai/easings.net for this util, converted to Kotlin.
 */
object EaseUtils {
    @JvmStatic
    fun easeOutQuart(x: Double): Double {
        return 1 - (1 - x).pow(4)
    }

    @JvmStatic
    fun easeOutBack(x: Double): Double {
        val c1 = 1.70158
        val c3 = c1 + 1

        return 1 + c3 * (x - 1).pow(3) + c1 * (x - 1).pow(2)
    }
}