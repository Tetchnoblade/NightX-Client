package net.aspw.client.injection.forge.mixins.item;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.combat.TPAura;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.AntiBlind;
import net.aspw.client.util.MinecraftInstance;
import net.aspw.client.util.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * The type Mixin item renderer.
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Shadow
    private float prevEquippedProgress;

    @Shadow
    private float equippedProgress;


    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    private ItemStack itemToRender;

    private float delay = 0;
    private final TimerUtils rotateTimer = new TimerUtils();

    /**
     * Rotate arround x and y.
     *
     * @param angle  the angle
     * @param angleY the angle y
     */
    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    /**
     * Sets light map from player.
     *
     * @param clientPlayer the client player
     */
    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    /**
     * Rotate with player rotations.
     *
     * @param entityplayerspIn the entityplayersp in
     * @param partialTicks     the partial ticks
     */
    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks);

    /**
     * Render item map.
     *
     * @param clientPlayer      the client player
     * @param pitch             the pitch
     * @param equipmentProgress the equipment progress
     * @param swingProgress     the swing progress
     */
    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    /**
     * Transform first person item.
     *
     * @param equipProgress the equip progress
     * @param swingProgress the swing progress
     */
    @Shadow
    protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

    /**
     * Perform drinking.
     *
     * @param clientPlayer the client player
     * @param partialTicks the partial ticks
     */
    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    /**
     * Do block transformations.
     */
    @Shadow
    protected abstract void doBlockTransformations();

    /**
     * Do bow transformations.
     *
     * @param partialTicks the partial ticks
     * @param clientPlayer the client player
     */
    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);

    /**
     * Do item used transformations.
     *
     * @param swingProgress the swing progress
     */
    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    /**
     * Render item.
     *
     * @param entityIn  the entity in
     * @param heldStack the held stack
     * @param transform the transform
     */
    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    /**
     * Render player arm.
     *
     * @param clientPlayer  the client player
     * @param equipProgress the equip progress
     * @param swingProgress the swing progress
     */
    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    @Shadow
    private int equippedItemSlot;

    @Unique
    private void func_178103_d(float qq) {
        GlStateManager.translate(-0.5F, qq, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    @Unique
    private void func_178103_d() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    @Unique
    private void func_178105_d(float p_178105_1_) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(p_178105_1_ * (float) Math.PI);
        GlStateManager.translate(f, f1, f2);
    }

    /**
     * Render item in first person.
     *
     * @param partialTicks the partial ticks
     * @author As_pw
     * @reason RenderItem
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
        float f1 = abstractclientplayer.getSwingProgress(partialTicks);
        float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        final float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
        GL11.glTranslated(Animations.itemPosX.get().doubleValue(), Animations.itemPosY.get().doubleValue(), Animations.itemPosZ.get().doubleValue());
        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP) abstractclientplayer, partialTicks);
        GlStateManager.scale(1, 1, -Animations.itemFov.getValue() + 1);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GL11.glTranslated(Animations.itemPosX.get().doubleValue(), Animations.itemPosY.get().doubleValue(), Animations.itemPosZ.get().doubleValue());

        if (this.itemToRender != null) {
            final KillAura killAura = Objects.requireNonNull(Client.moduleManager.getModule(KillAura.class));
            final TPAura tpAura = Objects.requireNonNull(Client.moduleManager.getModule(TPAura.class));

            if (Animations.oldAnimations.getValue() && (itemToRender.getItem() instanceof ItemCarrotOnAStick || itemToRender.getItem() instanceof ItemFishingRod)) {
                GlStateManager.translate(0.08F, -0.027F, -0.33F);
                GlStateManager.scale(0.93F, 1.0F, 1.0F);
            }

            if (this.itemToRender.getItem() instanceof ItemMap) {
                this.renderItemMap(abstractclientplayer, f2, f, f1);
            } else if (abstractclientplayer.getItemInUseCount() > 0
                    || (itemToRender.getItem() instanceof ItemSword && (killAura.getBlockingStatus() || killAura.getFakeBlock()) && !killAura.getAutoBlockModeValue().get().equals("None"))
                    || itemToRender.getItem() instanceof ItemSword && tpAura.getState() && tpAura.isBlocking() || (itemToRender.getItem() instanceof ItemSword
                    && killAura.getTarget() != null && !killAura.getAutoBlockModeValue().get().equals("None"))) {

                EnumAction enumaction = (killAura.getBlockingStatus()) ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();

                switch (enumaction) {
                    case NONE:
                        if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                            this.transformFirstPersonItem(0.0F, 0.0F);
                        else this.transformFirstPersonItem(f, 0.0F);
                        GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                        break;
                    case EAT:
                    case DRINK:
                        this.performDrinking(abstractclientplayer, partialTicks);
                        if (Animations.oldAnimations.getValue()) {
                            if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                                this.transformFirstPersonItem(0.0F, f1);
                            else this.transformFirstPersonItem(f, f1);
                        } else {
                            if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                                this.transformFirstPersonItem(0.0F, 0.0F);
                            else this.transformFirstPersonItem(f, 0.0F);
                        }
                        GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                        break;
                    case BLOCK:
                        final String z = Animations.Sword.get();
                        switch (z) {
                            case "Astolfo": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                GlStateManager.rotate(-var9 * 58.0F / 2.0F, var9 / 2.0F, 1.0F, 0.5F);
                                GlStateManager.rotate(-var9 * 43.0F, 1.0F, var9 / 3.0F, -0.0F);
                                this.func_178103_d(0.2F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Leaked": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.08, Animations.blockPosY.get().doubleValue() + 0.02, Animations.blockPosZ.get().doubleValue() - 0.02);
                                final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                if (Animations.cancelEquip.get())
                                    transformFirstPersonItem(0.0F, 0.0f);
                                else transformFirstPersonItem(f / 1.4F, 0.0f);
                                this.func_178103_d();
                                GlStateManager.rotate(-var * 35.5F, 1.0F, 0.7F, -0.2F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "AstolfoSpin": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue(), Animations.blockPosZ.get().doubleValue());
                                GlStateManager.rotate(this.delay, 0.0F, 0.0F, -0.1F);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 1.3F, 0.0F);
                                float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.0F);
                                if (this.rotateTimer.hasReached(1L)) {
                                    for (int i = 0; i < 1; i++) {
                                        ++this.delay;
                                    }
                                    this.rotateTimer.reset();
                                }
                                if (this.delay > 360.0F) {
                                    this.delay = 0.0F;
                                }
                                this.func_178103_d();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Astro": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else this.transformFirstPersonItem(f / 2.3F, f1);
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                GlStateManager.rotate(var9 * 50.0F / 9.0F, -var9, -0.0F, 90.0F);
                                GlStateManager.rotate(var9 * 50.0F, 200.0F, -var9 / 2.0F, -0.0F);
                                this.func_178103_d();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Spin": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    transformFirstPersonItem(0.0f, 0.0f);
                                else transformFirstPersonItem(f / 1.4F, 0.0F);
                                GlStateManager.translate(0, 0.2F, -1);
                                GlStateManager.rotate(-59, -1, 0, 3);
                                GlStateManager.rotate(-(System.currentTimeMillis() / 2 % 360), 1, 0, 0.0F);
                                GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                            }
                            case "Slash": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                if (Animations.cancelEquip.get())
                                    transformFirstPersonItem(0.0f, 0.0f);
                                else transformFirstPersonItem(f / 1.8f, 0.0f);
                                this.func_178103_d();
                                final float var16 = MathHelper.sin((float) (f1 * f1 * Math.PI));
                                GlStateManager.rotate(-var16 * 0f, 0.0f, 1.0f, 0.0f);
                                GlStateManager.rotate(-var * 62f, 0.0f, 0.0f, 1.0f);
                                GlStateManager.rotate(-var * 0f, 1.5f, 0.0f, 0.0f);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Reverse": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.15, Animations.blockPosZ.get().doubleValue() - 0.12);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else this.transformFirstPersonItem(f / 1.4f, f1);
                                this.doBlockTransformations();
                                GL11.glTranslated(0.08D, -0.1D, -0.3D);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Smooth": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.14, Animations.blockPosY.get().doubleValue() - 0.05, Animations.blockPosZ.get().doubleValue() - 0.24);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 1.4f, 0.0f);
                                float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                this.func_178103_d(0.2F);
                                GlStateManager.translate(-0.36f, 0.25f, -0.06f);
                                GlStateManager.rotate(-var91 * 35.0f, -8.0f, -0.0f, 9.0f);
                                GlStateManager.rotate(-var91 * 70.0f, 1.0f, 0.4f, -0.0f);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Rhys": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.24, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 1.4f, 0.0f);
                                GlStateManager.translate(0.41F, -0.25F, -0.5555557F);
                                GlStateManager.translate(0.0F, 0, 0.0F);
                                GlStateManager.rotate(35.0F, 0f, 1.5F, 0.0F);
                                final float racism = MathHelper.sin(f1 * f1 / 64 * (float) Math.PI);
                                GlStateManager.rotate(racism * -5.0F, 0.0F, 0.0F, 0.0F);
                                GlStateManager.rotate(f4 * -12.0F, 0.0F, 0.0F, 1.0F);
                                GlStateManager.rotate(f4 * -65.0F, 1.0F, 0.0F, 0.0F);
                                this.doBlockTransformations();
                                break;
                            }
                            case "Stab": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() - 0.25, Animations.blockPosY.get().doubleValue() + 0.5, Animations.blockPosZ.get().doubleValue() + 0.8);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 1.5f, 0.0f);
                                final float spin = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                GlStateManager.translate(0.6f, 0.3f, -0.6f + -spin * 0.7);
                                GlStateManager.rotate(6090, 0.0f, 0.0f, 0.1f);
                                GlStateManager.rotate(6085, 0.0f, 0.1f, 0.0f);
                                GlStateManager.rotate(6110, 0.1f, 0.0f, 0.0f);
                                this.transformFirstPersonItem(0.0F, 0.0f);
                                this.doBlockTransformations();
                                break;
                            }
                            case "Winter": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() - 0.11, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else transformFirstPersonItem(f / 1.5f, f1);
                                this.func_178103_d();
                                GL11.glTranslatef(-0.35F, 0.1F, 0.0F);
                                GL11.glTranslatef(-0.05F, -0.1F, 0.1F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Slide": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.13, Animations.blockPosY.get().doubleValue() - 0.06, Animations.blockPosZ.get().doubleValue() - 0.07);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 1.5F, 0.0F);
                                float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                this.func_178103_d(0.2F);
                                GlStateManager.translate(-0.4f, 0.28f, 0.0f);
                                GlStateManager.rotate(-var91 * 35.0f, -8.0f, -0.0f, 9.0f);
                                GlStateManager.rotate(-var91 * 70.0f, 1.0f, -0.4f, -0.0f);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Sigma4": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() - 0.6, Animations.blockPosY.get().doubleValue() - 0.06, Animations.blockPosZ.get().doubleValue() + 0.11);
                                final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else transformFirstPersonItem(f / 2.0F, 0.0F);
                                GlStateManager.rotate(-var * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                                GlStateManager.rotate(-var * 45, 1.0F, var / 2, 0.0F);
                                this.func_178103_d();
                                GL11.glTranslated(-0.08, -1.25, 1.25);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Old": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.08, Animations.blockPosY.get().doubleValue() - 0.09, Animations.blockPosZ.get().doubleValue() - 0.05);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else this.transformFirstPersonItem(f, f1);
                                this.doBlockTransformations();
                                GlStateManager.translate(-0.35F, 0.2F, 0.0F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Jigsaw": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() - 0.13, Animations.blockPosZ.get().doubleValue() - 0.1);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else transformFirstPersonItem(f, f1);
                                doBlockTransformations();
                                GlStateManager.translate(-0.5D, 0.0D, 0.0D);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Small": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() - 0.01, Animations.blockPosY.get().doubleValue() - 0.02, Animations.blockPosZ.get().doubleValue() - 0.24);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else this.transformFirstPersonItem(f / 1.2f, f1);
                                this.doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Dash": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else transformFirstPersonItem(f / 2.5f, 0.0f);
                                GL11.glRotated(-var9 * 20.0F, var9 / 2, 0.0F, 9.0F);
                                GL11.glRotated(-var9 * 50.0F, 0.8F, var9 / 2, 0F);
                                doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Remix": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 1.0F);
                                else transformFirstPersonItem(f / 2.5f, 1.0f);
                                this.func_178103_d();
                                GlStateManager.rotate(0.0F, -2.0F, 0.0F, 10.0F);
                                GlStateManager.rotate(-var * 25.0F, 0.5F, 0F, 1F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Xiv": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else transformFirstPersonItem(f / 1.5f, 0.0f);
                                this.func_178103_d();
                                final float var16 = MathHelper.sin((float) (f1 * f1 * Math.PI));
                                GlStateManager.rotate(-var16 * 20.0f, 0.0f, 1.0f, 0.0f);
                                GlStateManager.rotate(-var * 20.0f, 0.0f, 0.0f, 1.0f);
                                GlStateManager.rotate(-var * 80.0f, 1.0f, 0.0f, 0.0f);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Swank": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else transformFirstPersonItem(f / 2.0f, f1);
                                GL11.glRotatef(var9 * 30.0F / 2.0F, -var9, -0.0F, 9.0F);
                                GL11.glRotatef(var9 * 40.0F, 1.0F, -var9 / 2.0F, -0.0F);
                                doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Swonk": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.08, Animations.blockPosZ.get().doubleValue());
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float) Math.PI);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 1.8f, 0.0f);
                                GL11.glRotated(-var9 * -30.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                GL11.glRotated(-var9 * 7.5F, 1.0F, var9 / 3.0F, -0.0F);
                                doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "MoonPush": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else transformFirstPersonItem(f / 1.5f, 0.0f);
                                doBlockTransformations();
                                float sin = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                GlStateManager.scale(1.0F, 1.0F, 1.0F);
                                GlStateManager.translate(-0.2F, 0.45F, 0.25F);
                                GlStateManager.rotate(-sin * 20.0F, -5.0F, -5.0F, 9.0F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Stella": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                this.transformFirstPersonItem(-0.1F, f1);
                                GlStateManager.translate(-0.5F, 0.3F, -0.2F);
                                GlStateManager.rotate(32, 0, 1, 0);
                                GlStateManager.rotate(-70, 1, 0, 0);
                                GlStateManager.rotate(40, 0, 1, 0);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Sigma3": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.02, Animations.blockPosY.get().doubleValue() + 0.07, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else transformFirstPersonItem(f / 2.0f, f1);
                                GL11.glTranslated(0.4D, -0.06D, -0.46D);
                                float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                GlStateManager.rotate(Swang * 25.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                GlStateManager.rotate(Swang * 15.0F, 1.0F, -Swang / 2.0F, -0.0F);
                                doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Push": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                GlStateManager.rotate(-var9 * 40.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                GlStateManager.rotate(-var9 * 30.0F, 1.0F, var9 / 3.0F, -0.0F);
                                this.func_178103_d(0.2F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Aqua": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 6.0f, 0.0f);
                                GlStateManager.rotate(-var9 * 17.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                GlStateManager.rotate(-var9 * 6.0F, 1.0F, var9 / 3.0F, -0.0F);
                                this.func_178103_d(0.2F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Swang": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.08, Animations.blockPosZ.get().doubleValue());
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                GlStateManager.rotate(-var9 * 74.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                GlStateManager.rotate(-var9 * 52.0F, 1.0F, var9 / 3.0F, -0.0F);
                                this.func_178103_d(0.2F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Moon": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() - 0.08, Animations.blockPosY.get().doubleValue() + 0.11, Animations.blockPosZ.get().doubleValue());
                                float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                                GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 2.0f, 0.0f);
                                GlStateManager.rotate(-var9 * 65.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                GlStateManager.rotate(-var9 * 60.0F, 1.0F, var9 / 3.0F, -0.0F);
                                this.func_178103_d(0.2F);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "1.8": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else transformFirstPersonItem(f, 0.0f);
                                doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Swing": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else this.transformFirstPersonItem(f / 1.2f, f1);
                                this.doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Float": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 2.0f, 0.0f);
                                GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 40.0F / 2.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F, 9.0F);
                                GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 30.0F, 1.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F);
                                this.doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Invent": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                float table = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                GlStateManager.rotate(-table * 30.0F, -8.0F, -0.2F, 9.0F);
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, 0.0F);
                                else this.transformFirstPersonItem(f / 1.8f, 0.0f);
                                this.doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Fadeaway": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue(), Animations.blockPosY.get().doubleValue() + 0.05, Animations.blockPosZ.get().doubleValue());
                                final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, -0.3F);
                                else this.transformFirstPersonItem(f / 1.4f, -0.3f);
                                this.func_178103_d();
                                final float var16 = MathHelper.sin((float) (f1 * f1 * Math.PI));
                                GlStateManager.rotate(-var16 * 45f, 0.0f, 0.0f, 1.0f);
                                GlStateManager.rotate(-var * 0f, 0.0f, 0.0f, 1.0f);
                                GlStateManager.rotate(-var * 0f, 1.5f, 0.0f, 0.0f);
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                            case "Edit": {
                                GL11.glTranslated(Animations.blockPosX.get().doubleValue() + 0.03, Animations.blockPosY.get().doubleValue() + 0.11, Animations.blockPosZ.get().doubleValue());
                                if (Animations.cancelEquip.get())
                                    this.transformFirstPersonItem(0.0F, f1);
                                else transformFirstPersonItem(f / 1.6f, f1);
                                GL11.glTranslated(0.0D, 0.0D, 0.0D);
                                float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                GlStateManager.rotate(Swang * 16.0F / 2.0F, -Swang, -0.0F, 9.0F);
                                GlStateManager.rotate(Swang * 22.0F, 1.0F, -Swang / 3.0F, -0.0F);
                                doBlockTransformations();
                                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                                break;
                            }
                        }
                        break;
                    case BOW:
                        if (Animations.oldAnimations.getValue()) {
                            if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                                this.transformFirstPersonItem(0.0F, f1);
                            else this.transformFirstPersonItem(f, f1);
                        } else {
                            if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                                this.transformFirstPersonItem(0.0F, 0.0F);
                            else this.transformFirstPersonItem(f, 0.0F);
                        }
                        this.doBowTransformations(partialTicks, abstractclientplayer);
                        GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
                }
            } else {
                if (Animations.swingAnimValue.get().equals("1.7")) {
                    if (f1 != 0.0F) {
                        GlStateManager.scale(0.85F, 0.85F, 0.85F);
                        GlStateManager.translate(-0.06F, 0.003F, 0.05F);
                    }
                    this.doItemUsedTransformations(f1);
                    if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                        this.transformFirstPersonItem(0.0F, f1);
                    else this.transformFirstPersonItem(f, f1);
                }
                if (Animations.swingAnimValue.get().equals("1.8")) {
                    this.doItemUsedTransformations(f1);
                    if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                        this.transformFirstPersonItem(0.0F, f1);
                    else this.transformFirstPersonItem(f, f1);
                }
                if (Animations.swingAnimValue.get().equals("Flux")) {
                    if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                        this.transformFirstPersonItem(0.0F, f1);
                    else this.transformFirstPersonItem(f, f1);
                }
                if (Animations.swingAnimValue.get().equals("Smooth")) {
                    if (Animations.cancelEquip.get() && !Animations.blockingOnly.get())
                        this.transformFirstPersonItem(0.0F, f1);
                    else this.transformFirstPersonItem(f, f1);
                    func_178105_d(f1);
                }
                GlStateManager.scale(Animations.scale.get() + 1, Animations.scale.get() + 1, Animations.scale.get() + 1);
            }

            this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!abstractclientplayer.isInvisible())
            this.renderPlayerArm(abstractclientplayer, f, f1);

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GL11.glTranslated(-Animations.itemPosX.get().doubleValue(), -Animations.itemPosY.get().doubleValue(), -Animations.itemPosZ.get().doubleValue());
    }

    @ModifyArg(method = "updateEquippedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;clamp_float(FFF)F"), index = 0)
    private float handleItemSwitch(float original) {
        EntityPlayer entityplayer = MinecraftInstance.mc.thePlayer;
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        if (Animations.oldAnimations.get() && this.equippedItemSlot == entityplayer.inventory.currentItem && ItemStack.areItemsEqual(this.itemToRender, itemstack)) {
            return 1.0f - this.equippedProgress;
        }
        return original;
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void renderFireInFirstPerson(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = Client.moduleManager.getModule(AntiBlind.class);

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