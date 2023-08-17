package net.aspw.client.features.module.impl.movement.speeds.aac;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;

/**
 * The type Aac ground 2.
 */
public class AACGround2 extends SpeedMode {
    /**
     * Instantiates a new Aac ground 2.
     */
    public AACGround2() {
        super("AACGround2");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        if (!MovementUtils.isMoving())
            return;

        mc.timer.timerSpeed = Client.moduleManager.getModule(Speed.class).aacGroundTimerValue.get();
        MovementUtils.strafe(0.02F);
    }

    @Override
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }
}
