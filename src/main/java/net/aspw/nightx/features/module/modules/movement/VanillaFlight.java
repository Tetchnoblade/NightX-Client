package net.aspw.nightx.features.module.modules.movement;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;

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
    }
}