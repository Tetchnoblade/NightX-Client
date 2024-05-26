package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.player.InvManager;
import net.aspw.client.features.module.impl.player.Stealer;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.utils.MinecraftInstance;
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

import java.util.Objects;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends MixinGuiScreen {

    @Shadow
    protected int xSize;
    @Shadow
    protected int guiLeft;
    @Shadow
    private int dragSplittingButton;
    @Shadow
    private int dragSplittingRemnant;
    private GuiButton chestStealerButton, invManagerButton, killAuraButton;
    private float progress = 0F;
    private long lastMS = 0L;

    @Shadow
    protected abstract boolean checkHotbarKeys(int keyCode);

    @Inject(method = "initGui", at = @At("HEAD"))
    public void injectInitGui(final CallbackInfo callbackInfo) {
        final GuiScreen guiScreen = MinecraftInstance.mc.currentScreen;

        if (guiScreen instanceof GuiContainer) {
            buttonList.add(killAuraButton = new GuiButton(1024576, 5, 5, 150, 20, "Disable KillAura"));
            buttonList.add(chestStealerButton = new GuiButton(727, 5, 27, 150, 20, "Disable Stealer"));
            buttonList.add(invManagerButton = new GuiButton(321123, 5, 49, 150, 20, "Disable InvManager"));
        }

        lastMS = System.currentTimeMillis();
        progress = 0F;
    }

    @Override
    protected void injectedActionPerformed(final GuiButton button) {
        final KillAura killAura = Objects.requireNonNull(Launch.moduleManager.getModule(KillAura.class));
        final InvManager invManager = Objects.requireNonNull(Launch.moduleManager.getModule(InvManager.class));
        final Stealer stealer = Objects.requireNonNull(Launch.moduleManager.getModule(Stealer.class));
        if (button.id == 1024576)
            killAura.setState(false);
        if (button.id == 321123)
            invManager.setState(false);
        if (button.id == 727)
            stealer.setState(false);
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void drawScreenHead(final CallbackInfo callbackInfo) {
        final Stealer stealer = Objects.requireNonNull(Launch.moduleManager.getModule(Stealer.class));
        final KillAura killAura = Objects.requireNonNull(Launch.moduleManager.getModule(KillAura.class));
        final InvManager invManager = Objects.requireNonNull(Launch.moduleManager.getModule(InvManager.class));
        final Minecraft mc = MinecraftInstance.mc;

        if (progress >= 1F) progress = 1F;
        else progress = (float) (System.currentTimeMillis() - lastMS) / (float) 200;

        if ((!(mc.currentScreen instanceof GuiChest)
                || !stealer.getState()
                || !stealer.getSilenceValue().get()
                || stealer.getStillDisplayValue().get()))
            RenderUtils.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);

        final GuiScreen guiScreen = mc.currentScreen;

        if (killAuraButton != null)
            killAuraButton.enabled = killAura.getState();
        if (chestStealerButton != null) chestStealerButton.enabled = stealer.getState();
        if (invManagerButton != null)
            invManagerButton.enabled = invManager.getState();

        if (stealer.getState() && stealer.getSilenceValue().get() && guiScreen instanceof GuiChest) {
            mc.setIngameFocus();
            mc.currentScreen = guiScreen;

            //hide GUI
            if (stealer.getShowStringValue().get() && !stealer.getStillDisplayValue().get()) {
                final String tipString = "Stealing... Press Esc to stop.";

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

            if (!stealer.getOnce() && !stealer.getStillDisplayValue().get())
                callbackInfo.cancel();
        }
    }

    @Override
    protected boolean shouldRenderBackground() {
        return false;
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreenReturn(final CallbackInfo callbackInfo) {
        final Animations animMod = Objects.requireNonNull(Launch.moduleManager.getModule(Animations.class));
        final Stealer stealer = Objects.requireNonNull(Launch.moduleManager.getModule(Stealer.class));
        final Minecraft mc = MinecraftInstance.mc;
        final boolean checkFullSilence = stealer.getState() && stealer.getSilenceValue().get() && !stealer.getStillDisplayValue().get();

        if (animMod.getState() && !(mc.currentScreen instanceof GuiChest && checkFullSilence))
            GL11.glPopMatrix();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void checkCloseClick(final int mouseX, final int mouseY, final int mouseButton, final CallbackInfo ci) {
        if (mouseButton - 100 == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.thePlayer.closeScreen();
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void checkHotbarClicks(final int mouseX, final int mouseY, final int mouseButton, final CallbackInfo ci) {
        checkHotbarKeys(mouseButton - 100);
    }

    @Inject(method = "updateDragSplitting", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void fixRemnants(final CallbackInfo ci) {
        if (this.dragSplittingButton == 2) {
            this.dragSplittingRemnant = mc.thePlayer.inventory.getItemStack().getMaxStackSize();
            ci.cancel();
        }
    }
}