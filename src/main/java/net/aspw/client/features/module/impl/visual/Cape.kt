package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.ListValue

import net.minecraft.util.ResourceLocation
import java.util.*

@ModuleInfo(name = "Cape", category = ModuleCategory.VISUAL, description = "", array = false)
class Cape : Module() {

    val styleValue = ListValue(
        "Mode",
        arrayOf(
            "None",
            "NightX",
            "Delta",
            "Infinity",
            "Funny",
            "Astolfo",
            "Exhibition",
            "Novoline",
            "Dortware",
            "Diablo",
            "Skidware",
            "Crosssine",
            "Moon",
            "Rise5",
            "Rise6",
            "Tenacity",
            "FDP",
            "Lunar",
            "Minecon2011",
            "Minecon2012",
            "Minecon2013",
            "Minecon2015",
            "Minecon2016",
            "MojangDeveloper",
            "Migrator",
            "Vanilla"
        ),
        "NightX"
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
        NONE(ResourceLocation("client/cape/none.png")),
        DELTA(ResourceLocation("client/cape/delta.png")),
        FUNNY(ResourceLocation("client/cape/funny.png")),
        ASTOLFO(ResourceLocation("client/cape/astolfo.png")),
        EXHIBITION(ResourceLocation("client/cape/exhibition.png")),
        NOVOLINE(ResourceLocation("client/cape/novoline.png")),
        DORTWARE(ResourceLocation("client/cape/dortware.png")),
        DIABLO(ResourceLocation("client/cape/diablo.png")),
        SKIDWARE(ResourceLocation("client/cape/skidware.png")),
        CROSSSINE(ResourceLocation("client/cape/crosssine.png")),
        MOON(ResourceLocation("client/cape/moon.png")),
        RISE6(ResourceLocation("client/cape/rise6.png")),
        TENACITY(ResourceLocation("client/cape/tenacity.png")),
        FDP(ResourceLocation("client/cape/fdp.png")),
        LUNAR(ResourceLocation("client/cape/lunar.png")),
        MINECON2011(ResourceLocation("client/cape/2011.png")),
        MINECON2012(ResourceLocation("client/cape/2012.png")),
        MINECON2013(ResourceLocation("client/cape/2013.png")),
        MINECON2015(ResourceLocation("client/cape/2015.png")),
        MINECON2016(ResourceLocation("client/cape/2016.png")),
        MOJANGDEVELOPER(ResourceLocation("client/cape/mojangdeveloper.png")),
        MIGRATOR(ResourceLocation("client/cape/migrator.png")),
        VANILLA(ResourceLocation("client/cape/vanilla.png"))
    }

    override val tag: String
        get() = styleValue.get()

    init {
        state = true
    }
}