package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.injection.forge.mixins.gui.MixinGuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer extends MixinGuiContainer {

    @Shadow
    private boolean hasActivePotionEffects;

    /**
     * @author As_pw
     * @reason Effects
     */
    @Overwrite
    public void updateActivePotionEffects() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.hasActivePotionEffects = !this.mc.thePlayer.getActivePotionEffects().isEmpty();
    }
}