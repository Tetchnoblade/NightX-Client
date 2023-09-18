package net.aspw.client.injection.forge.mixins.block;

import net.aspw.client.protocol.Protocol;
import net.minecraft.block.BlockLadder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * The type Mixin block ladder.
 */
@Mixin(BlockLadder.class)
public abstract class MixinBlockLadder extends MixinBlock {

    @ModifyConstant(method = "setBlockBoundsBasedOnState", constant = @Constant(floatValue = 0.125F))
    private float ViaVersion_LadderBB(float constant) {
        if (!Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x"))
            return 0.1875F;
        return 0.125F;
    }
}