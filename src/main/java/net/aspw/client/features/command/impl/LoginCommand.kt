package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.visual.hud.element.elements.Notification

class LoginCommand : Command("login", arrayOf("l")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
        }
        mc.thePlayer.sendChatMessage("/register rrrr rrrr")
        mc.thePlayer.sendChatMessage("/login rrrr")
        Client.hud.addNotification(
            Notification(
                "Logging in...",
                Notification.Type.INFO
            )
        )
    }
}