package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;

/**
 * The type Aac 4 b hop.
 */
public class AAC4BHop extends SpeedMode {
    private boolean legitHop;

    /**
     * Instantiates a new Aac 4 b hop.
     */
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
                mc.thePlayer.jumpMovementFactor = 0.0211F;
        } else {
            legitHop = true;
        }
    }
}
