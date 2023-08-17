package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.util.misc.MiscUtils
import net.aspw.client.visual.hud.element.elements.Notification

class SkinStealerCommand : Command("skinstealer", arrayOf("steal")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                val amount = args[1]
                MiscUtils.showURL("https://minecraft.tools/download-skin/" + amount)
                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
                Client.hud.addNotification(
                    Notification(
                        "Open Web!",
                        Notification.Type.SUCCESS
                    )
                )
                return
            } catch (ex: NumberFormatException) {
                chatSyntaxError()
            }

            return
        }

        chatSyntax("skinstealer <id>")
    }
}