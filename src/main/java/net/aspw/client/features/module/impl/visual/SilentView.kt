package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.BowAura
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.features.module.impl.exploit.CivBreak
import net.aspw.client.features.module.impl.other.Annoy
import net.aspw.client.features.module.impl.player.BedBreaker
import net.aspw.client.features.module.impl.player.Nuker
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.util.RotationUtils
import net.aspw.client.value.BoolValue

@ModuleInfo(
    name = "SilentView",
    spacedName = "Silent View",
    description = "",
    category = ModuleCategory.VISUAL,
    array = false
)
class SilentView : Module() {
    val normalRotationsValue = BoolValue("NormalRotations", true) { !silentValue.get() }
    val moduleCheckValue = BoolValue("ModuleCheck", true) { !silentValue.get() }
    val bodyLockValue = BoolValue("BodyLock", true) { !silentValue.get() }
    val silentValue = BoolValue("Silent", false)

    var playerYaw: Float? = null

    companion object {
        @JvmStatic
        var prevHeadPitch = 0f

        @JvmStatic
        var headPitch = 0f

        @JvmStatic
        fun lerp(tickDelta: Float, old: Float, new: Float): Float {
            return old + (new - old) * tickDelta
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer
        if (thePlayer == null) {
            playerYaw = null
            return
        }
        prevHeadPitch = headPitch
        headPitch = RotationUtils.serverRotation?.pitch!!
        playerYaw = RotationUtils.serverRotation?.yaw!!
    }

    private fun getState(module: Class<out Module>) = Client.moduleManager[module]!!.state

    fun shouldRotate(): Boolean {
        val killAura = Client.moduleManager.getModule(KillAura::class.java) as KillAura
        val bowAim = Client.moduleManager.getModule(BowAura::class.java) as BowAura
        val civBreak = Client.moduleManager.getModule(CivBreak::class.java) as CivBreak
        val bedBreaker = Client.moduleManager.getModule(BedBreaker::class.java) as BedBreaker
        val nuker = Client.moduleManager.getModule(Nuker::class.java) as Nuker
        val annoy = Client.moduleManager.getModule(Annoy::class.java) as Annoy
        return (getState(KillAura::class.java) && killAura.target != null && killAura.silentRotationValue.get() && killAura.rotations.get() != "None" || (getState(
            Scaffold::class.java
        ) || (getState(Nuker::class.java) && nuker.isBreaking || (getState(
            BowAura::class.java
        ) && bowAim.silentValue.get() && bowAim.hasTarget() || (getState(
            CivBreak::class.java
        ) && civBreak.rotationsValue.get() && civBreak.isBreaking || (getState(
            BedBreaker::class.java
        ) && bedBreaker.breaking || (getState(
            Annoy::class.java
        ) && annoy.rotateValue.get()
                )))))))
    }

    init {
        state = true
    }
}