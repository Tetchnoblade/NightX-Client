package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.features.module.modules.misc.AntiBot
import net.aspw.nightx.ui.client.hud.element.elements.Notification

class TeleportCommand : Command("tp", emptyArray()) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 2) {
            val theName = args[1]

            // Get target player data
            val targetPlayer = mc.theWorld.playerEntities
                .filter { !AntiBot.isBot(it) && it.name.equals(theName, true) }
                .firstOrNull()

            // Attempt to teleport to player's position.
            if (targetPlayer != null) {
                mc.thePlayer.setPositionAndUpdate(targetPlayer.posX, targetPlayer.posY, targetPlayer.posZ)
                NightX.hud.addNotification(
                    Notification(
                        "Successfully teleported to §a${targetPlayer.name}",
                        Notification.Type.SUCCESS
                    )
                )
                return
            } else {
                NightX.hud.addNotification(
                    Notification(
                        "Failed to teleport",
                        Notification.Type.ERROR
                    )
                )
                return
            }
        } else if (args.size == 4) {
            try {
                val posX = if (args[1].equals("~", true)) mc.thePlayer.posX else args[1].toDouble()
                val posY = if (args[2].equals("~", true)) mc.thePlayer.posY else args[2].toDouble()
                val posZ = if (args[3].equals("~", true)) mc.thePlayer.posZ else args[3].toDouble()

                mc.thePlayer.setPositionAndUpdate(posX, posY, posZ)
                NightX.hud.addNotification(
                    Notification(
                        "Successfully teleported to §a$posX, $posY, $posZ",
                        Notification.Type.SUCCESS
                    )
                )
                return
            } catch (e: NumberFormatException) {
                NightX.hud.addNotification(
                    Notification(
                        "Failed to teleport",
                        Notification.Type.ERROR
                    )
                )
                return
            }
        }

        chatSyntax("tp <player name/x y z>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val pref = args[0]

        return when (args.size) {
            1 -> mc.theWorld.playerEntities
                .filter { !AntiBot.isBot(it) && it.name.startsWith(pref, true) }
                .map { it.name }
                .toList()

            else -> emptyList()
        }
    }

}