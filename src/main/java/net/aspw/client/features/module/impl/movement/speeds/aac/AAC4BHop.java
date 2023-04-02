package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class AAC4BHop extends SpeedMode {
    private boolean legitHop;

    public AAC4BHop() {
        super("AAC4BHop");
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
    public void onEnable() {
        legitHop = true;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02F;
    }

    @Override
    public void onTick() {
        if (MovementUtils.isMoving()) {
            if (legitHop) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.thePlayer.onGround = false;
                    legitHop = false;
                }
                return;
            }

            if (mc.thePlayer.onGround) {
                mc.thePlayer.onGround = false;
                MovementUtils.strafe(0.375F);
                mc.thePlayer.jump();
                mc.thePlayer.motionY = 0.41;
            } else
                mc.thePlayer.speedInAir = 0.0211F;
        } else {
            legitHop = true;
        }
    }
}
