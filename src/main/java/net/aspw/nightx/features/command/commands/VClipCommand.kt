package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.features.module.modules.client.Hud
import net.aspw.nightx.utils.PathUtils
import net.aspw.nightx.visual.hud.element.elements.Notification
import net.minecraft.network.play.client.C03PacketPlayer
import java.util.function.Consumer
import javax.vecmath.Vector3d

class VClipCommand : Command("vclip", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                val y = args[1].toDouble()
                val entity = if (mc.thePlayer.isRiding) mc.thePlayer.ridingEntity else mc.thePlayer

                if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                    NightX.tipSoundManager.popSound.asyncPlay(90f)
                }
                PathUtils.findPath(entity.posX, entity.posY + y, entity.posZ, 3.0).forEach(Consumer { pos: Vector3d ->
                    mc.netHandler
                        .addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                true
                            )
                        )
                })
                entity.setPosition(entity.posX, entity.posY + y, entity.posZ)
                NightX.hud.addNotification(
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
