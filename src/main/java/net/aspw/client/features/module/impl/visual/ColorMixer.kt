package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.features.api.ColorElement
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.render.BlendUtils
import java.awt.Color

@ModuleInfo(name = "ColorMixer", description = "", category = ModuleCategory.VISUAL, canEnable = false)
class ColorMixer : Module() {
    companion object {
        @JvmField
        var lastColors = arrayOf<Color?>()
        private var lastFraction = floatArrayOf()

        @JvmStatic
        fun getMixedColor(index: Int, seconds: Int): Color {
            Client.moduleManager.getModule(ColorMixer::class.java) ?: return Color.white
            if (lastColors.isEmpty() || lastFraction.isEmpty()) regenerateColors(true) // just to make sure it won't go white
            return BlendUtils.blendColors(
                lastFraction,
                lastColors,
                (System.currentTimeMillis() + index) % (seconds * 1000) / (seconds * 1000).toFloat()
            )
        }

        private fun regenerateColors(forceValue: Boolean) {
            val colMixer = Client.moduleManager.getModule(ColorMixer::class.java) ?: return

            // color generation
            if (forceValue || lastColors.isEmpty() || lastColors.size != 2 * 2 - 1) {
                val generator = arrayOfNulls<Color>(2 * 2 - 1)

                // reflection is cool
                for (i in 1..2) {
                    var result = Color.white
                    try {
                        val red = ColorMixer::class.java.getField("col" + i + "RedValue")
                        val green = ColorMixer::class.java.getField("col" + i + "GreenValue")
                        val blue = ColorMixer::class.java.getField("col" + i + "BlueValue")
                        val r = (red[colMixer] as ColorElement).get()
                        val g = (green[colMixer] as ColorElement).get()
                        val b = (blue[colMixer] as ColorElement).get()
                        result = Color(
                            0.coerceAtLeast(r.coerceAtMost(255)),
                            0.coerceAtLeast(g.coerceAtMost(255)),
                            0.coerceAtLeast(b.coerceAtMost(255))
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    generator[i - 1] = result
                }
                var h = 2
                for (z in 2 - 2 downTo 0) {
                    generator[h] = generator[z]
                    h++
                }
                lastColors = generator
            }

            // cache thingy
            if (forceValue || lastFraction.isEmpty() || lastFraction.size != 2 * 2 - 1) {
                // color frac regenerate if necessary
                val colorFraction = FloatArray(2 * 2 - 1)
                for (i in 0..2 * 2 - 2) {
                    colorFraction[i] = i.toFloat() / (2 * 2 - 2).toFloat()
                }
                lastFraction = colorFraction
            }
        }
    }
}