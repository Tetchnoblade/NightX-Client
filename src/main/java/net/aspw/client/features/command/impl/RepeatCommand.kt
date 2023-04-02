package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.utils.misc.StringUtils
import net.aspw.client.visual.hud.element.elements.Notification

class RepeatCommand : Command("repeat", arrayOf("rp")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 2) {
            try {
                val amount = args[1].toInt()
                for (cnt in 1..amount)
                    mc.thePlayer.sendChatMessage(StringUtils.toCompleteString(args, 2))
                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
                Client.hud.addNotification(
                    Notification(
                        "Sent Chat Successfully!",
                        Notification.Type.SUCCESS
                    )
                )
                return
            } catch (ex: NumberFormatException) {
                chatSyntaxError()
            }

            return
        }

        chatSyntax("repeat <amount> <message>")
    }
}
