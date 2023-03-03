package net.aspw.client.features.module.modules.client

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.modules.combat.KillAura
import net.aspw.client.features.module.modules.misc.Annoy
import net.aspw.client.features.module.modules.world.Scaffold
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue

@ModuleInfo(name = "SilentView", category = ModuleCategory.CLIENT, array = false)
class SilentView : Module() {

    var mode = ListValue("Mode", arrayOf("Normal", "CSGO"), "Normal")
    var headNormalRotate = BoolValue("Head-Rotation", true, { mode.get().equals("normal", true) })
    var headPrevRotate = BoolValue("Head-Fixer", true, { mode.get().equals("normal", true) })
    var headPitch = BoolValue("Head-Pitch", true, { mode.get().equals("normal", true) })
    var headPitchLimit =
        IntegerValue("Pitch-Limit", 180, 180, 540, { mode.get().equals("normal", true) && headPitch.get() })
    var bodyNormalRotate = BoolValue("Body-Rotation", true, { mode.get().equals("normal", true) })
    var bodyPrevRotate = BoolValue("Body-Fixer", true, { mode.get().equals("normal", true) })
    var R = FloatValue("R", 255f, 0f, 255f, { mode.get().equals("csgo", true) })
    var G = FloatValue("G", 120f, 0f, 255f, { mode.get().equals("csgo", true) })
    var B = FloatValue("B", 255f, 0f, 255f, { mode.get().equals("csgo", true) })
    var Alpha = FloatValue("Alpha", 85f, 0f, 255f, { mode.get().equals("csgo", true) })

    private fun getState(module: Class<out Module>) = Client.moduleManager[module]!!.state

    fun shouldRotate(): Boolean {
        val killAura = Client.moduleManager.getModule(KillAura::class.java) as KillAura
        return getState(Scaffold::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) ||
                (getState(Scaffold::class.java)) || (getState(Annoy::class.java))
    }

    init {
        state = true
    }
}