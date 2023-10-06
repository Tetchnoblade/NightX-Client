package net.aspw.client.features.api;

import net.aspw.client.value.IntegerValue;

/**
 * The type Color element.
 */
public class ColorElement extends IntegerValue {

    /**
     * Instantiates a new Color element.
     *
     * @param counter the counter
     * @param m       the m
     */
    public ColorElement(int counter, Material m) {
        super("Color" + counter + "-" + m.getColorName(), 255, 0, 255);
    }

    @Override
    protected void onChanged(final Integer oldValue, final Integer newValue) {
    }

    /**
     * The enum Material.
     */
    enum Material {
        /**
         * Red material.
         */
        RED("Red"),
        /**
         * Green material.
         */
        GREEN("Green"),
        /**
         * Blue material.
         */
        BLUE("Blue");

        private final String colName;

        Material(String name) {
            this.colName = name;
        }

        /**
         * Gets color name.
         *
         * @return the color name
         */
        public String getColorName() {
            return this.colName;
        }
    }

}