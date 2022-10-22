package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "Bhop", category = ModuleCategory.MOVEMENT)
public class Bhop extends Module {
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        mc.thePlayer.jumpMovementFactor = 0.16f;
    }
}