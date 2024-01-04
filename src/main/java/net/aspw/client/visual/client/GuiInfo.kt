package net.aspw.client.visual.client

import net.aspw.client.Client
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.util.ClientUtils
import net.aspw.client.util.misc.MiscUtils
import net.aspw.client.util.network.Access
import net.aspw.client.util.network.LoginID
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.visual.font.semi.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.GuiModList
import org.lwjgl.input.Keyboard

class GuiInfo(private val prevGui: GuiScreen) : GuiScreen() {

    override fun initGui() {
        buttonList.add(GuiButton(1, width / 2 - 100, height / 4 + 20 + 4, "Open Website"))
        buttonList.add(GuiButton(2, width / 2 - 100, height / 4 + 55 + 2, "Join Discord"))
        buttonList.add(GuiButton(3, width / 2 - 100, height / 4 + 90 - 2, "Reload Data"))
        buttonList.add(GuiButton(4, width / 2 - 100, height / 4 + 125 - 4, "Logout"))
        buttonList.add(GuiButton(5, width / 2 - 100, height / 4 + 160 - 6, "Mod List"))
        buttonList.add(GuiButton(6, width / 2 - 100, height / 4 + 195 - 8, "Done"))
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
            Access.announcementText,
            width - 4 - Fonts.minecraftFont.getStringWidth(Access.announcementText),
            13,
            -1
        )
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> MiscUtils.showURL(Client.CLIENT_WEBSITE)
            2 -> MiscUtils.showURL(Access.discord)
            3 -> {
                Access.checkStatus()
                Access.checkLatestVersion()
                Access.getAnnouncement()
                Access.checkStaffList()
                if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
            }

            4 -> {
                LoginID.loggedIn = false
                LoginID.id = ""
                LoginID.password = ""
                LoginID.uid = ""
                //Access.userList = ""
                mc.displayGuiScreen(GuiFirstMenu(this))
                ClientUtils.getLogger().info("Logout!")
                Access.canConnect = false
            }

            5 -> {
                mc.displayGuiScreen(GuiModList(this))
            }

            6 -> mc.displayGuiScreen(prevGui)
        }
    }
}