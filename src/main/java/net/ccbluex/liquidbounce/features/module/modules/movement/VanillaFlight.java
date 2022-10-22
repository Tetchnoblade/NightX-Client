package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "VanillaFlight", spacedName = "Vanilla Flight", category = ModuleCategory.MOVEMENT)
public class VanillaFlight extends Module {

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        mc.thePlayer.capabilities.allowFlying = true;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.capabilities.allowFlying = false;
        mc.thePlayer.capabilities.isFlying = false;

        if (mc.thePlayer.capabilities.isCreativeMode)
            mc.thePlayer.capabilities.allowFlying = true;

        if (mc.thePlayer.capabilities.isCreativeMode && mc.thePlayer.capabilities.isFlying)
            mc.thePlayer.capabilities.isFlying = true;
    }
}