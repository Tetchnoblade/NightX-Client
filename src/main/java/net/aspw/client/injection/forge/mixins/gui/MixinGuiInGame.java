package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Client;
import net.aspw.client.event.Render2DEvent;
import net.aspw.client.features.module.impl.visual.AntiBlind;
import net.aspw.client.features.module.impl.visual.Crosshair;
import net.aspw.client.features.module.impl.visual.Hud;
import net.aspw.client.util.render.RenderUtils;
import net.aspw.client.visual.font.semi.AWTFontRenderer;
import net.minecraft.client.Minecraft;
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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * The type Mixin gui in game.
 */
@Mixin(GuiIngame.class)
public abstract class MixinGuiInGame extends MixinGui {

    /**
     * The constant widgetsTexPath.
     */
    @Shadow
    @Final
    protected static ResourceLocation widgetsTexPath;
    /**
     * The Overlay player list.
     */
    @Final
    @Shadow
    public GuiPlayerTabOverlay overlayPlayerList;

    /**
     * Render hotbar item.
     *
     * @param index        the index
     * @param xPos         the x pos
     * @param yPos         the y pos
     * @param partialTicks the partial ticks
     * @param player       the player
     */
    @Shadow
    protected abstract void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player);

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void injectCrosshair(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final Crosshair crosshair = Objects.requireNonNull(Client.moduleManager.getModule(Crosshair.class));
        final Hud hud = Objects.requireNonNull(Client.moduleManager.getModule(Hud.class));

        if (crosshair.getState() || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0 && hud.getNof5crossHair().get())
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(ScoreObjective scoreObjective, ScaledResolution scaledResolution, CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = Objects.requireNonNull(Client.moduleManager.getModule(AntiBlind.class));
        if ((antiBlind.getState() && antiBlind.getScoreBoard().get()))
            callbackInfo.cancel();
    }

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    private void renderBossHealth(CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = Objects.requireNonNull(Client.moduleManager.getModule(AntiBlind.class));
        if (antiBlind.getState() && antiBlind.getBossHealth().get())
            callbackInfo.cancel();
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        final Hud hud = Objects.requireNonNull(Client.moduleManager.getModule(Hud.class));

        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer && hud.getState() && (hud.getBlackHotbarValue().get() || hud.getAnimHotbarValue().get())) {
            final Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer entityPlayer = (EntityPlayer) mc.getRenderViewEntity();

            boolean blackHB = hud.getBlackHotbarValue().get();
            int middleScreen = sr.getScaledWidth() / 2;
            float posInv = hud.getAnimPos(entityPlayer.inventory.currentItem * 20F);

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
            Client.eventManager.callEvent(new Render2DEvent(partialTicks));
            AWTFontRenderer.Companion.garbageCollectionTick();
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderTooltip", at = @At("TAIL"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        Client.eventManager.callEvent(new Render2DEvent(partialTicks));
        AWTFontRenderer.Companion.garbageCollectionTick();
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPumpkinOverlay(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = Objects.requireNonNull(Client.moduleManager.getModule(AntiBlind.class));

        if (antiBlind.getState() && antiBlind.getPumpkinEffect().get())
            callbackInfo.cancel();
    }
}