package net.aspw.client.injection.forge.mixins.packets;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * The type Mixin c 00 handshake.
 */
@Mixin(C00Handshake.class)
public class MixinC00Handshake {

    /**
     * The Port.
     */
    @Shadow
    public int port;
    /**
     * The Ip.
     */
    @Shadow
    public String ip;
    @Shadow
    private int protocolVersion;
    @Shadow
    private EnumConnectionState requestedState;
}