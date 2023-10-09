package net.aspw.client.visual.client

import net.aspw.client.Client
import net.aspw.client.util.ClientUtils
import net.aspw.client.util.network.CheckConnection
import net.aspw.client.util.network.LoginID
import net.aspw.client.util.network.LoginID.id
import net.aspw.client.util.network.LoginID.loggedIn
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.visual.font.semi.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.Display

class GuiLoginSelection(private val prevGui: GuiScreen) : GuiScreen() {

    override fun initGui() {
        buttonList.add(GuiButton(10, width / 2 - 100, height / 4 + 65 + 6, "Update & Connect"))
        buttonList.add(GuiButton(2, width / 2 - 100, height / 4 + 100 + 4, "Reconnect Database"))
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        RenderUtils.drawImage(
            ResourceLocation("client/background/portal.png"), 0, 0,
            width, height
        )
        Fonts.minecraftFont.drawString(
            "< §cAnnouncement §r>",
            width - 4 - Fonts.minecraftFont.getStringWidth("< §cAnnouncement §r>"),
            4,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.announcement,
            width - 4 - Fonts.minecraftFont.getStringWidth(CheckConnection.announcement),
            13,
            -1
        )
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            2 -> {
                CheckConnection.checkStatus()
                CheckConnection.getAnnouncement()
                CheckConnection.getContributors()
                CheckConnection.getRealContributors()
            }

            // Old Auth System
            //1 -> {
            //    mc.displayGuiScreen(GuiLoginScreen(this))
            //}

            10 -> {
                CheckConnection.checkLatestVersion()
                if (CheckConnection.isAvailable) {
                    if (CheckConnection.isLatest)
                        Display.setTitle("${Client.CLIENT_BEST} Client - ${Client.CLIENT_VERSION}")
                    else Display.setTitle("Outdated! Please Update on ${Client.CLIENT_WEBSITE} (your current version is ${Client.CLIENT_VERSION}")
                } else {
                    Display.setTitle("Temporary Unavailable. Wait a minute!")
                }
                if (CheckConnection.isLatest && CheckConnection.canConnect) {
                    loggedIn = true
                    id = "User"
                    LoginID.password = "Free"
                    LoginID.uid = "000"
                    mc.displayGuiScreen(GuiMainMenu())
                    ClientUtils.getLogger().info("Logged in with Free Account!")
                } else {
                    loggedIn = false
                }
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            if (!loggedIn)
                return
            else mc.displayGuiScreen(GuiFirstMenu(this))
        }

        super.keyTyped(typedChar, keyCode)
    }
}