package net.aspw.client.injection.forge.mixins.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.aspw.client.Client;
import net.aspw.client.event.PacketEvent;
import net.aspw.client.features.module.impl.exploit.ExtendedPosition;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.protocol.api.VFNetworkManager;
import net.aspw.client.util.PacketUtils;
import net.minecraft.network.*;
import net.minecraft.util.CryptManager;
import net.minecraft.util.LazyLoadBase;
import net.raphimc.vialoader.netty.VLLegacyPipeline;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.net.InetAddress;

/**
 * The type Mixin network manager.
 */
@Mixin(NetworkManager.class)
public class MixinNetworkManager implements VFNetworkManager {

    @Shadow
    private Channel channel;

    @Shadow
    private INetHandler packetListener;

    @Unique
    private Cipher viaForge$decryptionCipher;

    @Unique
    private VersionEnum viaForge$targetVersion;

    @Inject(method = "func_181124_a", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;group(Lio/netty/channel/EventLoopGroup;)Lio/netty/bootstrap/AbstractBootstrap;"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void trackSelfTarget(InetAddress address, int serverPort, boolean useNativeTransport, CallbackInfoReturnable<NetworkManager> cir, NetworkManager networkmanager, Class oclass, LazyLoadBase lazyloadbase) {
        ((VFNetworkManager) networkmanager).viaForge$setTrackedVersion(ProtocolBase.getManager().getTargetVersion());
    }

    @Inject(method = "enableEncryption", at = @At("HEAD"), cancellable = true)
    private void storeEncryptionCiphers(SecretKey key, CallbackInfo ci) {
        if (ProtocolBase.getManager().getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            ci.cancel();
            this.viaForge$decryptionCipher = CryptManager.createNetCipherInstance(2, key);
            this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
        }
    }

    @Inject(method = "setCompressionTreshold", at = @At("RETURN"))
    public void reorderPipeline(int p_setCompressionTreshold_1_, CallbackInfo ci) {
        ProtocolBase.getManager().reorderCompression(channel);
    }

    @Override
    public void viaForge$setupPreNettyDecryption() {
        this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "decrypt", new NettyEncryptingDecoder(this.viaForge$decryptionCipher));
    }

    @Override
    public VersionEnum viaForge$getTrackedVersion() {
        return viaForge$targetVersion;
    }

    @Override
    public void viaForge$setTrackedVersion(VersionEnum version) {
        viaForge$targetVersion = version;
    }

    /**
     * @author As_pw
     * @reason Packet Tracking
     */
    @Overwrite
    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_) throws Exception {
        final PacketEvent event = new PacketEvent(p_channelRead0_2_);
        ExtendedPosition extendedPosition = Client.moduleManager.getModule(ExtendedPosition.class);
        assert extendedPosition != null;
        if (extendedPosition.getState()) {
            try {
                extendedPosition.onPacket(event);
            } catch (Exception e) {
                // nothing
            }
        }
        Client.eventManager.callEvent(event);

        if (event.isCancelled())
            return;

        if (this.channel.isOpen()) {
            try {
                p_channelRead0_2_.processPacket(this.packetListener);
            } catch (ThreadQuickExitException var4) {
            }
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo callback) {
        if (PacketUtils.handleSendPacket(packet)) return;
        final PacketEvent event = new PacketEvent(packet);
        ExtendedPosition extendedPosition = Client.moduleManager.getModule(ExtendedPosition.class);
        assert extendedPosition != null;
        if (extendedPosition.getState()) {
            try {
                extendedPosition.onPacket(event);
            } catch (Exception e) {
                // nothing
            }
        }
        Client.eventManager.callEvent(event);

        if (event.isCancelled())
            callback.cancel();
    }
}