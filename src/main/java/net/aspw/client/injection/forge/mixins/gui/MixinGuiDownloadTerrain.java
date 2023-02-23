package net.aspw.client.injection.forge.mixins.gui;

import net.minecraft.client.gui.GuiDownloadTerrain;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiDownloadTerrain.class)
public abstract class MixinGuiDownloadTerrain extends MixinGuiScreen {
}