package net.aspw.client.features.module.impl.movement.speeds.other;

import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class AEMine extends SpeedMode {
    public AEMine() {
        super("AEMine");
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }

    @Override
    public void onMotion() {
        if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.thePlayer.jump();
            mc.timer.timerSpeed = 1F;
        } else {
            mc.timer.timerSpeed = 1.30919551F;
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}