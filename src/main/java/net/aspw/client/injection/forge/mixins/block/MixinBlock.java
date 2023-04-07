package net.aspw.client.injection.forge.mixins.block;

import net.aspw.client.Client;
import net.aspw.client.event.BlockBBEvent;
import net.aspw.client.features.module.impl.combat.Criticals;
import net.aspw.client.features.module.impl.exploit.NoMouseIntersect;
import net.aspw.client.features.module.impl.movement.NoFall;
import net.aspw.client.features.module.impl.visual.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Shadow
    @Final
    protected BlockState blockState;

    @Shadow
    public abstract AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state);

    @Shadow
    public abstract void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

    // Has to be implemented since a non-virtual call on an abstract method is illegal
    @Shadow
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return null;
    }

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(worldIn, pos, state);
        BlockBBEvent blockBBEvent = new BlockBBEvent(pos, blockState.getBlock(), axisalignedbb);
        Client.eventManager.callEvent(blockBBEvent);
        axisalignedbb = blockBBEvent.getBoundingBox();
        if (axisalignedbb != null && mask.intersectsWith(axisalignedbb))
            list.add(axisalignedbb);
    }

    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    private void shouldSideBeRendered(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final XRay xray = Client.moduleManager.getModule(XRay.class);

        if (xray.getState())
            callbackInfoReturnable.setReturnValue(xray.getXrayBlocks().contains(this));
    }

    @Inject(method = "isCollidable", at = @At("HEAD"), cancellable = true)
    private void isCollidable(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final NoMouseIntersect ghostHand = Client.moduleManager.getModule(NoMouseIntersect.class);

        if (ghostHand.getState() && !(ghostHand.getBlockValue().get() == Block.getIdFromBlock((Block) (Object) this)))
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "getAmbientOcclusionLightValue", at = @At("HEAD"), cancellable = true)
    private void getAmbientOcclusionLightValue(final CallbackInfoReturnable<Float> floatCallbackInfoReturnable) {
        if (Client.moduleManager.getModule(XRay.class).getState())
            floatCallbackInfoReturnable.setReturnValue(1F);
    }

    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("RETURN"), cancellable = true)
    public void modifyBreakSpeed(EntityPlayer playerIn, World worldIn, BlockPos pos, final CallbackInfoReturnable<Float> callbackInfo) {
        float f = callbackInfo.getReturnValue();
        if (playerIn.onGround) { // NoGround
            final NoFall noFall = Client.moduleManager.getModule(NoFall.class);
            final Criticals criticals = Client.moduleManager.getModule(Criticals.class);

            if (noFall.getState() && noFall.getTypeValue().get().equalsIgnoreCase("edit") && noFall.getEditMode().get().equalsIgnoreCase("noground") ||
                    criticals.getState() && criticals.getModeValue().get().equalsIgnoreCase("NoGround")) {
                f /= 5F;
            }
        }

        callbackInfo.setReturnValue(f);
    }
}