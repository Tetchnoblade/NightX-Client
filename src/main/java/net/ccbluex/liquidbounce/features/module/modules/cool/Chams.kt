package net.ccbluex.liquidbounce.features.module.modules.cool

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "Chams", description = "", category = ModuleCategory.COOL)
class Chams : Module() {
    val targetsValue = BoolValue("Targets", true)
    val chestsValue = BoolValue("Chests", false)
    val itemsValue = BoolValue("Items", false)
    val localPlayerValue = BoolValue("LocalPlayer", true)
    val legacyMode = BoolValue("Legacy-Mode", false)
    val texturedValue = BoolValue("Textured", false, { !legacyMode.get() })
    val colorModeValue = ListValue(
        "Color",
        arrayOf("Custom", "Rainbow", "Sky", "LiquidSlowly", "Fade", "Mixer"),
        "Rainbow",
        { !legacyMode.get() })
    val behindColorModeValue =
        ListValue("Behind-Color", arrayOf("Same", "Opposite", "Red"), "Opposite", { !legacyMode.get() })
    val redValue = IntegerValue("Red", 0, 0, 255, { !legacyMode.get() })
    val greenValue = IntegerValue("Green", 200, 0, 255, { !legacyMode.get() })
    val blueValue = IntegerValue("Blue", 0, 0, 255, { !legacyMode.get() })
    val alphaValue = IntegerValue("Alpha", 255, 0, 255, { !legacyMode.get() })
    val saturationValue = FloatValue("Saturation", 1F, 0F, 1F, { !legacyMode.get() })
    val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F, { !legacyMode.get() })
    val mixerSecondsValue = IntegerValue("Seconds", 2, 1, 10, { !legacyMode.get() })
}
