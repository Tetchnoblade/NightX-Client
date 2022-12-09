package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command

class CloudScriptCommand : Command("cloudscript", arrayOf("cs")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("${NightX.CLIENT_SCRIPTS}")
    }
}