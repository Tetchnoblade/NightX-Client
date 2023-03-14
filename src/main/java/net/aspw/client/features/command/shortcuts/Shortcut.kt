package net.aspw.client.features.command.shortcuts

import net.aspw.client.features.command.Command

class Shortcut(val name: String, val script: List<Pair<Command, Array<String>>>) : Command(name, emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        script.forEach { it.first.execute(it.second) }
    }
}
