package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventState;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class VanillaHop extends SpeedMode {

    private int groundTick = 0;

    public VanillaHop() {
        super("VanillaHop");
    }

    @Override
    public void onMotion(MotionEvent eventMotion) {
        final Speed speed = LiquidBounce.moduleManager.getModule(Speed.class);

        if (speed == null || eventMotion.getEventState() != EventState.PRE)
            return;

        if (MovementUtils.isMoving()) {
            mc.timer.timerSpeed = (mc.thePlayer.motionY > 0) ? speed.upTimerValue.get() : speed.downTimerValue.get();

            if (mc.thePlayer.onGround) {
                if (groundTick >= speed.groundStay.get()) {
                    if (speed.doLaunchSpeedValue.get()) {
                        MovementUtils.strafe(speed.launchSpeedValue.get());
                    }
                    if (speed.yValue.get() != 0) {
                        mc.thePlayer.motionY = speed.yValue.get();
                    }
                } else if (speed.groundResetXZValue.get()) {
                    mc.thePlayer.motionX = 0.0;
                    mc.thePlayer.motionZ = 0.0;
                }
                groundTick++;
            } else {
                groundTick = 0;
                switch (speed.strafeValue.get().toLowerCase()) {
                    case "strafe":
                        MovementUtils.strafe(speed.speedValue.get());
                        break;
                    case "boost":
                        MovementUtils.strafe();
                        break;
                    case "plus":
                        MovementUtils.accelerate(speed.speedValue.get() * 0.1f);
                        break;
                    case "plusonlyup":
                        if (mc.thePlayer.motionY > 0) {
                            MovementUtils.accelerate(speed.speedValue.get() * 0.1f);
                        } else {
                            MovementUtils.strafe();
                        }
                        break;
                }
                mc.thePlayer.motionY += speed.addYMotionValue.get() * 0.03;
            }
        } else if (speed.resetXZValue.get()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onEnable() {
        final Speed speed = LiquidBounce.moduleManager.getModule(Speed.class);

        if (speed == null)
            return;

        if (speed.resetXZValue.get()) mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
        if (speed.resetYValue.get()) mc.thePlayer.motionY = 0D;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
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
