package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.InventoryHelper
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect

@ModuleInfo(name = "AutoHeal", spacedName = "Auto Heal", category = ModuleCategory.COMBAT)
class AutoHeal : Module() {

    private val modeValue = ListValue("Mode", arrayOf("JumpOnly", "Ground"), "Ground")

    private val healthValue = FloatValue("Health", 6F, 0F, 100F, "%")
    private val delayValue = IntegerValue("Delay", 300, 0, 5000, "ms")

    private val regenValue = BoolValue("Heal", true)
    private val utilityValue = BoolValue("Utility", true)
    private val smartValue = BoolValue("Smart", true)
    private val smartTimeoutValue = IntegerValue("SmartTimeout", 500, 500, 5000, "ms", { smartValue.get() })

    private val spoofInvValue = BoolValue("InvSpoof", false)
    private val spoofDelayValue = IntegerValue("InvDelay", 500, 500, 5000, "ms", { spoofInvValue.get() })
    private val noCombatValue = BoolValue("NoCombat", true)

    private val customPitchValue = BoolValue("Custom-Pitch", false)
    private val customPitchAngle = FloatValue("Angle", 90F, -90F, 90F, "°", { customPitchValue.get() })

    private val debugValue = BoolValue("Debug", false)

    private var throwing = false
    private var rotated = false
    private var potIndex = -1

    private var throwTimer = MSTimer()
    private var resetTimer = MSTimer()
    private var invTimer = MSTimer()
    private var timeoutTimer = MSTimer()

    private val throwQueue = arrayListOf<Int>()

    private lateinit var killAura: KillAura
    private lateinit var scaffold: Scaffold

    override fun onInitialize() {
        killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java)!!
        scaffold = LiquidBounce.moduleManager.getModule(Scaffold::class.java)!!
    }

    private fun resetAll() {
        throwing = false
        rotated = false
        throwTimer.reset()
        resetTimer.reset()
        timeoutTimer.reset()
        invTimer.reset()
        throwQueue.clear()
    }

    override fun onEnable() = resetAll()

    @EventTarget
    fun onWorld(event: WorldEvent) = resetAll()

    private fun debug(s: String) {
        if (debugValue.get())
            ClientUtils.displayChatMessage("[AutoPot] $s")
    }

    @EventTarget(priority = 2)
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            if (smartValue.get() && !throwQueue.isEmpty()) {
                var foundPot = false
                for (k in throwQueue.indices.reversed()) {
                    if (mc.thePlayer.isPotionActive(throwQueue[k])) {
                        throwQueue.removeAt(k)
                        timeoutTimer.reset()
                        foundPot = true
                    }
                }
                if (!foundPot && timeoutTimer.hasTimePassed(smartTimeoutValue.get().toLong())) {
                    debug("reached timeout, clearing queue")
                    throwQueue.clear()
                    timeoutTimer.reset()
                }
            } else
                timeoutTimer.reset()

            if (spoofInvValue.get() && mc.currentScreen !is GuiContainer && !throwing) {
                if (invTimer.hasTimePassed(spoofDelayValue.get().toLong())) {
                    val invPotion = findPotion(9, 36)
                    if (invPotion != -1) {
                        if (InventoryUtils.hasSpaceHotbar()) {
                            InventoryHelper.openPacket()
                            mc.playerController.windowClick(0, invPotion, 0, 1, mc.thePlayer)
                            InventoryHelper.closePacket()
                        } else {
                            for (i in 36 until 45) {
                                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                                if (stack == null || stack.item !is ItemPotion || !ItemPotion.isSplash(stack.itemDamage))
                                    continue

                                InventoryHelper.openPacket()
                                mc.playerController.windowClick(0, invPotion, 0, 0, mc.thePlayer)
                                mc.playerController.windowClick(0, i, 0, 0, mc.thePlayer)
                                InventoryHelper.closePacket()
                                break
                            }
                        }
                        invTimer.reset()
                        debug("moved pot")
                        return
                    }
                }
            } else
                invTimer.reset()

            if (mc.currentScreen !is GuiContainer && !throwing && throwTimer.hasTimePassed(delayValue.get().toLong())) {
                val potion = findPotion(36, 45)
                if (potion != -1) {
                    potIndex = potion
                    throwing = true
                    debug("found pot, queueing")
                }
            }

            if (throwing && mc.currentScreen !is GuiContainer && (!killAura.state || killAura.target == null) && !scaffold.state) {
                if (mc.thePlayer.onGround && modeValue.get().equals("jumponly", true)) {
                    mc.thePlayer.jump()
                    debug("jumped")
                }

                RotationUtils.reset() // reset all rotations
                event.pitch = if (customPitchValue.get()) customPitchAngle.get() else 90F
                debug("silent rotation")
            }
        }
    }

    @EventTarget(priority = -1)
    fun onMotionPost(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            if (throwing && mc.currentScreen !is GuiContainer
                && ((mc.thePlayer.onGround && modeValue.get().equals("ground", true)) ||
                        (!mc.thePlayer.onGround && modeValue.get().equals("jumponly", true)))
                && (!noCombatValue.get() || !killAura.state || killAura.target == null) && !scaffold.state
            ) {
                val potionEffects = getPotionFromSlot(potIndex)
                if (potionEffects != null) {
                    val potionIds = potionEffects.map { it.potionID }

                    if (smartValue.get())
                        potionIds.filter { !throwQueue.contains(it) }.forEach { throwQueue.add(it) }

                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(potIndex - 36))
                    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))

                    potIndex = -1
                    throwing = false
                    throwTimer.reset()
                    debug("thrown")
                } else {
                    // refind
                    potIndex = -1
                    throwing = false
                    debug("failed to retrieve potion info, retrying...")
                }
            }
        }
    }

    private fun findPotion(startSlot: Int, endSlot: Int): Int {
        for (i in startSlot until endSlot) {
            if (findSinglePotion(i)) {
                return i
            }
        }
        return -1
    }

    private fun getPotionFromSlot(slot: Int): List<PotionEffect>? {
        val stack = mc.thePlayer.inventoryContainer.getSlot(slot).stack

        if (stack == null || stack.item !is ItemPotion || !ItemPotion.isSplash(stack.itemDamage))
            return null

        val itemPotion = stack.item as ItemPotion

        return itemPotion.getEffects(stack)
    }

    private fun findSinglePotion(slot: Int): Boolean {
        val stack = mc.thePlayer.inventoryContainer.getSlot(slot).stack

        if (stack == null || stack.item !is ItemPotion || !ItemPotion.isSplash(stack.itemDamage))
            return false

        val itemPotion = stack.item as ItemPotion

        if (mc.thePlayer.health / mc.thePlayer.maxHealth * 100F < healthValue.get() && regenValue.get()) {
            for (potionEffect in itemPotion.getEffects(stack))
                if (potionEffect.potionID == Potion.heal.id)
                    return true

            if (!mc.thePlayer.isPotionActive(Potion.regeneration) && (!smartValue.get() || !throwQueue.contains(Potion.regeneration.id)))
                for (potionEffect in itemPotion.getEffects(stack)) {
                    if (potionEffect.potionID == Potion.regeneration.id)
                        return true
                }

        } else if (utilityValue.get()) {
            for (potionEffect in itemPotion.getEffects(stack)) {
                if (isUsefulPotion(potionEffect.potionID))
                    return true
            }
        }

        return false
    }

    private fun isUsefulPotion(id: Int): Boolean {
        if (id == Potion.regeneration.id || id == Potion.heal.id || id == Potion.poison.id
            || id == Potion.blindness.id || id == Potion.harm.id || id == Potion.wither.id
            || id == Potion.digSlowdown.id || id == Potion.moveSlowdown.id || id == Potion.weakness.id
        ) {
            return false
        }
        return !mc.thePlayer.isPotionActive(id) && (!smartValue.get() || !throwQueue.contains(id))
    }

    override val tag: String
        get() = "${if (modeValue.get().equals("JumpOnly", true)) "Jump Only" else "Ground"}"

}