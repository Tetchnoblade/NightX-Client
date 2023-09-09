package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.visual.hud.element.elements.Notification

class RegisterCommand : Command("register", arrayOf("r")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
        }
        mc.thePlayer.sendChatMessage("/register rrrrr rrrrr")
        Client.hud.addNotification(
            Notification(
                "Registered with <rrrrrr> !",
                Notification.Type.INFO
            )
        )
    }
}