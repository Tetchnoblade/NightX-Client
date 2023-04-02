package net.aspw.client.features.command.impl

import net.aspw.client.features.command.Command
import net.aspw.client.utils.misc.StringUtils

class SayCommand : Command("say", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            mc.thePlayer.sendChatMessage(StringUtils.toCompleteString(args, 1))
            return
        }
        chatSyntax("say <message...>")
    }
}