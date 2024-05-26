package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.util.Vec3
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "InvMove", spacedName = "Inv Move", category = ModuleCategory.MOVEMENT)
class InvMove : Module() {

    val modeValue = ListValue("Mode", arrayOf("Vanilla", "Silent", "Hypixel"), "Vanilla")
    private val noDetectableValue = BoolValue("NoDetectable", false)

    private val positions = LinkedList<Vec3>()
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private val resetTimer = MSTimer()
    private var disableLogger = false
    private var started = false

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!modeValue.get().equals(
                "hypixel",
                true
            ) && (mc.currentScreen !is GuiChat && (!noDetectableValue.get() || mc.currentScreen !is GuiContainer)) || modeValue.get()
                .equals("hypixel", true) && (mc.currentScreen is GuiInventory || mc.currentScreen == null)
        ) {
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
            mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
            mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight)
            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
            mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint)
        }

        if (modeValue.get().equals("hypixel", true)) {
            if (mc.currentScreen is GuiInventory) {
                if (started) {
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    synchronized(positions) {
                        positions.add(
                            Vec3(
                                mc.thePlayer.posX,
                                mc.thePlayer.entityBoundingBox.minY,
                                mc.thePlayer.posZ
                            )
                        )
                    }
                } else {
                    synchronized(positions) {
                        positions.add(
                            Vec3(
                                mc.thePlayer.posX,
                                mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight() / 2,
                                mc.thePlayer.posZ
                            )
                        )
                        positions.add(Vec3(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY, mc.thePlayer.posZ))
                    }
                    resetTimer.reset()
                    started = true
                }
            } else if (started) {
                started = false
                sync()
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "silent" -> if (packet is C16PacketClientStatus && packet.status == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) event.cancelEvent()
            "hypixel" -> if (started && !disableLogger && mc.currentScreen is GuiInventory) {
                if (packet is C03PacketPlayer)
                    event.cancelEvent()
                if (packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C06PacketPlayerPosLook ||
                    packet is C08PacketPlayerBlockPlacement ||
                    packet is C0APacketAnimation ||
                    packet is C0BPacketEntityAction || packet is C02PacketUseEntity || packet is C0FPacketConfirmTransaction
                ) {
                    event.cancelEvent()
                    packets.add(packet)
                }
            }
        }
    }

    private fun sync() {
        try {
            disableLogger = true
            while (packets.isNotEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }
            disableLogger = false
        } catch (_: Exception) {
            disableLogger = false
        }
        synchronized(positions) { positions.clear() }
    }

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.currentScreen != null)
            mc.gameSettings.keyBindForward.pressed = false
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindBack) || mc.currentScreen != null)
            mc.gameSettings.keyBindBack.pressed = false
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.currentScreen != null)
            mc.gameSettings.keyBindRight.pressed = false
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.currentScreen != null)
            mc.gameSettings.keyBindLeft.pressed = false
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.currentScreen != null)
            mc.gameSettings.keyBindJump.pressed = false
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSprint) || mc.currentScreen != null)
            mc.gameSettings.keyBindSprint.pressed = false
        started = false
        sync()
    }
}