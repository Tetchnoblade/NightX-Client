package dev.tr7zw.waveycapes.config;

import java.util.Set;

import dev.tr7zw.waveycapes.WaveyCapesBase.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class WaveyCapesModGuiFactory implements IModGuiFactory {

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement paramRuntimeOptionCategoryElement) {
        return null;
    }

    @Override
    public void initialize(Minecraft paramMinecraft) {

    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigScreen.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

}
