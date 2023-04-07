package net.aspw.client.injection.forge.mixins.block;

import de.enzaxd.viaforge.ViaForge;
import net.minecraft.block.BlockLadder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockLadder.class)
public abstract class MixinBlockLadder extends MixinBlock {

    @ModifyConstant(method = "setBlockBoundsBasedOnState", constant = @Constant(floatValue = 0.125F))
    private float ViaVersion_LadderBB(float constant) {
        if (ViaForge.getInstance().getVersion() > 47)
            return 0.1875F;
        return 0.125F;
    }
}