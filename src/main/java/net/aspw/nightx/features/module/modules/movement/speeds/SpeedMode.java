package net.aspw.nightx.features.module.modules.movement.speeds;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.JumpEvent;
import net.aspw.nightx.event.MotionEvent;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.Speed;
import net.aspw.nightx.utils.MinecraftInstance;

public abstract class SpeedMode extends MinecraftInstance {

    public final String modeName;

    public SpeedMode(final String modeName) {
        this.modeName = modeName;
    }

    public boolean isActive() {
        final Speed speed = NightX.moduleManager.getModule(Speed.class);

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
