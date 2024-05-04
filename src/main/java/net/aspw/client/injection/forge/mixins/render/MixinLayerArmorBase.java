package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.player.Freecam;
import net.aspw.client.features.module.impl.player.ReverseFreecam;
import net.aspw.client.features.module.impl.visual.CustomModel;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin({LayerArmorBase.class})
public class MixinLayerArmorBase {

    @Inject(method = {"doRenderLayer"}, at = {@At("HEAD")}, cancellable = true)
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final CallbackInfo ci) {
        final CustomModel customModel = Objects.requireNonNull(Launch.moduleManager.getModule(CustomModel.class));
        final Freecam freecam = Objects.requireNonNull(Launch.moduleManager.getModule(Freecam.class));
        final ReverseFreecam reverseFreecam = Objects.requireNonNull(Launch.moduleManager.getModule(ReverseFreecam.class));

        if (customModel.getState() || freecam.getState() && entitylivingbaseIn == freecam.getFakePlayer() || reverseFreecam.getState() && entitylivingbaseIn == reverseFreecam.getFakePlayer())
            ci.cancel();
    }
}