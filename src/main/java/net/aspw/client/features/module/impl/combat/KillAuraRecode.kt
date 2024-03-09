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
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase


@ModuleInfo(
    name = "KillAuraRecode", spacedName = "Kill Aura Recode",
    category = ModuleCategory.COMBAT
)
class KillAuraRecode : Module() {

    private val cpsValue = ListValue("CPSMode", arrayOf("Low", "Normal", "High"), "Normal")

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

    val modifiedReach = BoolValue("ModifiedReach", false)
    val rangeValue = FloatValue("Range", 7f, 3f, 7f, "m") { modifiedReach.get() }

    private val fakeAutoBlock = BoolValue("VisualAutoBlock", true)
    private val realAutoBlock = BoolValue("RealAutoBlock", false)
    private val autoBlockDelay = IntegerValue("AutoBlockTick", 5, 1, 20) { realAutoBlock.get() }

    private var thread: Thread? = null
    private val clickTimer = MSTimer()
    private var lastTarget: EntityLivingBase? = null
    private val match = when (cpsValue.get().lowercase()) {
        "low" -> 8
        "normal" -> 12
        "high" -> 20
        else -> 0
    }
    private val attackDelay: Long get() = 1000L / match.toLong()
    private var blockTick = 0
    var isBlocking = false
    var isTargeting = false

    override fun onDisable() {
        isBlocking = false
        isTargeting = false
        clickTimer.reset()
        blockTick = 0
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
            isTargeting = false
            clickTimer.reset()
            blockTick = 0
            lastTarget = null
            return
        }

        if (lastTarget != null)
            RotationUtils.faceLook(lastTarget!!, minTurnSpeed.get(), maxTurnSpeed.get())

        if (!clickTimer.hasTimePassed(attackDelay)) return

        try {
            if (thread == null || !thread!!.isAlive) {
                thread = Thread { runAttack() }
                thread!!.start()
                clickTimer.reset()
            } else clickTimer.reset()
        } catch (_: Exception) {
        }
    }

    private fun runAttack() {
        if (mc.thePlayer == null || mc.theWorld == null) return

        val targets = arrayListOf<EntityLivingBase>()
        var entityCount = 0

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityLivingBase && EntityUtils.isSelected(entity, true) && mc.thePlayer.getDistanceToEntity(
                    entity
                ) <= (if (modifiedReach.get()) rangeValue.get() + 1.1f else 4.5f)
            ) {
                if (fovValue.get() < 180F && RotationUtils.getRotationDifference(entity) > fovValue.get())
                    continue

                if (entityCount >= 1)
                    break

                if (fakeAutoBlock.get())
                    isBlocking = true
                isTargeting = true
                targets.add(entity)
                entityCount++
            }
        }

        if (targets.isEmpty()) {
            lastTarget = null
            isBlocking = false
            isTargeting = false
            blockTick = 0
            return
        }

        targets.sortBy { it.health }

        targets.forEach {
            if (mc.thePlayer == null || mc.theWorld == null) return

            lastTarget = it

            if (!mc.thePlayer.canEntityBeSeen(lastTarget)) return

            if (mc.thePlayer.getDistanceToEntity(lastTarget) <= (if (modifiedReach.get()) rangeValue.get() - 0.6f else 3.4f)) {
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)

                if (realAutoBlock.get()) {
                    blockTick += 1
                    if (blockTick >= autoBlockDelay.get()) {
                        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
                        blockTick = 0
                    }
                }
            }
        }
    }
}