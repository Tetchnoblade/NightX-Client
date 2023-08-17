package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.visual.OptiFinePlus;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBaseFix extends Entity {

    public MixinEntityLivingBaseFix(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    @Inject(method = "getLook", at = @At("HEAD"), cancellable = true)
    private void getLook(float partialTicks, CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
        if (((EntityLivingBase) (Object) this) instanceof EntityPlayerSP) {
            if (Client.moduleManager.getModule(OptiFinePlus.class).getState() && Client.moduleManager.getModule(OptiFinePlus.class).mouseDelayFix.get()) {
                callbackInfoReturnable.setReturnValue(super.getLook(partialTicks));
            } else callbackInfoReturnable.setReturnValue(getVectorForRotation(this.rotationPitch, this.rotationYaw));
        }
    }
}