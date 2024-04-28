package net.aspw.client.injection.forge.mixins.packets;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.other.BrandSpoofer;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Objects;

@Mixin(C00Handshake.class)
public class MixinC00Handshake {

    @Shadow
    public int port;

    @ModifyConstant(method = "writePacketData", constant = @Constant(stringValue = "\u0000FML\u0000"))
    private String injectAntiForge(String constant) {
        return Objects.requireNonNull(Launch.moduleManager.getModule(BrandSpoofer.class)).getState() && !MinecraftInstance.mc.isIntegratedServerRunning() ? "" : "\u0000FML\u0000";
    }
}