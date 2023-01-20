package net.aspw.nightx.features.module.modules.client

import net.aspw.nightx.NightX
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.render.BlendUtils
import net.aspw.nightx.value.IntegerValue
import java.awt.Color

@ModuleInfo(name = "ColorMixer", category = ModuleCategory.CLIENT, canEnable = false)
class ColorMixer : Module() {
    val col1RedValue = ColorElement(1, ColorElement.Material.RED)
    val col1GreenValue = ColorElement(1, ColorElement.Material.GREEN)
    val blendAmount: IntegerValue = object : IntegerValue("Mixer-Amount", 2, 2, 10) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            regenerateColors(oldValue !== newValue)
        }
    }
    val col1BlueValue = ColorElement(1, ColorElement.Material.BLUE)
    val col2RedValue = ColorElement(2, ColorElement.Material.RED)
    val col2GreenValue = ColorElement(2, ColorElement.Material.GREEN)
    val col2BlueValue = ColorElement(2, ColorElement.Material.BLUE)
    val col3RedValue = ColorElement(3, ColorElement.Material.RED, blendAmount)
    val col3GreenValue = ColorElement(3, ColorElement.Material.GREEN, blendAmount)
    val col3BlueValue = ColorElement(3, ColorElement.Material.BLUE, blendAmount)
    val col4RedValue = ColorElement(4, ColorElement.Material.RED, blendAmount)
    val col4GreenValue = ColorElement(4, ColorElement.Material.GREEN, blendAmount)
    val col4BlueValue = ColorElement(4, ColorElement.Material.BLUE, blendAmount)
    val col5RedValue = ColorElement(5, ColorElement.Material.RED, blendAmount)
    val col5GreenValue = ColorElement(5, ColorElement.Material.GREEN, blendAmount)
    val col5BlueValue = ColorElement(5, ColorElement.Material.BLUE, blendAmount)
    val col6RedValue = ColorElement(6, ColorElement.Material.RED, blendAmount)
    val col6GreenValue = ColorElement(6, ColorElement.Material.GREEN, blendAmount)
    val col6BlueValue = ColorElement(6, ColorElement.Material.BLUE, blendAmount)
    val col7RedValue = ColorElement(7, ColorElement.Material.RED, blendAmount)
    val col7GreenValue = ColorElement(7, ColorElement.Material.GREEN, blendAmount)
    val col7BlueValue = ColorElement(7, ColorElement.Material.BLUE, blendAmount)
    val col8RedValue = ColorElement(8, ColorElement.Material.RED, blendAmount)
    val col8GreenValue = ColorElement(8, ColorElement.Material.GREEN, blendAmount)
    val col8BlueValue = ColorElement(8, ColorElement.Material.BLUE, blendAmount)
    val col9RedValue = ColorElement(9, ColorElement.Material.RED, blendAmount)
    val col9GreenValue = ColorElement(9, ColorElement.Material.GREEN, blendAmount)
    val col9BlueValue = ColorElement(9, ColorElement.Material.BLUE, blendAmount)
    val col10RedValue = ColorElement(10, ColorElement.Material.RED, blendAmount)
    val col10GreenValue = ColorElement(10, ColorElement.Material.GREEN, blendAmount)
    val col10BlueValue = ColorElement(10, ColorElement.Material.BLUE, blendAmount)

    companion object {
        @JvmField
        var lastColors = arrayOf<Color?>()
        private var lastFraction = floatArrayOf()

        @JvmStatic
        fun getMixedColor(index: Int, seconds: Int): Color {
            val colMixer = NightX.moduleManager.getModule(ColorMixer::class.java) ?: return Color.white
            if (lastColors.size <= 0 || lastFraction.size <= 0) regenerateColors(true) // just to make sure it won't go white
            return BlendUtils.blendColors(
                lastFraction,
                lastColors,
                (System.currentTimeMillis() + index) % (seconds * 1000) / (seconds * 1000).toFloat()
            )
        }

        fun regenerateColors(forceValue: Boolean) {
            val colMixer = NightX.moduleManager.getModule(ColorMixer::class.java) ?: return

            // color generation
            if (forceValue || lastColors.size <= 0 || lastColors.size != colMixer.blendAmount.get() * 2 - 1) {
                val generator = arrayOfNulls<Color>(colMixer.blendAmount.get() * 2 - 1)

                // reflection is cool
                for (i in 1..colMixer.blendAmount.get()) {
                    var result = Color.white
                    try {
                        val red = ColorMixer::class.java.getField("col" + i + "RedValue")
                        val green = ColorMixer::class.java.getField("col" + i + "GreenValue")
                        val blue = ColorMixer::class.java.getField("col" + i + "BlueValue")
                        val r = (red[colMixer] as ColorElement).get()
                        val g = (green[colMixer] as ColorElement).get()
                        val b = (blue[colMixer] as ColorElement).get()
                        result = Color(
                            Math.max(0, Math.min(r, 255)),
                            Math.max(0, Math.min(g, 255)),
                            Math.max(0, Math.min(b, 255))
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    generator[i - 1] = result
                }
                var h = colMixer.blendAmount.get()
                for (z in colMixer.blendAmount.get() - 2 downTo 0) {
                    generator[h] = generator[z]
                    h++
                }
                lastColors = generator
            }

            // cache thingy
            if (forceValue || lastFraction.size <= 0 || lastFraction.size != colMixer.blendAmount.get() * 2 - 1) {
                // color frac regenerate if necessary
                val colorFraction = FloatArray(colMixer.blendAmount.get() * 2 - 1)
                for (i in 0..colMixer.blendAmount.get() * 2 - 2) {
                    colorFraction[i] = i.toFloat() / (colMixer.blendAmount.get() * 2 - 2).toFloat()
                }
                lastFraction = colorFraction
            }
        }
    }
}