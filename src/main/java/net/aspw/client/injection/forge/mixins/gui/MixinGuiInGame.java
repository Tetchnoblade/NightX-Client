package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Launch;
import net.aspw.client.event.Render2DEvent;
import net.aspw.client.features.module.impl.other.SnakeGame;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.features.module.impl.visual.VisualAbilities;
import net.aspw.client.utils.MinecraftInstance;
import net.aspw.client.utils.PlayerUtils;
import net.aspw.client.utils.render.RenderUtils;
import net.aspw.client.visual.font.semi.AWTFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(GuiIngame.class)
public abstract class MixinGuiInGame extends Gui {

    @Shadow
    @Final
    protected static ResourceLocation widgetsTexPath;

    @Final
    @Shadow
    public GuiPlayerTabOverlay overlayPlayerList;

    @Shadow
    protected abstract void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player);

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void injectCrosshair(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final Interface anInterface = Objects.requireNonNull(Launch.moduleManager.getModule(Interface.class));
        final SnakeGame snakeGame = Objects.requireNonNull(Launch.moduleManager.getModule(SnakeGame.class));

        if (snakeGame.getState() || MinecraftInstance.mc.gameSettings.thirdPersonView != 0 && anInterface.getNof5crossHair().get())
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(ScoreObjective scoreObjective, ScaledResolution scaledResolution, CallbackInfo callbackInfo) {
        final VisualAbilities visualAbilities = Objects.requireNonNull(Launch.moduleManager.getModule(VisualAbilities.class));
        if (visualAbilities.getState() && visualAbilities.getScoreBoard().get())
            callbackInfo.cancel();
    }

    @ModifyConstant(method = "renderScoreboard", constant = @Constant(intValue = 553648127))
    private int fixTextBlending(int original) {
        return -1;
    }

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    private void renderBossHealth(CallbackInfo callbackInfo) {
        final VisualAbilities visualAbilities = Objects.requireNonNull(Launch.moduleManager.getModule(VisualAbilities.class));
        if (visualAbilities.getState() && visualAbilities.getBossHealth().get())
            callbackInfo.cancel();
    }

    /**
     * @author As_pw
     * @reason Hotbar Renderer
     */
    @Overwrite
    protected void renderTooltip(ScaledResolution sr, float partialTicks) {
        if (!(MinecraftInstance.mc.getRenderViewEntity() instanceof EntityPlayer))
            return;

        final Minecraft mc = MinecraftInstance.mc;
        EntityPlayer entityPlayer = (EntityPlayer) mc.getRenderViewEntity();

        int slot = PlayerUtils.renderGuiSlot(entityPlayer);

        final Interface anInterface = Objects.requireNonNull(Launch.moduleManager.getModule(Interface.class));

        if (anInterface.getState() && (anInterface.getBlackHotbarValue().get() || anInterface.getAnimHotbarValue().get())) {
            boolean blackHB = anInterface.getBlackHotbarValue().get();
            int middleScreen = sr.getScaledWidth() / 2;
            float posInv = anInterface.getAnimPos(slot * 20F);

            GlStateManager.resetColor();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(widgetsTexPath);

            float f = this.zLevel;
            this.zLevel = -90.0F;
            GlStateManager.resetColor();

            if (blackHB) {
                RenderUtils.originalRoundedRect(middleScreen - 91, sr.getScaledHeight() - 2, middleScreen + 91, sr.getScaledHeight() - 22, 3F, Integer.MIN_VALUE);
                RenderUtils.originalRoundedRect(middleScreen - 91 + posInv, sr.getScaledHeight() - 2, middleScreen - 91 + posInv + 22, sr.getScaledHeight() - 22, 3F, Integer.MAX_VALUE);
            } else {
                this.drawTexturedModalRect(middleScreen - 91F, sr.getScaledHeight() - 22, 0, 0, 182, 22);
                this.drawTexturedModalRect(middleScreen - 91F + posInv - 1, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            }

            this.zLevel = f;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int j = 0; j < 9; ++j) {
                int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
                int l = sr.getScaledHeight() - 19 - (blackHB ? 1 : 0);
                this.renderHotbarItem(j, k, l, partialTicks, entityPlayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.resetColor();
            Launch.eventManager.callEvent(new Render2DEvent(partialTicks));
            return;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        MinecraftInstance.mc.getTextureManager().bindTexture(widgetsTexPath);
        EntityPlayer lvt_3_1_ = (EntityPlayer) MinecraftInstance.mc.getRenderViewEntity();
        int lvt_4_1_ = sr.getScaledWidth() / 2;
        float lvt_5_1_ = this.zLevel;
        this.zLevel = -90.0F;
        this.drawTexturedModalRect(lvt_4_1_ - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
        this.drawTexturedModalRect(lvt_4_1_ - 91 - 1 + slot * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
        this.zLevel = lvt_5_1_;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();

        for (int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_) {
            int lvt_7_1_ = sr.getScaledWidth() / 2 - 90 + lvt_6_1_ * 20 + 2;
            int lvt_8_1_ = sr.getScaledHeight() - 16 - 3;
            this.renderHotbarItem(lvt_6_1_, lvt_7_1_, lvt_8_1_, partialTicks, lvt_3_1_);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();

        Launch.eventManager.callEvent(new Render2DEvent(partialTicks));
        AWTFontRenderer.Companion.garbageCollectionTick();
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPumpkinOverlay(final CallbackInfo callbackInfo) {
        final VisualAbilities visualAbilities = Objects.requireNonNull(Launch.moduleManager.getModule(VisualAbilities.class));

        if (visualAbilities.getState() && visualAbilities.getPumpkinEffect().get())
            callbackInfo.cancel();
    }
}