package net.aspw.client.features.module.modules.client

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.modules.movement.Flight
import net.aspw.client.features.module.modules.movement.Speed
import net.aspw.client.features.module.modules.player.BowLongJump
import net.aspw.client.features.module.modules.player.LongJump
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "LagBack", spacedName = "Lag Back", category = ModuleCategory.CLIENT, array = false)
class LagBack : Module() {
    private val flightValue = BoolValue("Flight", value = false)
    private val speedValue = BoolValue("Speed", value = true)
    private val longJumpValue = BoolValue("LongJump", value = true)
    private val bowLongJumpValue = BoolValue("BowLongJump", value = true)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook) {
            if (longJumpValue.get()) {
                val longJump = Client.moduleManager.getModule(LongJump::class.java)
                if (longJump!!.state) {
                    longJump.state = false
                    Client.hud.addNotification(
                        net.aspw.client.visual.hud.element.elements.Notification(
                            "LagBack LongJump.",
                            net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                        )
                    )
                    if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                        Client.tipSoundManager.popSound.asyncPlay(90f)
                    }
                }
            }
            if (speedValue.get()) {
                val speed = Client.moduleManager.getModule(Speed::class.java)
                if (speed!!.state) {
                    speed.state = false
                    Client.hud.addNotification(
                        net.aspw.client.visual.hud.element.elements.Notification(
                            "LagBack Speed.",
                            net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                        )
                    )
                    if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                        Client.tipSoundManager.popSound.asyncPlay(90f)
                    }
                }
            }
            if (bowLongJumpValue.get()) {
                val bowLongJump = Client.moduleManager.getModule(BowLongJump::class.java)
                if (bowLongJump!!.state) {
                    bowLongJump.state = false
                    Client.hud.addNotification(
                        net.aspw.client.visual.hud.element.elements.Notification(
                            "LagBack BowLongJump.",
                            net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                        )
                    )
                    if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                        Client.tipSoundManager.popSound.asyncPlay(90f)
                    }
                }
            }
            if (flightValue.get()) {
                val flight = Client.moduleManager.getModule(Flight::class.java)
                if (flight!!.state) {
                    flight.state = false
                    Client.hud.addNotification(
                        net.aspw.client.visual.hud.element.elements.Notification(
                            "LagBack Flight.",
                            net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                        )
                    )
                    if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                        Client.tipSoundManager.popSound.asyncPlay(90f)
                    }
                }
            }
        }
    }

    init {
        state = true
    }
}