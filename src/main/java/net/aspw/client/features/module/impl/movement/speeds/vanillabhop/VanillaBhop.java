package net.aspw.client.features.module.impl.movement.speeds.vanillabhop;

import net.aspw.client.Client;
import net.aspw.client.event.MotionEvent;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.util.MovementUtils;

import java.util.Objects;

/**
 * The type Vanilla bhop.
 */
public class VanillaBhop extends SpeedMode {

    /**
     * Instantiates a new Vanilla bhop.
     */
    public VanillaBhop() {
        super("VanillaBhop");
    }

    @Override
    public void onMotion(MotionEvent eventMotion) {
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(Objects.requireNonNull(Client.moduleManager.getModule(Speed.class)).vanillaBhopSpeed.getValue());
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
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround)
                mc.thePlayer.jump();
        }
    }

    @Override
    public void onDisable() {
        final Scaffold scaffold = Client.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}