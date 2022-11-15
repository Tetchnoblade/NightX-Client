package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.ListValue

import net.minecraft.util.ResourceLocation
import java.util.*

@ModuleInfo(name = "Cape", category = ModuleCategory.RENDER, array = false)
class Cape : Module() {

    val styleValue = ListValue(
        "Style",
        arrayOf(
            "None",
            "NewNightX",
            "OldNightX",
            "Infinity",
            "As",
            "Defoko1",
            "Defoko2",
            "Defoko3",
            "Defoko4",
            "Astolfo",
            "Exhibition",
            "Nuta",
            "Moon",
            "Rise",
            "Tenacity",
            "FDP",
            "Lunar",
            "Migration",
            "Vanilla"
        ),
        "NewNightX"
    )

    private val capeCache = hashMapOf<String, CapeStyle>()
    fun getCapeLocation(value: String): ResourceLocation {
        if (capeCache[value.uppercase(Locale.getDefault())] == null) {
            try {
                capeCache[value.uppercase(Locale.getDefault())] =
                    CapeStyle.valueOf(value.uppercase(Locale.getDefault()))
            } catch (e: Exception) {
                capeCache[value.uppercase(Locale.getDefault())] = CapeStyle.NONE
            }
        }
        return capeCache[value.uppercase(Locale.getDefault())]!!.location
    }

    enum class CapeStyle(val location: ResourceLocation) {
        NONE(ResourceLocation("liquidbounce+/cape/dark.png")),
        NEWNIGHTX(ResourceLocation("liquidbounce+/cape/newnightx.png")),
        OLDNIGHTX(ResourceLocation("liquidbounce+/cape/oldnightx.png")),
        INFINITY(ResourceLocation("liquidbounce+/cape/darker.png")),
        AS(ResourceLocation("liquidbounce+/cape/light.png")),
        DEFOKO1(ResourceLocation("liquidbounce+/cape/special1.png")),
        DEFOKO2(ResourceLocation("liquidbounce+/cape/special2.png")),
        DEFOKO3(ResourceLocation("liquidbounce+/cape/special3.png")),
        DEFOKO4(ResourceLocation("liquidbounce+/cape/special4.png")),
        ASTOLFO(ResourceLocation("liquidbounce+/cape/astolfo.png")),
        EXHIBITION(ResourceLocation("liquidbounce+/cape/exhibition.png")),
        NUTA(ResourceLocation("liquidbounce+/cape/nuta.png")),
        MOON(ResourceLocation("liquidbounce+/cape/moon.png")),
        RISE(ResourceLocation("liquidbounce+/cape/rise.png")),
        TENACITY(ResourceLocation("liquidbounce+/cape/tenacity.png")),
        FDP(ResourceLocation("liquidbounce+/cape/fdp.png")),
        LUNAR(ResourceLocation("liquidbounce+/cape/lunar.png")),
        MIGRATION(ResourceLocation("liquidbounce+/cape/migration.png")),
        VANILLA(ResourceLocation("liquidbounce+/cape/vanilla.png"))
    }

    override val tag: String
        get() = styleValue.get()

    init {
        state = true
    }
}