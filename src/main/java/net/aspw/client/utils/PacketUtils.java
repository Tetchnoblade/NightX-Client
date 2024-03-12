package net.aspw.client.utils;

import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Listenable;
import net.aspw.client.event.PacketEvent;
import net.aspw.client.event.TickEvent;
import net.aspw.client.utils.timer.MSTimer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

import java.util.ArrayList;

/**
 * The type Packet utils.
 */
public class PacketUtils extends MinecraftInstance implements Listenable {

    private static final MSTimer packetTimer = new MSTimer();
    private static final MSTimer wdTimer = new MSTimer();
    public static ArrayList<Packet<?>> packets = new ArrayList<>();
    /**
     * The constant inBound.
     */
    public static int inBound, /**
     * The Out bound.
     */
    outBound = 0;
    /**
     * The constant avgInBound.
     */
    public static int avgInBound, /**
     * The Avg out bound.
     */
    avgOutBound = 0;
    private static int transCount = 0;
    private static int wdVL = 0;

    private static boolean isInventoryAction(final short action) {
        return action > 0 && action < 100;
    }

    /**
     * Is watchdog active boolean.
     *
     * @return the boolean
     */
    public static boolean isWatchdogActive() {
        return wdVL >= 8;
    }

    public static void handlePacket(final Packet<?> packet) {
        if (packet.getClass().getSimpleName().startsWith("C")) outBound++;
        else if (packet.getClass().getSimpleName().startsWith("S")) inBound++;

        if (packet instanceof S32PacketConfirmTransaction) {
            if (!isInventoryAction(((S32PacketConfirmTransaction) packet).getActionNumber()))
                transCount++;
        }
    }

    /**
     * Send packet no event.
     *
     * @param packet the packet
     */
    /*
     * This code is from UnlegitMC/FDPClient. Please credit them when using this code in your repository.
     */
    public static void sendPacketNoEvent(final Packet<INetHandlerPlayServer> packet) {
        packets.add(packet);
        mc.getNetHandler().addToSendQueue(packet);
    }

    /**
     * Send packet silent.
     *
     * @param packet the packet
     */
    public static void sendPacketSilent(final Packet<INetHandlerPlayServer> packet) {
        packets.add(packet);
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    /**
     * Handle send packet boolean.
     *
     * @param packet the packet
     * @return the boolean
     */
    public static boolean handleSendPacket(final Packet<?> packet) {
        if (packets.contains(packet)) {
            packets.remove(packet);
            handlePacket(packet); // make sure not to skip silent packets.
            return true;
        }
        return false;
    }

    /**
     * On packet.
     *
     * @param event the event
     */
    @EventTarget
    public void onPacket(final PacketEvent event) {
        handlePacket(event.getPacket());
    }

    /**
     * On tick.
     *
     * @param event the event
     */
    @EventTarget
    public void onTick(final TickEvent event) {
        if (packetTimer.hasTimePassed(1000L)) {
            avgInBound = inBound;
            avgOutBound = outBound;
            inBound = outBound = 0;
            packetTimer.reset();
        }
        if (mc.thePlayer == null || mc.theWorld == null) {
            //reset all checks
            wdVL = 0;
            transCount = 0;
            wdTimer.reset();
        } else if (wdTimer.hasTimePassed(100L)) { // watchdog active when the transaction poll rate reaches about 100ms/packet.
            wdVL += (transCount > 0) ? 1 : -1;
            transCount = 0;
            if (wdVL > 10) wdVL = 10;
            if (wdVL < 0) wdVL = 0;
            wdTimer.reset();
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }

}