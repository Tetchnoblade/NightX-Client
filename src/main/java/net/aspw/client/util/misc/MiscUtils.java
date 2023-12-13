package net.aspw.client.util.misc;

import net.aspw.client.util.MinecraftInstance;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The type Misc utils.
 */
public final class MiscUtils extends MinecraftInstance {

    /**
     * Show error popup.
     *
     * @param title   the title
     * @param message the message
     */
    public static void showErrorPopup(final String title, final String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show url.
     *
     * @param url the url
     */
    public static void showURL(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (final IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open file chooser file.
     *
     * @return the file
     */
    public static File openFileChooser() {
        if (mc.isFullScreen())
            mc.toggleFullscreen();

        final JFileChooser fileChooser = new JFileChooser();
        final JFrame frame = new JFrame();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);

        final int action = fileChooser.showOpenDialog(frame);
        frame.dispose();

        return action == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }
}
