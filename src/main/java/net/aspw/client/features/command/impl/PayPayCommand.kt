package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.util.misc.RandomUtils
import net.aspw.client.visual.hud.element.elements.Notification

class PayPayCommand : Command("paypay", arrayOf("pay")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                val amount = args[1].toInt()
                for (cnt in 1..amount)
                    mc.thePlayer.sendChatMessage("https://pay.paypay.ne.jp/" + RandomUtils.randomString(16))
                if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
                Client.hud.addNotification(
                    Notification(
                        "Sent PayPay Link Successfully!",
                        Notification.Type.SUCCESS
                    )
                )
                return
            } catch (ex: NumberFormatException) {
                chatSyntaxError()
            }

            return
        }

        chatSyntax("paypay <amount>")
    }
}