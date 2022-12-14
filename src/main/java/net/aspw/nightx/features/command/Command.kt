package net.aspw.nightx.features.command

import net.aspw.nightx.NightX
import net.aspw.nightx.utils.ClientUtils
import net.aspw.nightx.utils.MinecraftInstance
import java.util.*

abstract class Command(val command: String, val alias: Array<String>) : MinecraftInstance() {
    /**
     * Execute commands with provided [args]
     */
    abstract fun execute(args: Array<String>)

    /**
     * Returns a list of command completions based on the provided [args].
     * If a command does not implement [tabComplete] an [EmptyList] is returned by default.
     *
     * @param args an array of command arguments that the player has passed to the command so far
     * @return a list of matching completions for the command the player is trying to autocomplete
     * @author NurMarvin
     */
    open fun tabComplete(args: Array<String>): List<String> {
        return emptyList()
    }

    /**
     * Print [msg] to chat
     */
    protected fun chat(msg: String) = ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§3$msg")

    /**
     * Print [syntax] of command to chat
     */
    protected fun chatSyntax(syntax: String) =
        ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§3Syntax: §7${NightX.commandManager.prefix}$syntax")

    /**
     * Print [syntaxes] of command to chat
     */
    protected fun chatSyntax(syntaxes: Array<String>) {
        ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§3Syntax:")

        for (syntax in syntaxes)
            ClientUtils.displayChatMessage(
                "§8> §7${NightX.commandManager.prefix}$command ${
                    syntax.lowercase(
                        Locale.getDefault()
                    )
                }"
            )
    }

    /**
     * Print a syntax error to chat
     */
    protected fun chatSyntaxError() = ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§3Syntax error")

    /**
     * Play edit sound
     */
    protected fun playEdit() {
    }
}