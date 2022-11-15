package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.minecraft.network.play.client.C03PacketPlayer

class PacketVClipCommand : Command("pvclip", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                val y = args[1].toDouble()
                val entity = if (mc.thePlayer.isRiding) mc.thePlayer.ridingEntity else mc.thePlayer

                mc.netHandler.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        entity.posX,
                        entity.posY + y,
                        entity.posZ,
                        false
                    )
                )
                LiquidBounce.hud.addNotification(
                    Notification(
                        "Successfully Teleported!",
                        Notification.Type.SUCCESS
                    )
                )
            } catch (ex: NumberFormatException) {
                chatSyntaxError()
            }

            return
        }

        chatSyntax("pvclip <value>")
    }
}
