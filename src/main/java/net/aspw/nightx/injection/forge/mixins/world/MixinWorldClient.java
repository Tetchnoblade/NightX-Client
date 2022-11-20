package net.aspw.nightx.injection.forge.mixins.world;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.render.ShowInvis;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @ModifyVariable(method = "doVoidFogParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;randomDisplayTick(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", shift = At.Shift.AFTER), ordinal = 0)
    private boolean handleBarriers(final boolean flag) {
        final ShowInvis trueSight = NightX.moduleManager.getModule(ShowInvis.class);
        return flag || trueSight.getState() && trueSight.getBarriersValue().get();
    }
/*
    @Inject(method = "tick", at = @At("RETURN"))
    public void injectWorldTick(CallbackInfo callbackInfo) {
        wdl.WDLHooks.onWorldClientTick((WorldClient) (Object) this);
    }

    @Inject(method = "doPreChunk", at = @At("HEAD"))
    public void doPreChunk(int p_73025_1_, int p_73025_2_, boolean p_73025_3_, CallbackInfo callbackInfo) {
        wdl.WDLHooks.onWorldClientDoPreChunk((WorldClient) (Object) this, p_73025_1_, p_73025_2_, p_73025_3_);
    }

    @Inject(method = "removeEntityFromWorld", at = @At("HEAD"))
    public void removeEntityFromWorld(int p_73028_1_, CallbackInfoReturnable<Entity> callbackInfo) {
        wdl.WDLHooks.onWorldClientRemoveEntityFromWorld((WorldClient) (Object) this, p_73028_1_);
    }
*/
}