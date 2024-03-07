package net.aspw.client.visual.client.clickgui.dropdown.style;

import net.aspw.client.utils.MinecraftInstance;
import net.aspw.client.visual.client.clickgui.dropdown.Panel;
import net.aspw.client.visual.client.clickgui.dropdown.elements.ButtonElement;
import net.aspw.client.visual.client.clickgui.dropdown.elements.ModuleElement;

public abstract class Style extends MinecraftInstance {

    public abstract void drawPanel(final int mouseX, final int mouseY, final Panel panel);

    public abstract void drawButtonElement(final int mouseX, final int mouseY, final ButtonElement buttonElement);

    public abstract void drawModuleElement(final int mouseX, final int mouseY, final ModuleElement moduleElement);

}
