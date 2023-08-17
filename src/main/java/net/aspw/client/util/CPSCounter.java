package net.aspw.client.util;

/**
 * The type Cps counter.
 */
public class CPSCounter {
    private static final int MAX_CPS = 50;
    private static final RollingArrayLongBuffer[] TIMESTAMP_BUFFERS = new RollingArrayLongBuffer[MouseButton.values().length];

    static {
        for (int i = 0; i < TIMESTAMP_BUFFERS.length; i++) {
            TIMESTAMP_BUFFERS[i] = new RollingArrayLongBuffer(MAX_CPS);
        }
    }

    /**
     * Register click.
     *
     * @param button the button
     */
    public static void registerClick(MouseButton button) {
        TIMESTAMP_BUFFERS[button.getIndex()].add(System.currentTimeMillis());
    }

    /**
     * Gets cps.
     *
     * @param button the button
     * @return the cps
     */
    public static int getCPS(MouseButton button) {
        return TIMESTAMP_BUFFERS[button.getIndex()].getTimestampsSince(System.currentTimeMillis() - 1000L);
    }

    /**
     * The enum Mouse button.
     */
    public enum MouseButton {
        /**
         * Left mouse button.
         */
        LEFT(0),
        /**
         * Middle mouse button.
         */
        MIDDLE(1),
        /**
         * Right mouse button.
         */
        RIGHT(2);

        private final int index;

        MouseButton(int index) {
            this.index = index;
        }

        private int getIndex() {
            return index;
        }
    }

}
