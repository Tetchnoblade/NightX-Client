package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "AirHop", spacedName = "Air Hop", category = ModuleCategory.MOVEMENT)
public class AirHop extends Module {

    @EventTarget
    public void onMotion(final MotionEvent event) {
        mc.thePlayer.onGround = true;
        mc.thePlayer.cameraYaw = 0.1F;
        mc.thePlayer.prevCameraYaw = 0.1F;
    }
}