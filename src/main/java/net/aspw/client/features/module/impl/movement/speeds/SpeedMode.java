package net.aspw.client.features.module.impl.movement.speeds;

import net.aspw.client.Client;
import net.aspw.client.event.JumpEvent;
import net.aspw.client.event.MotionEvent;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.util.MinecraftInstance;

/**
 * The type Speed mode.
 */
public abstract class SpeedMode extends MinecraftInstance {

    /**
     * The Mode name.
     */
    public final String modeName;

    /**
     * Instantiates a new Speed mode.
     *
     * @param modeName the mode name
     */
    public SpeedMode(final String modeName) {
        this.modeName = modeName;
    }

    /**
     * Is active boolean.
     *
     * @return the boolean
     */
    public boolean isActive() {
        final Speed speed = Client.moduleManager.getModule(Speed.class);

        return speed != null && !mc.thePlayer.isSneaking() && speed.getState() && speed.getModeName().equals(modeName);
    }

    /**
     * On motion.
     */
    public abstract void onMotion();

    /**
     * On motion.
     *
     * @param eventMotion the event motion
     */
    public void onMotion(MotionEvent eventMotion) {
    }

    /**
     * On update.
     */
    public abstract void onUpdate();

    /**
     * On move.
     *
     * @param event the event
     */
    public abstract void onMove(final MoveEvent event);

    /**
     * On jump.
     *
     * @param event the event
     */
    public void onJump(JumpEvent event) {
    }

    /**
     * On tick.
     */
    public void onTick() {
    }

    /**
     * On enable.
     */
    public void onEnable() {
    }

    /**
     * On disable.
     */
    public void onDisable() {
    }
}
