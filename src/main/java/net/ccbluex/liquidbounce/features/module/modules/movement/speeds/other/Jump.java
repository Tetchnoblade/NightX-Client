package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class Jump extends SpeedMode {

    public Jump() {
        super("Jump");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

        final Speed speed = LiquidBounce.moduleManager.getModule(Speed.class);

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
