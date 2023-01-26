package net.aspw.nightx.injection.forge.mixins.render;

import net.minecraft.client.renderer.BlockFluidRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockFluidRenderer.class)
public class MixinBlockFluidRenderer {
    @ModifyConstant(method = "renderFluid", constant = @Constant(floatValue = 0.001F))
    private float fixFluidStitching(float original) {
        return 0.0F;
    }
}
