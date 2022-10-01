package net.ccbluex.liquidbounce.features.module.modules.movement.speeds;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.JumpEvent;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;

public abstract class SpeedMode extends MinecraftInstance {

    public final String modeName;

    public SpeedMode(final String modeName) {
        this.modeName = modeName;
    }

    public boolean isActive() {
        final Speed speed = LiquidBounce.moduleManager.getModule(Speed.class);

        return speed != null && !mc.thePlayer.isSneaking() && speed.getState() && speed.getModeName().equals(modeName);
    }

    public abstract void onMotion();

    public void onMotion(MotionEvent eventMotion) {
    }

    public abstract void onUpdate();

    public abstract void onMove(final MoveEvent event);

    public void onJump(JumpEvent event) {
    }

    public void onTick() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }
}
