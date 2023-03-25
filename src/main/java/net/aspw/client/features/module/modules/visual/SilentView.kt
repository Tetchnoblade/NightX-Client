package net.aspw.client.features.module.modules.visual

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.modules.combat.BowAim
import net.aspw.client.features.module.modules.combat.KillAura
import net.aspw.client.features.module.modules.exploit.CivBreak
import net.aspw.client.features.module.modules.other.Annoy
import net.aspw.client.features.module.modules.player.Scaffold
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.ListValue

@ModuleInfo(name = "SilentView", spacedName = "Silent View", category = ModuleCategory.VISUAL)
class SilentView : Module() {

    var mode = ListValue("Mode", arrayOf("Normal", "CSGO", "ETB"), "Normal")
    var headNormalRotate = BoolValue("Head-Rotation", true, { mode.get().equals("normal", true) })
    var headPitch = BoolValue("Head-Pitch", true, { mode.get().equals("normal", true) })
    var bodyNormalRotate = BoolValue("Body-Rotation", true, { mode.get().equals("normal", true) })
    var bodyPrevRotate = BoolValue("Cape-Fixer", true, { mode.get().equals("normal", true) && bodyNormalRotate.get() })
    var R = FloatValue("R", 0f, 0f, 255f, { mode.get().equals("csgo", true) })
    var G = FloatValue("G", 255f, 0f, 255f, { mode.get().equals("csgo", true) })
    var B = FloatValue("B", 255f, 0f, 255f, { mode.get().equals("csgo", true) })
    var Alpha = FloatValue("Alpha", 55f, 0f, 255f, { mode.get().equals("csgo", true) })

    private fun getState(module: Class<out Module>) = Client.moduleManager[module]!!.state

    fun shouldRotate(): Boolean {
        val killAura = Client.moduleManager.getModule(KillAura::class.java) as KillAura
        val scaffold = Client.moduleManager.getModule(Scaffold::class.java) as Scaffold
        val bowAim = Client.moduleManager.getModule(BowAim::class.java) as BowAim
        val civBreak = Client.moduleManager.getModule(CivBreak::class.java) as CivBreak
        return (getState(KillAura::class.java) && killAura.target != null && killAura.silentRotationValue.get() && !killAura.rotations.get()
            .equals("None") || (getState(Scaffold::class.java) && scaffold.rotationsValue.get() || (getState(BowAim::class.java) && bowAim.silentValue.get() && bowAim.hasTarget() || (getState(
            CivBreak::class.java
        ) && civBreak.rotationsValue.get() && civBreak.isBreaking || mc.thePlayer.ridingEntity != null || (getState(
            Annoy::class.java
        ))))))
    }

    init {
        state = true
    }
}