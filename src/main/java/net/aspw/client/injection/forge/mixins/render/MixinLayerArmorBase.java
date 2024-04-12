package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Launch;
import net.aspw.client.features.api.McUpdatesHandler;
import net.aspw.client.features.module.impl.visual.CustomModel;
import net.aspw.client.protocol.api.ProtocolFixer;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * The type Mixin layer armor base.
 */
@Mixin({LayerArmorBase.class})
public class MixinLayerArmorBase {
    /**
     * Do render layer.
     *
     * @param entitylivingbaseIn the entitylivingbase in
     * @param limbSwing          the limb swing
     * @param limbSwingAmount    the limb swing amount
     * @param partialTicks       the partial ticks
     * @param ageInTicks         the age in ticks
     * @param netHeadYaw         the net head yaw
     * @param headPitch          the head pitch
     * @param scale              the scale
     * @param ci                 the ci
     */
    @Inject(method = {"doRenderLayer"}, at = {@At("HEAD")}, cancellable = true)
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final CallbackInfo ci) {
        final CustomModel customModel = Objects.requireNonNull(Launch.moduleManager.getModule(CustomModel.class));

        if (ProtocolFixer.newerThanOrEqualsTo1_13() && McUpdatesHandler.shouldAnimation() && entitylivingbaseIn == MinecraftInstance.mc.thePlayer)
            ci.cancel();

        if (customModel.getState() && customModel.getOnlySelf().get() && entitylivingbaseIn == MinecraftInstance.mc.thePlayer) {
            ci.cancel();
        } else if (customModel.getState() && !customModel.getOnlySelf().get()) {
            ci.cancel();
        }
    }
}