package net.aspw.client.visual.client

import net.aspw.client.features.api.ProxyManager
import net.aspw.client.visual.font.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.input.Keyboard
import java.net.Proxy

class GuiProxy(private val prevGui: GuiScreen) : GuiScreen() {
    private lateinit var textField: GuiTextField
    private lateinit var type: GuiButton
    private lateinit var stat: GuiButton

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        textField = GuiTextField(3, mc.fontRendererObj, width / 2 - 100, 60, 200, 20)
        textField.isFocused = true
        textField.text = ProxyManager.proxy
        textField.maxStringLength = 114514
        buttonList.add(GuiButton(1, width / 2 - 100, height / 4 + 96, "").also { type = it })
        buttonList.add(GuiButton(2, width / 2 - 100, height / 4 + 120, "").also { stat = it })
        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 144, "Done"))
        updateButtonStat()
    }

    private fun updateButtonStat() {
        type.displayString = "Type: §a" + ProxyManager.proxyType.name
        stat.displayString = "Status: " + if (ProxyManager.isEnable) "§aEnabled" else "§cDisabled"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        Fonts.fontLarge.drawCenteredString("Proxy Manager", width / 2.0f, 12.0f, 0xffffff)
        textField.drawTextBox()
        if (textField.text.isEmpty() && !textField.isFocused) {
            drawString(mc.fontRendererObj, "§7%ui.proxy.address%", width / 2 - 100, 66, 0xffffff)
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> {
                ProxyManager.proxy = textField.text
                mc.displayGuiScreen(prevGui)
            }

            1 -> {
                when (ProxyManager.proxyType) {
                    Proxy.Type.SOCKS -> ProxyManager.proxyType = Proxy.Type.HTTP
                    Proxy.Type.HTTP -> ProxyManager.proxyType = Proxy.Type.SOCKS
                    else -> throw IllegalStateException("Proxy type is not supported!")
                }
            }

            2 -> {
                ProxyManager.isEnable = !ProxyManager.isEnable
            }
        }
        updateButtonStat()
    }

    override fun onGuiClosed() {
        ProxyManager.proxy = textField.text
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        if (textField.isFocused) {
            textField.textboxKeyTyped(typedChar, keyCode)
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        textField.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun updateScreen() {
        textField.updateCursorCounter()
        super.updateScreen()
    }
}