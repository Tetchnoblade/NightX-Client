package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

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
