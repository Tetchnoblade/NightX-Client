package net.aspw.client.util;

import net.minecraft.client.gui.GuiTextField;

/**
 * The type Tab utils.
 */
public final class TabUtils {

    /**
     * Tab.
     *
     * @param textFields the text fields
     */
    public static void tab(final GuiTextField... textFields) {
        for (int i = 0; i < textFields.length; i++) {
            final GuiTextField textField = textFields[i];

            if (textField.isFocused()) {
                textField.setFocused(false);
                i++;

                if (i >= textFields.length)
                    i = 0;

                textFields[i].setFocused(true);
                break;
            }
        }
    }
}
