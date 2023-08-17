package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.features.module.impl.combat.TPAura
import net.aspw.client.features.module.impl.movement.Flight
import net.aspw.client.features.module.impl.movement.LongJump
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.player.BowLongJump
import net.aspw.client.features.module.impl.player.ChestStealer
import net.aspw.client.features.module.impl.player.InvManager
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "LagBack", spacedName = "Lag Back", description = "", category = ModuleCategory.OTHER, array = false)
class LagBack : Module() {
    private val invManagerValue = BoolValue("InvManager-WorldChange", value = false)
    private val stealerValue = BoolValue("ChestStealer-WorldChange", value = false)
    private val flightValue = BoolValue("Flight-LagBack", value = false)
    private val killAuraValue = BoolValue("KillAura-WorldChange", value = true)
    private val tpAuraValue = BoolValue("TPAura-WorldChange", value = true)
    private val flightwValue = BoolValue("Flight-WorldChange", value = true)
    private val speedwValue = BoolValue("Speed-WorldChange", value = true)
    private val speedValue = BoolValue("Speed-LagBack", value = true)
    private val longJumpValue = BoolValue("LongJump-LagBack", value = true)
    private val bowLongJumpValue = BoolValue("BowLongJump-LagBack", value = true)

    @EventTarget
    fun onWorld(e: WorldEvent) {
        val killAura = Client.moduleManager.getModule(KillAura::class.java)
        val flight = Client.moduleManager.getModule(Flight::class.java)
        val speed = Client.moduleManager.getModule(Speed::class.java)
        val invManager = Client.moduleManager.getModule(InvManager::class.java)
        val chestStealer = Client.moduleManager.getModule(ChestStealer::class.java)
        val tpAura = Client.moduleManager.getModule(TPAura::class.java)

        if (tpAura!!.state && tpAuraValue.get()) {
            tpAura.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "TPAura was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
        }
        if (killAura!!.state && killAuraValue.get()) {
            killAura.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "KillAura was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
        }
        if (flight!!.state && flightwValue.get()) {
            flight.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "Fly was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
        }
        if (speed!!.state && speedwValue.get()) {
            speed.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "Speed was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
        }
        if (invManager!!.state && invManagerValue.get()) {
            invManager.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "InvManager was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
        }
        if (chestStealer!!.state && stealerValue.get()) {
            chestStealer.state = false
            Client.hud.addNotification(
                net.aspw.client.visual.hud.element.elements.Notification(
                    "ChestStealer was disabled",
                    net.aspw.client.visual.hud.element.elements.Notification.Type.WARNING
                )
            )
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
                        "Disabling Fly due to lag back",
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