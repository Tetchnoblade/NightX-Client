package net.aspw.nightx.features.module.modules.movement;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;

@ModuleInfo(name = "Bhop", category = ModuleCategory.MOVEMENT)
public class Bhop extends Module {
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        mc.thePlayer.jumpMovementFactor = 0.16f;
    }
}