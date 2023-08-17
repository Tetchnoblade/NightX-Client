package net.aspw.client.visual.client.clickgui.dropdown;

import net.aspw.client.Client;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.impl.visual.Gui;
import net.aspw.client.util.render.EaseUtils;
import net.aspw.client.util.render.RenderUtils;
import net.aspw.client.visual.client.clickgui.dropdown.elements.ButtonElement;
import net.aspw.client.visual.client.clickgui.dropdown.elements.Element;
import net.aspw.client.visual.client.clickgui.dropdown.elements.ModuleElement;
import net.aspw.client.visual.client.clickgui.dropdown.style.Style;
import net.aspw.client.visual.client.clickgui.dropdown.style.styles.DropDown;
import net.aspw.client.visual.font.AWTFontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClickGui extends GuiScreen {

    public final List<Panel> panels = new ArrayList<>();
    public Style style = new DropDown();
    private Panel clickedPanel;
    private int mouseX;
    private int mouseY;

    public double slide, progress = 0;

    public long lastMS = System.currentTimeMillis();

    public ClickGui() {
        final int width = 100;
        final int height = 18;

        int xPos = 253;

        for (final ModuleCategory category : ModuleCategory.values()) {
            panels.add(new Panel(category.getDisplayName(), xPos, 30, width, height, true) {

                @Override
                public void setupItems() {
                    for (Module module : Client.moduleManager.getModules())
                        if (module.getCategory() == category)
                            getElements().add(new ModuleElement(module));
                }
            });

            xPos += 111;
        }
    }

    @Override
    public void initGui() {
        slide = progress = 0;
        lastMS = System.currentTimeMillis();
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        if (progress < 1) progress = (float) (System.currentTimeMillis() - lastMS) / (600F / 1.4F); // fully fps async
        else progress = 1;

        switch (Objects.requireNonNull(Client.moduleManager.getModule(Gui.class)).animationValue.get().toLowerCase()) {
            case "none": {
                slide = 1;
                break;
            }
            case "zoom": {
                slide = EaseUtils.easeOutBack(progress);
                break;
            }
        }

        // Enable DisplayList optimization
        AWTFontRenderer.Companion.setAssumeNonVolatile(true);

        final double scale = Objects.requireNonNull(Client.moduleManager.getModule(Gui.class)).scaleValue.get() - 0.1765;

        mouseX /= scale;
        mouseY /= scale;

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        GlStateManager.disableAlpha();
        GlStateManager.enableAlpha();

        switch (Objects.requireNonNull(Client.moduleManager.getModule(Gui.class)).animationValue.get().toLowerCase()) {
            case "none": {
                GlStateManager.scale(scale, scale, scale);
                break;
            }
            case "zoom": {
                GlStateManager.translate((1.0 - slide) * (width / 2.0), (1.0 - slide) * (height / 2.0), 0);
                GlStateManager.scale(scale * slide, scale * slide, scale * slide);
                break;
            }
        }

        RenderUtils.drawImage2(new ResourceLocation("client/images/" + Objects.requireNonNull(Client.moduleManager.getModule(Gui.class)).imageModeValue.getValue() + ".png"), width - 26F, height - 160F, 80, 80);
        GL11.glPushMatrix();
        GlStateManager.enableAlpha();
        GL11.glPopMatrix();

        for (final Panel panel : panels) {
            panel.updateFade(RenderUtils.deltaTime);
            panel.drawScreen(mouseX, mouseY, partialTicks);
        }

        for (final Panel panel : panels) {
            for (final Element element : panel.getElements()) {
                if (element instanceof ModuleElement) {
                    final ModuleElement moduleElement = (ModuleElement) element;
                }
            }
        }

        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        if (Objects.requireNonNull(Client.moduleManager.getModule(Gui.class)).animationValue.get().equalsIgnoreCase("zoom")) {
            GlStateManager.translate(-1 * (1.0 - slide) * (width / 2.0), -1 * (1.0 - slide) * (height / 2.0), 0);
        }
        GlStateManager.scale(1, 1, 1);

        AWTFontRenderer.Companion.setAssumeNonVolatile(false);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int wheel = Mouse.getEventDWheel();
        for (int i = panels.size() - 1; i >= 0; i--)
            if (panels.get(i).handleScroll(mouseX, mouseY, wheel))
                break;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        final double scale = Objects.requireNonNull(Client.moduleManager.getModule(Gui.class)).scaleValue.get() - 0.1765;

        mouseX /= scale;
        mouseY /= scale;

        for (int i = panels.size() - 1; i >= 0; i--) {
            if (panels.get(i).mouseClicked(mouseX, mouseY, mouseButton)) {
                break;
            }
        }

        for (final Panel panel : panels) {
            panel.drag = false;

            if (mouseButton == 0 && panel.isHovering(mouseX, mouseY)) {
                clickedPanel = panel;
                break;
            }
        }

        if (clickedPanel != null) {
            clickedPanel.x2 = clickedPanel.x - mouseX;
            clickedPanel.y2 = clickedPanel.y - mouseY;
            clickedPanel.drag = true;

            panels.remove(clickedPanel);
            panels.add(clickedPanel);
            clickedPanel = null;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        final double scale = Objects.requireNonNull(Client.moduleManager.getModule(Gui.class)).scaleValue.get() - 0.1765;

        mouseX /= scale;
        mouseY /= scale;

        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void updateScreen() {
        for (final Panel panel : panels) {
            for (final Element element : panel.getElements()) {
                if (element instanceof ButtonElement) {
                    final ButtonElement buttonElement = (ButtonElement) element;

                    if (buttonElement.isHovering(mouseX, mouseY)) {
                        if (buttonElement.hoverTime < 7)
                            buttonElement.hoverTime++;
                    } else if (buttonElement.hoverTime > 0)
                        buttonElement.hoverTime--;
                }

                if (element instanceof ModuleElement) {
                    if (((ModuleElement) element).getModule().getState()) {
                        if (((ModuleElement) element).slowlyFade < 255)
                            ((ModuleElement) element).slowlyFade += 100;
                    } else if (((ModuleElement) element).slowlyFade > 0)
                        ((ModuleElement) element).slowlyFade -= 100;

                    if (((ModuleElement) element).slowlyFade > 255)
                        ((ModuleElement) element).slowlyFade = 255;

                    if (((ModuleElement) element).slowlyFade < 0)
                        ((ModuleElement) element).slowlyFade = 0;
                }
            }
        }
        super.updateScreen();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
