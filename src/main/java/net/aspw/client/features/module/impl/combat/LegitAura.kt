package net.aspw.client.features.module.impl.combat

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.player.Freecam
import net.aspw.client.features.module.impl.player.LegitScaffold
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.utils.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase


@ModuleInfo(
    name = "LegitAura", spacedName = "Legit Aura",
    category = ModuleCategory.COMBAT
)
class LegitAura : Module() {

    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 12, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }
    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 10, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }

    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 80f, 0f, 180f, "°") {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }
    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 40f, 0f, 180f, "°") {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }

    private val fovValue = FloatValue("Fov", 180F, 0F, 180F, "°")
    private val autoBlock = BoolValue("FakeAutoBlock", true)

    /*
     * Variables
     */
    private val clickTimer = MSTimer()
    var isBlocking = false
    private var lastTarget: EntityLivingBase? = null
    private var thread: Thread? = null
    private var attackDelay = 0L

    override fun onDisable() {
        isBlocking = false
        clickTimer.reset()
        lastTarget = null
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        state = false
        chat("LegitAura was disabled")
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (Launch.moduleManager[Freecam::class.java]!!.state || Launch.moduleManager[Scaffold::class.java]!!.state || Launch.moduleManager[LegitScaffold::class.java]!!.state) {
            isBlocking = false
            clickTimer.reset()
            lastTarget = null
            return
        }

        if (lastTarget != null)
            RotationUtils.faceLook(lastTarget!!, minTurnSpeed.get(), maxTurnSpeed.get())

        if (!clickTimer.hasTimePassed(attackDelay)) return

        if (thread == null || !thread!!.isAlive) {
            thread = Thread { runAttack() }
            thread!!.start()
            clickTimer.reset()
        } else clickTimer.reset()
    }

    private fun runAttack() {
        if (mc.thePlayer == null || mc.theWorld == null) return

        val targets = arrayListOf<EntityLivingBase>()
        var entityCount = 0

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityLivingBase && EntityUtils.isSelected(entity, true) && mc.thePlayer.getDistanceToEntity(
                    entity
                ) <= 4.5f
            ) {
                if (fovValue.get() < 180F && RotationUtils.getRotationDifference(entity) > fovValue.get())
                    continue

                if (entityCount >= 1)
                    break

                if (autoBlock.get())
                    isBlocking = true
                targets.add(entity)
                entityCount++
            }
        }

        if (targets.isEmpty()) {
            lastTarget = null
            isBlocking = false
            return
        }

        targets.sortBy { it.health }

        targets.forEach {
            if (mc.thePlayer == null || mc.theWorld == null) return

            lastTarget = it

            if (mc.thePlayer.getDistanceToEntity(lastTarget) <= 3.4f)
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)
        }
    }
}