package net.aspw.client.injection.forge.mixins.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.aspw.client.Launch;
import net.aspw.client.event.PacketEvent;
import net.aspw.client.features.module.impl.combat.BackTrack;
import net.aspw.client.utils.MovementUtils;
import net.aspw.client.utils.PacketUtils;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Shadow
    private Channel channel;

    @Shadow
    private INetHandler packetListener;

    /**
     * @author As_pw
     * @reason Packet Tracking
     */
    @Overwrite
    protected void channelRead0(final ChannelHandlerContext p_channelRead0_1_, final Packet p_channelRead0_2_) {
        final PacketEvent event = new PacketEvent(p_channelRead0_2_);
        final BackTrack backTrack = Launch.moduleManager.getModule(BackTrack.class);
        assert backTrack != null;
        if (backTrack.getState()) {
            try {
                backTrack.onPacket(event);
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
        if (MovementUtils.predicting) callback.cancel();
        if (PacketUtils.handleSendPacket(packet)) return;
        final PacketEvent event = new PacketEvent(packet);
        final BackTrack backTrack = Launch.moduleManager.getModule(BackTrack.class);
        assert backTrack != null;
        if (backTrack.getState()) {
            try {
                backTrack.onPacket(event);
            } catch (final Exception ignored) {
            }
        }
        Launch.eventManager.callEvent(event);

        if (event.isCancelled())
            callback.cancel();
    }
}