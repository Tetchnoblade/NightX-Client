package net.aspw.client.features.module.modules.movement.speeds.other;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class SlowHop extends SpeedMode {

    public SlowHop() {
        super("SlowHop");
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.isInWater())
            return;

        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround && mc.thePlayer.jumpTicks == 0) {
                mc.thePlayer.jump();
                mc.thePlayer.jumpTicks = 10;
            } else
                MovementUtils.strafe(MovementUtils.getSpeed() * 1.011F);
        }
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
