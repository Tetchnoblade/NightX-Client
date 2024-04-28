package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Launch;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiOverlayDebug.class)
public class MixinGuiOverlayDebug {

    @Inject(method = "getDebugInfoRight", at = @At(value = "TAIL"))
    public void addInformation(CallbackInfoReturnable<List<String>> cir) {
        cir.getReturnValue().add("");

        cir.getReturnValue().add(Launch.CLIENT_BEST + " Client " + Launch.CLIENT_VERSION);
    }
}