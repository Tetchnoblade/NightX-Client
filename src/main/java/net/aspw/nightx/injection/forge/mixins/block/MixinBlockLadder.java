package net.aspw.nightx.injection.forge.mixins.block;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.movement.FastLadder;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockLadder.class)
public abstract class MixinBlockLadder extends MixinBlock {

    @Shadow
    @Final
    public static PropertyDirection FACING;

    /**
     * /@author CCBlueX
     */
    @Overwrite
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() instanceof BlockLadder) {
            final FastLadder fastClimb = NightX.moduleManager.getModule(FastLadder.class);
            final float f = fastClimb.getState() && fastClimb.getModeValue().get().equalsIgnoreCase("AAC3.0.0") ? 0.99f : 0.125f;

            switch (iblockstate.getValue(FACING)) {
                case NORTH:
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                    break;
                case SOUTH:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                    break;
                case WEST:
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case EAST:
                default:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        }
    }

}
