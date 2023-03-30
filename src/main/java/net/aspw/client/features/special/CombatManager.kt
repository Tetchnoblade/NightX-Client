package net.aspw.client.features.special

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.utils.*
import net.aspw.client.utils.timer.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

class CombatManager : Listenable, MinecraftInstance() {
    private val lastAttackTimer = MSTimer()

    var inCombat = false
        private set
    var target: EntityLivingBase? = null
        private set
    val attackedEntityList = mutableListOf<EntityLivingBase>()
    val focusedPlayerList = mutableListOf<EntityPlayer>()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null) return
        MovementUtilsFix.updateBlocksPerSecond()

        attackedEntityList.map { it }.forEach {
            if (it.isDead) {
                Client.eventManager.callEvent(EntityKilledEvent(it))
                attackedEntityList.remove(it)
            }
        }

        inCombat = false

        if (!lastAttackTimer.hasTimePassed(1000)) {
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


    fun getNearByEntity(radius: Float): EntityLivingBase? {
        return try {
            mc.theWorld.loadedEntityList
                .filter { mc.thePlayer.getDistanceToEntity(it) < radius && EntityUtils.isSelected(it, true) }
                .sortedBy { it.getDistanceToEntity(mc.thePlayer) }[0] as EntityLivingBase?
        } catch (e: Exception) {
            null
        }
    }

    fun isFocusEntity(entity: EntityPlayer): Boolean {
        if (focusedPlayerList.isEmpty()) {
            return true
        }

        return focusedPlayerList.contains(entity)
    }

    override fun handleEvents() = true
}