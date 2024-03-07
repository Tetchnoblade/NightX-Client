package net.aspw.client.features.module.impl.movement.speeds.server;

import net.aspw.client.Launch;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.utils.MovementUtils;

/**
 * The type Y port 2.
 */
public class YPort2 extends SpeedMode {

    /**
     * Instantiates a new Y port 2.
     */
    public YPort2() {
        super("YPort2");
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isInWeb || !MovementUtils.isMoving())
            return;

        if (mc.thePlayer.onGround)
            mc.thePlayer.jump();
        else
            mc.thePlayer.motionY = -1D;

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
