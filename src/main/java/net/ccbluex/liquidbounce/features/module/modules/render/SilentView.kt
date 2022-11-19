package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.misc.Annoy
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "SilentView", category = ModuleCategory.RENDER)
class SilentView : Module() {

    var mode = ListValue("Mode", arrayOf("Normal", "CSGO"),"Normal")
    var R = FloatValue("R", 255f, 0f, 255f, { mode.get().equals("csgo", true) })
    var G = FloatValue("G", 255f, 0f, 255f, { mode.get().equals("csgo", true) })
    var B = FloatValue("B", 0f, 0f, 255f, { mode.get().equals("csgo", true) })
    var Alpha = FloatValue("Alpha", 60f, 0f, 255f, { mode.get().equals("csgo", true) })

    override val tag: String
        get() = mode.get()

    private fun getState(module: Class<out Module>) = LiquidBounce.moduleManager[module]!!.state

    fun shouldRotate(): Boolean {
        val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        return getState(Scaffold::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) ||
                (getState(Scaffold::class.java)) || (getState(Annoy::class.java))
    }

    init {
        state = true
    }
}