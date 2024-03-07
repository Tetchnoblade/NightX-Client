package net.aspw.client.injection.forge.mixins.gui;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.aspw.client.Launch;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiOverlayDebug.class)
public class MixinGuiOverlayDebug {

    @Inject(method = "getDebugInfoRight", at = @At(value = "TAIL"))
    public void addProtocolVersion(CallbackInfoReturnable<List<String>> cir) {
        final ProtocolVersion version = ProtocolBase.getManager().getTargetVersion();

        cir.getReturnValue().add("");

        int protocolVersion = version.getVersion();

        if (!MinecraftInstance.mc.isIntegratedServerRunning())
            cir.getReturnValue().add("Protocol: " + version.getName() + " (" + protocolVersion + ")");
        else cir.getReturnValue().add("Protocol: 1.8.x (47)");

        cir.getReturnValue().add("");

        cir.getReturnValue().add(Launch.CLIENT_BEST + " Client " + Launch.CLIENT_VERSION);
    }
}
