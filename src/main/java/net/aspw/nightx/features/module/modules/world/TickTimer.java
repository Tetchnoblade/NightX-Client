package net.aspw.nightx.features.module.modules.world;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.value.IntegerValue;

@ModuleInfo(name = "TickTimer", spacedName = "Tick Timer", category = ModuleCategory.WORLD)
public class TickTimer extends Module {
    private final IntegerValue tickTimerValue = new IntegerValue("Speed", 3, 2, 10, "x");
    private final net.aspw.nightx.utils.timer.TickTimer tickTimer = new net.aspw.nightx.utils.timer.TickTimer();

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        tickTimer.reset();
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        tickTimer.update();

        if (tickTimer.hasTimePassed(10)) {
            mc.timer.timerSpeed = 1.0f;
        }

        if (tickTimer.hasTimePassed(20)) {
            mc.timer.timerSpeed = tickTimerValue.get();
            tickTimer.reset();
        }
    }
    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        tickTimer.reset();
    }
}