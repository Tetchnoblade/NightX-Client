package net.aspw.client.features.module.impl.movement.speeds.other;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;
import net.aspw.client.utils.timer.MSTimer;

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
            final float length = Client.moduleManager.getModule(Speed.class).cubecraftPortLengthValue.get();

            event.setX(-Math.sin(yaw) * length);
            event.setZ(Math.cos(yaw) * length);
            timer.reset();
        }
    }
}