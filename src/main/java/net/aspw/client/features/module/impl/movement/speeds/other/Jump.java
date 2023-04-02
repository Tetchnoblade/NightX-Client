package net.aspw.client.features.module.impl.movement.speeds.other;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;

public class Jump extends SpeedMode {

    public Jump() {
        super("Jump");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

        final Speed speed = Client.moduleManager.getModule(Speed.class);

        if (speed == null)
            return;
        if (MovementUtils.isMoving() && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) && mc.thePlayer.jumpTicks == 0) {
            mc.thePlayer.jump();
            mc.thePlayer.jumpTicks = 10;
        }
        if (speed.jumpStrafe.get() && MovementUtils.isMoving() && !mc.thePlayer.onGround && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava()))
            MovementUtils.strafe();
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
