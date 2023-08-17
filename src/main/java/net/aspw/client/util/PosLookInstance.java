package net.aspw.client.util;

import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

/**
 * The type Pos look instance.
 */
public class PosLookInstance {

    private double x = 0;
    private double y = 0;
    private double z = 0;
    private float yaw = 0;
    private float pitch = 0;

    /**
     * Instantiates a new Pos look instance.
     */
    public PosLookInstance() {
    }

    /**
     * Reset.
     */
    public void reset() {
        set(0, 0, 0, 0, 0);
    }

    /**
     * Set.
     *
     * @param packet the packet
     */
    public void set(S08PacketPlayerPosLook packet) {
        set(packet.x, packet.y, packet.z, packet.yaw, packet.pitch);
    }

    /**
     * Set.
     *
     * @param a the a
     * @param b the b
     * @param c the c
     * @param d the d
     * @param e the e
     */
    public void set(double a, double b, double c, float d, float e) {
        this.x = a;
        this.y = b;
        this.z = c;
        this.yaw = d;
        this.pitch = e;
    }

    /**
     * Equal flag boolean.
     *
     * @param packet the packet
     * @return the boolean
     */
    public boolean equalFlag(C06PacketPlayerPosLook packet) {
        return packet != null && !packet.onGround && packet.x == x && packet.y == y && packet.z == z && packet.yaw == yaw && packet.pitch == pitch;
    }

}
