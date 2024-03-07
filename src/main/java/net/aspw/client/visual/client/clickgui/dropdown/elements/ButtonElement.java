package net.aspw.client.visual.client.clickgui.dropdown.elements;

import net.aspw.client.Launch;

public class ButtonElement extends Element {

    protected String displayName;
    protected int color = 0xffffff;

    public int hoverTime;

    public ButtonElement(String displayName) {
        createButton(displayName);
    }

    public void createButton(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float button) {
        Launch.clickGui.style.drawButtonElement(mouseX, mouseY, this);
        super.drawScreen(mouseX, mouseY, button);
    }

    @Override
    public int getHeight() {
        return 16;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + 16;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
