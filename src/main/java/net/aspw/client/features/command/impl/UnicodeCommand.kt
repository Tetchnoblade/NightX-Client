package net.aspw.client.features.command.impl

import net.aspw.client.features.command.Command
import net.aspw.client.utils.misc.StringUtils

class UnicodeCommand : Command("unicode", arrayOf("uc")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            val message = StringUtils.toCompleteString(args, 1)
            val stringBuilder = StringBuilder()

            for (c in message.toCharArray())
                if (c.code in 33..128)
                    stringBuilder.append(Character.toChars(c.code + 65248)) else stringBuilder.append(c)

            mc.thePlayer.sendChatMessage(stringBuilder.toString())
            return
        }
        chatSyntax("unicode <message>")
    }
}