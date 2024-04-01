package net.aspw.client.visual.client.altmanager.menus

import net.aspw.client.Launch
import net.aspw.client.auth.account.CrackedAccount
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.utils.URLComponent
import net.aspw.client.utils.misc.MiscUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.visual.client.altmanager.GuiAltManager
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import java.io.IOException
import kotlin.concurrent.thread

class GuiAddAccount(private val prevGui: GuiAltManager) : GuiScreen() {

    private lateinit var addButton: GuiButton
    private lateinit var username: GuiTextField

    private var status = "§7Waiting..."

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)

        // Login via Microsoft account
        if (Launch.useAltManager)
            buttonList.add(GuiButton(3, width / 2 - 100, 133, "Microsoft Login"))
        else buttonList.add(GuiButton(4, width / 2 - 100, 133, "Set Java Path for Microsoft Login"))

        // Add and back button
        buttonList.add(
            GuiButton(
                1,
                width / 2 - 100,
                height - 54,
                98,
                20,
                "Add"
            ).also { addButton = it })
        buttonList.add(GuiButton(0, width / 2 + 2, height - 54, 98, 20, "Done"))

        username = GuiTextField(2, mc.fontRendererObj, width / 2 - 100, 90, 200, 20)
        username.isFocused = false
        username.maxStringLength = 16
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        RenderUtils.drawImage(
            ResourceLocation("client/background/portal.png"), 0, 0,
            width, height
        )
        RenderUtils.drawRect(30F, 30F, width - 30F, height - 30F, Int.MIN_VALUE)
        this.drawCenteredString(
            mc.fontRendererObj, "Add Account",
            width / 2,
            34,
            0xffffff
        )
        this.drawCenteredString(mc.fontRendererObj, status, width / 2, height - 74, 0xffffff)
        username.drawTextBox()
        if (username.text.isEmpty() && !username.isFocused) {
            this.drawCenteredString(
                mc.fontRendererObj, "§7Username (Cracked)",
                (width / 2 - 45),
                96,
                0xffffff
            )
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        if (!button.enabled) {
            return
        }

        when (button.id) {
            0 -> mc.displayGuiScreen(prevGui)

            1 -> checkAndAddAccount(username.text, account = CrackedAccount())

            3 -> mc.displayGuiScreen(GuiMicrosoftLogin(this))

            4 -> MiscUtils.showURL(URLComponent.JAVAUPDATE)
        }
    }

    @Throws(IOException::class)
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_ESCAPE -> {
                mc.displayGuiScreen(prevGui)
                return
            }
        }

        if (username.isFocused) {
            username.textboxKeyTyped(typedChar, keyCode)
        }
        super.keyTyped(typedChar, keyCode)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        username.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun updateScreen() {
        username.updateCursorCounter()
        super.updateScreen()
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    private fun checkAndAddAccount(usernameText: String, account: CrackedAccount) {
        if (usernameText.isEmpty()) {
            return
        }

        account.name = usernameText

        if (Launch.fileManager.accountsConfig.accountExists(account)) {
            status = "§cThe account has already been added."
            return
        }

        addButton.enabled = false

        thread(name = "Account-Checking-Task") {
            Launch.fileManager.accountsConfig.addAccount(account)
            Launch.fileManager.saveConfig(Launch.fileManager.accountsConfig)
            if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
            }
            status = "§aThe account has been added."
            prevGui.status = status
            mc.displayGuiScreen(prevGui)
        }
    }
}