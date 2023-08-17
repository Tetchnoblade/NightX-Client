package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.network.play.client.C01PacketChatMessage

@ModuleInfo(name = "ChatFilter", spacedName = "Chat Filter", description = "", category = ModuleCategory.OTHER)
class ChatFilter : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C01PacketChatMessage) {
            val chatMessage = event.packet
            val message = chatMessage.message
            val stringBuilder = StringBuilder()

            for (c in message.toCharArray())
                if (c.code in 33..128)
                    stringBuilder.append(Character.toChars(c.code + 65248)) else stringBuilder.append(c)

            chatMessage.message = stringBuilder.toString()
        }
    }
}