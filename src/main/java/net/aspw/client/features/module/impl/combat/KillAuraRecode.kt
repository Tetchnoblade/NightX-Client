package net.aspw.client.features.module.impl.combat

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.player.Freecam
import net.aspw.client.features.module.impl.player.LegitScaffold
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.utils.CooldownHelper
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemSword


@ModuleInfo(
    name = "KillAuraRecode", spacedName = "Kill Aura Recode",
    category = ModuleCategory.COMBAT
)
class KillAuraRecode : Module() {

    private val coolDownCheck = BoolValue("Cooldown-Check", false)
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 12, 1, 20, { !coolDownCheck.get() }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)
            delay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }
    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 10, 1, 20, { !coolDownCheck.get() }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)
            delay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
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

    val modifiedReach = BoolValue("ModifiedReach", false)
    val rangeValue = FloatValue("Range", 7f, 3f, 7f, "m") { modifiedReach.get() }

    private val fakeAutoBlock = BoolValue("VisualAutoBlock", true)
    private val realAutoBlock = BoolValue("RealAutoBlock", false)
    private val autoBlockDelay = IntegerValue("AutoBlockTick", 5, 1, 20) { realAutoBlock.get() }

    var lastTarget: EntityLivingBase? = null
    private var delay = if (coolDownCheck.get()) TimeUtils.randomClickDelay(20, 20) else TimeUtils.randomClickDelay(
        minCPS.get(),
        maxCPS.get()
    )
    private var lastSwing = 0L
    private var blockTick = 0
    var isBlocking = false
    var isTargeting = false

    override fun onDisable() {
        isBlocking = false
        isTargeting = false
        blockTick = 0
        lastSwing = 0L
        lastTarget = null
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        state = false
        chat("KillAuraRecode was disabled")
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || Launch.moduleManager[Freecam::class.java]!!.state || Launch.moduleManager[Scaffold::class.java]!!.state || Launch.moduleManager[LegitScaffold::class.java]!!.state) return

        if (lastTarget != null && mc.thePlayer.canEntityBeSeen(lastTarget))
            RotationUtils.faceLook(lastTarget!!, minTurnSpeed.get(), maxTurnSpeed.get())
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || Launch.moduleManager[Freecam::class.java]!!.state || Launch.moduleManager[Scaffold::class.java]!!.state || Launch.moduleManager[LegitScaffold::class.java]!!.state) {
            isBlocking = false
            isTargeting = false
            blockTick = 0
            lastSwing = 0L
            lastTarget = null
            return
        }

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
            lastSwing = 0L
            blockTick = 0
            return
        }

        targets.sortBy { it.health }

        targets.forEach {
            lastTarget = it

            if (!mc.thePlayer.canEntityBeSeen(lastTarget)) return

            if (coolDownCheck.get() && CooldownHelper.getAttackCooldownProgress() < 1f) return

            if (mc.thePlayer.getDistanceToEntity(lastTarget) <= (if (modifiedReach.get()) rangeValue.get() - 0.6f else 3.4f) && System.currentTimeMillis() - lastSwing >= delay) {
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)

                if (realAutoBlock.get()) {
                    blockTick += 1
                    if (blockTick >= autoBlockDelay.get()) {
                        if (mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword)
                            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
                        blockTick = 0
                    }
                }

                lastSwing = System.currentTimeMillis()
                delay = if (coolDownCheck.get())
                    TimeUtils.randomClickDelay(20, 20)
                else TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
            }
        }
    }
}