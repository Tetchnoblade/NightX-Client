package net.aspw.client.features.module.impl.combat

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.RaycastUtils
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.minecraft.init.Items

@ModuleInfo(
    name = "AutoRod", spacedName = "Auto Rod", description = "",
    category = ModuleCategory.COMBAT
)
class AutoRod : Module() {
    private val facingEnemy = BoolValue("FacingEnemy", true)

    private val pushDelay = IntegerValue("PushDelay", 100, 50, 1000)
    private val pullbackDelay = IntegerValue("PullbackDelay", 500, 50, 1000)

    private val pushTimer = MSTimer()
    private val rodPullTimer = MSTimer()

    private val killAura = Client.moduleManager.getModule(KillAura::class.java)!!
    private val scaffold = Client.moduleManager.getModule(Scaffold::class.java)!!

    private var rodInUse = false
    private var switchBack = -1

    override fun onDisable() {
        rodInUse = false
        switchBack = -1
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        rodInUse = false
        switchBack = -1
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (killAura.state && killAura.target != null || scaffold.state) {
            rodInUse = false
            switchBack = -1
            return
        }

        // Check if player is using rod
        val usingRod = (mc.thePlayer.isUsingItem && mc.thePlayer.heldItem?.item == Items.fishing_rod) || rodInUse

        if (usingRod) {
            // Check if rod pull timer has reached delay
            // mc.thePlayer.fishEntity?.caughtEntity != null is always null

            if (rodPullTimer.hasTimePassed(pullbackDelay.get().toLong())) {
                if (switchBack != -1 && mc.thePlayer.inventory.currentItem != switchBack) {
                    // Switch back to previous item
                    mc.thePlayer.inventory.currentItem = switchBack
                    mc.playerController.updateController()
                } else {
                    // Stop using rod
                    mc.thePlayer.stopUsingItem()
                }

                switchBack = -1
                rodInUse = false

                // Reset push timer. Push will always wait for pullback delay.
                pushTimer.reset()
            }
        } else {
            var rod = false

            if (facingEnemy.get()) {
                // Check if player is facing enemy
                var facingEntity = mc.objectMouseOver?.entityHit

                if (facingEntity == null) {
                    // Check if player is looking at enemy, 8 blocks should be enough
                    facingEntity = RaycastUtils.raycastEntity(8.0) { EntityUtils.isSelected(it, true) }
                }

                if (EntityUtils.isSelected(facingEntity, true)) {
                    rod = true
                }
            } else {
                // Rod anyway, spam it.
                rod = true
            }

            if (rod && pushTimer.hasTimePassed(pushDelay.get().toLong())) {
                // Check if player has rod in hand
                if (mc.thePlayer.heldItem?.item != Items.fishing_rod) {
                    // Check if player has rod in hotbar
                    val rod = findRod(36, 45)

                    if (rod == -1) {
                        // There is no rod in hotbar
                        return
                    }

                    // Switch to rod
                    switchBack = mc.thePlayer.inventory.currentItem

                    mc.thePlayer.inventory.currentItem = rod - 36
                    mc.playerController.updateController()
                }

                rod()
            }
        }
    }

    /**
     * Use rod
     */
    private fun rod() {
        val rod = findRod(36, 45)

        mc.thePlayer.inventory.currentItem = rod - 36
        // We do not need to send our own packet, because sendUseItem will handle it for us.
        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventoryContainer.getSlot(rod).stack)

        rodInUse = true
        rodPullTimer.reset()
    }

    /**
     * Find rod in inventory
     */
    private fun findRod(startSlot: Int, endSlot: Int): Int {
        for (i in startSlot until endSlot) {
            val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
            if (stack != null && stack.item === Items.fishing_rod) {
                return i
            }
        }
        return -1
    }
}