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
}