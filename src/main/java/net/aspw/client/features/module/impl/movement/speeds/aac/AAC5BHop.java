package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class AAC5BHop extends SpeedMode {
    private boolean legitJump;

    public AAC5BHop() {
        super("AAC5BHop");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onTick() {
        mc.timer.timerSpeed = 1F;

        if (mc.thePlayer.isInWater())
            return;

        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                if (legitJump) {
                    mc.thePlayer.jump();
                    legitJump = false;
                    return;
                }

                mc.thePlayer.motionY = 0.41;
                mc.thePlayer.onGround = false;
                MovementUtils.strafe(0.374F);
            } else if (mc.thePlayer.motionY < 0D) {
                mc.thePlayer.speedInAir = 0.0201F;
                mc.timer.timerSpeed = 1.02F;
            } else
                mc.timer.timerSpeed = 1.01F;
        } else {
            legitJump = true;
        }
    }
}