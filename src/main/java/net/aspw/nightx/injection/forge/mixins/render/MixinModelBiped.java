package net.aspw.nightx.injection.forge.mixins.render;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.client.SilentView;
import net.aspw.nightx.features.module.modules.combat.KillAura;
import net.aspw.nightx.features.module.modules.misc.Annoy;
import net.aspw.nightx.features.module.modules.render.Rotate;
import net.aspw.nightx.features.module.modules.world.Scaffold;
import net.aspw.nightx.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped<T extends MixinRendererLivingEntity> {

    @Shadow
    public ModelRenderer bipedRightArm;

    @Shadow
    public int heldItemRight;

    @Shadow
    public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void revertSwordAnimation(float p_setRotationAngles_1_, float p_setRotationAngles_2_, float p_setRotationAngles_3_, float p_setRotationAngles_4_, float p_setRotationAngles_5_, float p_setRotationAngles_6_, Entity p_setRotationAngles_7_, CallbackInfo callbackInfo) {
        if (heldItemRight == 3)
            this.bipedRightArm.rotateAngleY = 0F;

        if (p_setRotationAngles_7_ instanceof EntityPlayer && p_setRotationAngles_7_.equals(Minecraft.getMinecraft().thePlayer)) {
            final SilentView silentView = NightX.moduleManager.getModule(SilentView.class);
            final Rotate spinBot = NightX.moduleManager.getModule(Rotate.class);
            final KillAura killAura = NightX.moduleManager.getModule(KillAura.class);
            final Scaffold scaffold = NightX.moduleManager.getModule(Scaffold.class);
            final Annoy annoy = NightX.moduleManager.getModule(Annoy.class);
            if (spinBot.getState() && !spinBot.getPitchMode().get().equalsIgnoreCase("none"))
                this.bipedHead.rotateAngleX = spinBot.getPitch() / (180F / (float) Math.PI);
            if (silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadNormalRotate().get() && killAura.getTarget() != null || silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getOhioRotate().get() && silentView.getHeadPrevRotate().get() && killAura.getTarget() != null || silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadNormalRotate().get() && scaffold.getState() || silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadPrevRotate().get() && scaffold.getState() || silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadNormalRotate().get() && annoy.getState() || silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadPrevRotate().get() && annoy.getState()) {
                this.bipedHead.rotateAngleX = RotationUtils.serverRotation.getYaw() / (180F / (float) Math.PI);
            }
            if (!silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadNormalRotate().get() && killAura.getTarget() != null || !silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadPrevRotate().get() && killAura.getTarget() != null || !silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadNormalRotate().get() && scaffold.getState() || !silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadPrevRotate().get() && scaffold.getState() || !silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadNormalRotate().get() && annoy.getState() || !silentView.getOhioRotate().get() && silentView.getState() && silentView.getMode().get().equals("Normal") && silentView.getHeadPrevRotate().get() && annoy.getState()) {
                this.bipedHead.rotateAngleX = RotationUtils.serverRotation.getPitch() / (180F / (float) Math.PI);
            }
        }
    }
}