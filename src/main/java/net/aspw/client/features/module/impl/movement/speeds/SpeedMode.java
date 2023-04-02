package net.aspw.client.features.module.impl.movement.speeds;

import net.aspw.client.Client;
import net.aspw.client.event.JumpEvent;
import net.aspw.client.event.MotionEvent;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.utils.MinecraftInstance;

public abstract class SpeedMode extends MinecraftInstance {

    public final String modeName;

    public SpeedMode(final String modeName) {
        this.modeName = modeName;
    }

    public boolean isActive() {
        final Speed speed = Client.moduleManager.getModule(Speed.class);

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
