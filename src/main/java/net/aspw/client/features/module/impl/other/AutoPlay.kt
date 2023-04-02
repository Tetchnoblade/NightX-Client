package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.event.ClickEvent
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.IChatComponent
import java.util.*
import kotlin.concurrent.schedule

@ModuleInfo(name = "AutoPlay", spacedName = "Auto Play", category = ModuleCategory.OTHER)
class AutoPlay : Module() {
    private var clickState = 0
    private val modeValue = ListValue(
        "Server",
        arrayOf("RedeSky", "BlocksMC", "Minemora", "Hypixel", "Jartex"),
        "Hypixel"
    )
    private val bwModeValue = ListValue(
        "Mode",
        arrayOf("SOLO", "4v4v4v4"),
        "4v4v4v4",
        { modeValue.get().equals("minefc/heromc_bedwars", true) })
    private val autoStartValue =
        BoolValue("AutoStartAtLobby", true, { modeValue.get().equals("minefc/heromc_bedwars", true) })
    private val replayWhenKickedValue =
        BoolValue("ReplayWhenKicked", true, { modeValue.get().equals("minefc/heromc_bedwars", true) })
    private val showGuiWhenFailedValue =
        BoolValue("ShowGuiWhenFailed", true, { modeValue.get().equals("minefc/heromc_bedwars", true) })
    private val delayValue = IntegerValue("JoinDelay", 3, 0, 7, " seconds")

    private var clicking = false
    private var queued = false
    private var waitForLobby = false

    override fun onEnable() {
        clickState = 0
        clicking = false
        queued = false
        waitForLobby = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "redesky" -> {
                if (clicking && (packet is C0EPacketClickWindow || packet is C07PacketPlayerDigging)) {
                    event.cancelEvent()
                    return
                }
                if (clickState == 2 && packet is S2DPacketOpenWindow) {
                    event.cancelEvent()
                }
            }

            "hypixel" -> {
                if (clickState == 1 && packet is S2DPacketOpenWindow) {
                    event.cancelEvent()
                }
            }
        }

        if (packet is S2FPacketSetSlot) {
            val item = packet.func_149174_e() ?: return
            val windowId = packet.func_149175_c()
            val slot = packet.func_149173_d()
            val itemName = item.unlocalizedName
            val displayName = item.displayName

            when (modeValue.get().lowercase(Locale.getDefault())) {
                "redesky" -> {
                    if (clickState == 0 && windowId == 0 && slot == 42 && itemName.contains(
                            "paper",
                            ignoreCase = true
                        ) && displayName.contains("Jogar novamente", ignoreCase = true)
                    ) {
                        clickState = 1
                        clicking = true
                        queueAutoPlay {
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(6))
                            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(item))
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                            clickState = 2
                        }
                    } else if (clickState == 2 && windowId != 0 && slot == 11 && itemName.contains(
                            "enderPearl",
                            ignoreCase = true
                        )
                    ) {
                        Timer().schedule(500L) {
                            clicking = false
                            clickState = 0
                            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 1919))
                        }
                    }
                }

                "blocksmc", "hypixel" -> {
                    if (clickState == 0 && windowId == 0 && slot == 43 && itemName.contains(
                            "paper",
                            ignoreCase = true
                        )
                    ) {
                        queueAutoPlay {
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(7))
                            repeat(2) {
                                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(item))
                            }
                        }
                        clickState = 1
                    }
                    if (modeValue.equals("hypixel") && clickState == 1 && windowId != 0 && itemName.equals(
                            "item.fireworks",
                            ignoreCase = true
                        )
                    ) {
                        mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 1919))
                        mc.netHandler.addToSendQueue(C0DPacketCloseWindow(windowId))
                    }
                }
            }
        } else if (packet is S02PacketChat) {
            val text = packet.chatComponent.unformattedText
            when (modeValue.get().lowercase(Locale.getDefault())) {
                "minemora" -> {
                    if (text.contains("Has click en alguna de las siguientes opciones", true)) {
                        queueAutoPlay {
                            mc.thePlayer.sendChatMessage("/join")
                        }
                    }
                }

                "blocksmc" -> {
                    if (clickState == 1 && text.contains("Only VIP players can join full servers!", true)) {
                        Client.hud.addNotification(
                            Notification(
                                "Join failed! trying again...",
                                Notification.Type.WARNING,
                                3000L
                            )
                        )
                        // connect failed so try to join again
                        Timer().schedule(1500L) {
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(7))
                            repeat(2) {
                                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                            }
                        }
                    }
                }

                "jartex" -> {
                    val component = packet.chatComponent
                    if (text.contains("Click here to play again", true)) {
                        component.siblings.forEach { sib ->
                            val clickEvent = sib.chatStyle.chatClickEvent
                            if (clickEvent != null && clickEvent.action == ClickEvent.Action.RUN_COMMAND && clickEvent.value.startsWith(
                                    "/"
                                )
                            ) {
                                //println("clickEvent: ${clickEvent.value}")
                                queueAutoPlay {
                                    mc.thePlayer.sendChatMessage(clickEvent.value)
                                }
                            }
                        }
                    }
                }

                "hypixel" -> {
                    fun process(component: IChatComponent) {
                        val value = component.chatStyle.chatClickEvent?.value
                        if (value != null && value.startsWith("/play", true)) {
                            queueAutoPlay {
                                mc.thePlayer.sendChatMessage(value)
                            }
                        }
                        component.siblings.forEach {
                            process(it)
                        }
                    }
                    process(packet.chatComponent)
                }
            }
        }
    }

    private fun queueAutoPlay(delay: Long = delayValue.get().toLong() * 1000, runnable: () -> Unit) {
        if (queued)
            return
        queued = true
        if (this.state) {
            Timer().schedule(delay) {
                queued = false
                if (state) {
                    runnable()
                }
            }
            Client.hud.addNotification(
                Notification(
                    "Sending you to next game in ${delayValue.get()}s...",
                    Notification.Type.INFO,
                    delayValue.get().toLong() * 1000L
                )
            )
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clicking = false
        clickState = 0
        queued = false
    }

    override val tag: String
        get() = modeValue.get()
}
