package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.util.StatisticsUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The type Mixin gui game over.
 */
@Mixin(GuiGameOver.class)
public abstract class MixinGuiGameOver extends MixinGuiScreen implements GuiYesNoCallback {
    @Shadow
    public int enableButtonsTimer;

    @Inject(method = "initGui", at = @At("HEAD"))
    private void allowClickable(CallbackInfo ci) {
        this.enableButtonsTimer = 0;
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        switch (button.id) {
            case 0:
                StatisticsUtils.addDeaths();
                this.mc.thePlayer.respawnPlayer();
                this.mc.displayGuiScreen(null);
                break;
            case 1:
                if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                } else {
                    GuiYesNo lvt_2_1_ = new GuiYesNo(this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"), 0);
                    this.mc.displayGuiScreen(lvt_2_1_);
                    lvt_2_1_.setButtonDelay(20);
                }
        }
    }

    public void confirmClicked(boolean p_confirmClicked_1_, int p_confirmClicked_2_, CallbackInfo ci) {
        if (p_confirmClicked_1_) {
            StatisticsUtils.addDeaths();
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            this.mc.displayGuiScreen(new GuiMainMenu());
        } else {
            StatisticsUtils.addDeaths();
            this.mc.thePlayer.respawnPlayer();
            this.mc.displayGuiScreen(null);
        }
    }
}
