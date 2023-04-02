package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.combat.AntiBot
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.pathfinder.MainPathFinder
import net.aspw.client.utils.pathfinder.Vec3
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

class TeleportCommand : Command("tp", arrayOf("teleport")) {

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
                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
                Thread {
                    val path: ArrayList<Vec3> = MainPathFinder.computePath(
                        Vec3(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ
                        ),
                        Vec3(targetPlayer.posX, targetPlayer.posY, targetPlayer.posZ)
                    )
                    for (point in path) PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            point.x,
                            point.y,
                            point.z,
                            true
                        )
                    )
                    mc.thePlayer.setPosition(targetPlayer.posX, targetPlayer.posY, targetPlayer.posZ)
                }.start()
                Client.hud.addNotification(
                    Notification(
                        "Successfully teleported to §a${targetPlayer.name}",
                        Notification.Type.SUCCESS
                    )
                )
                return
            } else {
                Client.hud.addNotification(
                    Notification(
                        "No players found!",
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
                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
                Thread {
                    val path: ArrayList<Vec3> = MainPathFinder.computePath(
                        Vec3(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ
                        ),
                        Vec3(posX, posY, posZ)
                    )
                    for (point in path) PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            point.x,
                            point.y,
                            point.z,
                            true
                        )
                    )
                    mc.thePlayer.setPosition(posX, posY, posZ)
                }.start()
                Client.hud.addNotification(
                    Notification(
                        "Successfully teleported to §a$posX, $posY, $posZ",
                        Notification.Type.SUCCESS
                    )
                )
                return
            } catch (e: NumberFormatException) {
                Client.hud.addNotification(
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