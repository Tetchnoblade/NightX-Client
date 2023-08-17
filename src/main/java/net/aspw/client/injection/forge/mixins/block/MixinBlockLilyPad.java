package net.aspw.client.injection.forge.mixins.block;

import net.aspw.client.protocol.Protocol;
import net.aspw.client.protocol.ViaPatcher;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * The type Mixin block lily pad.
 */
@Mixin(BlockLilyPad.class)
public abstract class MixinBlockLilyPad extends BlockBush {

    /**
     * @author As_pw
     * @reason Via Fixer
     */
    @Overwrite
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        if (ViaPatcher.INSTANCE.getLilyPadFix() && !Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x"))
            return new AxisAlignedBB((double) pos.getX() + 0.0625D, (double) pos.getY() + 0.0D, (double) pos.getZ() + 0.0625D, (double) pos.getX() + 0.9375D, (double) pos.getY() + 0.09375D, (double) pos.getZ() + 0.9375D);
        return new AxisAlignedBB((double) pos.getX() + 0.0D, (double) pos.getY() + 0.0D, (double) pos.getZ() + 0.0D, (double) pos.getX() + 1.0D, (double) pos.getY() + 0.015625D, (double) pos.getZ() + 1.0D);
    }
}