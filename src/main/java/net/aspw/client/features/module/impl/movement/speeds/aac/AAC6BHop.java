package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;

/**
 * The type Aac 6 b hop.
 */
public class AAC6BHop extends SpeedMode {
    private boolean legitJump;

    /**
     * Instantiates a new Aac 6 b hop.
     */
    public AAC6BHop() {
        super("AAC6BHop");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        mc.timer.timerSpeed = 1F;

        if (mc.thePlayer.isInWater())
            return;

        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                if (legitJump) {
                    mc.thePlayer.motionY = 0.4;
                    MovementUtils.strafe(0.15F);
                    mc.thePlayer.onGround = false;
                    legitJump = false;
                    return;
                }

                mc.thePlayer.motionY = 0.41;
                MovementUtils.strafe(0.47458485F);
            }

            if (mc.thePlayer.motionY < 0 && mc.thePlayer.motionY > -0.2)
                mc.timer.timerSpeed = ((float) (1.2 + mc.thePlayer.motionY));

            mc.thePlayer.jumpMovementFactor = 0.022151F;
        } else {
            legitJump = true;
        }
    }

    @Override
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onEnable() {
        legitJump = true;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }

}