package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.APIConnecter
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue
import net.minecraft.util.ResourceLocation
import java.util.*

@ModuleInfo(
    name = "Cape",
    category = ModuleCategory.VISUAL,
    forceNoSound = true,
    onlyEnable = true,
    array = false
)
class Cape : Module() {

    val customCape = BoolValue("CustomCape", true)
    val styleValue = ListValue(
        "Mode",
        arrayOf(
            "None",
            "NightX"
        ),
        "NightX"
    ) { customCape.get() }

    private val capeCache = hashMapOf<String, CapeStyle>()
    fun getCapeLocation(value: String): ResourceLocation? {
        if (capeCache[value.uppercase(Locale.getDefault())] == null)
            capeCache[value.uppercase(Locale.getDefault())] = CapeStyle.valueOf(value.uppercase(Locale.getDefault()))
        return capeCache[value.uppercase(Locale.getDefault())]!!.location
    }

    enum class CapeStyle(val location: ResourceLocation?) {
        NONE(APIConnecter.callImage("none", "cape")),
        NIGHTX(APIConnecter.callImage("nightx", "cape"))
    }

    override val tag: String
        get() = styleValue.get()
}