package net.aspw.nightx.injection.forge.mixins.render;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(ClientCommandHandler.class)
public class MixinClientCommandHandler {
    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyArg(method = {"executeCommand", "func_71556_a"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), remap = false)
    private Object makeLowerCaseForGet(Object s) {
        if (s instanceof String) {
            return ((String) s).toLowerCase(Locale.ENGLISH);
        }
        return s;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = {"executeCommand", "func_71556_a"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void checkForSlash(ICommandSender sender, String message, CallbackInfoReturnable<Integer> cir) {
        if (!message.trim().startsWith("/")) {
            cir.setReturnValue(0);
        }
    }
}
