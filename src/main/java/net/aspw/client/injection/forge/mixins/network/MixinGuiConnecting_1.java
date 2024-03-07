package net.aspw.client.injection.forge.mixins.network;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.protocol.api.ExtendedServerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;

@Mixin(targets = "net.minecraft.client.multiplayer.GuiConnecting$1")
public class MixinGuiConnecting_1 {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;func_181124_a(Ljava/net/InetAddress;IZ)Lnet/minecraft/network/NetworkManager;"), remap = false)
    public NetworkManager trackVersion(InetAddress address, int i, boolean b) {
        if (Minecraft.getMinecraft().getCurrentServerData() instanceof ExtendedServerData) {
            final ProtocolVersion version = ((ExtendedServerData) Minecraft.getMinecraft().getCurrentServerData()).viaForge$getVersion();
            if (version != null) {
                ProtocolBase.getManager().setTargetVersionSilent(version);
            }
        }

        return NetworkManager.createNetworkManagerAndConnect(address, i, b);
    }

}
