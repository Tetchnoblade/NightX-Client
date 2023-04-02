package net.aspw.client.features.module.impl.player

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.injection.implementations.IItemStack
import net.aspw.client.utils.ClientUtils
import net.aspw.client.utils.InventoryHelper
import net.aspw.client.utils.InventoryUtils
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.item.ArmorPart
import net.aspw.client.utils.item.ItemHelper
import net.aspw.client.utils.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.Enchantment
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*

@ModuleInfo(
    name = "InventoryManager",
    spacedName = "Inventory Manager",
    category = ModuleCategory.PLAYER
)
class InventoryManager : Module() {

    /**
     * OPTIONS
     */

    // Delay
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 14, 0, 1000, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minDelayValue.get()
            if (minCPS > newValue) set(minCPS)
        }
    }

    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 10, 0, 1000, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelay = maxDelayValue.get()
            if (maxDelay < newValue) set(maxDelay)
        }
    }

    private val eventModeValue = ListValue("OnEvent", arrayOf("Update", "MotionPre", "MotionPost"), "MotionPost")

    // Inventory options
    private val invOpenValue = BoolValue("InvOpen", false)
    private val invSpoof = BoolValue("InvSpoof", false)
    private val invSpoofOld = BoolValue("InvSpoof-Old", false, { invSpoof.get() })

    // Others
    private val noMoveValue = BoolValue("NoMove", false)
    private val hotbarValue = BoolValue("Hotbar", false)
    private val randomSlotValue = BoolValue("RandomSlot", false)
    private val sortValue = BoolValue("Sort", true)
    private val cleanGarbageValue = BoolValue("CleanGarbage", true)
    private val itemDelayValue = IntegerValue("ItemDelay", 100, 0, 5000, "ms")
    private val ignoreVehiclesValue = BoolValue("IgnoreVehicles", true)
    private val onlyPositivePotionValue = BoolValue("OnlyPositivePotion", true)

    // NBT
    private val nbtGoalValue =
        ListValue("NBTGoal", ItemHelper.EnumNBTPriorityType.values().map { it.toString() }.toTypedArray(), "NONE")
    private val nbtItemNotGarbage = BoolValue("NBTItemNotGarbage", true, { !nbtGoalValue.equals("NONE") })
    private val nbtArmorPriority = FloatValue("NBTArmorPriority", 0f, 0f, 5f, { !nbtGoalValue.equals("NONE") })
    private val nbtWeaponPriority = FloatValue("NBTWeaponPriority", 0f, 0f, 5f, { !nbtGoalValue.equals("NONE") })

    private val items = arrayOf(
        "None",
        "Ignore",
        "Sword",
        "Bow",
        "Pickaxe",
        "Axe",
        "Food",
        "Block",
        "Water",
        "Gapple",
        "Pearl",
        "Potion"
    )
    private val sortSlot1Value = ListValue("SortSlot-1", items, "Sword", { sortValue.get() })
    private val sortSlot2Value = ListValue("SortSlot-2", items, "Pickaxe", { sortValue.get() })
    private val sortSlot3Value = ListValue("SortSlot-3", items, "Axe", { sortValue.get() })
    private val sortSlot4Value = ListValue("SortSlot-4", items, "None", { sortValue.get() })
    private val sortSlot5Value = ListValue("SortSlot-5", items, "Gapple", { sortValue.get() })
    private val sortSlot6Value = ListValue("SortSlot-6", items, "None", { sortValue.get() })
    private val sortSlot7Value = ListValue("SortSlot-7", items, "Bow", { sortValue.get() })
    private val sortSlot8Value = ListValue("SortSlot-8", items, "Block", { sortValue.get() })
    private val sortSlot9Value = ListValue("SortSlot-9", items, "Potion", { sortValue.get() })

    private var garbageQueue = mutableMapOf<Int, ItemStack>()
    private var armorQueue = arrayOf<ArmorPart?>()

    private val goal: ItemHelper.EnumNBTPriorityType
        get() = ItemHelper.EnumNBTPriorityType.valueOf(nbtGoalValue.get())

    private var spoofInventory = false
        set(value) {
            if (value != field && !invOpenValue.get()) {
                if (value)
                    InventoryHelper.openPacket()
                else
                    InventoryHelper.closePacket()
            }
            field = value
        }

    /**
     * VALUES
     */

    private var delay = 0L

    override fun onEnable() {
        if (invSpoof.get() && !invSpoofOld.get())
            spoofInventory = false

        garbageQueue.clear()
        armorQueue = arrayOf<ArmorPart?>()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (invSpoof.get() && !invSpoofOld.get())
            spoofInventory = false

        garbageQueue.clear()
        armorQueue = arrayOf<ArmorPart?>()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (eventModeValue.get().equals("update", true))
            performManager()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (eventModeValue.get().equals("motion${event.eventState.stateName}", true))
            performManager()
    }

    fun performManager() {
        if (!InventoryUtils.CLICK_TIMER.hasTimePassed(
                delay
            ) ||
            noMoveValue.get() && MovementUtils.isMoving() ||
            mc.thePlayer.openContainer != null && mc.thePlayer.openContainer.windowId != 0
        )
            return

        findQueueItems()

        if (hotbarValue.get() && !spoofInventory && mc.currentScreen !is GuiInventory) {
            for (index in 0..8) {
                val bestItem = mc.thePlayer.inventory.getStackInSlot(index) ?: continue
                if (bestItem.item != null && garbageQueue.containsValue(bestItem)) {
                    if (index != mc.thePlayer.inventory.currentItem) mc.netHandler.addToSendQueue(
                        C09PacketHeldItemChange(index)
                    )
                    mc.netHandler.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.DROP_ALL_ITEMS,
                            BlockPos.ORIGIN,
                            EnumFacing.DOWN
                        )
                    )
                    if (index != mc.thePlayer.inventory.currentItem) mc.netHandler.addToSendQueue(
                        C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem)
                    )

                    garbageQueue.remove(index, bestItem)
                }
            }
        }

        if (mc.currentScreen !is GuiInventory && invOpenValue.get() && !invSpoof.get()) return

        if (garbageQueue.isEmpty() && armorQueue.size <= 0) {
            if (invSpoof.get() && !invSpoofOld.get())
                spoofInventory = false
            return
        }

        if (sortValue.get())
            sortHotbar()

        if (invSpoof.get() && !invSpoofOld.get())
            spoofInventory = true

        if (cleanGarbageValue.get()) while (InventoryUtils.CLICK_TIMER.hasTimePassed(delay)) {
            val garbageItems = garbageQueue.keys.toMutableList()

            // Shuffle items
            if (randomSlotValue.get())
                garbageItems.shuffle()

            val garbageItem = garbageItems.firstOrNull() ?: break

            // Drop all useless items
            val openInventory = mc.currentScreen !is GuiInventory && invSpoof.get() && invSpoofOld.get()

            if (openInventory)
                InventoryHelper.openPacket()

            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, garbageItem, 1, 4, mc.thePlayer)

            if (openInventory)
                InventoryHelper.closePacket()

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            if (delay == 0L || InventoryUtils.CLICK_TIMER.hasTimePassed(delay))
                break // prevent infinite loop, resulting in frozen state
        }
    }

    /**
     * INVENTORY SORTER
     */

    /**
     * Sort hotbar
     */
    private fun sortHotbar() {
        for (index in 0..8) {
            val bestItem = findBetterItem(index, mc.thePlayer.inventory.getStackInSlot(index)) ?: continue

            if (bestItem != index) {
                val openInventory = mc.currentScreen !is GuiInventory && invSpoof.get() && invSpoofOld.get()

                if (openInventory)
                    InventoryHelper.openPacket()

                mc.playerController.windowClick(
                    0, if (bestItem < 9) bestItem + 36 else bestItem, index,
                    2, mc.thePlayer
                )

                if (openInventory)
                    InventoryHelper.closePacket()

                delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
                break
            }
        }
    }

    private fun findQueueItems() {
        garbageQueue.clear()
        garbageQueue = items(9, 45).filter { !isUseful(it.value, it.key) }.toMutableMap()
    }

    /**
     * Shift+Left clicks the specified item
     *
     * @param item        Slot of the item to click
     * @param isArmorSlot
     * @return True if it is unable to move the item
     */
    fun move(item: Int, isArmorSlot: Boolean): Boolean {
        if (!isArmorSlot && item < 9 && hotbarValue.get() && mc.currentScreen !is GuiInventory && !spoofInventory) {
            if (item != mc.thePlayer.inventory.currentItem) mc.netHandler.addToSendQueue(C09PacketHeldItemChange(item))
            mc.netHandler.addToSendQueue(
                C08PacketPlayerBlockPlacement(
                    mc.thePlayer.inventoryContainer.getSlot(item).stack
                )
            )
            if (item != mc.thePlayer.inventory.currentItem) mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

            return true
        } else if (!(noMoveValue.get() && MovementUtils.isMoving()) && (!invOpenValue.get() || mc.currentScreen is GuiInventory) && item != -1) {
            val openInventory = invSpoof.get() && mc.currentScreen !is GuiInventory && !invSpoofOld.get()

            if (openInventory)
                InventoryHelper.openPacket()

            mc.playerController.windowClick(
                mc.thePlayer.inventoryContainer.windowId,
                (if (isArmorSlot) item else if (item < 9) item + 36 else item),
                0,
                1,
                mc.thePlayer
            )

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

            if (openInventory)
                InventoryHelper.closePacket()

            return true
        }

        return false
    }

    /**
     * Checks if the item is useful
     *
     * @param slot Slot id of the item. If the item isn't in the inventory -1
     * @return Returns true when the item is useful
     */
    fun isUseful(itemStack: ItemStack, slot: Int): Boolean {
        return try {
            val item = itemStack.item

            if (item is ItemSword || item is ItemTool) {
                if (slot >= 36 && findBetterItem(
                        slot - 36,
                        mc.thePlayer.inventory.getStackInSlot(slot - 36)
                    ) == slot - 36
                ) {
                    return true
                }

                for (i in 0..8) {
                    if (type(i).equals("sword", true) && item is ItemSword ||
                        type(i).equals("pickaxe", true) && item is ItemPickaxe ||
                        type(i).equals("axe", true) && item is ItemAxe
                    ) {
                        if (findBetterItem(i, mc.thePlayer.inventory.getStackInSlot(i)) == null) {
                            return true
                        }
                    }
                }

                val damage = (itemStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                    ?: 0.0) + ItemHelper.getWeaponEnchantFactor(itemStack, nbtWeaponPriority.get(), goal)

                items(0, 45).none { (_, stack) ->
                    stack != itemStack && stack.javaClass == itemStack.javaClass && damage <= (stack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                        ?: 0.0) + ItemHelper.getWeaponEnchantFactor(stack, nbtWeaponPriority.get(), goal)
                }
            } else if (item is ItemBow) {
                val currPower = ItemHelper.getEnchantment(itemStack, Enchantment.power)

                items().none { (_, stack) ->
                    itemStack != stack && stack.item is ItemBow &&
                            currPower <= ItemHelper.getEnchantment(stack, Enchantment.power)
                }
            } else if (item is ItemArmor) {
                val currArmor = ArmorPart(itemStack, slot)

                items().none { (slot, stack) ->
                    if (stack != itemStack && stack.item is ItemArmor) {
                        val armor = ArmorPart(stack, slot)

                        if (armor.armorType != currArmor.armorType) {
                            false
                        } else {
                            ItemHelper.compareArmor(currArmor, armor, nbtArmorPriority.get(), goal) <= 0
                        }
                    } else {
                        false
                    }
                }
            } else if (itemStack.unlocalizedName == "item.compass") {
                items(0, 45).none { (_, stack) -> itemStack != stack && stack.unlocalizedName == "item.compass" }
            } else {
                (nbtItemNotGarbage.get() && ItemHelper.hasNBTGoal(itemStack, goal)) ||
                        item is ItemFood || itemStack.unlocalizedName == "item.arrow" ||
                        (item is ItemBlock && !InventoryHelper.isBlockListBlock(item)) ||
                        item is ItemBed || (item is ItemPotion && (!onlyPositivePotionValue.get() || InventoryHelper.isPositivePotion(
                    item,
                    itemStack
                ))) ||
                        item is ItemEnderPearl || item is ItemBucket || ignoreVehiclesValue.get() && (item is ItemBoat || item is ItemMinecart)
            }
        } catch (ex: Exception) {
            ClientUtils.getLogger()
                .error(Client.CLIENT_CHAT + "§fFailed to check item: ${itemStack.unlocalizedName}.", ex)
            true
        }
    }

    private fun findBetterItem(targetSlot: Int, slotStack: ItemStack?): Int? {
        val type = type(targetSlot)

        when (type.lowercase(Locale.getDefault())) {
            "sword", "pickaxe", "axe" -> {
                val currentType: Class<out Item> = when {
                    type.equals("Sword", ignoreCase = true) -> ItemSword::class.java
                    type.equals("Pickaxe", ignoreCase = true) -> ItemPickaxe::class.java
                    type.equals("Axe", ignoreCase = true) -> ItemAxe::class.java
                    else -> return null
                }

                var bestWeapon = if (slotStack?.item?.javaClass == currentType) {
                    targetSlot
                } else {
                    -1
                }

                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, itemStack ->
                    if (itemStack != null && itemStack.item.javaClass == currentType && !type(index).equals(
                            type,
                            ignoreCase = true
                        )
                    ) {
                        if (bestWeapon == -1) {
                            bestWeapon = index
                        } else {
                            val currDamage = (itemStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                                ?: 0.0) + ItemHelper.getWeaponEnchantFactor(itemStack, nbtWeaponPriority.get(), goal)

                            val bestStack = mc.thePlayer.inventory.getStackInSlot(bestWeapon) ?: return@forEachIndexed
                            val bestDamage = (bestStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                                ?: 0.0) + ItemHelper.getWeaponEnchantFactor(bestStack, nbtWeaponPriority.get(), goal)

                            if (bestDamage < currDamage) {
                                bestWeapon = index
                            }
                        }
                    }
                }

                return if (bestWeapon != -1 || bestWeapon == targetSlot) bestWeapon else null
            }

            "bow" -> {
                var bestBow = if (slotStack?.item is ItemBow) targetSlot else -1
                var bestPower = if (bestBow != -1) {
                    ItemHelper.getEnchantment(slotStack!!, Enchantment.power)
                } else {
                    0
                }

                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, itemStack ->
                    if (itemStack?.item is ItemBow && !type(index).equals(type, ignoreCase = true)) {
                        if (bestBow == -1) {
                            bestBow = index
                        } else {
                            val power = ItemHelper.getEnchantment(itemStack, Enchantment.power)

                            if (ItemHelper.getEnchantment(itemStack, Enchantment.power) > bestPower) {
                                bestBow = index
                                bestPower = power
                            }
                        }
                    }
                }

                return if (bestBow != -1) bestBow else null
            }

            "food" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemFood && item !is ItemAppleGold && !type(index).equals("Food", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemFood

                        return if (replaceCurr) index else null
                    }
                }
            }

            "block" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemBlock && !InventoryHelper.isBlockListBlock(item) &&
                        !type(index).equals("Block", ignoreCase = true)
                    ) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemBlock

                        return if (replaceCurr) index else null
                    }
                }
            }

            "water" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemBucket && item.isFull == Blocks.flowing_water && !type(index).equals(
                            "Water",
                            ignoreCase = true
                        )
                    ) {
                        val replaceCurr =
                            slotStack == null || slotStack.item !is ItemBucket || (slotStack.item as ItemBucket).isFull != Blocks.flowing_water

                        return if (replaceCurr) index else null
                    }
                }
            }

            "gapple" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemAppleGold && !type(index).equals("Gapple", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemAppleGold

                        return if (replaceCurr) index else null
                    }
                }
            }

            "pearl" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemEnderPearl && !type(index).equals("Pearl", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemEnderPearl

                        return if (replaceCurr) index else null
                    }
                }
            }

            "potion" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if ((item is ItemPotion && ItemPotion.isSplash(stack.itemDamage)) &&
                        !type(index).equals("Potion", ignoreCase = true)
                    ) {
                        val replaceCurr =
                            slotStack == null || slotStack.item !is ItemPotion || !ItemPotion.isSplash(slotStack.itemDamage)

                        return if (replaceCurr) index else null
                    }
                }
            }
        }

        return null
    }

    /**
     * Get items in inventory
     */
    private fun items(start: Int = 0, end: Int = 45): MutableMap<Int, ItemStack> {
        val items = mutableMapOf<Int, ItemStack>()

        for (i in end - 1 downTo start) {
            val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
            itemStack.item ?: continue

            if (i in 36..44 && type(i).equals("Ignore", ignoreCase = true))
                continue

            if (System.currentTimeMillis() - (itemStack as IItemStack).itemDelay >= itemDelayValue.get())
                items[i] = itemStack
        }

        return items
    }

    /**
     * Get type of [targetSlot]
     */
    private fun type(targetSlot: Int) = when (targetSlot) {
        0 -> sortSlot1Value.get()
        1 -> sortSlot2Value.get()
        2 -> sortSlot3Value.get()
        3 -> sortSlot4Value.get()
        4 -> sortSlot5Value.get()
        5 -> sortSlot6Value.get()
        6 -> sortSlot7Value.get()
        7 -> sortSlot8Value.get()
        8 -> sortSlot9Value.get()
        else -> ""
    }
}
