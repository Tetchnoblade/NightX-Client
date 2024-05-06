package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Launch;
import net.aspw.client.features.api.PacketManager;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.combat.KillAuraRecode;
import net.aspw.client.features.module.impl.combat.TPAura;
import net.aspw.client.features.module.impl.movement.SilentSneak;
import net.aspw.client.features.module.impl.player.LegitScaffold;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.CustomModel;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ModelBiped.class)
public abstract class MixinModelBiped {

    @Shadow
    public ModelRenderer bipedRightArm;

    @Shadow
    public int heldItemRight;

    @Shadow
    public boolean isSneak;

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void revertSwordAnimation(float p_setRotationAngles1, float p_setRotationAngles2, float p_setRotationAngles3, float p_setRotationAngles4, float p_setRotationAngles5, float p_setRotationAngles6, Entity p_setRotationAngles7, CallbackInfo callbackInfo) {
        final KillAura killAura = Objects.requireNonNull(Launch.moduleManager.getModule(KillAura.class));
        final TPAura tpAura = Objects.requireNonNull(Launch.moduleManager.getModule(TPAura.class));
        final KillAuraRecode killAuraRecode = Objects.requireNonNull(Launch.moduleManager.getModule(KillAuraRecode.class));
        final SilentSneak silentSneak = Objects.requireNonNull(Launch.moduleManager.getModule(SilentSneak.class));
        final CustomModel customModel = Objects.requireNonNull(Launch.moduleManager.getModule(CustomModel.class));
        final Scaffold scaffold = Objects.requireNonNull(Launch.moduleManager.getModule(Scaffold.class));
        final LegitScaffold legitScaffold = Objects.requireNonNull(Launch.moduleManager.getModule(LegitScaffold.class));

        if (silentSneak.getState() && silentSneak.modeValue.get().equals("Normal") && p_setRotationAngles7.equals(MinecraftInstance.mc.thePlayer))
            this.isSneak = true;

        if (scaffold.getState() && MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(scaffold.getLastSlot()) == null || legitScaffold.getState() && MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(legitScaffold.getLastSlot()) == null)
            this.bipedRightArm.rotateAngleX = MathHelper.cos(p_setRotationAngles1 * 0.6662F + (float) Math.PI) * 2.0F * p_setRotationAngles2 * 0.5F;

        if (heldItemRight == 3) {
            this.bipedRightArm.rotateAngleY = -0.5235988f;
            return;
        }
        if (heldItemRight == 0 || heldItemRight == 2)
            return;
        if (!Animations.thirdPersonBlockingValue.get().equals("Off") && PacketManager.isVisualBlocking && (Animations.thirdPersonBlockingValue.get().equals("1.7") || Animations.thirdPersonBlockingValue.get().equals("1.8"))) {
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX - ((float) Math.PI / 0.94f) * 0.034f;
            this.bipedRightArm.rotateAngleY = -0.5235988f;
        }
        PacketManager.isVisualBlocking = (killAura.getState() && killAura.getTarget() != null && !killAura.getAutoBlockModeValue().get().equals("None") || tpAura.getState() && tpAura.isBlocking() || killAuraRecode.getState() && killAuraRecode.isBlocking()) && p_setRotationAngles7 instanceof EntityPlayer && p_setRotationAngles7.equals(MinecraftInstance.mc.thePlayer) && MinecraftInstance.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && MinecraftInstance.mc.thePlayer.getHeldItem() != null && !customModel.getState();
    }
}