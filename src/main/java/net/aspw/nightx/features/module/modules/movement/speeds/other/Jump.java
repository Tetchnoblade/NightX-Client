package net.aspw.nightx.features.module.modules.movement.speeds.other;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.Speed;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.utils.MovementUtils;

public class Jump extends SpeedMode {

    public Jump() {
        super("Jump");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

        final Speed speed = NightX.moduleManager.getModule(Speed.class);

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
