package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Launch;
import net.aspw.client.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat extends MixinGuiScreen {

    @Unique
    private final float fade = 14;
    @Shadow
    protected GuiTextField inputField;
    @Shadow
    private List<String> foundPlayerNames;
    @Shadow
    private boolean waitingOnAutocomplete;
    @Unique
    private float yPosOfInputField;

    @Shadow
    public abstract void onAutocompleteResponse(String[] p_onAutocompleteResponse_1_);

    @Inject(method = "initGui", at = @At("RETURN"))
    private void init(final CallbackInfo callbackInfo) {
        inputField.yPosition = height + 1;
        yPosOfInputField = inputField.yPosition;
    }

    @Inject(method = "keyTyped", at = @At("RETURN"))
    private void updateLength(final CallbackInfo callbackInfo) {
        if (inputField.getText().startsWith((".")))
            Launch.commandManager.autoComplete(inputField.getText());
        else inputField.setMaxStringLength(100);
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(final CallbackInfo callbackInfo) {
        yPosOfInputField = height - 12;
        inputField.yPosition = (int) yPosOfInputField;
    }

    @Inject(method = "autocompletePlayerNames", at = @At("HEAD"))
    private void prioritizeClientFriends(final CallbackInfo callbackInfo) {
        foundPlayerNames.sort(
                Comparator.comparing(s -> !Launch.fileManager.friendsConfig.isFriend(s)));
    }

    @Inject(method = "sendAutocompleteRequest", at = @At("HEAD"), cancellable = true)
    private void handleClientCommandCompletion(final String full, final String ignored, final CallbackInfo callbackInfo) {
        if (Launch.commandManager.autoComplete(full)) {
            waitingOnAutocomplete = true;

            final String[] latestAutoComplete = Launch.commandManager.getLatestAutoComplete();

            if (full.toLowerCase().endsWith(latestAutoComplete[latestAutoComplete.length - 1].toLowerCase()))
                return;

            this.onAutocompleteResponse(latestAutoComplete);

            callbackInfo.cancel();
        }
    }

    /**
     * @author As_pw
     * @reason Draw Chat
     */
    @Overwrite
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtils.drawRect(2F, this.height - fade, this.width - 2, this.height - fade + 12, Integer.MIN_VALUE);
        this.inputField.drawTextBox();

        if (Launch.commandManager.getLatestAutoComplete().length > 0 && !inputField.getText().isEmpty() && inputField.getText().startsWith(".")) {
            final String[] latestAutoComplete = Launch.commandManager.getLatestAutoComplete();
            final String[] textArray = inputField.getText().split(" ");
            final String trimmedString = latestAutoComplete[0].replaceFirst("(?i)" + textArray[textArray.length - 1], "");

            mc.fontRendererObj.drawStringWithShadow(trimmedString, inputField.xPosition + mc.fontRendererObj.getStringWidth(inputField.getText()), inputField.yPosition, new Color(165, 165, 165).getRGB());
        }

        final IChatComponent ichatcomponent =
                this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null)
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
    }
}