package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.NightX
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.combat.KillAura
import net.aspw.nightx.features.module.modules.misc.Annoy
import net.aspw.nightx.features.module.modules.world.Scaffold
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.ListValue

@ModuleInfo(name = "SilentView", category = ModuleCategory.RENDER)
class SilentView : Module() {

    var mode = ListValue("Mode", arrayOf("Normal", "CSGO"),"CSGO")
    var R = FloatValue("R", 200f, 0f, 255f, { mode.get().equals("csgo", true) })
    var G = FloatValue("G", 0f, 0f, 255f, { mode.get().equals("csgo", true) })
    var B = FloatValue("B", 255f, 0f, 255f, { mode.get().equals("csgo", true) })
    var Alpha = FloatValue("Alpha", 80f, 0f, 255f, { mode.get().equals("csgo", true) })

    private fun getState(module: Class<out Module>) = NightX.moduleManager[module]!!.state

    fun shouldRotate(): Boolean {
        val killAura = NightX.moduleManager.getModule(KillAura::class.java) as KillAura
        return getState(Scaffold::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) ||
                (getState(Scaffold::class.java)) || (getState(Annoy::class.java))
    }

    init {
        state = true
    }
}