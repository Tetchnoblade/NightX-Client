package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.ListValue

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
            "Minecon2011",
            "Minecon2012",
            "Minecon2013",
            "Minecon2015",
            "Minecon2016",
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
        NONE(ResourceLocation("nightx/cape/dark.png")),
        NEWNIGHTX(ResourceLocation("nightx/cape/newnightx.png")),
        OLDNIGHTX(ResourceLocation("nightx/cape/oldnightx.png")),
        INFINITY(ResourceLocation("nightx/cape/darker.png")),
        AS(ResourceLocation("nightx/cape/light.png")),
        DEFOKO1(ResourceLocation("nightx/cape/special1.png")),
        DEFOKO2(ResourceLocation("nightx/cape/special2.png")),
        DEFOKO3(ResourceLocation("nightx/cape/special3.png")),
        DEFOKO4(ResourceLocation("nightx/cape/special4.png")),
        ASTOLFO(ResourceLocation("nightx/cape/astolfo.png")),
        EXHIBITION(ResourceLocation("nightx/cape/exhibition.png")),
        NUTA(ResourceLocation("nightx/cape/nuta.png")),
        MOON(ResourceLocation("nightx/cape/moon.png")),
        RISE(ResourceLocation("nightx/cape/rise.png")),
        TENACITY(ResourceLocation("nightx/cape/tenacity.png")),
        FDP(ResourceLocation("nightx/cape/fdp.png")),
        LUNAR(ResourceLocation("nightx/cape/lunar.png")),
        MINECON2011(ResourceLocation("nightx/cape/2011.png")),
        MINECON2012(ResourceLocation("nightx/cape/2012.png")),
        MINECON2013(ResourceLocation("nightx/cape/2013.png")),
        MINECON2015(ResourceLocation("nightx/cape/2015.png")),
        MINECON2016(ResourceLocation("nightx/cape/2016.png")),
        MIGRATION(ResourceLocation("nightx/cape/migration.png")),
        VANILLA(ResourceLocation("nightx/cape/vanilla.png"))
    }

    override val tag: String
        get() = styleValue.get()

    init {
        state = true
    }
}