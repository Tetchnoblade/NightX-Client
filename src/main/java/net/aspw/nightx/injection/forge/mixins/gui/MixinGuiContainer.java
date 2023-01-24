package net.aspw.nightx.injection.forge.mixins.gui;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.client.Animations;
import net.aspw.nightx.features.module.modules.client.Hud;
import net.aspw.nightx.features.module.modules.combat.KillAura;
import net.aspw.nightx.features.module.modules.player.InventoryManager;
import net.aspw.nightx.features.module.modules.world.Stealer;
import net.aspw.nightx.utils.render.EaseUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends MixinGuiScreen {
    @Shadow
    protected int xSize;
    @Shadow
    protected int ySize;
    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;
    @Shadow
    private int dragSplittingButton;
    @Shadow
    private int dragSplittingRemnant;
    private GuiButton stealButton, chestStealerButton, invManagerButton, killAuraButton;
    private float progress = 0F;
    private long lastMS = 0L;

    @Shadow
    protected abstract boolean checkHotbarKeys(int keyCode);

    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    public void injectInitGui(CallbackInfo callbackInfo) {
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
        final Hud hud = NightX.moduleManager.getModule(Hud.class);

        int firstY = 0;

        if (guiScreen instanceof GuiChest) {
            switch (hud.getContainerButton().get()) {
                case "TopLeft":
                    if (NightX.moduleManager.getModule(KillAura.class).getState()) {
                        buttonList.add(killAuraButton = new GuiButton(1024576, 5, 5, 140, 20, "Disable KillAura"));
                        firstY += 20;
                    }
                    if (NightX.moduleManager.getModule(InventoryManager.class).getState()) {
                        buttonList.add(invManagerButton = new GuiButton(321123, 5, 5 + firstY, 140, 20, "Disable InventoryManager"));
                        firstY += 20;
                    }
                    if (NightX.moduleManager.getModule(Stealer.class).getState()) {
                        buttonList.add(chestStealerButton = new GuiButton(727, 5, 5 + firstY, 140, 20, "Disable Stealer"));
                        firstY += 20;
                    }
                    buttonList.add(stealButton = new GuiButton(1234123, 5, 5 + firstY, 140, 20, "Steal"));
                    break;
                case "TopRight":
                    if (NightX.moduleManager.getModule(KillAura.class).getState()) {
                        buttonList.add(killAuraButton = new GuiButton(1024576, width - 145, 5, 140, 20, "Disable KillAura"));
                        firstY += 20;
                    }
                    if (NightX.moduleManager.getModule(InventoryManager.class).getState()) {
                        buttonList.add(invManagerButton = new GuiButton(321123, width - 145, 5 + firstY, 140, 20, "Disable InventoryManager"));
                        firstY += 20;
                    }
                    if (NightX.moduleManager.getModule(Stealer.class).getState()) {
                        buttonList.add(chestStealerButton = new GuiButton(727, width - 145, 5 + firstY, 140, 20, "Disable Stealer"));
                        firstY += 20;
                    }
                    buttonList.add(stealButton = new GuiButton(1234123, width - 145, 5 + firstY, 140, 20, "Steal"));
                    break;
            }
        }

        lastMS = System.currentTimeMillis();
        progress = 0F;
    }

    @Override
    protected void injectedActionPerformed(GuiButton button) {
        Stealer chestStealer = NightX.moduleManager.getModule(Stealer.class);

        if (button.id == 1024576)
            NightX.moduleManager.getModule(KillAura.class).setState(false);
        if (button.id == 321123)
            NightX.moduleManager.getModule(InventoryManager.class).setState(false);
        if (button.id == 727)
            chestStealer.setState(false);
        if (button.id == 1234123 && !chestStealer.getState()) {
            chestStealer.setContentReceived(mc.thePlayer.openContainer.windowId);
            chestStealer.setOnce(true);
            chestStealer.setState(true);
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void drawScreenHead(CallbackInfo callbackInfo) {
        final Animations animMod = NightX.moduleManager.getModule(Animations.class);
        Stealer chestStealer = NightX.moduleManager.getModule(Stealer.class);
        final Hud hud = NightX.moduleManager.getModule(Hud.class);
        final Minecraft mc = Minecraft.getMinecraft();

        if (progress >= 1F) progress = 1F;
        else progress = (float) (System.currentTimeMillis() - lastMS) / (float) Animations.animTimeValue.get();

        double trueAnim = EaseUtils.easeOutQuart(progress);

        if (hud.getContainerBackground().get()
                && (!(mc.currentScreen instanceof GuiChest)
                || !chestStealer.getState()
                || !chestStealer.getSilenceValue().get()
                || chestStealer.getStillDisplayValue().get()))
            RenderUtils.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);

        boolean checkFullSilence = chestStealer.getState() && chestStealer.getSilenceValue().get() && !chestStealer.getStillDisplayValue().get();

        if (animMod != null && animMod.getState() && !(mc.currentScreen instanceof GuiChest && checkFullSilence)) {
            GL11.glPushMatrix();
            switch (Animations.guiAnimations.get()) {
                case "Zoom":
                    GL11.glTranslated((1 - trueAnim) * (width / 2D), (1 - trueAnim) * (height / 2D), 0D);
                    GL11.glScaled(trueAnim, trueAnim, trueAnim);
                    break;
                case "Slide":
                    switch (Animations.hSlideValue.get()) {
                        case "Right":
                            GL11.glTranslated((1 - trueAnim) * -width, 0D, 0D);
                            break;
                        case "Left":
                            GL11.glTranslated((1 - trueAnim) * width, 0D, 0D);
                            break;
                    }
                    switch (Animations.vSlideValue.get()) {
                        case "Upward":
                            GL11.glTranslated(0D, (1 - trueAnim) * height, 0D);
                            break;
                        case "Downward":
                            GL11.glTranslated(0D, (1 - trueAnim) * -height, 0D);
                            break;
                    }
                    break;
                case "Smooth":
                    GL11.glTranslated((1 - trueAnim) * -width, (1 - trueAnim) * -height / 4F, 0D);
                    break;
            }
        }

        try {
            GuiScreen guiScreen = mc.currentScreen;

            if (stealButton != null) stealButton.enabled = !chestStealer.getState();
            if (killAuraButton != null)
                killAuraButton.enabled = NightX.moduleManager.getModule(KillAura.class).getState();
            if (chestStealerButton != null) chestStealerButton.enabled = chestStealer.getState();
            if (invManagerButton != null)
                invManagerButton.enabled = NightX.moduleManager.getModule(InventoryManager.class).getState();

            if (chestStealer.getState() && chestStealer.getSilenceValue().get() && guiScreen instanceof GuiChest) {
                mc.setIngameFocus();
                mc.currentScreen = guiScreen;

                //hide GUI
                if (chestStealer.getShowStringValue().get() && !chestStealer.getStillDisplayValue().get()) {
                    String tipString = "Stealing... Press Esc to stop.";

                    mc.fontRendererObj.drawString(tipString,
                            (width / 2F) - (mc.fontRendererObj.getStringWidth(tipString) / 2F) - 0.5F,
                            (height / 2F) + 30, 0, false);
                    mc.fontRendererObj.drawString(tipString,
                            (width / 2F) - (mc.fontRendererObj.getStringWidth(tipString) / 2F) + 0.5F,
                            (height / 2F) + 30, 0, false);
                    mc.fontRendererObj.drawString(tipString,
                            (width / 2F) - (mc.fontRendererObj.getStringWidth(tipString) / 2F),
                            (height / 2F) + 29.5F, 0, false);
                    mc.fontRendererObj.drawString(tipString,
                            (width / 2F) - (mc.fontRendererObj.getStringWidth(tipString) / 2F),
                            (height / 2F) + 30.5F, 0, false);
                    mc.fontRendererObj.drawString(tipString,
                            (width / 2F) - (mc.fontRendererObj.getStringWidth(tipString) / 2F),
                            (height / 2F) + 30, 0xffffffff, false);
                }

                if (!chestStealer.getOnce() && !chestStealer.getStillDisplayValue().get())
                    callbackInfo.cancel();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    protected boolean shouldRenderBackground() {
        return false;
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreenReturn(CallbackInfo callbackInfo) {
        final Animations animMod = NightX.moduleManager.getModule(Animations.class);
        Stealer chestStealer = NightX.moduleManager.getModule(Stealer.class);
        final Minecraft mc = Minecraft.getMinecraft();
        boolean checkFullSilence = chestStealer.getState() && chestStealer.getSilenceValue().get() && !chestStealer.getStillDisplayValue().get();

        if (animMod != null && animMod.getState() && !(mc.currentScreen instanceof GuiChest && checkFullSilence))
            GL11.glPopMatrix();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void checkCloseClick(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (mouseButton - 100 == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.thePlayer.closeScreen();
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void checkHotbarClicks(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        checkHotbarKeys(mouseButton - 100);
    }

    @Inject(method = "updateDragSplitting", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void fixRemnants(CallbackInfo ci) {
        if (this.dragSplittingButton == 2) {
            this.dragSplittingRemnant = mc.thePlayer.inventory.getItemStack().getMaxStackSize();
            ci.cancel();
        }
    }
}