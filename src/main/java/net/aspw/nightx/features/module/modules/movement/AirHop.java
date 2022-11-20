package net.aspw.nightx.features.module.modules.movement;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.MotionEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;

@ModuleInfo(name = "AirHop", spacedName = "Air Hop", category = ModuleCategory.MOVEMENT)
public class AirHop extends Module {

    @EventTarget
    public void onMotion(final MotionEvent event) {
        mc.thePlayer.onGround = true;
        mc.thePlayer.cameraYaw = 0.1F;
        mc.thePlayer.prevCameraYaw = 0.1F;
    }
}