package net.aspw.client.visual.client.clickgui.tab;

import net.aspw.client.Launch;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.impl.visual.Gui;
import net.aspw.client.utils.AnimationUtils;
import net.aspw.client.utils.MouseUtils;
import net.aspw.client.utils.render.BlurUtils;
import net.aspw.client.utils.render.RenderUtils;
import net.aspw.client.utils.render.Stencil;
import net.aspw.client.visual.client.clickgui.tab.elements.CategoryElement;
import net.aspw.client.visual.client.clickgui.tab.elements.ModuleElement;
import net.aspw.client.visual.client.clickgui.tab.elements.SearchElement;
import net.aspw.client.visual.font.smooth.FontLoaders;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewUi extends GuiScreen {

    private static NewUi instance;
    public final List<CategoryElement> categoryElements = new ArrayList<>();
    private float startYAnim = height / 2F;
    private float endYAnim = height / 2F;
    private float fading = 0F;
    private SearchElement searchElement;

    private NewUi() {
        for (final ModuleCategory c : ModuleCategory.values())
            categoryElements.add(new CategoryElement(c));
        categoryElements.get(0).setFocused(true);
    }

    public static NewUi getInstance() {
        return instance == null ? instance = new NewUi() : instance;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        for (final CategoryElement ce : categoryElements) {
            for (final ModuleElement me : ce.getModuleElements()) {
                if (me.listeningKeybind())
                    me.resetState();
            }
        }
        searchElement = new SearchElement(36F, 38F, 190F, 20F);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        for (final CategoryElement ce : categoryElements) {
            if (ce.getFocused())
                ce.handleMouseRelease(-1, -1, 0, 0, 0, 0, 0);
        }
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawFullSized(mouseX, mouseY, partialTicks, Objects.requireNonNull(Launch.moduleManager.getModule(Gui.class)).generateColor());
    }

    private void drawFullSized(final int mouseX, final int mouseY, final float partialTicks, final Color accentColor) {
        if (Objects.requireNonNull(Launch.moduleManager.getModule(Gui.class)).getGuiBlur().get()) {
            BlurUtils.blurArea(
                    0,
                    0,
                    this.width,
                    this.height,
                    10
            );
        }
        RenderUtils.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        RenderUtils.drawRoundedRect(31F, 31F, this.width - 31F, this.height - 31F, 8F, new Color(5, 5, 5, 170).getRGB());
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, this.width - 54F, 30F, this.width - 30F, 50F))
            fading += 0.2F * RenderUtils.deltaTime * 0.045F;
        else
            fading -= 0.2F * RenderUtils.deltaTime * 0.045F;
        fading = MathHelper.clamp_float(fading, 0F, 2F);
        GlStateManager.disableAlpha();
        RenderUtils.drawImage(IconManager.removeIcon, this.width - 49, 34, 16, 16);
        GlStateManager.enableAlpha();
        Stencil.write(true);
        Stencil.erase(true);
        Stencil.dispose();

        if (searchElement.isTyping()) {
            FontLoaders.SF20.drawStringWithShadow(
                    "Search",
                    242f,
                    FontLoaders.SF18.getHeight() + 30f,
                    -1
            );
        } else {
            FontLoaders.SF20.drawStringWithShadow(
                    "Modules",
                    242f,
                    FontLoaders.SF18.getHeight() + 30f,
                    -1
            );
        }

        if (searchElement.drawBox(mouseX, mouseY, accentColor)) {
            searchElement.drawPanel(mouseX, mouseY, 230, 50, width - 260, height - 80, Mouse.getDWheel(), categoryElements, accentColor);
            return;
        }

        final float elementHeight = 24;
        float startY = 60F;
        for (final CategoryElement ce : categoryElements) {
            ce.drawLabel(mouseX, mouseY, 30F, startY, 200F, elementHeight);
            if (ce.getFocused()) {
                final float goY;
                final float goDelta = RenderUtils.deltaTime * 0.025F;
                final float goCondition = startYAnim - (startY + 5F);
                if (goCondition > 0) {
                    goY = AnimationUtils.animate(startY + 6F, startYAnim, 0.65F * goDelta);
                } else {
                    goY = AnimationUtils.animate(startY + 6F, startYAnim, 0.55F * goDelta);
                }
                startYAnim = goY;

                final float finishY;
                final float endDelta = RenderUtils.deltaTime * 0.025F;
                final float endCondition = endYAnim - (startY + elementHeight - 5F);
                if (endCondition < 0) {
                    finishY = AnimationUtils.animate(startY + elementHeight - 6F, endYAnim, 0.65F * endDelta);
                } else {
                    finishY = AnimationUtils.animate(startY + elementHeight - 6F, endYAnim, 0.55F * endDelta);
                }
                endYAnim = finishY;

                ce.drawPanel(mouseX, mouseY, 230, 0, width - 260, height - 40, Mouse.getDWheel(), accentColor);
                ce.drawPanel(mouseX, mouseY, 230, 0, width - 260, height - 40, Mouse.getDWheel(), accentColor);
            }
            startY += elementHeight;
        }
        RenderUtils.originalRoundedRect(32F, startYAnim, 34F, endYAnim, 1F, accentColor.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, this.width - 54F, 30F, this.width - 30F, 50F)) {
            mc.displayGuiScreen(null);
            return;
        }
        final float elementHeight = 24;
        float startY = 60F;
        searchElement.handleMouseClick(mouseX, mouseY, mouseButton, 230, 50, width - 260, height - 80, categoryElements);
        if (!searchElement.isTyping()) for (final CategoryElement ce : categoryElements) {
            if (ce.getFocused())
                ce.handleMouseClick(mouseX, mouseY, mouseButton, 230, 0, width - 260, height - 40);
            if (MouseUtils.mouseWithinBounds(mouseX, mouseY, 30F, startY, 230F, startY + elementHeight) && !searchElement.isTyping()) {
                categoryElements.forEach(e -> e.setFocused(false));
                ce.setFocused(true);
                return;
            }
            startY += elementHeight;
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        for (final CategoryElement ce : categoryElements) {
            if (ce.getFocused()) {
                if (ce.handleKeyTyped(typedChar, keyCode))
                    return;
            }
        }
        if (searchElement.handleTyping(typedChar, keyCode, 230, 50, width - 260, height - 80, categoryElements))
            return;
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        searchElement.handleMouseRelease(mouseX, mouseY, state, 230, 50, width - 260, height - 80, categoryElements);
        if (!searchElement.isTyping())
            for (final CategoryElement ce : categoryElements) {
                if (ce.getFocused())
                    ce.handleMouseRelease(mouseX, mouseY, state, 230, 50, width - 260, height - 80);
            }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
