package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.exploit.Disabler
import net.ccbluex.liquidbounce.features.module.modules.misc.Annoy
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "SilentView", spacedName = "Silent View", category = ModuleCategory.RENDER)
class SilentView : Module() {

    var mode = ListValue("Mode", arrayOf("Normal", "CSGO"),"CSGO")
    var R = FloatValue("R", 154f, 0f, 255f)
    var G = FloatValue("G", 114f, 0f, 255f)
    var B = FloatValue("B", 175f, 0f, 255f)
    var Alpha = FloatValue("Alpha", 50f, 0f, 255f)

    private fun getState(module: Class<out Module>) = LiquidBounce.moduleManager[module]!!.state

    fun shouldRotate(): Boolean {
        val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        val disabler = LiquidBounce.moduleManager.getModule(Disabler::class.java) as Disabler
        return getState(Scaffold::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) ||
                (getState(Disabler::class.java) && disabler.canRenderInto3D) ||
                (getState(Scaffold::class.java)) || (getState(Annoy::class.java))
    }

    init {
        state = true
    }
}