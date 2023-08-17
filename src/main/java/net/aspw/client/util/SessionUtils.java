package net.aspw.client.util;

import net.aspw.client.event.*;
import net.aspw.client.util.timer.MSTimer;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;

/**
 * The type Session utils.
 */
public class SessionUtils extends MinecraftInstance implements Listenable {

    private static final MSTimer sessionTimer = new MSTimer();
    private static final MSTimer worldTimer = new MSTimer();

    /**
     * The constant lastSessionTime.
     */
    public static long lastSessionTime = 0L;
    /**
     * The constant backupSessionTime.
     */
    public static long backupSessionTime = 0L;
    /**
     * The constant lastWorldTime.
     */
    public static long lastWorldTime = 0L;

    private static boolean requireDelay = false;

    private static GuiScreen lastScreen = null;

    /**
     * Handle connection.
     */
    public static void handleConnection() {
        backupSessionTime = 0L;
        requireDelay = true;
        lastSessionTime = System.currentTimeMillis() - sessionTimer.time;
        if (lastSessionTime < 0L) lastSessionTime = 0L;
        sessionTimer.reset();
    }

    /**
     * Handle reconnection.
     */
    public static void handleReconnection() {
        if (requireDelay) sessionTimer.time = System.currentTimeMillis() - backupSessionTime;
    }

    /**
     * Gets format session time.
     *
     * @return the format session time
     */
    public static String getFormatSessionTime() {
        if (System.currentTimeMillis() - sessionTimer.time < 0L) sessionTimer.reset();

        int realTime = (int) (System.currentTimeMillis() - sessionTimer.time) / 1000;
        int hours = realTime / 3600;
        int seconds = (realTime % 3600) % 60;
        int minutes = (realTime % 3600) / 60;

        return hours + "h " + minutes + "m " + seconds + "s";
    }

    /**
     * Gets format world time.
     *
     * @return the format world time
     */
    public static String getFormatWorldTime() {
        if (System.currentTimeMillis() - worldTimer.time < 0L) worldTimer.reset();

        int realTime = (int) (System.currentTimeMillis() - worldTimer.time) / 1000;
        int hours = realTime / 3600;
        int seconds = (realTime % 3600) % 60;
        int minutes = (realTime % 3600) / 60;

        return hours + "h " + minutes + "m " + seconds + "s";
    }

    /**
     * On world.
     *
     * @param event the event
     */
    @EventTarget
    public void onWorld(WorldEvent event) {
        lastWorldTime = System.currentTimeMillis() - worldTimer.time;
        worldTimer.reset();

        if (event.getWorldClient() == null) {
            backupSessionTime = System.currentTimeMillis() - sessionTimer.time;
            requireDelay = true;
        } else {
            requireDelay = false;
        }
    }

    /**
     * On session.
     *
     * @param event the event
     */
    @EventTarget
    public void onSession(SessionEvent event) {
        handleConnection();
    }

    /**
     * On screen.
     *
     * @param event the event
     */
    @EventTarget
    public void onScreen(ScreenEvent event) {
        if (event.getGuiScreen() == null && lastScreen != null && (lastScreen instanceof GuiDownloadTerrain || lastScreen instanceof GuiConnecting))
            handleReconnection();

        lastScreen = event.getGuiScreen();
    }

    @Override
    public boolean handleEvents() {
        return true;
    }

}