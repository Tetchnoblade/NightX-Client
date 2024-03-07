package net.aspw.client.features.command.impl

import net.aspw.client.Launch
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.utils.misc.RandomUtils

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
                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                }
                chat("Sent PayPay Link Successfully!")
                return
            } catch (ex: NumberFormatException) {
                chatSyntaxError()
            }

            return
        }

        chatSyntax("paypay <amount>")
    }
}