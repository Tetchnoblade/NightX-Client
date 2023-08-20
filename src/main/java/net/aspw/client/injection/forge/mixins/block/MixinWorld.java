package net.aspw.client.injection.forge.mixins.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(World.class)
public abstract class MixinWorld implements IBlockAccess {

    /**
     * @author As_pw
     * @reason Fix Destroy Sounds
     */
    @Overwrite
    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        IBlockState iblockstate = Minecraft.getMinecraft().theWorld.getBlockState(pos);
        Block block = iblockstate.getBlock();

        // Moving playAusSFX out of else-statement to play sound correctly (For some reason block.getMaterial() always returns Material.air on 1.9+ protocols)
        // This should also function correctly on 1.8.x protocol, so no need for base version checks
        Minecraft.getMinecraft().theWorld.playAuxSFX(2001, pos, Block.getStateId(iblockstate));

        if (block.getMaterial() == Material.air) {
            return false;
        } else {
            if (dropBlock) {
                block.dropBlockAsItem(Minecraft.getMinecraft().theWorld, pos, iblockstate, 0);
            }

            return Minecraft.getMinecraft().theWorld.setBlockState(pos, Blocks.air.getDefaultState(), 3);
        }
    }
}