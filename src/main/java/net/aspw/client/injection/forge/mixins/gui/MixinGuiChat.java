package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.other.InfiniteChat;
import net.aspw.client.util.AnimationUtils;
import net.aspw.client.util.render.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scala.Int;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * The type Mixin gui chat.
 */
@Mixin(GuiChat.class)
public abstract class MixinGuiChat extends MixinGuiScreen {
    /**
     * The Input field.
     */
    @Shadow
    protected GuiTextField inputField;

    @Shadow
    private List<String> foundPlayerNames;
    @Shadow
    private boolean waitingOnAutocomplete;
    private float yPosOfInputField;
    private float fade = 0;

    /**
     * On autocomplete response.
     *
     * @param p_onAutocompleteResponse_1_ the p on autocomplete response 1
     */
    @Shadow
    public abstract void onAutocompleteResponse(String[] p_onAutocompleteResponse_1_);

    @Inject(method = "initGui", at = @At("RETURN"))
    private void init(CallbackInfo callbackInfo) {
        inputField.yPosition = height + 1;
        yPosOfInputField = inputField.yPosition;
    }

    @Inject(method = "keyTyped", at = @At("RETURN"))
    private void updateLength(CallbackInfo callbackInfo) {
        if (Objects.requireNonNull(Client.moduleManager.getModule(InfiniteChat.class)).getState())
            inputField.setMaxStringLength(Int.MaxValue());
        if (!inputField.getText().startsWith((".")) || Objects.requireNonNull(Client.moduleManager.getModule(InfiniteChat.class)).getState())
            return;
        inputField.setMaxStringLength(100);
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo callbackInfo) {
        final int delta = RenderUtils.deltaTime;

        if (fade < 14) fade = AnimationUtils.animate(14F, fade, 10F * delta);
        if (fade > 14) fade = 14;

        if (yPosOfInputField > height - 12)
            yPosOfInputField = AnimationUtils.animate(height - 12, yPosOfInputField, 10F * (12F / 14F) * delta);
        if (yPosOfInputField < height - 12) yPosOfInputField = height - 12;

        inputField.yPosition = (int) yPosOfInputField;
    }

    @Inject(method = "autocompletePlayerNames", at = @At("HEAD"))
    private void prioritizeClientFriends(final CallbackInfo callbackInfo) {
        foundPlayerNames.sort(
                Comparator.comparing(s -> !Client.fileManager.friendsConfig.isFriend(s)));
    }

    /**
     * Draw screen.
     *
     * @param mouseX       the mouse x
     * @param mouseY       the mouse y
     * @param partialTicks the partial ticks
     * @author As_pw
     * @reason Draw
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(2F, this.height - fade, this.width - 2, this.height - fade + 12, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        if (!inputField.getText().isEmpty() && inputField.getText().startsWith(".")) {

            mc.fontRendererObj.drawStringWithShadow("", inputField.xPosition + mc.fontRendererObj.getStringWidth(inputField.getText()), inputField.yPosition, new Color(165, 165, 165).getRGB());
        }

        IChatComponent ichatcomponent =
                this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null)
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
    }
}
