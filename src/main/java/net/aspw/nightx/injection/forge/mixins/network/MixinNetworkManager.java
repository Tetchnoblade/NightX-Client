package net.aspw.nightx.injection.forge.mixins.network;

import io.netty.channel.ChannelHandlerContext;
import net.aspw.nightx.NightX;
import net.aspw.nightx.event.PacketEvent;
import net.aspw.nightx.features.module.modules.render.Hud;
import net.aspw.nightx.utils.PacketUtils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void read(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        final PacketEvent event = new PacketEvent(packet);
        NightX.eventManager.callEvent(event);

        if (event.isCancelled())
            callback.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo callback) {
        if (PacketUtils.handleSendPacket(packet)) return;
        final PacketEvent event = new PacketEvent(packet);
        NightX.eventManager.callEvent(event);

        if (event.isCancelled())
            callback.cancel();
    }

    /**
     * show player head in tab bar
     *
     * @author Liulihaocai, FDPClient
     */
    @Inject(method = "getIsencrypted", at = @At("HEAD"), cancellable = true)
    private void injectEncryption(CallbackInfoReturnable<Boolean> cir) {
        final Hud hud = NightX.moduleManager.getModule(Hud.class);
        if (hud != null && hud.getTabHead().get()) {
            cir.setReturnValue(true);
        }
    }

}