package net.aspw.client.injection.forge.mixins.gui;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Gui.class)
public abstract class MixinGui {

    @Shadow
    protected float zLevel;

    @Shadow
    public abstract void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV);

}
