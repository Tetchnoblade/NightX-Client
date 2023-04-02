package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class AACYPort2 extends SpeedMode {

    public AACYPort2() {
        super("AACYPort2");
    }

    @Override
    public void onMotion() {
        if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            mc.thePlayer.motionY = 0.3851F;
            mc.thePlayer.motionX *= 1.01;
            mc.thePlayer.motionZ *= 1.01;
        } else
            mc.thePlayer.motionY = -0.21D;
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
