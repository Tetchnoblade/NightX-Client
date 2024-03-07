package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S45PacketTitle

@ModuleInfo(
    name = "AutoAuth",
    spacedName = "Auto Auth",
    category = ModuleCategory.OTHER
)
class AutoAuth : Module() {

    private val doubleRegister = BoolValue("Double-Register", true)
    private val delayValue = IntegerValue("Delay", 5000, 0, 5000, "ms")

    private val loginPackets = arrayListOf<C01PacketChatMessage>()
    private val registerPackets = arrayListOf<C01PacketChatMessage>()
    private val regTimer = MSTimer()
    private val logTimer = MSTimer()

    override fun onEnable() = resetEverything()

    @EventTarget
    fun onWorld(event: WorldEvent) = resetEverything()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (registerPackets.isEmpty())
            regTimer.reset()
        else if (regTimer.hasTimePassed(delayValue.get().toLong())) {
            for (packet in registerPackets)
                PacketUtils.sendPacketNoEvent(packet)
            chat("Successfully registered!")
            registerPackets.clear()
            regTimer.reset()
        }

        if (loginPackets.isEmpty())
            logTimer.reset()
        else if (logTimer.hasTimePassed(delayValue.get().toLong())) {
            for (packet in loginPackets)
                PacketUtils.sendPacketNoEvent(packet)
            chat("Successfully logged in!")
            loginPackets.clear()
            logTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null)
            return

        val packet = event.packet

        if (packet is S45PacketTitle) {
            val messageOrigin = packet.message ?: return
            val message: String = messageOrigin.unformattedText

            if (message.contains("/login", true))
                sendLogin("/login Aspw95639535".replace("%p", "Aspw95639535", true))

            if (message.contains("/register", true))
                sendRegister(
                    ("/register Aspw95639535" + if (doubleRegister.get()) " Aspw95639535" else "").replace(
                        "%p",
                        "Aspw95639535",
                        true
                    )
                )
        }

        if (packet is S02PacketChat) {
            val message: String = packet.chatComponent.unformattedText

            if (message.contains("/login", true))
                sendLogin("/login Aspw95639535".replace("%p", "Aspw95639535", true))

            if (message.contains("/register", true))
                sendRegister(
                    ("/register Aspw95639535" + if (doubleRegister.get()) " Aspw95639535" else "").replace(
                        "%p",
                        "Aspw95639535",
                        true
                    )
                )
        }
    }

    private fun sendLogin(str: String) = loginPackets.add(C01PacketChatMessage(str))
    private fun sendRegister(str: String) = registerPackets.add(C01PacketChatMessage(str))

    private fun resetEverything() {
        registerPackets.clear()
        loginPackets.clear()
        regTimer.reset()
        logTimer.reset()
    }
}