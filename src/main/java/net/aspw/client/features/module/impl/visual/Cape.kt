package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue

import net.minecraft.util.ResourceLocation
import java.util.*

@ModuleInfo(
    name = "Cape",
    category = ModuleCategory.VISUAL,
    description = "",
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
            "NightX",
            "Delta",
            "Funny",
            "Astolfo",
            "Exhibition",
            "Novoline",
            "Dortware",
            "DortwareDev",
            "Diablo",
            "Ayoz",
            "RektSky",
            "LiquidBounceNG",
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
    ) { customCape.get() }

    val movingModeValue = ListValue(
        "MovingMode",
        arrayOf(
            "Smooth",
            "Vanilla"
        ),
        "Smooth"
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
        NIGHTX(ResourceLocation("client/cape/animation/nightx/base.png")),
        DELTA(ResourceLocation("client/cape/delta.png")),
        FUNNY(ResourceLocation("client/cape/funny.png")),
        ASTOLFO(ResourceLocation("client/cape/astolfo.png")),
        EXHIBITION(ResourceLocation("client/cape/animation/exhibition/base.png")),
        NOVOLINE(ResourceLocation("client/cape/novoline.png")),
        DORTWARE(ResourceLocation("client/cape/dortware.png")),
        DORTWAREDEV(ResourceLocation("client/cape/dortwaredev.png")),
        DIABLO(ResourceLocation("client/cape/diablo.png")),
        AYOZ(ResourceLocation("client/cape/ayoz.png")),
        REKTSKY(ResourceLocation("client/cape/rektsky.png")),
        LIQUIDBOUNCENG(ResourceLocation("client/cape/liquidnext.png")),
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
}