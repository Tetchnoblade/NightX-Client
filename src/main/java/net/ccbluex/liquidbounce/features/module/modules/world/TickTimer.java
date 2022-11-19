package net.ccbluex.liquidbounce.features.module.modules.world;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.IntegerValue;

@ModuleInfo(name = "TickTimer", spacedName = "Tick Timer", category = ModuleCategory.WORLD)
public class TickTimer extends Module {
    private final IntegerValue tickTimerValue = new IntegerValue("Speed", 3, 2, 10, "x");
    private final net.ccbluex.liquidbounce.utils.timer.TickTimer tickTimer = new net.ccbluex.liquidbounce.utils.timer.TickTimer();

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