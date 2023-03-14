package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Client;
import net.aspw.client.utils.AnimationUtils;
import net.aspw.client.utils.render.RenderUtils;
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

import java.awt.*;
import java.util.Comparator;
import java.util.List;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat extends MixinGuiScreen {
    @Shadow
    protected GuiTextField inputField;

    @Shadow
    private List<String> foundPlayerNames;
    @Shadow
    private boolean waitingOnAutocomplete;
    private float yPosOfInputField;
    private float fade = 0;

    @Shadow
    public abstract void onAutocompleteResponse(String[] p_onAutocompleteResponse_1_);

    @Inject(method = "initGui", at = @At("RETURN"))
    private void init(CallbackInfo callbackInfo) {
        inputField.yPosition = height + 1;
        yPosOfInputField = inputField.yPosition;
    }

    @Inject(method = "keyTyped", at = @At("RETURN"))
    private void updateLength(CallbackInfo callbackInfo) {
        if (!inputField.getText().startsWith(String.valueOf(Client.commandManager.getPrefix()))) return;
        Client.commandManager.autoComplete(inputField.getText());

        if (!inputField.getText().startsWith(Client.commandManager.getPrefix() + "lc"))
            inputField.setMaxStringLength(10000);
        else
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
     * Adds client command auto completion and cancels sending an auto completion request packet
     * to the server if the message contains a client command.
     *
     * @author NurMarvin
     */
    @Inject(method = "sendAutocompleteRequest", at = @At("HEAD"), cancellable = true)
    private void handleClientCommandCompletion(String full, final String ignored, CallbackInfo callbackInfo) {
        if (Client.commandManager.autoComplete(full)) {
            waitingOnAutocomplete = true;

            String[] latestAutoComplete = Client.commandManager.getLatestAutoComplete();

            if (full.toLowerCase().endsWith(latestAutoComplete[latestAutoComplete.length - 1].toLowerCase()))
                return;

            this.onAutocompleteResponse(latestAutoComplete);

            callbackInfo.cancel();
        }
    }

    @Inject(method = "onAutocompleteResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;autocompletePlayerNames(F)V", shift = At.Shift.BEFORE), cancellable = true)
    private void onAutocompleteResponse(String[] autoCompleteResponse, CallbackInfo callbackInfo) {
        if (Client.commandManager.getLatestAutoComplete().length != 0) callbackInfo.cancel();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(2F, this.height - fade, this.width - 2, this.height - fade + 12, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        if (Client.commandManager.getLatestAutoComplete().length > 0 && !inputField.getText().isEmpty() && inputField.getText().startsWith(String.valueOf(Client.commandManager.getPrefix()))) {
            String[] latestAutoComplete = Client.commandManager.getLatestAutoComplete();
            String[] textArray = inputField.getText().split(" ");
            String trimmedString = latestAutoComplete[0].replaceFirst("(?i)" + textArray[textArray.length - 1], "");

            mc.fontRendererObj.drawStringWithShadow(trimmedString, inputField.xPosition + mc.fontRendererObj.getStringWidth(inputField.getText()), inputField.yPosition, new Color(165, 165, 165).getRGB());
        }

        IChatComponent ichatcomponent =
                this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null)
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
    }
}
