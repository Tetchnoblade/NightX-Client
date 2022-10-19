package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "RealBobbing", spacedName = "Real Bobbing", category = ModuleCategory.RENDER)
public class RealBobbing extends Module {

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.cameraYaw = 0.03F;
            mc.thePlayer.prevCameraYaw = 0.03F;
        }
    }
}
