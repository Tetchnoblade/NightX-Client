package net.aspw.nightx.features.module.modules.combat;

import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;

@ModuleInfo(name = "NoClickDelay", spacedName = "NoClick Delay", category = ModuleCategory.COMBAT)
public class NoClickDelay extends Module {

    public void onUpdate(final UpdateEvent event) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (!mc.inGameHasFocus) return;

            mc.leftClickCounter = 0;
        }
    }
}