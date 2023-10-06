package net.aspw.client.features.api

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.MinecraftInstance
import net.aspw.client.util.MovementUtilsFix
import net.aspw.client.util.timer.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

class CombatManager : Listenable, MinecraftInstance() {
    private val lastAttackTimer = MSTimer()

    private var inCombat = false
    var target: EntityLivingBase? = null
        private set
    private val attackedEntityList = mutableListOf<EntityLivingBase>()
    private val focusedPlayerList = mutableListOf<EntityPlayer>()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null) return
        MovementUtilsFix.updateBlocksPerSecond()

        // bypass java.util.ConcurrentModificationException
        attackedEntityList.map { it }.forEach {
            if (it.isDead) {
                Client.eventManager.callEvent(EntityKilledEvent(it))
                attackedEntityList.remove(it)
            }
        }

        inCombat = false

        if (!lastAttackTimer.hasTimePassed(250)) {
            inCombat = true
            return
        }

        if (target != null) {
            if (mc.thePlayer.getDistanceToEntity(target) > 7 || !inCombat || target!!.isDead) {
                target = null
            } else {
                inCombat = true
            }
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val target = event.targetEntity

        if (target is EntityLivingBase && EntityUtils.isSelected(target, true)) {
            this.target = target
            if (!attackedEntityList.contains(target)) {
                attackedEntityList.add(target)
            }
        }
        lastAttackTimer.reset()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        inCombat = false
        target = null
        attackedEntityList.clear()
        focusedPlayerList.clear()
    }

    override fun handleEvents() = true
}
