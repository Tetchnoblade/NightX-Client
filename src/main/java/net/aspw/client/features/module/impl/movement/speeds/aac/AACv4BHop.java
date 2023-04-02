package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;

public class AACv4BHop extends SpeedMode {
    public AACv4BHop() {
        super("AACv4BHop");
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.isInWater())
            return;

        if (mc.thePlayer.moveForward > 0) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.timer.timerSpeed = 1.6105F;
                mc.thePlayer.motionX *= 1.0708D;
                mc.thePlayer.motionZ *= 1.0708D;
            } else if (mc.thePlayer.fallDistance > 0) {
                mc.timer.timerSpeed = 0.6F;
            }
        }
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
