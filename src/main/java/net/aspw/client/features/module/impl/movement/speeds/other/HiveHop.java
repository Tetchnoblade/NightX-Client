package net.aspw.client.features.module.impl.movement.speeds.other;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class HiveHop extends SpeedMode {

    public HiveHop() {
        super("HiveHop");
    }

    @Override
    public void onEnable() {
        mc.thePlayer.speedInAir = 0.0425F;
        mc.timer.timerSpeed = 1.04F;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02F;
        mc.timer.timerSpeed = 1F;
    }

    @Override
    public void onMotion() {
    }

    @Override
    public void onUpdate() {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround)
                mc.thePlayer.motionY = 0.3;

            mc.thePlayer.speedInAir = 0.0425F;
            mc.timer.timerSpeed = 1.04F;
            MovementUtils.strafe();
        } else {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
            mc.thePlayer.speedInAir = 0.02F;
            mc.timer.timerSpeed = 1F;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}