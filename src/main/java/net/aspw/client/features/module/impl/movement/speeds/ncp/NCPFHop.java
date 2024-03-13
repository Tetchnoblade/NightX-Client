package net.aspw.client.features.module.impl.movement.speeds.ncp;

import net.aspw.client.Launch;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.utils.MovementUtils;

/**
 * The type Ncpf hop.
 */
public class NCPFHop extends SpeedMode {

    /**
     * Instantiates a new Ncpf hop.
     */
    public NCPFHop() {
        super("NCPFHop");
    }

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1.0866F;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;

        final Scaffold scaffold = Launch.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState()) {
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
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.thePlayer.motionX *= 1.01D;
                mc.thePlayer.motionZ *= 1.01D;
                mc.thePlayer.jumpMovementFactor = 0.0223F;
            }

            mc.thePlayer.motionY -= 0.00099999D;

            MovementUtils.strafe();
        }
    }

    @Override
    public void onMove(MoveEvent event) {

    }


}
