package net.aspw.client.injection.forge.mixins.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.utils.MinecraftInstance;
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
        if (ProtocolBase.getManager().getTargetVersion().newerThan(ProtocolVersion.v1_8) && !MinecraftInstance.mc.isIntegratedServerRunning())
            return 0.1875F;
        return 0.125F;
    }
}