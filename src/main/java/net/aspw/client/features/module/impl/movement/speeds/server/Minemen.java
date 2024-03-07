package net.aspw.client.features.module.impl.movement.speeds.server;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

/**
 * The type Minemen hop.
 */
public class Minemen extends SpeedMode {

    public Minemen() {
        super("Minemen");
    }

    @Override
    public void onMotion() {
        if (MovementUtils.isMoving()) {
            mc.gameSettings.keyBindJump.pressed = false;
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.timer.timerSpeed = 1.02f;
            } else {
                mc.timer.timerSpeed = 1.0f;
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