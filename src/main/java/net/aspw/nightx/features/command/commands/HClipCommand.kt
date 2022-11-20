package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.ui.client.hud.element.elements.Notification
import net.aspw.nightx.utils.MovementUtils

class HClipCommand : Command("hclip", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                MovementUtils.forward(args[1].toDouble())
                NightX.hud.addNotification(
                    Notification(
                        "Successfully Teleported!",
                        Notification.Type.SUCCESS
                    )
                )
            } catch (exception: NumberFormatException) {
                chatSyntaxError()
            }
            return
        }

        chatSyntax("hclip <value>")
    }
}