package net.aspw.nightx.features.module.modules.movement.speeds.other;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.Speed;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.utils.MovementUtils;
import net.aspw.nightx.utils.timer.MSTimer;

public class TeleportCubeCraft extends SpeedMode {

    private final MSTimer timer = new MSTimer();

    public TeleportCubeCraft() {
        super("TeleportCubeCraft");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(final MoveEvent event) {
        if (MovementUtils.isMoving() && mc.thePlayer.onGround && timer.hasTimePassed(300L)) {
            final double yaw = MovementUtils.getDirection();
            final float length = NightX.moduleManager.getModule(Speed.class).cubecraftPortLengthValue.get();

            event.setX(-Math.sin(yaw) * length);
            event.setZ(Math.cos(yaw) * length);
            timer.reset();
        }
    }
}