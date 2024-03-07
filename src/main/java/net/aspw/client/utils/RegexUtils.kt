package net.aspw.client.utils

import java.math.BigDecimal
import java.math.RoundingMode

object RegexUtils {

    /**
     * Rounds a double. From https://stackoverflow.com/a/2808648/9140494
     *
     * @param value  the value to be rounded
     * @param places Decimal places
     * @return The rounded value
     */
    fun round(value: Double, places: Int): Double {
        require(places >= 0)
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).toDouble()
    }
}