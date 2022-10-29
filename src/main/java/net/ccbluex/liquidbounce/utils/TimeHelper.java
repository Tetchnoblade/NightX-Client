package net.ccbluex.liquidbounce.utils;

public class TimeHelper {
    private long prevMS;

    public boolean reach(double milliSeconds) {
        return (float) (this.getTime() - this.prevMS) >= milliSeconds;
    }

    public void reset() {
        this.prevMS = this.getTime();
    }

    private long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public long getMs() {
        return (getDelay() / 1000000L) - this.prevMS;
    }

    public long getDelay() {
        return System.nanoTime();
    }
}