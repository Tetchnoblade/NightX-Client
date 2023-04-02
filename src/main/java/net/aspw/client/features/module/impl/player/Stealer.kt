package net.aspw.client.features.module.impl.player

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.utils.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.network.play.server.S30PacketWindowItems
import net.minecraft.util.ResourceLocation
import kotlin.random.Random

@ModuleInfo(name = "Stealer", spacedName = "Stealer", category = ModuleCategory.PLAYER)
class Stealer : Module() {

    /**
     * OPTIONS
     */

    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 80, 0, 400, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelayValue.get()
            if (i > newValue)
                set(i)

            nextDelay = TimeUtils.randomDelay(minDelayValue.get(), get())
        }
    }

    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 60, 0, 400, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelayValue.get()

            if (i < newValue)
                set(i)

            nextDelay = TimeUtils.randomDelay(get(), maxDelayValue.get())
        }
    }

    private val eventModeValue =
        ListValue("OnEvent", arrayOf("Render3D", "Update", "MotionPre", "MotionPost"), "Update")

    private val takeRandomizedValue = BoolValue("TakeRandomized", true)
    private val onlyItemsValue = BoolValue("OnlyItems", false)
    private val noCompassValue = BoolValue("NoCompass", false)
    private val noDuplicateValue = BoolValue("NoDuplicateNonStackable", false)
    private val instantValue = BoolValue("Instant", false)
    private val autoCloseValue = BoolValue("AutoClose", true)
    val silenceValue = BoolValue("SilentMode", true)
    val showStringValue = BoolValue("Silent-ShowString", true, { silenceValue.get() })
    val stillDisplayValue = BoolValue("Silent-StillDisplay", true, { silenceValue.get() })

    private val autoCloseMaxDelayValue: IntegerValue = object : IntegerValue("AutoCloseMaxDelay", 5, 0, 400, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = autoCloseMinDelayValue.get()
            if (i > newValue) set(i)
            nextCloseDelay = TimeUtils.randomDelay(autoCloseMinDelayValue.get(), this.get())
        }
    }

    private val autoCloseMinDelayValue: IntegerValue = object : IntegerValue("AutoCloseMinDelay", 5, 0, 400, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = autoCloseMaxDelayValue.get()
            if (i < newValue) set(i)
            nextCloseDelay = TimeUtils.randomDelay(this.get(), autoCloseMaxDelayValue.get())
        }
    }

    private val closeOnFullValue = BoolValue("CloseOnFull", true)
    private val chestTitleValue = BoolValue("ChestTitle", false)

    /**
     * VALUES
     */

    private val delayTimer = MSTimer()
    private var nextDelay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

    private val autoCloseTimer = MSTimer()
    private var nextCloseDelay = TimeUtils.randomDelay(autoCloseMinDelayValue.get(), autoCloseMaxDelayValue.get())

    var contentReceived = 0

    var once = false

    override fun onDisable() {
        once = false
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val screen = mc.currentScreen ?: return

        if (eventModeValue.get().equals("render3d", true))
            performStealer(screen)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (instantValue.get()) {
            if (mc.currentScreen is GuiChest) {
                val chest = mc.currentScreen as GuiChest
                val rows = chest.inventoryRows * 9
                for (i in 0 until rows) {
                    val slot = chest.inventorySlots.getSlot(i)
                    if (slot.hasStack) {
                        mc.thePlayer.sendQueue.addToSendQueue(
                            C0EPacketClickWindow(
                                chest.inventorySlots.windowId,
                                i,
                                0,
                                1,
                                slot.stack,
                                1.toShort()
                            )
                        )
                    }
                }
                mc.thePlayer.closeScreen()
            }
        }

        val screen = mc.currentScreen ?: return

        if (eventModeValue.get().equals("update", true))
            performStealer(screen)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val screen = mc.currentScreen ?: return

        if (eventModeValue.get().equals("motion${event.eventState.stateName}", true))
            performStealer(screen)
    }

    fun performStealer(screen: GuiScreen) {
        if (once && screen !is GuiChest) {
            // prevent a bug where the chest suddenly closed while not finishing stealing items inside, leaving cheststealer turned on alone.
            state = false
            return
        }

        if (screen !is GuiChest || !delayTimer.hasTimePassed(nextDelay)) {
            autoCloseTimer.reset()
            return
        }

        // No Compass
        if (!once && noCompassValue.get() && mc.thePlayer.inventory.getCurrentItem()?.item?.unlocalizedName == "item.compass")
            return

        // Chest title
        if (!once && chestTitleValue.get() && (screen.lowerChestInventory == null || !screen.lowerChestInventory.name.contains(
                ItemStack(Item.itemRegistry.getObject(ResourceLocation("minecraft:chest"))).displayName
            ))
        )
            return

        // inventory cleaner
        val inventoryCleaner = Client.moduleManager[InventoryManager::class.java] as InventoryManager

        // Is empty?
        if (!isEmpty(screen) && !(closeOnFullValue.get() && fullInventory)) {
            autoCloseTimer.reset()

            // Randomized
            if (takeRandomizedValue.get()) {
                var noLoop = false
                do {
                    val items = mutableListOf<Slot>()

                    for (slotIndex in 0 until screen.inventoryRows * 9) {
                        val slot = screen.inventorySlots.inventorySlots[slotIndex]

                        if (slot.stack != null && (!onlyItemsValue.get() || slot.stack.item !is ItemBlock) && (!noDuplicateValue.get() || slot.stack.maxStackSize > 1 || !mc.thePlayer.inventory.mainInventory.filter { it != null && it.item != null }
                                .map { it.item!! }
                                .contains(slot.stack.item)) && (!inventoryCleaner.state || inventoryCleaner.isUseful(
                                slot.stack,
                                -1
                            ))
                        )
                            items.add(slot)
                    }

                    val randomSlot = Random.nextInt(items.size)
                    val slot = items[randomSlot]

                    move(screen, slot)
                    if (nextDelay == 0L || delayTimer.hasTimePassed(nextDelay))
                        noLoop = true
                } while (delayTimer.hasTimePassed(nextDelay) && items.isNotEmpty() && !noLoop)
                return
            }

            // Non randomized
            for (slotIndex in 0 until screen.inventoryRows * 9) {
                val slot = screen.inventorySlots.inventorySlots[slotIndex]

                if (delayTimer.hasTimePassed(nextDelay) && slot.stack != null &&
                    (!onlyItemsValue.get() || slot.stack.item !is ItemBlock) && (!noDuplicateValue.get() || slot.stack.maxStackSize > 1 || !mc.thePlayer.inventory.mainInventory.filter { it != null && it.item != null }
                        .map { it.item!! }
                        .contains(slot.stack.item)) && (!inventoryCleaner.state || inventoryCleaner.isUseful(
                        slot.stack,
                        -1
                    ))
                ) {
                    move(screen, slot)
                }
            }
        } else if (autoCloseValue.get() && screen.inventorySlots.windowId == contentReceived && autoCloseTimer.hasTimePassed(
                nextCloseDelay
            )
        ) {
            mc.thePlayer.closeScreen()

            if (silenceValue.get() && !stillDisplayValue.get()) Client.hud.addNotification(
                Notification(
                    "Closed chest.",
                    Notification.Type.INFO
                )
            )
            nextCloseDelay = TimeUtils.randomDelay(autoCloseMinDelayValue.get(), autoCloseMaxDelayValue.get())

            if (once) {
                once = false
                state = false
                return
            }
        }
    }

    @EventTarget
    private fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S30PacketWindowItems)
            contentReceived = packet.func_148911_c()
    }

    private fun move(screen: GuiChest, slot: Slot) {
        screen.handleMouseClick(slot, slot.slotNumber, 0, 1)
        delayTimer.reset()
        nextDelay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
    }

    private fun isEmpty(chest: GuiChest): Boolean {
        val inventoryCleaner = Client.moduleManager[InventoryManager::class.java] as InventoryManager

        for (i in 0 until chest.inventoryRows * 9) {
            val slot = chest.inventorySlots.inventorySlots[i]

            if (slot.stack != null && (!onlyItemsValue.get() || slot.stack.item !is ItemBlock) && (!noDuplicateValue.get() || slot.stack.maxStackSize > 1 || !mc.thePlayer.inventory.mainInventory.filter { it != null && it.item != null }
                    .map { it.item!! }
                    .contains(slot.stack.item)) && (!inventoryCleaner.state || inventoryCleaner.isUseful(
                    slot.stack,
                    -1
                ))
            )
                return false
        }

        return true
    }

    private val fullInventory: Boolean
        get() = mc.thePlayer.inventory.mainInventory.none { it == null }

}