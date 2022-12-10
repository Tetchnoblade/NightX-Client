package net.aspw.nightx.injection.forge.mixins.item;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.combat.KillAura;
import net.aspw.nightx.features.module.modules.cool.AntiBlind;
import net.aspw.nightx.features.module.modules.render.Animations;
import net.aspw.nightx.utils.timer.MSTimer;
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
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
    }

    private void Random() {
        ++this.delay;
        GlStateManager.translate(0.7D, -0.4000000059604645D, -0.800000011920929D);
        GlStateManager.rotate(50.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(50.0F, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate((float) this.delay * 0.2F * (Animations.SpinSpeed.get()).floatValue(), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
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
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
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
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
    }

    private void slide3(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0f, 1.0F);
        GlStateManager.rotate(var12 * -60.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
    }

    private void slideKnife(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0f, 1.0F);
        GlStateManager.rotate(var12 * 60.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
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
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
    }

    private void dortwarenew(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.5F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0f, 1.0F);
        GlStateManager.rotate(var12 * -90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
    }

    private void astolfo(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.5F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * -40.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0f, 1.0F);
        GlStateManager.rotate(var12 * -60.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
    }

    private void nightx(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.5F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * -40.0F, 0.0F, 0.0f, 1.0F);
        GlStateManager.rotate(var12 * -90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
    }

    private void funny(float var10, float var9) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0f, 0.0f, 0.0f, 0.0f);
        float var11 = MathHelper.sin(var9 * var9 * (float) Math.PI);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0f, 0.0F);
        GlStateManager.rotate(var12 * -90.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
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
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
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
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
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
        GlStateManager.scale(Animations.Scale.get(), Animations.Scale.get(), Animations.Scale.get());
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
        if (NightX.moduleManager.getModule(Animations.class).getState()) {
            GL11.glTranslated(Animations.itemPosX.get().doubleValue(), Animations.itemPosY.get().doubleValue(), Animations.itemPosZ.get().doubleValue());
        }
        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP) abstractclientplayer, partialTicks);
        GlStateManager.scale(Animations.itemFovX.getValue(), Animations.itemFovY.getValue(), Animations.itemFovZ.getValue());
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (NightX.moduleManager.getModule(Animations.class).getState()) {
            GL11.glTranslated(Animations.itemPosX.get().doubleValue(), Animations.itemPosY.get().doubleValue(), Animations.itemPosZ.get().doubleValue());
        }

        if (this.itemToRender != null) {
            final KillAura killAura = NightX.moduleManager.getModule(KillAura.class);

            boolean canBlockEverything = NightX.moduleManager.getModule(Animations.class).getState() && Animations.blockEverything.get() && killAura.getTarget() != null
                    && (itemToRender.getItem() instanceof ItemBucketMilk || itemToRender.getItem() instanceof ItemFood
                    || itemToRender.getItem() instanceof ItemPotion || itemToRender.getItem() instanceof ItemAxe || itemToRender.getItem().equals(Items.stick));

            if (this.itemToRender.getItem() instanceof ItemMap) {
                this.renderItemMap(abstractclientplayer, f2, f, f1);
            } else if (abstractclientplayer.getItemInUseCount() > 0
                    || (itemToRender.getItem() instanceof ItemSword && (killAura.getBlockingStatus() || killAura.getFakeBlock()))
                    || (itemToRender.getItem() instanceof ItemSword && NightX.moduleManager.getModule(Animations.class).getState()
                    && Animations.fakeBlock.get() && killAura.getTarget() != null) || canBlockEverything) {

                EnumAction enumaction = (killAura.getBlockingStatus() || canBlockEverything) ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();

                switch (enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        this.performDrinking(abstractclientplayer, partialTicks);
                        this.transformFirstPersonItem(f, f1);

                        if (NightX.moduleManager.getModule(Animations.class).getState() && Animations.RotateItems.get())
                            rotateItemAnim();
                        break;
                    case BLOCK:
                        if (NightX.moduleManager.getModule(Animations.class).getState()) {
                            GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue(), Animations.blockPosZ.get().doubleValue());
                            final String z = Animations.Sword.get();
                            switch (z) {
                                case "LiquidBounce": {
                                    this.transformFirstPersonItem(f + 0.1F, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Old": {
                                    this.transformFirstPersonItem(f + 0.1F, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.35F, 0.2F, 0.0F);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "OldFull": {
                                    this.transformFirstPersonItem(0f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.35F, 0.2F, 0.0F);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "SlideFull": {
                                    this.slide2(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "SlideMedium": {
                                    this.slide3(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "SlideCut": {
                                    this.slideKnife(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "DortwareNew": {
                                    this.dortwarenew(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "NightX": {
                                    this.nightx(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Astolfo": {
                                    this.astolfo(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Dash": {
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0F);
                                    GL11.glTranslated(-0.07D, 0.16D, 0.0D);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GL11.glRotated(-var9 * 60.0F / 2.0F, -var9 / 2.0F, -0.0F, 9.0F);
                                    GL11.glRotated(-var9 * 53.0F, 1.0F, var9 / 2.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Jigsaw": {
                                    transformFirstPersonItem(0.1F, f1);
                                    doBlockTransformations();
                                    GlStateManager.translate(-0.5D, 0.0D, 0.0D);
                                    break;
                                }
                                case "Ninja": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 1000.0F, 0.0f);
                                    GlStateManager.rotate(-var9 * 120.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 0.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Spin": {
                                    Random();
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "MoonPush": {
                                    transformFirstPersonItem(f, 0.0F);
                                    doBlockTransformations();
                                    float sin = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.scale(1.0F, 1.0F, 1.0F);
                                    GlStateManager.translate(-0.2F, 0.45F, 0.25F);
                                    GlStateManager.rotate(-sin * 20.0F, -5.0F, -5.0F, 9.0F);
                                    break;
                                }
                                case "Funny": {
                                    this.funny(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
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
                                case "Sigma3": {
                                    transformFirstPersonItem(-0.1F, f1);
                                    GL11.glTranslated(0.14D, -0.03D, -0.3D);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(Swang * 25.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swang * 15.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Reverse": {
                                    this.func_178096_b(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    GL11.glTranslated(0.08D, -0.1D, -0.3D);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Push": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.06D, 0.03D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 40.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 30.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Flux3": {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    this.func_178103_d(0.2F);
                                    GlStateManager.translate(-0.4f, 0.28f, 0.0f);
                                    GlStateManager.rotate(-var91 * 35.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-var91 * 70.0f, 1.0f, -0.4f, -0.0f);
                                    break;
                                }
                                case "Lennox": {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    this.func_178103_d(0.2F);
                                    GlStateManager.translate(-0.36f, 0.25f, -0.06f);
                                    GlStateManager.rotate(-var91 * 35.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-var91 * 70.0f, 1.0f, 0.4f, -0.0f);
                                    break;
                                }
                                case "Swang": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.06D, 0.03D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 60.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 45.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Moon": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.1D, 0.06D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 65.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 60.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Leaked": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.02D, -0.08D);
                                    this.transformFirstPersonItem(f / 1000.0F, 0.0f);
                                    GlStateManager.rotate(-var9 * -32.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * -10.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Dortware1": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 0.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 120.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Dortware2": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * 120.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 120.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Avatar": {
                                    this.avatar(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "SlideLow": {
                                    this.slide(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "ETB": {
                                    this.ETB(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "1.8":{
                                    transformFirstPersonItem(0F,0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Sigma4": {
                                    float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
                                    this.sigmaold(f * 0.5f, 0);
                                    GlStateManager.rotate(-var15 * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-var15 * 45, 1.0F, var15 / 2, -0.0F);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    GL11.glTranslated(1.2, 0.3, 0.5);
                                    GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                                    GlStateManager.scale(1.2f, 1.2f, 1.2f);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Spinny": {
                                    this.func_178096_b(0.0f, 0.95f);
                                    GlStateManager.rotate(this.delay, 1.0F, 0.0F, 2.0F);
                                    if (this.rotateTimer.hasTimePassed(1)) {
                                        ++this.delay;
                                        this.delay = this.delay + Animations.SpeedRotate.get();
                                        this.rotateTimer.reset();
                                    }
                                    if (this.delay > 360.0F) {
                                        this.delay = 0.0F;
                                    }
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    GlStateManager.rotate(this.delay, 0.0F, 1.0F, 0.0F);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Rotate": {
                                    this.func_178096_b(0.0f, 0.95f);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    GlStateManager.rotate(this.delay, 1.0F, 0.0F, 2.0F);
                                    if (this.rotateTimer.hasTimePassed(1)) {
                                        ++this.delay;
                                        this.delay = this.delay + Animations.SpeedRotate.get();
                                        this.rotateTimer.reset();
                                    }
                                    if (this.delay > 360.0F) {
                                        this.delay = 0.0F;
                                    }
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Swing": {
                                    this.func_178096_b(f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                                case "Flux1": {
                                    this.transformFirstPersonItem(f / 2, 0);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 40.0F / 2.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 30.0F, 1.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "Flux2": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / 2.0F, 0.0f);
                                    GlStateManager.rotate(-var9 * 70.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 0.0F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Smart": {
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                                    GL11.glTranslated(0.0D, 0.16D, 0.0D);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(Swang * 25.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swang * 15.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Swank": {
                                    transformFirstPersonItem(f / Animations.Equip.getValue(), f1);
                                    GL11.glTranslated(0.0D, 0.08D, 0.1D);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(Swang * 35.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                    GlStateManager.rotate(Swang * 45.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Swaing": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.0D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * -30.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 7.5F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Swong": {
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                    GL11.glTranslated(-0.04D, 0.06D, 0.0D);
                                    this.transformFirstPersonItem(f / Animations.Equip.getValue(), 0.0f);
                                    GlStateManager.rotate(-var9 * -70.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                    GlStateManager.rotate(-var9 * 17.5F, 1.0F, var9 / 3.0F, -0.0F);
                                    this.func_178103_d(0.2F);
                                    break;
                                }
                                case "Zoom": {
                                    this.Zoom(0.0f, f1);
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();

                                    this.func_178103_d();
                                    if (Animations.RotateItems.get())
                                        rotateItemAnim();
                                    break;
                                }
                            }
                        } else {
                            transformFirstPersonItem(0F,0F);
                            doBlockTransformations();
                        }
                        break;
                    case BOW:
                        this.transformFirstPersonItem(f, f1);
                        if (NightX.moduleManager.getModule(Animations.class).getState() && Animations.RotateItems.get())
                            rotateItemAnim();
                        this.doBowTransformations(partialTicks, abstractclientplayer);
                        if (NightX.moduleManager.getModule(Animations.class).getState() && Animations.RotateItems.get())
                            rotateItemAnim();
                }
            } else {
                if (!Animations.swingAnimValue.get())
                    this.doItemUsedTransformations(f1);
                this.transformFirstPersonItem(f, f1);
                if (NightX.moduleManager.getModule(Animations.class).getState() && Animations.RotateItems.get())
                    rotateItemAnim();
            }

            this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!abstractclientplayer.isInvisible())
            this.renderPlayerArm(abstractclientplayer, f, f1);

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();

        if (NightX.moduleManager.getModule(Animations.class).getState())
            GL11.glTranslated(-Animations.itemPosX.get().doubleValue(), -Animations.itemPosY.get().doubleValue(), -Animations.itemPosZ.get().doubleValue());
    }

    private void rotateItemAnim() {
        if (Animations.transformFirstPersonRotate.get().equalsIgnoreCase("RotateY")) {
            GlStateManager.rotate(this.delay, 0.0F, 1.0F, 0.0F);
        }
        if (Animations.transformFirstPersonRotate.get().equalsIgnoreCase("RotateXY")) {
            GlStateManager.rotate(this.delay, 1.0F, 1.0F, 0.0F);
        }

        if (Animations.transformFirstPersonRotate.get().equalsIgnoreCase("Custom")) {
            GlStateManager.rotate(this.delay, Animations.customRotate1.get(), Animations.customRotate2.get(), Animations.customRotate3.get());
        }

        if (this.rotateTimer.hasTimePassed(1)) {
            ++this.delay;
            this.delay = this.delay + Animations.SpeedRotate.get();
            this.rotateTimer.reset();
        }
        if (this.delay > 360.0F) {
            this.delay = 0.0F;
        }
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void renderFireInFirstPerson(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = NightX.moduleManager.getModule(AntiBlind.class);

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