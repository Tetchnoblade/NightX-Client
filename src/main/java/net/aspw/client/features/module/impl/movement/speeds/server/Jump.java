package net.aspw.client.features.module.impl.movement.speeds.server;

import net.aspw.client.Launch;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

/**
 * The type Jump.
 */
public class Jump extends SpeedMode {

    /**
     * Instantiates a new Jump.
     */
    public Jump() {
        super("Jump");
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.isInWater())
            return;

        final Speed speed = Launch.moduleManager.getModule(Speed.class);

        if (speed == null)
            return;

        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround && mc.thePlayer.jumpTicks == 0) {
                mc.thePlayer.jump();
                mc.thePlayer.jumpTicks = 10;
            }
            if (speed.jumpStrafe.get() && !mc.thePlayer.onGround)
                MovementUtils.strafe();
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
