package net.aspw.client.injection.forge.mixins.block;

import net.aspw.client.Client;
import net.aspw.client.event.BlockBBEvent;
import net.aspw.client.features.module.impl.combat.Criticals;
import net.aspw.client.features.module.impl.combat.KillAura;
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
import java.util.Objects;

/**
 * The type Mixin block.
 */
@Mixin(Block.class)
public abstract class MixinBlock {

    /**
     * The Block state.
     */
    @Shadow
    @Final
    protected BlockState blockState;

    /**
     * Gets collision bounding box.
     *
     * @param worldIn the world in
     * @param pos     the pos
     * @param state   the state
     * @return the collision bounding box
     */
    @Shadow
    public abstract AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state);

    /**
     * Sets block bounds.
     *
     * @param minX the min x
     * @param minY the min y
     * @param minZ the min z
     * @param maxX the max x
     * @param maxY the max y
     * @param maxZ the max z
     */
    @Shadow
    public abstract void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

    /**
     * On block placed block state.
     *
     * @param worldIn the world in
     * @param pos     the pos
     * @param facing  the facing
     * @param hitX    the hit x
     * @param hitY    the hit y
     * @param hitZ    the hit z
     * @param meta    the meta
     * @param placer  the placer
     * @return the block state
     */
// Has to be implemented since a non-virtual call on an abstract method is illegal
    @Shadow
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return null;
    }

    @Shadow
    private IBlockState defaultBlockState;

    /**
     * Add collision boxes to list.
     *
     * @param worldIn         the world in
     * @param pos             the pos
     * @param state           the state
     * @param mask            the mask
     * @param list            the list
     * @param collidingEntity the colliding entity
     * @author As_pw
     * @reason XRay
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
        final XRay xray = Objects.requireNonNull(Client.moduleManager.getModule(XRay.class));

        if (xray.getState())
            callbackInfoReturnable.setReturnValue(xray.getXrayBlocks().contains(this));
    }

    @Inject(method = "isCollidable", at = @At("HEAD"), cancellable = true)
    private void isCollidable(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final NoMouseIntersect noMouseIntersect = Objects.requireNonNull(Client.moduleManager.getModule(NoMouseIntersect.class));
        final KillAura killAura = Objects.requireNonNull(Client.moduleManager.getModule(KillAura.class));

        if (noMouseIntersect.getState() && !(noMouseIntersect.getBlockValue().get() == Block.getIdFromBlock((Block) (Object) this)) || killAura.getState() && killAura.getTarget() != null)
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "getAmbientOcclusionLightValue", at = @At("HEAD"), cancellable = true)
    private void getAmbientOcclusionLightValue(final CallbackInfoReturnable<Float> floatCallbackInfoReturnable) {
        final XRay xray = Objects.requireNonNull(Client.moduleManager.getModule(XRay.class));

        if (xray.getState())
            floatCallbackInfoReturnable.setReturnValue(1F);
    }

    /**
     * Modify break speed.
     *
     * @param playerIn     the player in
     * @param worldIn      the world in
     * @param pos          the pos
     * @param callbackInfo the callback info
     */
    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("RETURN"), cancellable = true)
    public void modifyBreakSpeed(EntityPlayer playerIn, World worldIn, BlockPos pos, final CallbackInfoReturnable<Float> callbackInfo) {
        float f = callbackInfo.getReturnValue();
        if (playerIn.onGround) { // NoGround
            final NoFall noFall = Objects.requireNonNull(Client.moduleManager.getModule(NoFall.class));
            final Criticals criticals = Objects.requireNonNull(Client.moduleManager.getModule(Criticals.class));

            if (noFall.getState() && noFall.getTypeValue().get().equalsIgnoreCase("edit") && noFall.getEditMode().get().equalsIgnoreCase("noground") ||
                    criticals.getState() && criticals.getModeValue().get().equalsIgnoreCase("NoGround")) {
                f /= 5F;
            }
        }

        callbackInfo.setReturnValue(f);
    }
}