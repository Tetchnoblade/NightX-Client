package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command

class CloudConfigCommand : Command("cloudconfig", arrayOf("cc")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("${LiquidBounce.CLIENT_CONFIGS}")
    }
}