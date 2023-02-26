package net.aspw.client.features.module.modules.movement.speeds.ncp;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.client.features.module.modules.world.Scaffold;
import net.aspw.client.utils.MovementUtils;

public class NCPHop extends SpeedMode {

    public NCPHop() {
        super("NCPHop");
    }

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1.0865F;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02F;
        mc.timer.timerSpeed = 1F;
        super.onDisable();

        final Scaffold scaffold = Client.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState())
            MovementUtils.strafe(0.2f);
    }

    @Override
    public void onMotion() {
    }

    @Override
    public void onUpdate() {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.thePlayer.speedInAir = 0.0223F;
            }

            MovementUtils.strafe();
        }
    }

    @Override
    public void onMove(MoveEvent event) {

    }
}