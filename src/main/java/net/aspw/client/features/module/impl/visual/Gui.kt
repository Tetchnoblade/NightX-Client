package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.visual.ColorMixer.Companion.getMixedColor
import net.aspw.client.util.render.ColorUtils.LiquidSlowly
import net.aspw.client.util.render.ColorUtils.fade
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.client.clickgui.dropdown.style.styles.DropDown
import net.aspw.client.visual.client.clickgui.tab.NewUi
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.util.*

@ModuleInfo(
    name = "Gui",
    description = "",
    category = ModuleCategory.VISUAL,
    keyBind = Keyboard.KEY_RSHIFT,
    forceNoSound = true,
    onlyEnable = true,
    array = false
)
class Gui : Module() {
    private val styleValue: ListValue = object : ListValue(
        "Mode",
        arrayOf("DropDown", "Tab"),
        "DropDown"
    ) {
    }

    @JvmField
    val animationValue: ListValue =
        object : ListValue("Animation", arrayOf("None", "Zoom"), "Zoom", { styleValue.get() == "DropDown" }) {}

    @JvmField
    val scaleValue = FloatValue("Scale", 1.0f, 0.4f, 2f) { styleValue.get() == "DropDown" }

    @JvmField
    val imageModeValue =
        ListValue(
            "Image",
            arrayOf("none", "mahiro", "delta", "defoko", "astolfo", "nao", "miguel", "infinity"),
            "none"
        ) { styleValue.get() == "DropDown" }

    override fun onEnable() {
        Client.clickGui.progress = 0.0
        Client.clickGui.slide = 0.0
        Client.clickGui.lastMS = System.currentTimeMillis()
        mc.displayGuiScreen(Client.clickGui)
        when (styleValue.get().lowercase(Locale.getDefault())) {
            "dropdown" -> Client.clickGui.style =
                DropDown()

            "tab" -> mc.displayGuiScreen(NewUi.getInstance())
        }
    }

    companion object {
        private val colorModeValue =
            ListValue("Color", arrayOf("Custom", "Sky", "Rainbow", "LiquidSlowly", "Fade", "Mixer"), "Sky")
        private val colorRedValue = IntegerValue("Red", 255, 0, 255)
        private val colorGreenValue = IntegerValue("Green", 255, 0, 255)
        private val colorBlueValue = IntegerValue("Blue", 255, 0, 255)
        private val saturationValue = FloatValue("Saturation", 0.4f, 0f, 1f)
        private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
        private val mixerSecondsValue = IntegerValue("Seconds", 6, 1, 10)

        @JvmStatic
        fun generateColor(): Color {
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
                "liquidslowly" -> c = LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())
                "fade" -> c = fade(Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100)
                "mixer" -> c = getMixedColor(0, mixerSecondsValue.get())
            }
            return c
        }
    }
}