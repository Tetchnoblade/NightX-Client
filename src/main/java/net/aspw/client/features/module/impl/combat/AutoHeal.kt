package net.aspw.client.features.module.impl.combat

import net.aspw.client.Launch
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.player.LegitScaffold
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.utils.*
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.utils.timer.TickTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Items
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import org.lwjgl.opengl.Display
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@ModuleInfo(name = "AutoHeal", spacedName = "Auto Heal", category = ModuleCategory.COMBAT)
class AutoHeal : Module() {
    // Auto Pot
    private val autoPotValue = BoolValue("AutoPot", true)
    private val healthValue = FloatValue("Health-Pot", 50F, 0F, 100F, "%") { autoPotValue.get() }
    private val regenValue = BoolValue("Heal-Pot", true) { autoPotValue.get() }
    private val utilityValue = BoolValue("Utility-Pot", true) { autoPotValue.get() }
    private val spoofInvValue = BoolValue("Auto-Recharge", false) { autoPotValue.get() }
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
    private var potting = false
    private var potIndex = -1
    private var oldSlot = -1

    private var throwTimer = MSTimer()
    private var resetTimer = MSTimer()
    private var invTimer = MSTimer()
    private var timeoutTimer = MSTimer()
    private val timer = MSTimer()
    private val tickTimer = TickTimer()

    private val throwQueue = arrayListOf<Int>()

    val killAura = Launch.moduleManager.getModule(KillAura::class.java)
    val scaffold = Launch.moduleManager.getModule(Scaffold::class.java)
    private val legitScaffold = Launch.moduleManager.getModule(LegitScaffold::class.java)

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))

    override val tag: String
        get() = decimalFormat.format(healthValue.get()) + "%"

    private fun resetAll() {
        potting = false
        throwing = false
        isRotating = false
        rotated = false
        throwTimer.reset()
        resetTimer.reset()
        timeoutTimer.reset()
        invTimer.reset()
        tickTimer.reset()
        throwQueue.clear()
    }

    override fun onDisable() {
        if (autoPotValue.get()) {
            resetAll()
        }
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
            ClientUtils.displayChatMessage(Launch.CLIENT_CHAT + "§3$s")
    }

    @EventTarget(priority = 2)
    fun onMotion(event: MotionEvent) {
        if (autoPotValue.get()) {
            if (event.eventState == EventState.PRE) {
                if (throwQueue.isNotEmpty()) {
                    var foundPot = false
                    for (k in throwQueue.indices.reversed()) {
                        if (mc.thePlayer.isPotionActive(throwQueue[k])) {
                            throwQueue.removeAt(k)
                            timeoutTimer.reset()
                            foundPot = true
                        }
                    }
                    if (!foundPot && timeoutTimer.hasTimePassed(500.toLong())) {
                        debug("reached timeout, clearing queue")
                        throwQueue.clear()
                        timeoutTimer.reset()
                    }
                } else
                    timeoutTimer.reset()

                if (spoofInvValue.get() && mc.currentScreen !is GuiContainer && !throwing) {
                    if (invTimer.hasTimePassed(800L)) {
                        val invPotion = findPotion(9, 36)
                        if (invPotion != -1) {
                            if (InventoryUtils.hasSpaceHotbar()) {
                                InventoryHelper.openPacket()
                                mc.playerController.windowClick(0, invPotion, 0, 1, mc.thePlayer)
                                InventoryHelper.closePacket()
                            }
                            invTimer.reset()
                            debug("moved pot")
                            return
                        }
                    }
                } else
                    invTimer.reset()

                if (mc.currentScreen !is GuiContainer && !throwing && throwTimer.hasTimePassed(
                        1000.toLong()
                    )
                ) {
                    val potion = findPotion(36, 45)
                    if (potion != -1) {
                        potIndex = potion
                        throwing = true
                        potting = true
                        debug("found pot, queueing")
                    }
                }

                if (potting) {
                    oldSlot = mc.thePlayer.inventory.currentItem
                }

                if (throwing && !mc.thePlayer.isEating && !mc.thePlayer.isInWater && MovementUtils.isRidingBlock() && mc.inGameHasFocus && Display.isActive() && mc.currentScreen !is GuiContainer && (!killAura?.state!! || killAura.target == null) && !scaffold?.state!! && !legitScaffold?.state!!) {
                    if (mc.thePlayer.onGround) {
                        potting = false
                        RotationUtils.setTargetRotation(
                            Rotation(
                                mc.thePlayer.rotationYaw,
                                90F
                            )
                        )
                        if (tickTimer.hasTimePassed(1) && mc.thePlayer.inventory.currentItem != potIndex - 36) {
                            mc.thePlayer.inventory.currentItem = potIndex - 36
                            mc.playerController.updateController()
                            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
                            debug("switch")
                        }
                        tickTimer.update()
                        debug("silent rotation")
                        isRotating = true
                    } else {
                        potting = false
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
                if (tickTimer.hasTimePassed(1) && !tickTimer.hasTimePassed(2))
                    oldSlot = mc.thePlayer.inventory.currentItem
                if (tickTimer.hasTimePassed(2) && !tickTimer.hasTimePassed(3) && !mc.thePlayer.onGround) {
                    if (mc.thePlayer.inventory.currentItem == potIndex - 36) {
                        mc.thePlayer.inventory.currentItem = oldSlot
                        mc.playerController.updateController()
                        potting = false
                        throwing = false
                        tickTimer.reset()
                        debug("switch back")
                    }
                    return
                }
                if (throwing && mc.currentScreen !is GuiContainer
                    && mc.inGameHasFocus && Display.isActive()
                    && !mc.thePlayer.isEating
                    && MovementUtils.isRidingBlock()
                    && !mc.thePlayer.isInWater
                    && tickTimer.hasTimePassed(4) && (!killAura?.state!! || killAura.target == null) && !scaffold?.state!! && !legitScaffold?.state!!
                ) {
                    val potionEffects = getPotionFromSlot(potIndex)
                    if (potionEffects != null) {
                        val potionIds = potionEffects.map { it.potionID }

                        potionIds.filter { !throwQueue.contains(it) }.forEach { throwQueue.add(it) }

                        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)

                        mc.thePlayer.inventory.currentItem = oldSlot
                        mc.playerController.updateController()

                        potIndex = -1
                        oldSlot = -1
                        throwing = false
                        throwTimer.reset()
                        isRotating = false
                        tickTimer.reset()
                        debug("thrown")
                    } else {
                        potIndex = -1
                        mc.thePlayer.inventory.currentItem = oldSlot
                        mc.playerController.updateController()
                        potting = false
                        throwing = false
                        tickTimer.reset()
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

            if (!mc.thePlayer.isPotionActive(Potion.regeneration) && !throwQueue.contains(Potion.regeneration.id))
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
        return !mc.thePlayer.isPotionActive(id) && !throwQueue.contains(id)
    }
}