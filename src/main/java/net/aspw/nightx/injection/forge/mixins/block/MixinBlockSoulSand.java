package net.aspw.nightx.injection.forge.mixins.block;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.movement.NoSlow;
import net.minecraft.block.BlockSoulSand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSoulSand.class)
public class MixinBlockSoulSand {

    @Inject(method = "onEntityCollidedWithBlock", at = @At("HEAD"), cancellable = true)
    private void onEntityCollidedWithBlock(CallbackInfo callbackInfo) {
        final NoSlow noSlow = NightX.moduleManager.getModule(NoSlow.class);

        if (noSlow.getState() && noSlow.getSoulsandValue().get())
            callbackInfo.cancel();
    }
}