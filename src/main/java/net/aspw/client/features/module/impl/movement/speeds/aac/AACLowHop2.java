package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class AACLowHop2 extends SpeedMode {
    private boolean legitJump;

    public AACLowHop2() {
        super("AACLowHop2");
    }

    @Override
    public void onEnable() {
        legitJump = true;
        mc.timer.timerSpeed = 1F;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }

    @Override
    public void onMotion() {
        mc.timer.timerSpeed = 1F;

        if (mc.thePlayer.isInWater())
            return;

        if (MovementUtils.isMoving()) {
            mc.timer.timerSpeed = 1.09F;

            if (mc.thePlayer.onGround) {
                if (legitJump) {
                    mc.thePlayer.jump();
                    legitJump = false;
                    return;
                }

                mc.thePlayer.motionY = 0.343F;
                MovementUtils.strafe(0.534F);
            }
        } else {
            legitJump = true;
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
