package net.aspw.nightx.features.module.modules.movement.speeds.ncp;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.features.module.modules.world.Scaffold;
import net.aspw.nightx.utils.MovementUtils;

public class OnGround extends SpeedMode {

    public OnGround() {
        super("OnGround");
    }

    @Override
    public void onMotion() {
        if (!MovementUtils.isMoving())
            return;

        if (mc.thePlayer.fallDistance > 3.994)
            return;

        if (mc.thePlayer.isInWater() || mc.thePlayer.isOnLadder() || mc.thePlayer.isCollidedHorizontally)
            return;

        mc.thePlayer.posY -= 0.3993000090122223;
        mc.thePlayer.motionY = -1000.0;
        mc.thePlayer.distanceWalkedModified = 44.0F;
        mc.timer.timerSpeed = 1F;

        if (mc.thePlayer.onGround) {
            mc.thePlayer.posY += 0.3993000090122223;
            mc.thePlayer.motionY = 0.3993000090122223;
            mc.thePlayer.distanceWalkedOnStepModified = 44.0f;
            mc.thePlayer.motionX *= 1.590000033378601;
            mc.thePlayer.motionZ *= 1.590000033378601;
            mc.timer.timerSpeed = 1.199F;
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onDisable() {
        final Scaffold scaffold = NightX.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState())
            MovementUtils.strafe(0.2f);
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
