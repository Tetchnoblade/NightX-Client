package net.aspw.client.features.module.impl.movement.speeds.ncp;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.util.MovementUtils;
import net.aspw.client.util.timer.TickTimer;

/**
 * The type Ncp frame.
 */
public class NCPFrame extends SpeedMode {

    private final TickTimer tickTimer = new TickTimer();
    private int motionTicks;
    private boolean move;

    /**
     * Instantiates a new Ncp frame.
     */
    public NCPFrame() {
        super("NCPFrame");
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.movementInput.moveForward > 0.0f || mc.thePlayer.movementInput.moveStrafe > 0.0f) {
            final double speed = 4.25;

            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();

                if (motionTicks == 1) {
                    tickTimer.reset();
                    if (move) {
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                        move = false;
                    }
                    motionTicks = 0;
                } else
                    motionTicks = 1;
            } else if (!move && motionTicks == 1 && tickTimer.hasTimePassed(5)) {
                mc.thePlayer.motionX *= speed;
                mc.thePlayer.motionZ *= speed;
                move = true;
            }

            if (!mc.thePlayer.onGround)
                MovementUtils.strafe();
            tickTimer.update();
        }
    }

    @Override
    public void onUpdate() {
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
