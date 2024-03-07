package net.aspw.client.utils.misc;

import java.util.Random;

/**
 * The type Random utils.
 */
public final class RandomUtils {

    /**
     * Next boolean boolean.
     *
     * @return the boolean
     */
    public static boolean nextBoolean() {
        return new Random().nextBoolean();
    }

    /**
     * Next int int.
     *
     * @param startInclusive the start inclusive
     * @param endExclusive   the end exclusive
     * @return the int
     */
    public static int nextInt(final int startInclusive, final int endExclusive) {
        if (endExclusive - startInclusive <= 0)
            return startInclusive;

        return startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }

    /**
     * Next double double.
     *
     * @param startInclusive the start inclusive
     * @param endInclusive   the end inclusive
     * @return the double
     */
    public static double nextDouble(final double startInclusive, final double endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0D)
            return startInclusive;

        return startInclusive + ((endInclusive - startInclusive) * Math.random());
    }

    /**
     * Next float float.
     *
     * @param startInclusive the start inclusive
     * @param endInclusive   the end inclusive
     * @return the float
     */
    public static float nextFloat(final float startInclusive, final float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0F)
            return startInclusive;

        return (float) (startInclusive + ((endInclusive - startInclusive) * Math.random()));
    }

    /**
     * Random number string.
     *
     * @param length the length
     * @return the string
     */
    public static String randomNumber(final int length) {
        return random(length, "123456789");
    }

    /**
     * Random string string.
     *
     * @param length the length
     * @return the string
     */
    public static String randomString(final int length) {
        return random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    /**
     * Random string.
     *
     * @param length the length
     * @param chars  the chars
     * @return the string
     */
    public static String random(final int length, final String chars) {
        return random(length, chars.toCharArray());
    }

    /**
     * Random string.
     *
     * @param length the length
     * @param chars  the chars
     * @return the string
     */
    public static String random(final int length, final char[] chars) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++)
            stringBuilder.append(chars[new Random().nextInt(chars.length)]);
        return stringBuilder.toString();
    }
}
