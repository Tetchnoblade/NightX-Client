package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.visual.CustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerArmorBase.class})
public class MixinLayerArmorBase {
    @Inject(method = {"doRenderLayer"}, at = {@At("HEAD")}, cancellable = true)
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final CallbackInfo ci) {
        if (Client.moduleManager.getModule(CustomModel.class).getState() && Client.moduleManager.getModule(CustomModel.class).getOnlySelf().get() && entitylivingbaseIn == Minecraft.getMinecraft().thePlayer) {
            ci.cancel();
        } else if (Client.moduleManager.getModule(CustomModel.class).getState() && !Client.moduleManager.getModule(CustomModel.class).getOnlySelf().get()) {
            ci.cancel();
        }
    }
}