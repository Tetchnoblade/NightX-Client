package net.aspw.nightx.visual.hud.designer

import net.aspw.nightx.NightX
import net.aspw.nightx.visual.hud.element.Element
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import kotlin.math.min

class GuiHudDesigner : GuiScreen() {

    private var editorPanel = EditorPanel(this, 2, 2)

    var selectedElement: Element? = null
    private var buttonAction = false

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        editorPanel = EditorPanel(this, width / 2, height / 2)
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        NightX.hud.render(true)
        NightX.hud.handleMouseMove(mouseX, mouseY)

        if (!NightX.hud.elements.contains(selectedElement))
            selectedElement = null

        val wheel = Mouse.getDWheel()

        editorPanel.drawPanel(mouseX, mouseY, wheel)

        if (wheel != 0) {
            for (element in NightX.hud.elements) {
                if (element.isInBorder(
                        mouseX / element.scale - element.renderX,
                        mouseY / element.scale - element.renderY
                    )
                ) {
                    element.scale = element.scale + if (wheel > 0) 0.05f else -0.05f
                    break
                }
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        if (buttonAction) {
            buttonAction = false
            return
        }

        NightX.hud.handleMouseClick(mouseX, mouseY, mouseButton)

        if (!(mouseX >= editorPanel.x && mouseX <= editorPanel.x + editorPanel.width && mouseY >= editorPanel.y &&
                    mouseY <= editorPanel.y + min(editorPanel.realHeight, 200))
        ) {
            selectedElement = null
            editorPanel.create = false
        }

        if (mouseButton == 0) {
            for (element in NightX.hud.elements) {
                if (element.isInBorder(
                        mouseX / element.scale - element.renderX,
                        mouseY / element.scale - element.renderY
                    )
                ) {
                    selectedElement = element
                    break
                }
            }
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)

        NightX.hud.handleMouseReleased()
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
        NightX.fileManager.saveConfig(NightX.fileManager.hudConfig)

        super.onGuiClosed()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_DELETE -> if (Keyboard.KEY_DELETE == keyCode && selectedElement != null)
                NightX.hud.removeElement(selectedElement!!)

            Keyboard.KEY_ESCAPE -> {
                selectedElement = null
                editorPanel.create = false
            }

            else -> NightX.hud.handleKey(typedChar, keyCode)
        }

        super.keyTyped(typedChar, keyCode)
    }
}