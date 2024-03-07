package net.aspw.client.visual.client.clickgui.dropdown.elements;

import net.aspw.client.Launch;
import net.aspw.client.features.module.Module;
import org.lwjgl.input.Mouse;

public class ModuleElement extends ButtonElement {

    private final Module module;

    private boolean showSettings;
    private boolean wasPressed;

    public int slowlySettingsYPos;
    public int slowlyFade;

    public ModuleElement(final Module module) {
        super(null);

        this.displayName = module.getName();
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float button) {
        Launch.clickGui.style.drawModuleElement(mouseX, mouseY, this);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY) && isVisible()) {
            module.toggle();
            return true;
        }
        if (mouseButton == 1 && isVisible()) {
            if (isHovering(mouseX, mouseY))
                showSettings = !showSettings;
            else showSettings = false;
            return false;
        }
        return false;
    }

    public Module getModule() {
        return module;
    }

    public boolean isShowSettings() {
        return showSettings;
    }

    public boolean isntPressed() {
        return !wasPressed;
    }

    public void updatePressed() {
        wasPressed = Mouse.isButtonDown(0);
    }

    public float getSettingsWidth() {
        return 140;
    }

    public void setSettingsWidth(float settingsWidth) {
    }
}
