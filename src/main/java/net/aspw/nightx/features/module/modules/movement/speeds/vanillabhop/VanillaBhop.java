package net.aspw.nightx.features.module.modules.movement.speeds.vanillabhop;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.EventState;
import net.aspw.nightx.event.MotionEvent;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.Speed;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.features.module.modules.world.Scaffold;
import net.aspw.nightx.utils.MovementUtils;

public class VanillaBhop extends SpeedMode {

    public VanillaBhop() {
        super("VanillaBhop");
    }

    @Override
    public void onMotion(MotionEvent eventMotion) {
        if (MovementUtils.isMoving()) {
            MovementUtils.strafe(0.9f);
        }
        final Speed speed = NightX.moduleManager.getModule(Speed.class);

        if (speed == null || eventMotion.getEventState() != EventState.PRE) {
        }
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
    public void onDisable() {
        final Scaffold scaffold = NightX.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState())
            MovementUtils.strafe(0.2f);
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}