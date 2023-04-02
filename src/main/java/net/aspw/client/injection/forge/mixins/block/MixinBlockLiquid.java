package net.aspw.client.injection.forge.mixins.block;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.movement.NoSlow;
import net.aspw.client.features.module.impl.other.LiquidInteract;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid {

    @Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
    private void onCollideCheck(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Client.moduleManager.getModule(LiquidInteract.class).getState())
            callbackInfoReturnable.setReturnValue(true);
    }

    @Inject(method = "modifyAcceleration", at = @At("HEAD"), cancellable = true)
    private void onModifyAcceleration(CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
        final NoSlow noSlow = Client.moduleManager.getModule(NoSlow.class);

        if (noSlow.getState() && noSlow.getLiquidPushValue().get()) {
            callbackInfoReturnable.setReturnValue(new Vec3(0.0D, 0.0D, 0.0D));
        }
    }
}