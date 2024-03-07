package net.aspw.client.features.command.impl

import net.aspw.client.Launch
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.targets.AntiBots
import net.aspw.client.features.module.impl.visual.Interface
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity
import kotlin.math.roundToInt

class SpotCommand : Command("spot", arrayOf("st")) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 2) {
            val theName = args[1]

            // Get target player data
            val targetPlayer = mc.theWorld.playerEntities
                .filter { !AntiBots.isBot(it) && it.name.equals(theName, true) }
                .firstOrNull()

            // Attempt to teleport to player's position.
            if (targetPlayer != null) {
                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                }
                mc.netHandler.handleSpawnGlobalEntity(
                    S2CPacketSpawnGlobalEntity(
                        EntityLightningBolt(
                            mc.theWorld,
                            targetPlayer.posX,
                            targetPlayer.posY,
                            targetPlayer.posZ
                        )
                    )
                )
                chat(
                    "Target Position: ${targetPlayer.posX.toInt()}, ${targetPlayer.posY.toInt()}, ${targetPlayer.posZ.toInt()} (" + mc.thePlayer.getDistance(
                        targetPlayer.posX,
                        mc.thePlayer.entityBoundingBox.minY,
                        targetPlayer.posZ
                    ).roundToInt() + " blocks away)"
                )
                return
            } else {
                chat("No players found!")
                return
            }
        }

        chatSyntax("spot <player name>")
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