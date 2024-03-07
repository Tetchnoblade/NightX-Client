package net.aspw.client.features.module.impl.movement.speeds.velocity;

import net.aspw.client.Launch;
import net.aspw.client.event.MotionEvent;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.utils.MovementUtils;

import java.util.Objects;

/**
 * The type Vanilla bhop.
 */
public class Velocity extends SpeedMode {

    /**
     * Instantiates a new Vanilla bhop.
     */
    public Velocity() {
        super("Velocity");
    }

    @Override
    public void onMotion(MotionEvent eventMotion) {
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(Objects.requireNonNull(Launch.moduleManager.getModule(Speed.class)).velocitySpeed.getValue());
        } else {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onMotion() {
    }

    @Override
    public void onUpdate() {
        if (MovementUtils.isMoving() && mc.thePlayer.onGround && Objects.requireNonNull(Launch.moduleManager.getModule(Speed.class)).velocityBHop.get())
            mc.thePlayer.jump();
    }

    @Override
    public void onDisable() {
        final Scaffold scaffold = Launch.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}