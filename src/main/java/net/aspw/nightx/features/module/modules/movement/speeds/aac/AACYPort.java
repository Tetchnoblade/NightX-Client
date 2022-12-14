package net.aspw.nightx.features.module.modules.movement.speeds.aac;

import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.utils.MovementUtils;

public class AACYPort extends SpeedMode {
    public AACYPort() {
        super("AACYPort");
    }

    @Override
    public void onMotion() {
        if (MovementUtils.isMoving() && !mc.thePlayer.isSneaking() && mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.3425F;
            mc.thePlayer.motionX *= 1.5893F;
            mc.thePlayer.motionZ *= 1.5893F;
        } else
            mc.thePlayer.motionY = -0.19D;
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
