package net.aspw.nightx.features.command.commands

import net.aspw.nightx.features.command.Command
import net.aspw.nightx.utils.misc.StringUtils

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