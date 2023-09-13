package net.aspw.client.features.command

import net.aspw.client.Client
import net.aspw.client.util.ClientUtils
import net.aspw.client.util.MinecraftInstance
import java.util.*

abstract class Command(val command: String, val alias: Array<String>) : MinecraftInstance() {
    /**
     * Execute commands with provided [args]
     */
    abstract fun execute(args: Array<String>)

    open fun tabComplete(args: Array<String>): List<String> {
        return emptyList()
    }

    /**
     * Print [msg] to chat
     */
    fun chat(msg: String) = ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§3$msg")

    /**
     * Print [syntax] of command to chat
     */
    protected fun chatSyntax(syntax: String) =
        ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§r§cSyntax: §7.$syntax")

    /**
     * Print [syntaxes] of command to chat
     */
    protected fun chatSyntax(syntaxes: Array<String>) {
        ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§3Syntax:")

        for (syntax in syntaxes)
            ClientUtils.displayChatMessage(
                "§8> §7.$command ${
                    syntax.lowercase(
                        Locale.getDefault()
                    )
                }"
            )
    }

    /**
     * Print a syntax error to chat
     */
    protected fun chatSyntaxError() = ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§3Syntax error")
}