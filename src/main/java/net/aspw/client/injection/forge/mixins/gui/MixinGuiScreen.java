package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Client;
import net.aspw.client.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

/**
 * The type Mixin gui screen.
 */
@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {
    /**
     * The Mc.
     */
    @Shadow
    public Minecraft mc;
    /**
     * The Width.
     */
    @Shadow
    public int width;
    /**
     * The Height.
     */
    @Shadow
    public int height;
    /**
     * The Button list.
     */
    @Shadow
    protected List<GuiButton> buttonList;
    /**
     * The Font renderer obj.
     */
    @Shadow
    protected FontRenderer fontRendererObj;

    /**
     * Update screen.
     */
    @Shadow
    public void updateScreen() {
    }

    /**
     * Handle component hover.
     *
     * @param component the component
     * @param x         the x
     * @param y         the y
     */
    @Shadow
    public abstract void handleComponentHover(IChatComponent component, int x, int y);

    /**
     * Draw hovering text.
     *
     * @param textLines the text lines
     * @param x         the x
     * @param y         the y
     */
    @Shadow
    protected abstract void drawHoveringText(List<String> textLines, int x, int y);

    @Shadow
    public abstract void drawDefaultBackground();

    @Shadow
    public abstract void drawBackground(int p_drawBackground_1_);

    @Redirect(method = "handleKeyboardInput", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", remap = false))
    private boolean checkCharacter() {
        return Keyboard.getEventKey() == 0 && Keyboard.getEventCharacter() >= ' ' || Keyboard.getEventKeyState();
    }

    @Inject(method = "drawWorldBackground", at = @At("HEAD"), cancellable = true)
    private void drawWorldBackground(final CallbackInfo callbackInfo) {
        if (!shouldRenderBackground()) {
            callbackInfo.cancel();
        }
    }


    @Inject(method = "drawBackground", at = @At("HEAD"), cancellable = true)
    private void drawClientBackground(final CallbackInfo callbackInfo) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();

        RenderUtils.drawImage(
                new ResourceLocation("client/background/portal.png"), 0, 0,
                width, height
        );

        callbackInfo.cancel();
    }

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    private void messageSend(String msg, boolean addToChat, final CallbackInfo callbackInfo) {
        if (msg.startsWith(".") && addToChat) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);

            Client.commandManager.executeCommands(msg);
            callbackInfo.cancel();
        }
    }

    @Inject(method = "handleComponentHover", at = @At("HEAD"))
    private void handleHoverOverComponent(IChatComponent component, int x, int y, final CallbackInfo callbackInfo) {
        if (component == null || component.getChatStyle().getChatClickEvent() == null)
            return;

        final ChatStyle chatStyle = component.getChatStyle();

        final ClickEvent clickEvent = chatStyle.getChatClickEvent();
        final HoverEvent hoverEvent = chatStyle.getChatHoverEvent();

        drawHoveringText(Collections.singletonList("§c§l" + clickEvent.getAction().getCanonicalName().toUpperCase() + ": §a" + clickEvent.getValue()), x, y - (hoverEvent != null ? 17 : 0));
    }

    /**
     * Inject action performed.
     *
     * @param button       the button
     * @param callbackInfo the callback info
     */
    @Inject(method = "actionPerformed", at = @At("RETURN"))
    protected void injectActionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        this.injectedActionPerformed(button);
    }

    /**
     * Should render background boolean.
     *
     * @return the boolean
     */
    protected boolean shouldRenderBackground() {
        return true;
    }

    /**
     * Injected action performed.
     *
     * @param button the button
     */
    protected void injectedActionPerformed(GuiButton button) {

    }
}