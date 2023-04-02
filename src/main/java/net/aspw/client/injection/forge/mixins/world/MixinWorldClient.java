package net.aspw.client.injection.forge.mixins.world;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.visual.ShowInvis;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @ModifyVariable(method = "doVoidFogParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;randomDisplayTick(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", shift = At.Shift.AFTER), ordinal = 0)
    private boolean handleBarriers(final boolean flag) {
        final ShowInvis trueSight = Client.moduleManager.getModule(ShowInvis.class);
        return flag || trueSight.getState() && trueSight.getBarriersValue().get();
    }
}