package net.aspw.client.utils.timer;

import net.aspw.client.utils.misc.RandomUtils;

/**
 * The type Time utils.
 */
public final class TimeUtils {

    /**
     * Random delay long.
     *
     * @param minDelay the min delay
     * @param maxDelay the max delay
     * @return the long
     */
    public static long randomDelay(final int minDelay, final int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    /**
     * Random click delay long.
     *
     * @param minCPS the min cps
     * @param maxCPS the max cps
     * @return the long
     */
    public static long randomClickDelay(final int minCPS, final int maxCPS) {
        return (long) ((Math.random() * (1000 / minCPS - 1000 / maxCPS + 1)) + 1000 / maxCPS);
    }
}