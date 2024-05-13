package net.aspw.client.visual.client.altmanager.menus

import net.aspw.client.Launch
import net.aspw.client.auth.account.MicrosoftAccount
import net.aspw.client.auth.compat.OAuthServer
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.utils.ClientUtils
import net.aspw.client.utils.misc.MiscUtils
import net.aspw.client.utils.render.RenderUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

class GuiMicrosoftLogin(private val prevGui: GuiScreen) : GuiScreen() {
    private var stage = "Initializing..."
    private lateinit var server: OAuthServer

    override fun initGui() {
        try {
            server = MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
                override fun openUrl(url: String) {
                    stage = "Logging in..."
                    ClientUtils.getLogger().info("Opening URL: $url")

                    MiscUtils.showURL(url)
                }

                override fun authError(error: String) {
                    stage = "Error: $error"
                }

                override fun authResult(account: MicrosoftAccount) {
                    if (Launch.fileManager.accountsConfig.accountExists(account)) {
                        stage = "§cThe account has already been added."
                        return
                    }
                    Launch.fileManager.accountsConfig.addAccount(account)
                    Launch.fileManager.saveConfig(Launch.fileManager.accountsConfig)
                    if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                        Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                    }
                    stage = "§aThe account has been added."
                    mc.displayGuiScreen(prevGui)
                }
            })
        } catch (e: Exception) {
            stage = "Failed [$e]"
        }

        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 120 + 12, "Cancel"))
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 0) {
            try {
                server.stop(true)
            } catch (ignored: Exception) {
            }
            mc.displayGuiScreen(prevGui)
        }
    }

    public override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_ESCAPE -> {
                server.stop(true)
                mc.displayGuiScreen(prevGui)
                return
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        RenderUtils.drawImage(
            ResourceLocation("client/background/portal.png"), 0, 0,
            width, height
        )
        this.drawCenteredString(mc.fontRendererObj, stage, width / 2, height / 2 - 50, 0xffffff)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}