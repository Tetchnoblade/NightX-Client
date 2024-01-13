package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.protocol.ProtocolBase;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiOverlayDebug.class)
public class MixinGuiOverlayDebug {

    @Inject(method = "getDebugInfoRight", at = @At(value = "TAIL"))
    public void addViaForgeVersion(CallbackInfoReturnable<List<String>> cir) {
        final ProtocolBase common = ProtocolBase.getManager();
        final VersionEnum version = ProtocolBase.getManager().getTargetVersion();

        cir.getReturnValue().add("");

        int protocolVersion = version.getVersion();
        if (version.isOlderThanOrEqualTo(VersionEnum.r1_6_4))
            protocolVersion = LegacyProtocolVersion.getRealProtocolVersion(protocolVersion);

        if (!common.getPlatform().isSingleplayer().get())
            cir.getReturnValue().add("Protocol: " + version.getName() + " (" + protocolVersion + ")");
        else cir.getReturnValue().add("Protocol: 1.8.x (47)");
    }
}
