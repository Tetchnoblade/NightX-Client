package net.aspw.client.utils.timer;

public final class TickTimer {

    public int tick;

    public void update() {
        tick++;
    }

    public void reset() {
        tick = 0;
    }

    public boolean hasTimePassed(final int ticks) {
        return tick >= ticks;
    }
}
