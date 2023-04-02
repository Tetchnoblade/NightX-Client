package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.visual.ColorMixer.Companion.getMixedColor
import net.aspw.client.utils.render.ColorUtils.LiquidSlowly
import net.aspw.client.utils.render.ColorUtils.fade
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.client.clickgui.NewUi
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.util.*

@ModuleInfo(
    name = "Gui",
    category = ModuleCategory.VISUAL,
    keyBind = Keyboard.KEY_RSHIFT,
    forceNoSound = true,
    onlyEnable = true,
    array = false
)
class Gui : Module() {
    override fun onEnable() {
        mc.displayGuiScreen(NewUi.getInstance())
    }

    companion object {
        private val colorModeValue =
            ListValue("Color", arrayOf("Custom", "Sky", "Rainbow", "LiquidSlowly", "Fade", "Mixer"), "Custom")
        private val colorRedValue = IntegerValue("Red", 255, 0, 255)
        private val colorGreenValue = IntegerValue("Green", 100, 0, 255)
        private val colorBlueValue = IntegerValue("Blue", 255, 0, 255)
        private val saturationValue = FloatValue("Saturation", 0.5f, 0f, 1f)
        private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
        private val mixerSecondsValue = IntegerValue("Seconds", 2, 1, 10)

        @JvmStatic
        val accentColor: Color
            get() {
                var c = Color(255, 255, 255, 255)
                when (colorModeValue.get().lowercase(Locale.getDefault())) {
                    "custom" -> c = Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
                    "rainbow" -> c = Color(
                        RenderUtils.getRainbowOpaque(
                            mixerSecondsValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            0
                        )
                    )

                    "sky" -> c = RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get())
                    "liquidslowly" -> c =
                        LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())

                    "fade" -> c = fade(Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100)
                    "mixer" -> c = getMixedColor(0, mixerSecondsValue.get())
                }
                return c
            }
    }
}