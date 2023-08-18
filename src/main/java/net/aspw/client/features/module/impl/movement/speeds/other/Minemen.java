package net.aspw.client.features.module.impl.movement.speeds.other;

import net.aspw.client.event.EventTarget;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;

/**
 * The type Minemen hop.
 */
public class Minemen extends SpeedMode {

    public Minemen() {
        super("Minemen");
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.onGround && MovementUtils.isMoving())
            mc.thePlayer.jump();
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer.hurtTime <= 6)
            MovementUtils.strafe();
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}