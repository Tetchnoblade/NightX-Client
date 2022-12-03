package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.MotionEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;

@ModuleInfo(name = "RealBobbing", spacedName = "Real Bobbing", category = ModuleCategory.RENDER)
public class RealBobbing extends Module {

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.cameraYaw = 0.03F;
        }
    }
}