package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3


@ModuleInfo(name = "AirPlace", spacedName = "Air Place", description = "", category = ModuleCategory.PLAYER)
class AirPlace : Module() {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.playerController.onPlayerRightClick(
                mc.thePlayer,
                mc.theWorld,
                mc.thePlayer.heldItem,
                BlockPos(mc.thePlayer).down(1),
                mc.thePlayer.horizontalFacing,
                Vec3(0.0, 0.0, 0.0)
            )
        else {
            mc.gameSettings.keyBindSneak.pressed = false
            mc.playerController.onPlayerRightClick(
                mc.thePlayer,
                mc.theWorld,
                mc.thePlayer.heldItem,
                BlockPos(mc.thePlayer).down(2),
                mc.thePlayer.horizontalFacing,
                Vec3(0.0, 0.0, 0.0)
            )
        }
    }
}