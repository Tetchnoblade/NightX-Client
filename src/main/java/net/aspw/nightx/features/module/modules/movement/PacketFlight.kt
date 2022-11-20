package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.MovementUtils
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

@ModuleInfo(name = "PacketFlight", spacedName = "Packet Flight", category = ModuleCategory.MOVEMENT)
class PacketFlight : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        MovementUtils.strafe(0f)
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.onGround = true
        mc.timer.timerSpeed = 1.3f
        val playerYaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        var x = -Math.sin(playerYaw) * 0.2873;
        var z = Math.cos(playerYaw) * 0.2873;

        if (MovementUtils.isMoving() || !GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && !GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX + x,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ + z,
                    false
                )
            )
            mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX + x,
                    mc.thePlayer.posY + 60,
                    mc.thePlayer.posZ + z,
                    true
                )
            )
        }

        if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
            mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
            mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 20,
                    mc.thePlayer.posZ,
                    true
                )
            )
        }

        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
            mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY - 20,
                    mc.thePlayer.posZ,
                    true
                )
            )
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
}