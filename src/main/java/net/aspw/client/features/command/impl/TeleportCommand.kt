package net.aspw.client.features.command.impl

import net.aspw.client.Launch
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.targets.AntiBots
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.pathfinder.MainPathFinder
import net.aspw.client.utils.pathfinder.Vec3
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

class TeleportCommand : Command("tp", arrayOf("teleport")) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 2) {
            val theName = args[1]

            // Get target player data
            val targetPlayer =
                mc.theWorld.playerEntities.firstOrNull { !AntiBots.isBot(it) && it.name.equals(theName, true) }

            // Attempt to teleport to player's position.
            if (targetPlayer != null) {
                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
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
                chat("Successfully teleported to §a${targetPlayer.name}")
                return
            } else {
                chat("No players found!")
                return
            }
        } else if (args.size == 4) {
            try {
                val posX = if (args[1].equals("~", true)) mc.thePlayer.posX else args[1].toDouble()
                val posY = if (args[2].equals("~", true)) mc.thePlayer.posY else args[2].toDouble()
                val posZ = if (args[3].equals("~", true)) mc.thePlayer.posZ else args[3].toDouble()
                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
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
                chat("Successfully teleported to §a$posX, $posY, $posZ")
                return
            } catch (e: NumberFormatException) {
                chat("Failed to teleport")
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
                .filter { !AntiBots.isBot(it) && it.name.startsWith(pref, true) }
                .map { it.name }
                .toList()

            else -> emptyList()
        }
    }

}