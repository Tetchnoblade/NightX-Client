package net.aspw.client.features.module.impl.movement.speeds.ncp;

import net.aspw.client.Launch;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.utils.MovementUtils;

/**
 * The type Ncp mini jump.
 */
public class NCPMiniJump extends SpeedMode {

    /**
     * Instantiates a new Ncp mini jump.
     */
    public NCPMiniJump() {
        super("NCPMiniJump");
    }

    @Override
    public void onMotion() {
        if (!MovementUtils.isMoving())
            return;

        if (mc.thePlayer.onGround && !mc.thePlayer.movementInput.jump) {
            mc.thePlayer.motionY += 0.1;

            final double multiplier = 1.8;

            mc.thePlayer.motionX *= multiplier;
            mc.thePlayer.motionZ *= multiplier;

            final double currentSpeed = Math.sqrt(Math.pow(mc.thePlayer.motionX, 2) + Math.pow(mc.thePlayer.motionZ, 2));
            final double maxSpeed = 0.66;
            if (currentSpeed > maxSpeed) {
                mc.thePlayer.motionX = mc.thePlayer.motionX / currentSpeed * maxSpeed;
                mc.thePlayer.motionZ = mc.thePlayer.motionZ / currentSpeed * maxSpeed;
            }
        }

        MovementUtils.strafe();
    }

    @Override
    public void onUpdate() {
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
