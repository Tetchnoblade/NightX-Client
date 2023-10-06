package net.aspw.client.features.module.impl.combat

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.util.*
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.init.Items
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "AutoHeal", spacedName = "Auto Heal", description = "", category = ModuleCategory.COMBAT)
class AutoHeal : Module() {
    // Auto Pot
    private val autoPotValue = BoolValue("AutoPot", true)
    private val healthValue = FloatValue("Health-Pot", 15F, 0F, 100F, "%") { autoPotValue.get() }
    private val delayValue = IntegerValue("Delay-Pot", 300, 0, 5000, "ms") { autoPotValue.get() }
    private val regenValue = BoolValue("Heal-Pot", true) { autoPotValue.get() }
    private val utilityValue = BoolValue("Utility-Pot", true) { autoPotValue.get() }
    private val smartValue = BoolValue("Smart-Pot", true) { autoPotValue.get() }
    private val smartTimeoutValue =
        IntegerValue("SmartTimeout-Pot", 500, 500, 5000, "ms") { smartValue.get() && autoPotValue.get() }
    private val spoofInvValue = BoolValue("InvSpoof-Pot", false) { autoPotValue.get() }
    private val spoofDelayValue =
        IntegerValue("InvDelay-Pot", 500, 500, 5000, "ms") { spoofInvValue.get() && autoPotValue.get() }
    private val noCombatValue = BoolValue("NoCombat-Pot", true) { autoPotValue.get() }
    private val customPitchValue = BoolValue("Custom-Pitch-Pot", false) { autoPotValue.get() }
    private val customPitchAngle =
        FloatValue("Angle-Pot", 90F, -90F, 90F, "°") { customPitchValue.get() && autoPotValue.get() }
    private val debugValue = BoolValue("Debug-Pot", false) { autoPotValue.get() }

    // Auto Soup
    private val autoSoupValue = BoolValue("AutoSoup", false)
    private val healthValueA = FloatValue("Health-Soup", 15f, 0f, 20f) { autoSoupValue.get() }
    private val delayValueA = IntegerValue("Delay-Soup", 150, 0, 500, "ms") { autoSoupValue.get() }
    private val openInventoryValue = BoolValue("OpenInv-Soup", false) { autoSoupValue.get() }
    private val simulateInventoryValue = BoolValue("SimulateInventory-Soup", true) { autoSoupValue.get() }
    private val bowlValue = ListValue("Bowl-Soup", arrayOf("Drop", "Move", "Stay"), "Drop") { autoSoupValue.get() }

    private var isRotating = false
    private var throwing = false
    private var rotated = false
    private var prevSlot = -1
    private var potIndex = -1

    private var throwTimer = MSTimer()
    private var resetTimer = MSTimer()
    private var invTimer = MSTimer()
    private var timeoutTimer = MSTimer()
    private val timer = MSTimer()

    private val throwQueue = arrayListOf<Int>()

    val killAura = Client.moduleManager.getModule(KillAura::class.java)
    val scaffold = Client.moduleManager.getModule(Scaffold::class.java)

    override val tag: String
        get() = healthValue.get().toString() + "%"

    private fun resetAll() {
        throwing = false
        rotated = false
        throwTimer.reset()
        resetTimer.reset()
        timeoutTimer.reset()
        invTimer.reset()
        throwQueue.clear()
    }

    override fun onDisable() {
        isRotating = false
    }

    override fun onEnable() {
        if (autoPotValue.get()) {
            resetAll()
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (autoPotValue.get()) {
            resetAll()
        }
    }

    private fun debug(s: String) {
        if (debugValue.get())
            ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§3$s")
    }

    @EventTarget(priority = 2)
    fun onMotion(event: MotionEvent) {
        if (autoPotValue.get()) {
            if (event.eventState == EventState.PRE) {
                if (smartValue.get() && throwQueue.isNotEmpty()) {
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

                if (mc.currentScreen !is GuiContainer && !throwing && throwTimer.hasTimePassed(
                        delayValue.get().toLong()
                    )
                ) {
                    val potion = findPotion(36, 45)
                    if (potion != -1) {
                        potIndex = potion
                        throwing = true
                        debug("found pot, queueing")
                    }
                }

                if (throwing && mc.currentScreen !is GuiContainer && (!killAura?.state!! || killAura.target == null) && !scaffold?.state!!) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                        mc.thePlayer.jump()
                        debug("jumped")
                    }
                    RotationUtils.reset() // reset all rotations
                    event.pitch = if (customPitchValue.get()) customPitchAngle.get() else 90F
                    debug("silent rotation")
                    isRotating = true
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        val packet = event.packet

        if (autoPotValue.get()) {
            if (throwing) {
                if (!mc.isSingleplayer && packet is C09PacketHeldItemChange) {
                    if (packet.slotId == prevSlot) {
                        event.cancelEvent()
                    } else {
                        prevSlot = packet.slotId
                    }
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (autoSoupValue.get()) {
            if (!timer.hasTimePassed(delayValueA.get().toLong()))
                return

            val soupInHotbar = InventoryUtils.findItem(36, 45, Items.mushroom_stew)
            if (mc.thePlayer.health <= healthValueA.get() && soupInHotbar != -1) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(soupInHotbar - 36))
                mc.netHandler.addToSendQueue(
                    C08PacketPlayerBlockPlacement(
                        mc.thePlayer.inventoryContainer
                            .getSlot(soupInHotbar).stack
                    )
                )
                if (bowlValue.get().equals("Drop", true))
                    mc.netHandler.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.DROP_ITEM,
                            BlockPos.ORIGIN, EnumFacing.DOWN
                        )
                    )
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                timer.reset()
                return
            }

            val bowlInHotbar = InventoryUtils.findItem(36, 45, Items.bowl)
            if (bowlValue.get().equals("Move", true) && bowlInHotbar != -1) {
                if (openInventoryValue.get() && mc.currentScreen !is GuiInventory)
                    return

                var bowlMovable = false

                for (i in 9..36) {
                    val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack

                    if (itemStack == null) {
                        bowlMovable = true
                        break
                    } else if (itemStack.item == Items.bowl && itemStack.stackSize < 64) {
                        bowlMovable = true
                        break
                    }
                }

                if (bowlMovable) {
                    val openInventory = mc.currentScreen !is GuiInventory && simulateInventoryValue.get()

                    if (openInventory)
                        mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                    mc.playerController.windowClick(0, bowlInHotbar, 0, 1, mc.thePlayer)
                }
            }

            val soupInInventory = InventoryUtils.findItem(9, 36, Items.mushroom_stew)
            if (soupInInventory != -1 && InventoryUtils.hasSpaceHotbar()) {
                if (openInventoryValue.get() && mc.currentScreen !is GuiInventory)
                    return

                val openInventory = mc.currentScreen !is GuiInventory && simulateInventoryValue.get()
                if (openInventory)
                    mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))

                mc.playerController.windowClick(0, soupInInventory, 0, 1, mc.thePlayer)

                if (openInventory)
                    mc.netHandler.addToSendQueue(C0DPacketCloseWindow())

                timer.reset()
            }
        }
    }

    @EventTarget(priority = -1)
    fun onMotionPost(event: MotionEvent) {
        if (autoPotValue.get()) {
            if (event.eventState == EventState.POST) {
                if (throwing && mc.currentScreen !is GuiContainer
                    && !mc.thePlayer.onGround
                    && !mc.thePlayer.isEating
                    && MovementUtils.isRidingBlock()
                    && (!noCombatValue.get() || !killAura?.state!! || killAura.target == null) && !scaffold?.state!!
                ) {
                    val potionEffects = getPotionFromSlot(potIndex)
                    if (potionEffects != null) {
                        val potionIds = potionEffects.map { it.potionID }

                        if (smartValue.get())
                            potionIds.filter { !throwQueue.contains(it) }.forEach { throwQueue.add(it) }

                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(potIndex - 36))
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                        mc.itemRenderer.resetEquippedProgress2()

                        potIndex = -1
                        throwing = false
                        throwTimer.reset()
                        isRotating = false
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
}