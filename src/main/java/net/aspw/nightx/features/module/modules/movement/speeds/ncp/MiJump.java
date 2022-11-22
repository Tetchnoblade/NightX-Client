package net.aspw.nightx.features.module.modules.movement.speeds.ncp;

import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.utils.MovementUtils;

public class MiJump extends SpeedMode {

    public MiJump() {
        super("MiJump");
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
        if (!mc.thePlayer.isSneaking())
            MovementUtils.strafe(0.3f);
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
