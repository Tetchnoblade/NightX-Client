package net.aspw.client.injection.forge.mixins.network;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.aspw.client.Launch;
import net.aspw.client.event.PacketEvent;
import net.aspw.client.features.module.impl.exploit.ExtendedPosition;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.protocol.api.VFNetworkManager;
import net.aspw.client.utils.PacketUtils;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.LazyLoadBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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
    private ProtocolVersion viaForge$targetVersion;

    @Inject(method = "func_181124_a", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;group(Lio/netty/channel/EventLoopGroup;)Lio/netty/bootstrap/AbstractBootstrap;"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void trackSelfTarget(final InetAddress address, final int serverPort, final boolean useNativeTransport, final CallbackInfoReturnable<NetworkManager> cir, final NetworkManager networkmanager, final Class oclass, final LazyLoadBase lazyloadbase) {
        ((VFNetworkManager) networkmanager).viaForge$setTrackedVersion(ProtocolBase.getManager().getTargetVersion());
    }

    @Inject(method = "setCompressionTreshold", at = @At("RETURN"))
    public void reorderPipeline(final int p_setCompressionTreshold_1_, final CallbackInfo ci) {
        ProtocolBase.getManager().reorderCompression(channel);
    }

    @Override
    public ProtocolVersion viaForge$getTrackedVersion() {
        return viaForge$targetVersion;
    }

    @Override
    public void viaForge$setTrackedVersion(final ProtocolVersion version) {
        viaForge$targetVersion = version;
    }

    /**
     * @author As_pw
     * @reason Packet Tracking
     */
    @Overwrite
    protected void channelRead0(final ChannelHandlerContext p_channelRead0_1_, final Packet p_channelRead0_2_) {
        final PacketEvent event = new PacketEvent(p_channelRead0_2_);
        final ExtendedPosition extendedPosition = Launch.moduleManager.getModule(ExtendedPosition.class);
        assert extendedPosition != null;
        if (extendedPosition.getState()) {
            try {
                extendedPosition.onPacket(event);
            } catch (final Exception ignored) {
            }
        }
        Launch.eventManager.callEvent(event);

        if (event.isCancelled())
            return;

        if (this.channel.isOpen()) {
            try {
                p_channelRead0_2_.processPacket(this.packetListener);
            } catch (final ThreadQuickExitException ignored) {
            }
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(final Packet<?> packet, final CallbackInfo callback) {
        if (PacketUtils.handleSendPacket(packet)) return;
        final PacketEvent event = new PacketEvent(packet);
        final ExtendedPosition extendedPosition = Launch.moduleManager.getModule(ExtendedPosition.class);
        assert extendedPosition != null;
        if (extendedPosition.getState()) {
            try {
                extendedPosition.onPacket(event);
            } catch (final Exception ignored) {
            }
        }
        Launch.eventManager.callEvent(event);

        if (event.isCancelled())
            callback.cancel();
    }
}