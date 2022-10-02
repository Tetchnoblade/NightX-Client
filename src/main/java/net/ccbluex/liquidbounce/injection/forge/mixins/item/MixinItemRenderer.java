package net.ccbluex.liquidbounce.injection.forge.mixins.item;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.modules.cool.AntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.BlockAnimations;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
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

    private void genCustom(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(25F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F);
        GlStateManager.rotate(var3 * -15F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -10F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -30F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

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
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void test(float i, float i2) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, i * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(i2 * i2 * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(i2) * (float) Math.PI);
        float var5 = MathHelper.ceiling_float_int(MathHelper.floor_double(i2) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var5 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void tap2(final float var2, final float swing) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        final float var3 = MathHelper.sin(swing * swing * 3.1415927f);
        final float var4 = MathHelper.sin(MathHelper.sqrt_float(swing) * 3.1415927f);
        GlStateManager.translate(0.56f, -0.42f, -0.71999997f);
        GlStateManager.translate(0.1f * var4, -0.0f, -0.21999997f * var4);
        GlStateManager.translate(0.0f, var2 * -0.15f, 0.0f);
        GlStateManager.rotate(var3 * 45.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void avatar(final float equipProgress, final float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        final float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
        final float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f);
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f2 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f2 * -40.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void tap1(float tap1, float tap2) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, tap1 * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(tap2 * tap2 * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(tap2) * (float) Math.PI);
        GlStateManager.rotate(var3 * -40.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * 0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
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
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void slide(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0f, 1.0F);
        GlStateManager.rotate(var12 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void slide2(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0f, 1.0F);
        GlStateManager.rotate(var12 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void jello(float var11, float var12) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(48.57f, 0f, 0.24f, 0.14f);
        float var13 = MathHelper.sin(var12 * var12 * (float) Math.PI);
        float var14 = MathHelper.sin(MathHelper.sqrt_float(var12) * (float) Math.PI);
        GlStateManager.rotate(var13 * -35.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var14 * 0.0F, 0.0F, 0.0f, 0.0F);
        GlStateManager.rotate(var14 * 20.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
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
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void poke(final float var5, final float var6) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        final float var7 = MathHelper.sin(var6 * var6 * 3.1415927f);
        final float var8 = MathHelper.sin(MathHelper.sqrt_float(var6) * 3.1415927f);
        GlStateManager.translate(0.56f, -0.42f, -0.71999997f);
        GlStateManager.translate(0.1f * var8, -0.0f, -0.21999997f * var8);
        GlStateManager.translate(0.0f, var5 * -0.15f, 0.0f);
        GlStateManager.rotate(var7 * 0.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
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
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void strange(float lul, float lol) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var26 = MathHelper.sin(lol * lul * 3.1415927f);
        float var27 = MathHelper.cos(MathHelper.sqrt_double(lul) * (float) Math.PI);
        float var28 = MathHelper.abs(MathHelper.sqrt_float(lul) * (float) Math.PI);
        GlStateManager.rotate(var26 * var27, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var28 * 15.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var27 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void move(final float test1, final float test2) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var30 = MathHelper.sin(test2 * MathHelper.sqrt_float(test1) * 3.1415927f);
        float var31 = MathHelper.cos(MathHelper.sqrt_float(test2) * (float) Math.PI);
        float var29 = -MathHelper.abs(MathHelper.sqrt_float(test1) * test2 * (float) Math.PI);
        GlStateManager.rotate(var30 * var29 * -90.0f, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var29 * var31 * 5.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var31 * 5.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
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
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
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
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void push1(float idk, float idc) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, idk * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(idc * idc * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(idc) * (float) Math.PI);
        GlStateManager.rotate(var3 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void push2(float idk, float idc) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, idk * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(idc * idc * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(idc) * (float) Math.PI);
        GlStateManager.rotate(var3 * -10.0F, 2.0F, 2.0F, 2.0F);
        GlStateManager.rotate(var4 * -10.0F, 2.0F, 2.0F, 0.0F);
        GlStateManager.rotate(var4 * -10.0F, 2.0F, 2.0F, 0.0F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
    }

    private void up(float idk, float idc) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, idk * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(idc * idc * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(idc) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.scale(BlockAnimations.Scale.get(), BlockAnimations.Scale.get(), BlockAnimations.Scale.get());
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
        if (LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState()) {
            GL11.glTranslated(BlockAnimations.itemPosX.get().doubleValue(), BlockAnimations.itemPosY.get().doubleValue(), BlockAnimations.itemPosZ.get().doubleValue());
        }
        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP) abstractclientplayer, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState()) {
            GL11.glTranslated(BlockAnimations.itemPosX.get().doubleValue(), BlockAnimations.itemPosY.get().doubleValue(), BlockAnimations.itemPosZ.get().doubleValue());
        }

        if (this.itemToRender != null) {
            final KillAura killAura = LiquidBounce.moduleManager.getModule(KillAura.class);

            boolean canBlockEverything = LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState() && BlockAnimations.blockEverything.get() && killAura.getTarget() != null
                    && (itemToRender.getItem() instanceof ItemBucketMilk || itemToRender.getItem() instanceof ItemFood
                    || itemToRender.getItem() instanceof ItemPotion || itemToRender.getItem() instanceof ItemAxe || itemToRender.getItem().equals(Items.stick));

            if (this.itemToRender.getItem() instanceof ItemMap) {
                this.renderItemMap(abstractclientplayer, f2, f, f1);
            } else if (abstractclientplayer.getItemInUseCount() > 0
                    || (itemToRender.getItem() instanceof ItemSword && (killAura.getBlockingStatus() || killAura.getFakeBlock()))
                    || (itemToRender.getItem() instanceof ItemSword && LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState()
                    && BlockAnimations.fakeBlock.get() && killAura.getTarget() != null) || canBlockEverything) {

                EnumAction enumaction = (killAura.getBlockingStatus() || canBlockEverything) ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();

                switch (enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        this.performDrinking(abstractclientplayer, partialTicks);
                        this.transformFirstPersonItem(f, 0.0F);

                        if (LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState() && BlockAnimations.RotateItems.get())
                            rotateItemAnim();
                        break;
                    case BLOCK:
                        if (LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState()) {
                            GL11.glTranslated(BlockAnimations.blockPosX.get().doubleValue(), BlockAnimations.blockPosY.get().doubleValue(), BlockAnimations.blockPosZ.get().doubleValue());
                            final String z = BlockAnimations.Sword.get();
                            switch (z) {
                                case "Old": {
                                    this.transformFirstPersonItem(f + 0.1F, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.35F, 0.2F, 0.0F);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "SlideFull": {
                                    this.slide2(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Stella": {
                                    this.transformFirstPersonItem(-0.1F, f1);
                                    GlStateManager.translate(-0.5F, 0.3F, -0.2F);
                                    GlStateManager.rotate(32, 0, 1, 0);
                                    GlStateManager.rotate(-70, 1, 0, 0);
                                    GlStateManager.rotate(40, 0, 1, 0);
                                    break;
                                }
                                case "Cool": {
                                    this.transformFirstPersonItem(f, f1);
                                    GlStateManager.translate(0, 0.2, 0);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "Bruh": {
                                    this.slide2(f, f1);
                                    this.transformFirstPersonItem(f + 0.1F, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Sigma3": {
                                    this.func_178096_b(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.doBlockTransformations();
                                    GlStateManager.translate(0.5F, 0.2F, 0.0F);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Okura": {
                                    this.func_178096_b(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    GL11.glTranslated(0.08D, -0.1D, -0.3D);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Push": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * 40.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 30.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Swang": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * 60.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 45.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "OldAstolfo": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(0.12D, 0.0D, -0.08D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * -35.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 35.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Leaked": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(0.12D, 0.02D, -0.08D);
                                    this.transformFirstPersonItem(f / 10.0F, 0.0f);
                                    GlStateManager.rotate(-var9 * -40.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 0.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Leet": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 10.0F, 0.0f);
                                    GlStateManager.rotate(-var9 * 120.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 0.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Dortware1": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * 0.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 120.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Dortware2": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * 120.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 120.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Avatar": {
                                    this.avatar(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Tap2": {
                                    this.tap2(0.0f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    GlStateManager.scale(2f, 2f, 2f);
                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Poke": {
                                    this.poke(0.1f, f1);
                                    GlStateManager.scale(2.5f, 2.5f, 2.5f);
                                    GL11.glTranslated(1.2, -0.5, 0.5);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Slide": {
                                    this.slide(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Push1": {
                                    this.push1(0.1f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Up": {
                                    this.up(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Shield": {
                                    this.jello(0.0f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Akrien": {
                                    this.func_178096_b(f1, 0.0F);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "VisionFX": {
                                    this.continuity(0.1f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Strange": {
                                    this.strange(f1 + 0.2f, 0.1f);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Lucky": {
                                    this.move(-0.3f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "ETB": {
                                    this.ETB(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "OldSwang": {
                                    this.transformFirstPersonItem(f / 2.0F, 0.0F);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F) * 40.0F / 2.0F, MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) / 2.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 30.0F, 1.0F, MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) / 2.0F, -0.0F);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Sigma4": {
                                    float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
                                    this.sigmaold(f * 0.5f, 0);
                                    GlStateManager.rotate(-var15 * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-var15 * 45, 1.0F, var15 / 2, -0.0F);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    GL11.glTranslated(1.2, 0.3, 0.5);
                                    GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                                    GlStateManager.scale(1.2f, 1.2f, 1.2f);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "SmoothFloat": {
                                    this.func_178096_b(0.0f, 0.95f);
                                    GlStateManager.rotate(this.delay, 1.0F, 0.0F, 2.0F);
                                    if (this.rotateTimer.hasTimePassed(1)) {
                                        ++this.delay;
                                        this.delay = this.delay + BlockAnimations.SpeedRotate.get();
                                        this.rotateTimer.reset();
                                    }
                                    if (this.delay > 360.0F) {
                                        this.delay = 0.0F;
                                    }
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    GlStateManager.rotate(this.delay, 0.0F, 1.0F, 0.0F);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Rotate360": {
                                    this.func_178096_b(0.0f, 0.95f);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    GlStateManager.rotate(this.delay, 1.0F, 0.0F, 2.0F);
                                    if (this.rotateTimer.hasTimePassed(1)) {
                                        ++this.delay;
                                        this.delay = this.delay + BlockAnimations.SpeedRotate.get();
                                        this.rotateTimer.reset();
                                    }
                                    if (this.delay > 360.0F) {
                                        this.delay = 0.0F;
                                    }
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Swing": {
                                    this.func_178096_b(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Flux1": {
                                    this.transformFirstPersonItem(f / 2, 0);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 40.0F / 2.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 30.0F, 1.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F);
                                    this.doBlockTransformations();
                                    GL11.glTranslatef(-0.05F, this.mc.thePlayer.isSneaking() ? -0.2F : 0.0F, 0.1F);
                                    break;
                                }
                                case "Flux2": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * 70.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 0.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Swank": {
                                    transformFirstPersonItem(f / 2.0F, f1);
                                    GL11.glTranslated(0.0D, 0.2D, 0.0D);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(Swang * 30.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swang * 40.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Swaing": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * -30.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 7.5F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Swong": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.06D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * -70.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 17.5F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "NightX": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.06D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.5F, 0.0f);
                                    GlStateManager.rotate(-var9 * -60.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 30.6F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Zoom": {
                                    this.Zoom(0.0f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Move": {
                                    this.test(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Tap1": {
                                    this.tap1(f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Stab": {
                                    this.stab(0.1f, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Push2": {
                                    this.push2(0.1F, f1);
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (BlockAnimations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Jello": {
                                    this.func_178096_b(0, 0.0F);
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

                            }
                        } else {
                            this.transformFirstPersonItem(f + 0.1F, f1);
                            this.doBlockTransformations();
                            GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                        }
                        break;
                    case BOW:
                        this.transformFirstPersonItem(f, 0.0F);
                        if (LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState() && BlockAnimations.RotateItems.get())
                            rotateItemAnim();
                        this.doBowTransformations(partialTicks, abstractclientplayer);
                        if (LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState() && BlockAnimations.RotateItems.get())
                            rotateItemAnim();
                }
            } else {
                this.doItemUsedTransformations(f1);
                this.transformFirstPersonItem(f, f1);
                if (LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState() && BlockAnimations.RotateItems.get())
                    rotateItemAnim();
            }

            this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!abstractclientplayer.isInvisible())
            this.renderPlayerArm(abstractclientplayer, f, f1);

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();

        if (LiquidBounce.moduleManager.getModule(BlockAnimations.class).getState())
            GL11.glTranslated(-BlockAnimations.itemPosX.get().doubleValue(), -BlockAnimations.itemPosY.get().doubleValue(), -BlockAnimations.itemPosZ.get().doubleValue());
    }

    private void rotateItemAnim() {
        if (BlockAnimations.transformFirstPersonRotate.get().equalsIgnoreCase("RotateY")) {
            GlStateManager.rotate(this.delay, 0.0F, 1.0F, 0.0F);
        }
        if (BlockAnimations.transformFirstPersonRotate.get().equalsIgnoreCase("RotateXY")) {
            GlStateManager.rotate(this.delay, 1.0F, 1.0F, 0.0F);
        }

        if (BlockAnimations.transformFirstPersonRotate.get().equalsIgnoreCase("Custom")) {
            GlStateManager.rotate(this.delay, BlockAnimations.customRotate1.get(), BlockAnimations.customRotate2.get(), BlockAnimations.customRotate3.get());
        }

        if (this.rotateTimer.hasTimePassed(1)) {
            ++this.delay;
            this.delay = this.delay + BlockAnimations.SpeedRotate.get();
            this.rotateTimer.reset();
        }
        if (this.delay > 360.0F) {
            this.delay = 0.0F;
        }
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void renderFireInFirstPerson(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = LiquidBounce.moduleManager.getModule(AntiBlind.class);

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