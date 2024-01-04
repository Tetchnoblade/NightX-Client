package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.extensions.getDistanceToEntityBox
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import kotlin.random.Random

@ModuleInfo(
    name = "SmartRange", spacedName = "Smart Range", description = "",
    category = ModuleCategory.COMBAT
)
class SmartRange : Module() {
    private var playerTicks = 0
    private var smartTick = 0
    private var cooldownTick = 0

    // Condition to confirm
    private var confirmTick = false
    private var confirmMove = false

    // Condition to prevent getting timer speed stuck
    private var confirmAttack = false

    private val timerBoostMode = ListValue("TimerMode", arrayOf("Normal", "Smart"), "Normal")

    private val ticksValue = IntegerValue("Ticks", 10, 1, 20)
    private val timerBoostValue = FloatValue("TimerBoost", 1.5f, 0.01f, 35f)
    private val timerChargedValue = FloatValue("TimerCharged", 0.45f, 0.05f, 5f)

    // Normal Mode Settings
    private val rangeValue = FloatValue("Range", 3.5f, 1f, 5f) { timerBoostMode.get() == "Normal" }
    private val cooldownTickValue = IntegerValue("CooldownTick", 10, 1, 50) { timerBoostMode.get() == "Normal" }

    // Smart Mode Settings
    private val minRange = FloatValue("MinRange", 1f, 1f, 5f) { timerBoostMode.get() != "Normal" }
    private val maxRange = FloatValue("MaxRange", 5f, 1f, 5f) { timerBoostMode.get() != "Normal" }

    private val minTickDelay: IntegerValue =
        object : IntegerValue("MinTickDelay", 50, 1, 500, { timerBoostMode.get() != "Normal" }) {
            override fun onChanged(oldValue: Int, newValue: Int) {
                newValue.coerceAtMost(maxTickDelay.get())
            }
        }

    private val maxTickDelay: IntegerValue =
        object : IntegerValue("MinTickDelay", 100, 1, 500, { timerBoostMode.get() != "Normal" }) {
            override fun onChanged(oldValue: Int, newValue: Int) {
                newValue.coerceAtLeast(minTickDelay.get())
            }
        }

    private fun timerReset() {
        mc.timer.timerSpeed = 1f
    }

    override val tag: String
        get() = timerBoostMode.get()

    override fun onEnable() {
        timerReset()
    }

    override fun onDisable() {
        timerReset()
        smartTick = 0
        cooldownTick = 0
        playerTicks = 0
        confirmTick = false
        confirmMove = false
        confirmAttack = false
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        timerReset()
        smartTick = 0
        cooldownTick = 0
        playerTicks = 0
        confirmTick = false
        confirmMove = false
        confirmAttack = false
    }

    /**
     * Attack event
     */
    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity !is EntityLivingBase || shouldResetTimer()) {
            timerReset()
            return
        } else {
            confirmAttack = true
        }

        val targetEntity = event.targetEntity
        val entityDistance = mc.thePlayer.getDistanceToEntityBox(targetEntity)
        val randomTickDelay = Random.nextInt(minTickDelay.get(), maxTickDelay.get())
        val randomRange = Random.nextDouble(minRange.get().toDouble(), maxRange.get().toDouble())

        smartTick++
        cooldownTick++

        val shouldSlowed = when (timerBoostMode.get()) {
            "Normal" -> cooldownTick >= cooldownTickValue.get() && entityDistance <= rangeValue.get()
            "Smart" -> smartTick >= randomTickDelay && entityDistance <= randomRange
            else -> false
        }

        if (shouldSlowed && confirmAttack) {
            confirmAttack = false
            playerTicks = ticksValue.get()
            cooldownTick = 0
            smartTick = 0
        } else {
            timerReset()
        }
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        // Randomize the timer & charged delay a bit, to bypass some AntiCheat
        val timerboost = Random.nextDouble(0.5, 0.56)
        val charged = Random.nextDouble(0.75, 0.91)

        if (playerTicks <= 0) {
            timerReset()
            return
        }

        val tickProgress = playerTicks.toDouble() / ticksValue.get().toDouble()
        val playerSpeed = when {
            tickProgress < timerboost -> timerBoostValue.get()
            tickProgress < charged -> timerChargedValue.get()
            else -> 1f
        }

        val speedAdjustment = if (playerSpeed >= 0) playerSpeed else 1f + ticksValue.get() - playerTicks
        val adjustedTimerSpeed = maxOf(speedAdjustment, 0f)

        mc.timer.timerSpeed = adjustedTimerSpeed

        playerTicks--
    }

    /**
     * Get all entities in the world.
     */
    private fun getAllEntities(): List<Entity> {
        return mc.theWorld.loadedEntityList
            .filter { EntityUtils.isSelected(it, true) }
            .toList()
    }

    /**
     * Separate condition to make it cleaner
     */
    private fun shouldResetTimer(): Boolean {
        return (playerTicks >= 1
                || mc.thePlayer.isSpectator || mc.thePlayer.isDead
                || mc.thePlayer.isInWater || mc.thePlayer.isInLava
                || mc.thePlayer.isInWeb || mc.thePlayer.isOnLadder
                || mc.thePlayer.isRiding)
    }
}