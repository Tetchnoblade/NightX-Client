package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class AAC2BHop extends SpeedMode {
    public AAC2BHop() {
        super("AAC2BHop");
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.isInWater())
            return;

        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.thePlayer.motionX *= 1.02D;
                mc.thePlayer.motionZ *= 1.02D;
            } else if (mc.thePlayer.motionY > -0.2D) {
                mc.thePlayer.jumpMovementFactor = 0.08F;
                mc.thePlayer.motionY += 0.0143099999999999999999999999999D;
                mc.thePlayer.jumpMovementFactor = 0.07F;
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
