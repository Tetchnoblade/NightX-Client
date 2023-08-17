package net.aspw.client.util;

/**
 * The type Time helper.
 */
public class TimeHelper {
    private long prevMS;

    /**
     * Reset.
     */
    public void reset() {
        this.prevMS = this.getTime();
    }

    private long getTime() {
        return System.nanoTime() / 1000000L;
    }

    /**
     * Gets ms.
     *
     * @return the ms
     */
    public long getMs() {
        return (getDelay() / 1000000L) - this.prevMS;
    }

    /**
     * Gets delay.
     *
     * @return the delay
     */
    public long getDelay() {
        return System.nanoTime();
    }
}