package net.aspw.client.features.module.impl.movement.speeds.other;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;

/**
 * The type Hive hop.
 */
public class HiveHop extends SpeedMode {

    /**
     * Instantiates a new Hive hop.
     */
    public HiveHop() {
        super("HiveHop");
    }

    @Override
    public void onEnable() {
        mc.thePlayer.jumpMovementFactor = 0.0425F;
        mc.timer.timerSpeed = 1.04F;
    }

    @Override
    public void onDisable() {
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

            mc.thePlayer.jumpMovementFactor = 0.0425F;
            mc.timer.timerSpeed = 1.04F;
            MovementUtils.strafe();
        } else {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
            mc.thePlayer.jumpMovementFactor = 0.02F;
            mc.timer.timerSpeed = 1F;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}