package net.aspw.nightx.features.module.modules.movement.speeds.watchdog;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.EventState;
import net.aspw.nightx.event.MotionEvent;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.Speed;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.utils.MovementUtils;
import net.aspw.nightx.utils.timer.TickTimer;
import net.minecraft.potion.Potion;

public class WatchdogNew extends SpeedMode {

    private final TickTimer tickTimer = new TickTimer();
    private int groundTick = 0;

    public WatchdogNew() {
        super("WatchdogNew");
    }

    @Override
    public void onMotion(MotionEvent eventMotion) {
        final Speed speed = NightX.moduleManager.getModule(Speed.class);

        if (speed == null || eventMotion.getEventState() != EventState.PRE)
            return;

        if (!mc.thePlayer.onGround || !MovementUtils.isMoving()) {
            mc.timer.timerSpeed = 1;
        }

        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                if (groundTick >= 0) {
                    mc.timer.timerSpeed = 1.02f;
                    if (speed.sendJumpValue.get()) {
                        mc.thePlayer.jump();
                    } else {
                        mc.thePlayer.motionY = 0.42;
                    }
                    if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MovementUtils.strafe(0.43f);
                    }
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MovementUtils.strafe(0.63f);
                    }
                }
                groundTick++;
            } else {
                groundTick = 0;
                mc.thePlayer.motionY += -0.03 * 0.03;
            }
        }
    }

    @Override
    public void onEnable() {
        final Speed speed = NightX.moduleManager.getModule(Speed.class);

        if (speed == null)
            return;

        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
