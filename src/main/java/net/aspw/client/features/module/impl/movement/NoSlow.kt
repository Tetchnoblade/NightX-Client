package net.aspw.client.features.module.impl.movement

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.utils.ClientUtils
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.server.S30PacketWindowItems
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*

@ModuleInfo(name = "NoSlow", spacedName = "No Slow", category = ModuleCategory.MOVEMENT)
class NoSlow : Module() {
    private val msTimer = MSTimer()
    private val modeValue = ListValue(
        "Mode",
        arrayOf(
            "Vanilla",
            "Watchdog",
            "LatestWatchdog",
            "OldHypixel",
            "Blink",
            "Experimental",
            "NCP",
            "NewNCP",
            "AAC",
            "AAC5",
            "Custom"
        ),
        "NCP"
    )
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F, "x")
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F, "x")
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F, "x")
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F, "x")
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F, "x")
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F, "x")
    val sneakForwardMultiplier = FloatValue("SneakForwardMultiplier", 0.3F, 0.3F, 1.0F, "x")
    val sneakStrafeMultiplier = FloatValue("SneakStrafeMultiplier", 0.3F, 0.3F, 1.0F, "x")
    private val customRelease = BoolValue("CustomReleasePacket", false, { modeValue.get().equals("custom", true) })
    private val customPlace = BoolValue("CustomPlacePacket", false, { modeValue.get().equals("custom", true) })
    private val customOnGround = BoolValue("CustomOnGround", false, { modeValue.get().equals("custom", true) })
    private val customDelayValue =
        IntegerValue("CustomDelay", 60, 0, 1000, "ms", { modeValue.get().equals("custom", true) })
    private val testValue = BoolValue("SendPacket", false, { modeValue.get().equals("watchdog", true) })
    private val ciucValue = BoolValue("CheckInUseCount", false, { modeValue.get().equals("blink", true) })
    private val packetTriggerValue = ListValue(
        "PacketTrigger",
        arrayOf("PreRelease", "PostRelease"),
        "PostRelease",
        { modeValue.get().equals("blink", true) })
    private val debugValue =
        BoolValue("Debug", false, { modeValue.get().equals("watchdog", true) || modeValue.get().equals("blink", true) })

    // Soulsand
    val soulsandValue = BoolValue("Soulsand", true)
    val liquidPushValue = BoolValue("LiquidPush", true)

    private val blinkPackets = mutableListOf<Packet<INetHandlerPlayServer>>()
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0
    private var lastOnGround = false

    private var fasterDelay = false
    private var placeDelay = 0L
    private val timer = MSTimer()

    override fun onEnable() {
        blinkPackets.clear()
        msTimer.reset()
    }

    override fun onDisable() {
        blinkPackets.forEach {
            PacketUtils.sendPacketNoEvent(it)
        }
        blinkPackets.clear()
    }

    override val tag: String
        get() = modeValue.get()

    private fun sendPacket(
        event: MotionEvent,
        sendC07: Boolean,
        sendC08: Boolean,
        delay: Boolean,
        delayValue: Long,
        onGround: Boolean,
        watchDog: Boolean = false
    ) {
        val digging = C07PacketPlayerDigging(
            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
            BlockPos(-1, -1, -1),
            EnumFacing.DOWN
        )
        val blockPlace = C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem())
        val blockMent = C08PacketPlayerBlockPlacement(
            BlockPos(-1, -1, -1),
            255,
            mc.thePlayer.inventory.getCurrentItem(),
            0f,
            0f,
            0f
        )
        if (onGround && !mc.thePlayer.onGround) {
            return
        }
        if (sendC07 && event.eventState == EventState.PRE) {
            if (delay && msTimer.hasTimePassed(delayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if (!delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if (sendC08 && event.eventState == EventState.POST) {
            if (delay && msTimer.hasTimePassed(delayValue) && !watchDog) {
                mc.netHandler.addToSendQueue(blockPlace)
                msTimer.reset()
            } else if (!delay && !watchDog) {
                mc.netHandler.addToSendQueue(blockPlace)
            } else if (watchDog) {
                mc.netHandler.addToSendQueue(blockMent)
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val killAura = Client.moduleManager[KillAura::class.java]!!
        if (modeValue.get().equals(
                "watchdog",
                true
            ) && packet is S30PacketWindowItems && (mc.thePlayer.isUsingItem)
        ) {
            event.cancelEvent()
            if (debugValue.get())
                ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "detected reset item packet")
        }
        if (modeValue.get().equals(
                "blink",
                true
            ) && !(killAura.state && killAura.blockingStatus) && mc.thePlayer.itemInUse != null && mc.thePlayer.itemInUse.item != null
        ) {
            val item = mc.thePlayer.itemInUse.item
            if (mc.thePlayer.isUsingItem && (item is ItemFood || item is ItemBucketMilk || item is ItemPotion) && (!ciucValue.get() || mc.thePlayer.itemInUseCount >= 1)) {
                if (packet is C04PacketPlayerPosition || packet is C06PacketPlayerPosLook) {
                    if (mc.thePlayer.positionUpdateTicks >= 20 && packetTriggerValue.get()
                            .equals("postrelease", true)
                    ) {
                        (packet as C03PacketPlayer).x = lastX
                        packet.y = lastY
                        packet.z = lastZ
                        packet.onGround = lastOnGround
                        if (debugValue.get())
                            ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "pos update reached 20")
                    } else {
                        event.cancelEvent()
                        if (packetTriggerValue.get().equals("postrelease", true))
                            PacketUtils.sendPacketNoEvent(C03PacketPlayer(lastOnGround))
                        blinkPackets.add(packet as Packet<INetHandlerPlayServer>)
                        if (debugValue.get())
                            ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "packet player (movement) added at ${blinkPackets.size - 1}")
                    }
                } else if (packet is C05PacketPlayerLook) {
                    event.cancelEvent()
                    if (packetTriggerValue.get().equals("postrelease", true))
                        PacketUtils.sendPacketNoEvent(C03PacketPlayer(lastOnGround))
                    blinkPackets.add(packet as Packet<INetHandlerPlayServer>)
                    if (debugValue.get())
                        ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "packet player (rotation) added at ${blinkPackets.size - 1}")
                } else if (packet is C03PacketPlayer) {
                    if (packetTriggerValue.get().equals("prerelease", true) || packet.onGround != lastOnGround) {
                        event.cancelEvent()
                        blinkPackets.add(packet as Packet<INetHandlerPlayServer>)
                        if (debugValue.get())
                            ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "packet player (idle) added at ${blinkPackets.size - 1}")
                    }
                }
                if (packet is C0BPacketEntityAction) {
                    event.cancelEvent()
                    blinkPackets.add(packet as Packet<INetHandlerPlayServer>)
                    if (debugValue.get())
                        ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "packet action added at ${blinkPackets.size - 1}")
                }
                if (packet is C07PacketPlayerDigging && packetTriggerValue.get().equals("prerelease", true)) {
                    if (blinkPackets.size > 0) {
                        blinkPackets.forEach {
                            PacketUtils.sendPacketNoEvent(it)
                        }
                        if (debugValue.get())
                            ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "sent ${blinkPackets.size} packets.")
                        blinkPackets.clear()
                    }
                }
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (!MovementUtils.isMoving() && !modeValue.get().equals("blink", true))
            return

        val killAura = Client.moduleManager[KillAura::class.java]!!

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "latestwatchdog" -> {
                if (!mc.thePlayer.isUsingItem || mc.thePlayer.heldItem.item !is ItemSword) return
                if (event.eventState == EventState.PRE) {
                    PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1))
                    PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                }
                if (event.eventState == EventState.POST) {
                    PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                }
            }

            "aac5" -> if (event.eventState == EventState.POST && (mc.thePlayer.isUsingItem || mc.thePlayer.isBlocking || killAura.blockingStatus)
            ) {
                PacketUtils.sendPacketNoEvent(
                    C08PacketPlayerBlockPlacement(
                        BlockPos(-1, -1, -1),
                        255,
                        mc.thePlayer.inventory.getCurrentItem(),
                        0f,
                        0f,
                        0f
                    )
                )
            }

            "watchdog" -> if (testValue.get() && (!killAura.state || !killAura.blockingStatus)
                && event.eventState == EventState.PRE
                && mc.thePlayer.itemInUse != null && mc.thePlayer.itemInUse.item != null
            ) {
                val item = mc.thePlayer.itemInUse.item
                if (mc.thePlayer.isUsingItem && (item is ItemFood || item is ItemBucketMilk || item is ItemPotion) && mc.thePlayer.getItemInUseCount() >= 1)
                    PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }

            "blink" -> {
                if (event.eventState == EventState.PRE && !mc.thePlayer.isUsingItem && !mc.thePlayer.isBlocking) {
                    lastX = event.x
                    lastY = event.y
                    lastZ = event.z
                    lastOnGround = event.onGround
                    if (blinkPackets.size > 0 && packetTriggerValue.get().equals("postrelease", true)) {
                        blinkPackets.forEach {
                            PacketUtils.sendPacketNoEvent(it)
                        }
                        if (debugValue.get())
                            ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "sent ${blinkPackets.size} packets.")
                        blinkPackets.clear()
                    }
                }
            }

            "experimental" -> {
                if ((mc.thePlayer.isUsingItem || mc.thePlayer.isBlocking) && timer.hasTimePassed(placeDelay)) {
                    mc.playerController.syncCurrentPlayItem()
                    mc.netHandler.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                            BlockPos.ORIGIN,
                            EnumFacing.DOWN
                        )
                    )
                    if (event.eventState == EventState.POST) {
                        placeDelay = 200L
                        if (fasterDelay) {
                            placeDelay = 100L
                            fasterDelay = false
                        } else
                            fasterDelay = true
                        timer.reset()
                    }
                }
            }

            else -> {
                if (!mc.thePlayer.isBlocking && !killAura.blockingStatus)
                    return
                when (modeValue.get().lowercase(Locale.getDefault())) {
                    "aac" -> {
                        if (mc.thePlayer.ticksExisted % 3 == 0)
                            sendPacket(event, true, false, false, 0, false)
                        else
                            sendPacket(event, false, true, false, 0, false)
                    }

                    "ncp" -> sendPacket(event, true, true, false, 0, false)

                    "newncp" -> {
                        if (mc.thePlayer.ticksExisted % 2 == 0)
                            sendPacket(event, true, false, false, 50, true)
                        else
                            sendPacket(event, false, true, false, 0, true, true)
                    }

                    "oldhypixel" -> {
                        if (event.eventState == EventState.PRE)
                            mc.netHandler.addToSendQueue(
                                C07PacketPlayerDigging(
                                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                    BlockPos(-1, -1, -1),
                                    EnumFacing.DOWN
                                )
                            )
                        else
                            mc.netHandler.addToSendQueue(
                                C08PacketPlayerBlockPlacement(
                                    BlockPos(-1, -1, -1),
                                    255,
                                    null,
                                    0.0f,
                                    0.0f,
                                    0.0f
                                )
                            )
                    }

                    "custom" -> sendPacket(
                        event,
                        customRelease.get(),
                        customPlace.get(),
                        customDelayValue.get() > 0,
                        customDelayValue.get().toLong(),
                        customOnGround.get()
                    )
                }
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item?, isForward: Boolean) = when (item) {
        is ItemFood, is ItemPotion, is ItemBucketMilk -> {
            if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
        }

        is ItemSword -> {
            if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
        }

        is ItemBow -> {
            if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
        }

        else -> 0.2F
    }
}