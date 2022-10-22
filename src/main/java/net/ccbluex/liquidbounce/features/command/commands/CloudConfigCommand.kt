package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.SettingsUtils
import net.ccbluex.liquidbounce.utils.misc.StringUtils
import java.io.File
import java.io.IOException
import java.util.*

class CloudConfigCommand : Command("cloudconfig", arrayOf("cc")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("${LiquidBounce.CLIENT_CONFIGS}")
    }
}