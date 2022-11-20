package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.ui.client.hud.element.elements.Notification

class VClipCommand : Command("vclip", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                val y = args[1].toDouble()
                val entity = if (mc.thePlayer.isRiding) mc.thePlayer.ridingEntity else mc.thePlayer

                entity.setPosition(entity.posX, entity.posY + y, entity.posZ)
                NightX.hud.addNotification(
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

        chatSyntax("vclip <value>")
    }
}
