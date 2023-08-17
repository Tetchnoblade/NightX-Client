package net.aspw.client.util.timer;

/**
 * The type Ms timer.
 */
public final class MSTimer {

    /**
     * The Time.
     */
    public long time = -1L;

    /**
     * Has time passed boolean.
     *
     * @param MS the ms
     * @return the boolean
     */
    public boolean hasTimePassed(final long MS) {
        return System.currentTimeMillis() >= time + MS;
    }

    /**
     * Has time left long.
     *
     * @param MS the ms
     * @return the long
     */
    public long hasTimeLeft(final long MS) {
        return (MS + time) - System.currentTimeMillis();
    }

    /**
     * Reset.
     */
    public void reset() {
        time = System.currentTimeMillis();
    }
}
