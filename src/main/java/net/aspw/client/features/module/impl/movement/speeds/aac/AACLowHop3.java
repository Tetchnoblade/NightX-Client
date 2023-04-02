package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class AACLowHop3 extends SpeedMode {
    private boolean firstJump;
    private boolean waitForGround;

    public AACLowHop3() {
        super("AACLowHop3");
    }

    @Override
    public void onEnable() {
        firstJump = true;
    }

    @Override
    public void onMotion() {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.hurtTime <= 0) {
                if (mc.thePlayer.onGround) {
                    waitForGround = false;

                    if (!firstJump)
                        firstJump = true;

                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0.41;
                } else {
                    if (waitForGround)
                        return;

                    if (mc.thePlayer.isCollidedHorizontally)
                        return;

                    firstJump = false;
                    mc.thePlayer.motionY -= 0.0149;
                }

                if (!mc.thePlayer.isCollidedHorizontally)
                    MovementUtils.forward(firstJump ? 0.0016 : 0.001799);
            } else {
                firstJump = true;
                waitForGround = true;
            }
        }

        final double speed = MovementUtils.getSpeed();
        mc.thePlayer.motionX = -(Math.sin(MovementUtils.getDirection()) * speed);
        mc.thePlayer.motionZ = Math.cos(MovementUtils.getDirection()) * speed;
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
