package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.player.InventoryManager;
import net.aspw.client.features.module.impl.player.Stealer;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.Hud;
import net.aspw.client.utils.render.RenderUtils;
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

        if (guiScreen instanceof GuiChest) {
            buttonList.add(killAuraButton = new GuiButton(1024576, 5, 5, 150, 20, "Disable KillAura"));
            buttonList.add(invManagerButton = new GuiButton(321123, 5, 27, 150, 20, "Disable InventoryManager"));
            buttonList.add(chestStealerButton = new GuiButton(727, 5, 49, 150, 20, "Disable Stealer"));
        }

        lastMS = System.currentTimeMillis();
        progress = 0F;
    }

    @Override
    protected void injectedActionPerformed(GuiButton button) {
        if (button.id == 1024576)
            Client.moduleManager.getModule(KillAura.class).setState(false);
        if (button.id == 321123)
            Client.moduleManager.getModule(InventoryManager.class).setState(false);
        if (button.id == 727)
            Client.moduleManager.getModule(Stealer.class).setState(false);
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void drawScreenHead(CallbackInfo callbackInfo) {
        Stealer chestStealer = Client.moduleManager.getModule(Stealer.class);
        final Hud hud = Client.moduleManager.getModule(Hud.class);
        final Minecraft mc = Minecraft.getMinecraft();

        if (progress >= 1F) progress = 1F;
        else progress = (float) (System.currentTimeMillis() - lastMS) / (float) 200;

        if (hud.getContainerBackground().get()
                && (!(mc.currentScreen instanceof GuiChest)
                || !chestStealer.getState()
                || !chestStealer.getSilenceValue().get()
                || chestStealer.getStillDisplayValue().get()))
            RenderUtils.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);

        try {
            GuiScreen guiScreen = mc.currentScreen;

            if (stealButton != null) stealButton.enabled = !chestStealer.getState();
            if (killAuraButton != null)
                killAuraButton.enabled = Client.moduleManager.getModule(KillAura.class).getState();
            if (chestStealerButton != null) chestStealerButton.enabled = chestStealer.getState();
            if (invManagerButton != null)
                invManagerButton.enabled = Client.moduleManager.getModule(InventoryManager.class).getState();

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
        final Animations animMod = Client.moduleManager.getModule(Animations.class);
        Stealer chestStealer = Client.moduleManager.getModule(Stealer.class);
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