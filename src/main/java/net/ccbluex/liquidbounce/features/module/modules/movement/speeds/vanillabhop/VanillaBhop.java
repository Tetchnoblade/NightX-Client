package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.vanillabhop;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventState;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class VanillaBhop extends SpeedMode {

    public VanillaBhop() {
        super("VanillaBhop");
    }

    @Override
    public void onMotion(MotionEvent eventMotion) {
        MovementUtils.strafe(1);
        final Speed speed = LiquidBounce.moduleManager.getModule(Speed.class);

        if (speed == null || eventMotion.getEventState() != EventState.PRE)
            return;
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
    public void onMove(MoveEvent event) {
    }
}