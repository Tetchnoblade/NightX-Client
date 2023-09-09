package net.aspw.client.visual.client

import net.aspw.client.Client
import net.aspw.client.util.ClientUtils
import net.aspw.client.util.misc.MiscUtils
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

class GuiLoginSelection(private val prevGui: GuiScreen) : GuiScreen() {

    override fun initGui() {
        if (CheckConnection.isAvailable) {
            if (CheckConnection.isLatest) {
                buttonList.add(GuiButton(10, width / 2 - 100, height / 4 + 104, "Connect"))
                // Old Auth System
                // buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 104, "Free Login"))
                //buttonList.add(GuiButton(1, width / 2 - 100, height / 4 + 144, "Premium Login"))
            } else {
                buttonList.add(GuiButton(10, width / 2 - 100, height / 4 + 65 + 6, "Connect"))
                buttonList.add(GuiButton(2, width / 2 - 100, height / 4 + 100 + 4, "Access Website"))
                buttonList.add(GuiButton(3, width / 2 - 100, height / 4 + 135 + 2, "Official Discord"))
            }
        } else {
            buttonList.add(GuiButton(10, width / 2 - 100, height / 4 + 65 + 6, "Connect"))
            buttonList.add(GuiButton(2, width / 2 - 100, height / 4 + 100 + 4, "Access Website"))
            buttonList.add(GuiButton(3, width / 2 - 100, height / 4 + 135 + 2, "Official Discord"))
        }
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
        if (CheckConnection.isAvailable) {
            if (CheckConnection.isLatest)
                this.drawCenteredString(
                    mc.fontRendererObj,
                    "Your current build is §eLatest!",
                    width / 2,
                    height / 4 + 64,
                    0xffffff
                )
            else this.drawCenteredString(
                mc.fontRendererObj,
                "Your current build is §cOutdated!",
                width / 2,
                height / 4 + 44,
                0xffffff
            )
        } else {
            this.drawCenteredString(
                mc.fontRendererObj,
                "§cTemporary unavailable!",
                width / 2,
                height / 4 + 44,
                0xffffff
            )
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            3 -> {
                MiscUtils.showURL(CheckConnection.discord)
            }

            2 -> {
                MiscUtils.showURL(Client.CLIENT_WEBSITE)
            }

            // Old Auth System
            //1 -> {
            //    mc.displayGuiScreen(GuiLoginScreen(this))
            //}

            10 -> {
                CheckConnection.checkStatus()
                CheckConnection.getAnnouncement()
                CheckConnection.getContributors()
                CheckConnection.getRealContributors()
                if (CheckConnection.canConnect) {
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