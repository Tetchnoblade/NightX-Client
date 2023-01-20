package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.MovementUtils

@ModuleInfo(name = "Parkour", category = ModuleCategory.MOVEMENT)
class Parkour : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (MovementUtils.isMoving() && mc.thePlayer.onGround && !mc.thePlayer.isSneaking && !mc.gameSettings.keyBindSneak.isKeyDown && !mc.gameSettings.keyBindJump.isKeyDown &&
            mc.theWorld.getCollidingBoundingBoxes(
                mc.thePlayer, mc.thePlayer.entityBoundingBox
                    .offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)
            ).isEmpty()
        )
            mc.thePlayer.jump()
    }
}
