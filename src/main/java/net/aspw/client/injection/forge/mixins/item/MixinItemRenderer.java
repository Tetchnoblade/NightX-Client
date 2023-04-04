package net.aspw.client.injection.forge.mixins.item;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.NoEffect;
import net.aspw.client.utils.timer.MSTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    float delay = 0.0F;
    MSTimer rotateTimer = new MSTimer();

    @Shadow
    private float prevEquippedProgress;

    @Shadow
    private float equippedProgress;


    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    private ItemStack itemToRender;

    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks);

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    @Shadow
    protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    private void func_178103_d(float qq) {
        GlStateManager.translate(-0.5F, qq, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    private void func_178096_b(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void dortware(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void slide1(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float) Math.PI);
        GlStateManager.rotate(var3 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void slide2(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float) Math.PI);
        GlStateManager.rotate(var3 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -60.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void slide3(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float) Math.PI);
        GlStateManager.rotate(var3 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void Random() {
        ++this.delay;
        GlStateManager.translate(0.7D, -0.4000000059604645D, -0.800000011920929D);
        GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate(this.delay * 0.2F * Animations.SpeedRotate.get() + 20, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void avatar(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void dortwarepush(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var3 * 20.0F, -4.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void Zoom(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void ETB(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void sigmaold(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(25F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F);
        GlStateManager.rotate(var3 * -15F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -10F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -30F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void lennox(final float test1, final float test2) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var30 = MathHelper.sin(test2 * MathHelper.sqrt_float(test1) * 3.1415927f);
        float var31 = MathHelper.cos(MathHelper.sqrt_float(test2) * (float) Math.PI);
        float var29 = -MathHelper.abs(MathHelper.sqrt_float(test1) * test2 * (float) Math.PI);
        GlStateManager.rotate(var30 * var29 * -90.0f, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var29 * var31 * 5.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var31 * 5.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void stab(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0f, 0.0F);
        GlStateManager.rotate(var12 * -10.0F, 1.0F, 0.0F, -4.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void continuity(float var11, float var10) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var12 = -MathHelper.sin(var10 * var10 * (float) Math.PI);
        float var13 = MathHelper.cos(MathHelper.sqrt_float(var10) * (float) Math.PI);
        float var14 = MathHelper.abs(MathHelper.sqrt_float(var11) * (float) Math.PI);
        GlStateManager.rotate(var12 * var14 * 30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var13 * 0.0F, 0.0F, 0.0f, 1.0F);
        GlStateManager.rotate(var13 * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void shield(float var11, float var12) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(48.57f, 0f, 0.24f, 0.14f);
        float var13 = MathHelper.sin(var12 * var12 * (float) Math.PI);
        float var14 = MathHelper.sin(MathHelper.sqrt_float(var12) * (float) Math.PI);
        GlStateManager.rotate(var13 * -35.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var14 * 0.0F, 0.0F, 0.0f, 0.0F);
        GlStateManager.rotate(var14 * 20.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    }

    private void func_178103_d() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
        float f1 = abstractclientplayer.getSwingProgress(partialTicks);
        float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        GL11.glTranslated(Animations.itemPosX.get().doubleValue(), Animations.itemPosY.get().doubleValue(), Animations.itemPosZ.get().doubleValue());
        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP) abstractclientplayer, partialTicks);
        GlStateManager.scale(Animations.itemFovX.getValue() + 1, Animations.itemFovY.getValue() + 1, -Animations.itemFovZ.getValue() + 1);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GL11.glTranslated(Animations.itemPosX.get().doubleValue(), Animations.itemPosY.get().doubleValue(), Animations.itemPosZ.get().doubleValue());

        if (this.itemToRender != null) {
            if (Animations.oldAnimations.getValue() && (itemToRender.getItem() instanceof ItemCarrotOnAStick || itemToRender.getItem() instanceof ItemFishingRod)) {
                GlStateManager.translate(0.08F, -0.027F, -0.33F);
                GlStateManager.scale(0.93F, 1.0F, 1.0F);
            }

            final KillAura killAura = Client.moduleManager.getModule(KillAura.class);

            if (this.itemToRender.getItem() instanceof ItemMap) {
                this.renderItemMap(abstractclientplayer, f2, f, f1);
            } else if (abstractclientplayer.getItemInUseCount() > 0
                    || (itemToRender.getItem() instanceof ItemSword && (killAura.getBlockingStatus() || killAura.getFakeBlock()) && !killAura.getAutoBlockModeValue().get().equals("None"))
                    || (itemToRender.getItem() instanceof ItemSword
                    && killAura.getTarget() != null && !killAura.getAutoBlockModeValue().get().equals("None"))) {

                EnumAction enumaction = (killAura.getBlockingStatus()) ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();

                switch (enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        this.performDrinking(abstractclientplayer, partialTicks);
                        if (Animations.oldAnimations.getValue()) {
                            this.transformFirstPersonItem(f, f1);
                        } else {
                            this.transformFirstPersonItem(f, 0.0F);
                        }
                        break;
                    case BLOCK:
                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("jello")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.1, Animations.blockPosY.get().doubleValue() + 0.31, Animations.blockPosZ.get().doubleValue() + 0.24);
                            this.func_178096_b(f / Animations.Equip.getValue(), 0.0f);
                            this.func_178103_d();
                            int alpha = (int) Math.min(255, ((System.currentTimeMillis() % 255) > 255 / 2 ? (Math.abs(Math.abs(System.currentTimeMillis()) % 255 - 255)) : System.currentTimeMillis() % 255) * 2);
                            float f5 = (f1 > 0.5 ? 1 - f1 : f1);
                            GlStateManager.translate(0.3f, -0.0f, 0.40f);
                            GlStateManager.rotate(0.0f, 0.0f, 0.0f, 1.0f);
                            GlStateManager.translate(0, 0.5f, 0);

                            GlStateManager.rotate(90, 1.0f, 0.0f, -1.0f);
                            GlStateManager.translate(0.6f, 0.5f, 0);
                            GlStateManager.rotate(-90, 1.0f, 0.0f, -1.0f);


                            GlStateManager.rotate(-10, 1.0f, 0.0f, -1.0f);
                            GlStateManager.rotate((-f5) * 10.0f, 10.0f, 10.0f, -9.0f);
                            GlStateManager.rotate(10.0f, -1.0f, 0.0f, 0.0f);

                            GlStateManager.translate(0, 0, -0.5);
                            GlStateManager.rotate(mc.thePlayer.isSwingInProgress ? -alpha / 5f : 1, 1.0f, -0.0f, 1.0f);
                            GlStateManager.translate(0, 0, 0.5);
                            break;
                        }
                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("sloth")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.11, Animations.blockPosY.get().doubleValue() - 0.02, Animations.blockPosZ.get().doubleValue() - 0.24);
                            this.func_178096_b(f / Animations.Equip.getValue(), f1);
                            this.func_178103_d();
                            break;
                        }

                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("shield")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.22, Animations.blockPosZ.get().doubleValue() + 0.06);
                            this.shield(f / Animations.Equip.getValue(), f1);
                            this.func_178103_d();
                            break;
                        }

                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("reverse")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.16, Animations.blockPosZ.get().doubleValue() - 0.12);
                            this.func_178096_b(f / Animations.Equip.getValue(), f1);
                            this.func_178103_d();
                            GL11.glTranslated(0.08D, -0.1D, -0.3D);
                            break;
                        }

                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("smooth")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.08, Animations.blockPosY.get().doubleValue(), Animations.blockPosZ.get().doubleValue() - 0.24);
                            this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                            float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                            this.func_178103_d(0.2F);
                            GlStateManager.translate(-0.36f, 0.25f, -0.06f);
                            GlStateManager.rotate(-var91 * 35.0f, -8.0f, -0.0f, 9.0f);
                            GlStateManager.rotate(-var91 * 70.0f, 1.0f, 0.4f, -0.0f);
                            break;
                        }

                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("hide")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue(), Animations.blockPosZ.get().doubleValue());
                            if (Animations.swingAnimValue.get().equals("Vanilla")) {
                                this.doItemUsedTransformations(f1);
                                this.transformFirstPersonItem(f, f1);
                            }
                            if (Animations.swingAnimValue.get().equals("Flux")) {
                                this.transformFirstPersonItem(f, f1);
                            }
                            break;
                        }

                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("autumn")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.1, Animations.blockPosY.get().doubleValue() - 0.01, Animations.blockPosZ.get().doubleValue() - 0.04);
                            this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                            float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                            this.func_178103_d(0.2F);
                            GlStateManager.translate(-0.4f, 0.28f, 0.0f);
                            GlStateManager.rotate(-var91 * 35.0f, -8.0f, -0.0f, 9.0f);
                            GlStateManager.rotate(-var91 * 70.0f, 1.0f, -0.4f, -0.0f);
                            break;
                        }

                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("spin")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue() - 0.21, Animations.blockPosY.get().doubleValue() + 0.42, Animations.blockPosZ.get().doubleValue() + 0.66);
                            this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                            Random();
                            this.doBlockTransformations();
                            break;
                        }

                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("old")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.08, Animations.blockPosY.get().doubleValue() - 0.08, Animations.blockPosZ.get().doubleValue() - 0.05);
                            this.transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                            this.doBlockTransformations();
                            GlStateManager.translate(-0.35F, 0.2F, 0.0F);
                            break;
                        }

                        if (Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("jigsaw")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() - 0.12, Animations.blockPosZ.get().doubleValue() - 0.1);
                            transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                            doBlockTransformations();
                            GlStateManager.translate(-0.5D, 0.0D, 0.0D);
                            break;
                        }

                        if (!Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("reverse") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("hide") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("jello") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("shield") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("jigsaw") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("spin") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("old") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("sloth") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("smooth") && !Client.moduleManager.getModule(Animations.class).Sword.get().equalsIgnoreCase("autumn")) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.06, Animations.blockPosZ.get().doubleValue());
                            final String z = Animations.Sword.get();
                            switch (z) {
                                case "SlideFull": {
                                    this.slide1(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "SlideMedium": {
                                    this.slide2(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "Stab": {
                                    this.stab(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "VisionFX": {
                                    this.continuity(0.1f, f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "Astolfo": {
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                                    GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(Swang * -50.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swang * 40.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Dash": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GL11.glRotated(-var9 * 20.0F, var9 / 2, 0.0F, 9.0F);
                                    GL11.glRotated(-var9 * 50.0F, 0.8F, var9 / 2, 0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Swank": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                                    GL11.glRotatef(var9 * 30.0F / 2.0F, -var9, -0.0F, 9.0F);
                                    GL11.glRotatef(var9 * 40.0F, 1.0F, -var9 / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Swonk": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GL11.glRotated(-var9 * -30.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GL11.glRotated(-var9 * 7.5F, 1.0F, var9 / 3.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "MoonPush": {
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    doBlockTransformations();
                                    float sin = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.scale(1.0F, 1.0F, 1.0F);
                                    GlStateManager.translate(-0.2F, 0.45F, 0.25F);
                                    GlStateManager.rotate(-sin * 20.0F, -5.0F, -5.0F, 9.0F);
                                    break;
                                }
                                case "Stella": {
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                                    GlStateManager.translate(-0.5F, 0.3F, -0.2F);
                                    GlStateManager.rotate(32, 0, 1, 0);
                                    GlStateManager.rotate(-70, 1, 0, 0);
                                    GlStateManager.rotate(40, 0, 1, 0);
                                    break;
                                }
                                case "Sigma3": {
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                                    GL11.glTranslated(0.14D, -0.03D, -0.3D);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(Swang * 25.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swang * 15.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Push": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 40.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 30.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Swang": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 60.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 45.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Moon": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 65.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 60.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "DortwarePush": {
                                    this.dortwarepush(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "Double1": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 0.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 120.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Double2": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 120.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 120.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Lennox": {
                                    this.lennox(-0.3f, f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "Avatar": {
                                    this.avatar(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "SlideLow": {
                                    this.slide3(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "ETB": {
                                    this.ETB(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "1.8": {
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Sigma4": {
                                    float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
                                    this.sigmaold(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var15 * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-var15 * 45, 1.0F, var15 / 2, -0.0F);

                                    this.func_178103_d();
                                    GL11.glTranslated(1.2, 0.3, 0.5);
                                    GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                                    GlStateManager.scale(1.2f, 1.2f, 1.2f);
                                    break;
                                }
                                case "Spinny": {
                                    this.func_178096_b(0.0f, 0.95f);
                                    GlStateManager.rotate(this.delay, 1.0F, 0.0F, 2.0F);
                                    if (this.rotateTimer.hasTimePassed(1)) {
                                        ++this.delay;
                                        this.delay = this.delay + Animations.SpeedRotate.get() - 9;
                                        this.rotateTimer.reset();
                                    }
                                    if (this.delay > 360.0F) {
                                        this.delay = 0.0F;
                                    }

                                    this.func_178103_d();
                                    GlStateManager.rotate(this.delay, 0.0F, 1.0F, 0.0F);
                                    break;
                                }
                                case "Rotate": {
                                    this.func_178096_b(f / Animations.Equip.getValue(), 0.95f);

                                    this.func_178103_d();
                                    GlStateManager.rotate(this.delay, 1.0F, 0.0F, 2.0F);
                                    if (this.rotateTimer.hasTimePassed(1)) {
                                        ++this.delay;
                                        this.delay = this.delay + Animations.SpeedRotate.get() - 9;
                                        this.rotateTimer.reset();
                                    }
                                    if (this.delay > 360.0F) {
                                        this.delay = 0.0F;
                                    }
                                    break;
                                }
                                case "Swing": {
                                    this.func_178096_b(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "Dortware": {
                                    this.dortware(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                                case "Float": {
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 40.0F / 2.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 30.0F, 1.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "Ninja": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 70.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 0.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Edit": {
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                                    GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(Swang * 28.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swang * 30.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Zoom": {
                                    this.Zoom(f / Animations.Equip.getValue(), f1);
                                    this.func_178103_d();
                                    break;
                                }
                            }
                        }
                        break;
                    case BOW:
                        if (Animations.oldAnimations.getValue()) {
                            this.transformFirstPersonItem(f, f1);
                        } else {
                            this.transformFirstPersonItem(f, 0.0F);
                        }
                        this.doBowTransformations(partialTicks, abstractclientplayer);
                }
            } else {
                if (Animations.swingAnimValue.get().equals("Vanilla")) {
                    this.doItemUsedTransformations(f1);
                    this.transformFirstPersonItem(f, f1);
                }
                if (Animations.swingAnimValue.get().equals("Flux")) {
                    this.transformFirstPersonItem(f, f1);
                }
            }

            this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!abstractclientplayer.isInvisible())
            this.renderPlayerArm(abstractclientplayer, f, f1);

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GL11.glTranslated(-Animations.itemPosX.get().doubleValue(), -Animations.itemPosY.get().doubleValue(), -Animations.itemPosZ.get().doubleValue());
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void renderFireInFirstPerson(final CallbackInfo callbackInfo) {
        final NoEffect antiBlind = Client.moduleManager.getModule(NoEffect.class);

        if (antiBlind.getState() && antiBlind.getFireEffect().get()) {
            //vanilla's method
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            callbackInfo.cancel();
        }
    }
}