package net.aspw.client.features.module.impl.movement.speeds.watchdog;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.util.MovementUtils;

/**
 * The type Watchdog stable.
 */
public class WatchdogStable extends SpeedMode {

    /**
     * Instantiates a new Watchdog stable.
     */
    public WatchdogStable() {
        super("WatchdogStable");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
        mc.timer.timerSpeed = 1F;
        final Speed speed = Client.moduleManager.getModule(Speed.class);
        if (speed == null) return;

        if (MovementUtils.isMoving() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
            double moveSpeed = Math.max(MovementUtils.getBaseMoveSpeed() * speed.baseStrengthValue.get(), MovementUtils.getSpeed());

            if (mc.thePlayer.onGround) {
                if (speed.sendJumpValue.get()) mc.thePlayer.jump();
                if (speed.recalcValue.get())
                    moveSpeed = Math.max(MovementUtils.getBaseMoveSpeed() * speed.baseStrengthValue.get(), MovementUtils.getSpeed());
                event.setY(mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier((mc.thePlayer.isCollidedHorizontally ? 0.41999998688698 : speed.jumpYValue.get())));
                moveSpeed *= speed.moveSpeedValue.get();
            } else if (speed.glideStrengthValue.get() > 0 && event.getY() < 0) {
                event.setY(mc.thePlayer.motionY += speed.glideStrengthValue.get());
            }
        }
    }
}
