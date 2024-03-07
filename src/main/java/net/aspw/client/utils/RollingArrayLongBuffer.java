package net.aspw.client.utils;

/**
 * The type Rolling array long buffer.
 */
public class RollingArrayLongBuffer {
    private final long[] contents;
    private int currentIndex = 0;

    /**
     * Instantiates a new Rolling array long buffer.
     *
     * @param length the length
     */
    public RollingArrayLongBuffer(int length) {
        this.contents = new long[length];
    }

    /**
     * Add.
     *
     * @param l the l
     */
    public void add(long l) {
        currentIndex = (currentIndex + 1) % contents.length;
        contents[currentIndex] = l;
    }

    /**
     * Gets timestamps since.
     *
     * @param l the l
     * @return the timestamps since
     */
    public int getTimestampsSince(long l) {
        for (int i = 0; i < contents.length; i++) {
            if (contents[currentIndex < i ? contents.length - i + currentIndex : currentIndex - i] < l) {
                return i;
            }
        }

        // If every element is lower than l, return the array length
        return contents.length;
    }
}
