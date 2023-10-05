package net.aspw.client.features.module.impl.movement.speeds.spartan;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;

/**
 * The type Spartan y port.
 */
public class SpartanYPort extends SpeedMode {

    private int airMoves;

    /**
     * Instantiates a new Spartan y port.
     */
    public SpartanYPort() {
        super("SpartanYPort");
    }

    @Override
    public void onMotion() {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                airMoves = 0;
            } else {
                mc.timer.timerSpeed = 1.08F;

                if (airMoves >= 3)
                    mc.thePlayer.jumpMovementFactor = 0.0275F;

                if (airMoves >= 4 && airMoves % 2 == 0.0) {
                    mc.thePlayer.motionY = -0.32F - 0.009 * Math.random();
                    mc.thePlayer.jumpMovementFactor = 0.0238F;
                }

                airMoves++;
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