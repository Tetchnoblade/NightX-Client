package net.aspw.client.visual.client

import net.aspw.client.features.api.ProxyManager
import net.aspw.client.util.render.RenderUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import java.net.Proxy

class GuiProxyManager(private val prevGui: GuiScreen) : GuiScreen() {
    private lateinit var textField: GuiTextField
    private lateinit var type: GuiButton
    private lateinit var stat: GuiButton

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        textField = GuiTextField(3, mc.fontRendererObj, width / 2 - 96, 60, 200, 20)
        textField.text = ProxyManager.proxy
        textField.maxStringLength = Int.MAX_VALUE
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
        RenderUtils.drawImage(
            ResourceLocation("client/background/portal.png"), 0, 0,
            width, height
        )
        this.drawCenteredString(mc.fontRendererObj, "Proxy Manager", width / 2, 12, 0xffffff)
        textField.drawTextBox()
        if (textField.text.isEmpty() && !textField.isFocused) {
            drawString(mc.fontRendererObj, "§7Address : Port", width / 2 - 92, 66, 0xffffff)
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