package net.aspw.client.features.module.impl.movement.speeds.other;

import net.aspw.client.event.EventTarget;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;

/**
 * The type Hive hop.
 */
public class Minemen extends SpeedMode {

    /**
     * Instantiates a new Hive hop.
     */
    public Minemen() {
        super("Minemen");
    }

    @EventTarget
    public void onStrafe() {
        if (mc.thePlayer.hurtTime <= 6)
            MovementUtils.strafe();
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.onGround)
            mc.thePlayer.jump();
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}