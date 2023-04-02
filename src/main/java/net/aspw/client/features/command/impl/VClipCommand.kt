package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.pathfinder.MainPathFinder
import net.aspw.client.utils.pathfinder.Vec3
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.network.play.client.C03PacketPlayer

class VClipCommand : Command("vclip", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                val y = args[1].toDouble()
                val entity = if (mc.thePlayer.isRiding) mc.thePlayer.ridingEntity else mc.thePlayer

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
                        Vec3(entity.posX, entity.posY + y, entity.posZ)
                    )
                    for (point in path) PacketUtils.sendPacketNoEvent(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            point.x,
                            point.y,
                            point.z,
                            true
                        )
                    )
                    mc.thePlayer.setPosition(entity.posX, entity.posY + y, entity.posZ)
                }.start()
                Client.hud.addNotification(
                    Notification(
                        "Successfully Teleported!",
                        Notification.Type.SUCCESS
                    )
                )
            } catch (ex: NumberFormatException) {
                chatSyntaxError()
            }

            return
        }

        chatSyntax("vclip <value>")
    }
}
