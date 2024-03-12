package net.aspw.client.visual.client.clickgui.smooth

import net.aspw.client.Launch
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.impl.visual.Gui
import net.aspw.client.utils.render.BlurUtils.blurArea
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.visual.client.clickgui.smooth.buttons.CategoryPanel
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt

class SmoothClickGui : GuiScreen() {
    private var panels = ArrayList<CategoryPanel>()
    private val scale: Float
        get() = Launch.moduleManager.getModule(Gui::class.java)?.scaleValue?.get()!!

    init {
        var xPos = 84f
        for (cat in ModuleCategory.entries) {
            panels.add(CategoryPanel(xPos, 20f, cat, Color(200, 200, 200, 80)))
            xPos += SmoothConstants.PANEL_WIDTH.toInt() + 10
        }
    }

    override fun drawScreen(mouseXIn: Int, mouseYIn: Int, partialTicks: Float) {
        val mouseX = (mouseXIn / scale).roundToInt()
        val mouseY = (mouseYIn / scale).roundToInt()

        if (Objects.requireNonNull(Launch.moduleManager.getModule(Gui::class.java))?.guiBlur?.get()!!) {
            blurArea(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                10f
            )
        }
        RenderUtils.drawGradientRect(0, 0, width, height, -1072689136, -804253680)

        GL11.glPushMatrix()
        GL11.glScalef(scale, scale, scale)

        if (Mouse.hasWheel()) {
            val wheel = Mouse.getDWheel()
            if (wheel != 0) {
                if (wheel > 0)
                    panels.map { it.y += 100 }
                else panels.map { it.y -= 100 }
            }
        }

        for (catPanel in panels) {
            catPanel.drawPanel(mouseX, mouseY)
        }

        GL11.glPopMatrix()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    private fun mouseAction(mouseXIn: Int, mouseYIn: Int, mouseButton: Int, state: Boolean) {
        val mouseX = (mouseXIn / scale).roundToInt()
        val mouseY = (mouseYIn / scale).roundToInt()

        for (panel in panels) {
            panel.mouseAction(mouseX, mouseY, state, mouseButton)
            if (panel.open) {
                for (moduleButton in panel.moduleButtons) {
                    moduleButton.mouseAction(mouseX, mouseY, state, mouseButton)
                    if (moduleButton.open) {
                        for (pan in moduleButton.valueButtons) {
                            pan.mouseAction(mouseX, mouseY, state, mouseButton)
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseXIn: Int, mouseYIn: Int, mouseButton: Int) {
        mouseAction(mouseXIn, mouseYIn, mouseButton, true)
    }

    override fun mouseReleased(mouseXIn: Int, mouseYIn: Int, mouseButton: Int) {
        mouseAction(mouseXIn, mouseYIn, mouseButton, false)
    }
}