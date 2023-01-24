package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.features.module.modules.client.Hud
import net.minecraft.network.play.client.C0BPacketEntityAction

class RemoteViewCommand : Command("remoteview", arrayOf("rv")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size < 2) {
            if (mc.renderViewEntity != mc.thePlayer) {
                mc.renderViewEntity = mc.thePlayer
                return
            }
            chatSyntax("remoteview <username>")
            return
        }

        val targetName = args[1]

        for (entity in mc.theWorld.loadedEntityList) {
            if (targetName == entity.name) {
                mc.renderViewEntity = entity
                if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    NightX.tipSoundManager.popSound.asyncPlay(90f)
                }
                chat("Now viewing perspective of §8${entity.name}§3.")
                chat("Execute §8${NightX.commandManager.prefix}remoteview §3again to go back to yours.")
                break
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C0BPacketEntityAction && (packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING || packet.action == C0BPacketEntityAction.Action.START_SPRINTING)) {
            event.cancelEvent()
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> return mc.theWorld.playerEntities
                .map { it.name }
                .filter { it.startsWith(args[0], true) }

            else -> emptyList()
        }
    }
}