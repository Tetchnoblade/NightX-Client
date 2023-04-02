package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.utils.PacketUtils
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition


class StuckCommand : Command("stuck", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
        }
        PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(mc.thePlayer.posX, -1.0, mc.thePlayer.posZ, false))
        Client.hud.addNotification(
            Notification(
                "You are stuck!",
                Notification.Type.INFO
            )
        )
    }
}