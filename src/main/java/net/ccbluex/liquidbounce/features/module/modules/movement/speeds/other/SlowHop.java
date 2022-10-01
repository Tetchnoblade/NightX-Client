package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

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
        } else {
            mc.thePlayer.motionX = 0D;
            mc.thePlayer.motionZ = 0D;
        }
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
