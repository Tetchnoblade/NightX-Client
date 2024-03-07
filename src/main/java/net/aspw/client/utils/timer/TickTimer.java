package net.aspw.client.utils.timer;

/**
 * The type Tick timer.
 */
public final class TickTimer {

    /**
     * The Tick.
     */
    public int tick;

    /**
     * Update.
     */
    public void update() {
        tick++;
    }

    /**
     * Reset.
     */
    public void reset() {
        tick = 0;
    }

    /**
     * Has time passed boolean.
     *
     * @param ticks the ticks
     * @return the boolean
     */
    public boolean hasTimePassed(final int ticks) {
        return tick >= ticks;
    }
}
