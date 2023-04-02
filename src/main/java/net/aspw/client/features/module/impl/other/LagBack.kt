package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.features.module.impl.movement.Flight
import net.aspw.client.features.module.impl.movement.LongJump
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.player.BowLongJump
import net.aspw.client.features.module.impl.player.InventoryManager
import net.aspw.client.features.module.impl.player.Stealer
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "LagBack", spacedName = "Lag Back", category = ModuleCategory.OTHER, array = false)
class LagBack : Module() {
    private val killAuraValue = BoolValue("KillAura-WorldChange", value = true)
    private val flightwValue = BoolValue("Flight-WorldChange", value = true)
    private val speedwValue = BoolValue("Speed-WorldChange", value = true)
    private val invManagerValue = BoolValue("InventoryManager-WorldChange", value = false)
    private val stealerValue = BoolValue("Stealer-WorldChange", value = false)
    private val flightValue = BoolValue("Flight-LagBack", value = false)
    private val speedValue = BoolValue("Speed-LagBack", value = true)
    private val longJumpValue = BoolValue("LongJump-LagBack", value = true)
    private val bowLongJumpValue = BoolValue("BowLongJump-LagBack", value = true)

    @EventTarget
    fun onWorld(e: WorldEvent) {
        val killAura = Client.moduleManager.getModule(KillAura::class.java)
        val flight = Client.moduleManager.getModule(Flight::class.java)
        val speed = Client.moduleManager.getModule(Speed::class.java)
        val invManager = Client.moduleManager.getModule(InventoryManager::class.java)
        val stealer = Client.moduleManager.getModule(Stealer::class.java)

        if (killAura!!.state && killAuraValue.get()) {
            killAura.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "KillAura was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
            }
        }
        if (flight!!.state && flightwValue.get()) {
            flight.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "Flight was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
            }
        }
        if (speed!!.state && speedwValue.get()) {
            speed.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "Speed was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
            }
        }
        if (invManager!!.state && invManagerValue.get()) {
            invManager.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "InventoryManager was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
            }
        }
        if (stealer!!.state && stealerValue.get()) {
            stealer.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "Stealer was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook) {
            val longJump = Client.moduleManager.getModule(LongJump::class.java)
            val speed = Client.moduleManager.getModule(Speed::class.java)
            val bowLongJump = Client.moduleManager.getModule(BowLongJump::class.java)
            val flight = Client.moduleManager.getModule(Flight::class.java)

            if (longJump!!.state && longJumpValue.get()) {
                longJump.state = false
                Client.hud.addNotification(
                    net.aspw.client.visual.hud.element.elements.Notification(
                        "Disabling LongJump due to lag back",
                        net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                    )
                )
                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
            }
            if (speed!!.state && speedValue.get()) {
                speed.state = false
                Client.hud.addNotification(
                    net.aspw.client.visual.hud.element.elements.Notification(
                        "Disabling Speed due to lag back",
                        net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                    )
                )
                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
            }
            if (bowLongJump!!.state && bowLongJumpValue.get()) {
                bowLongJump.state = false
                Client.hud.addNotification(
                    net.aspw.client.visual.hud.element.elements.Notification(
                        "Disabling BowLongJump due to lag back",
                        net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                    )
                )
                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
            }
            if (flight!!.state && flightValue.get()) {
                flight.state = false
                Client.hud.addNotification(
                    net.aspw.client.visual.hud.element.elements.Notification(
                        "Disabling Flight due to lag back",
                        net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                    )
                )
                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
            }
        }
    }

    init {
        state = true
    }
}