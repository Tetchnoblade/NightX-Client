package net.aspw.client.injection.forge.mixins.optimize;

import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = {WorldClient.class})
public class MixinAnimationTick {
    @ModifyConstant(method = "doVoidFogParticles", constant = @Constant(intValue = 1000))
    private int patcher$lowerTickCount(int original) {
        return 100;
    }
}