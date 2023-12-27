package net.aspw.client.visual.client

import net.aspw.client.Client
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.util.ClientUtils
import net.aspw.client.util.MinecraftInstance
import net.aspw.client.util.network.Access
import net.aspw.client.util.network.LoginID
import net.aspw.client.util.network.LoginID.id
import net.aspw.client.util.network.LoginID.loggedIn
import net.aspw.client.util.network.getHWID
import net.aspw.client.util.render.RenderUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

class GuiFirstMenu(private val prevGui: GuiScreen) : GuiScreen() {
    //private lateinit var username: GuiTextField
    //private lateinit var password: GuiTextField
    //private lateinit var uid: GuiTextField

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        //username = GuiTextField(2, mc.fontRendererObj, width / 2 - 96, 65, 200, 20)
        //password = GuiPasswordField(3, mc.fontRendererObj, width / 2 - 96, 100, 200, 20)
        //uid = GuiTextField(6, mc.fontRendererObj, width / 2 - 96, 135, 200, 20)
        //username.text = id
        //username.maxStringLength = Int.MAX_VALUE
        //uid.text = LoginID.uid
        //uid.maxStringLength = 3
        //password.text = LoginID.password
        //password.maxStringLength = 20
        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 105, "Login"))
        buttonList.add(GuiButton(20, width / 2 - 100, height / 4 + 145, "Exit"))
        //buttonList.add(GuiButton(5, width / 2 - 100, height / 4 + 145, "Copy HWID"))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        RenderUtils.drawImage(
            ResourceLocation("client/background/portal.png"), 0, 0,
            width, height
        )
        //this.drawCenteredString(mc.fontRendererObj, "Login with your account", width / 2, 12, 0xffffff)
        //this.drawCenteredString(
        //    mc.fontRendererObj,
        //    "You can login with use username & password or uid!",
        //    width / 2,
        //    28,
        //    0xffffff
        //)
        //if (!loggedIn)
        //    this.drawCenteredString(mc.fontRendererObj, "waiting for authentication...", width / 2, 44, 0xffffff)
        //else
        //    this.drawCenteredString(mc.fontRendererObj, "You have been logged in!", width / 2, 44, 0xffffff)
        //username.drawTextBox()
        //password.drawTextBox()
        //uid.drawTextBox()
        //if (username.text.isNotEmpty() || password.text.isNotEmpty())
        //    uid.text = ""
        //if (username.text.isEmpty() && !username.isFocused) {
        //    drawString(mc.fontRendererObj, "§7Username", width / 2 - 92, 65 + 6, 0xffffff)
        //}
        //if (uid.text.isEmpty() && !uid.isFocused) {
        //    drawString(mc.fontRendererObj, "§7UID", width / 2 - 92, 135 + 6, 0xffffff)
        //}
        //if (password.text.isEmpty()) {
        //    if (!password.isFocused)
        //        drawString(mc.fontRendererObj, "§7Password", width / 2 - 92, 100 + 6, 0xffffff)
        //}
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> {
                Access.checkLatestVersion()
                Access.getAnnouncement()
                if (!Access.isLatest) return
                id = "User"
                LoginID.password = "aiueo700"
                LoginID.hwid = "User"
                LoginID.uid = "000"
                Access.canConnect = true
                loggedIn = true
                if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
                ClientUtils.getLogger().info("Logged in with Your Account!")
                MinecraftInstance.mc.displayGuiScreen(GuiMainMenu())

                //Access.getUserList()
                //authenticate(Access.userList, username.text, password.text, LoginID.currentHWID, uid.text)
            }

            20 -> {
                mc.shutdown()
            }

            5 -> {
                getHWID()
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            if (!loggedIn) {
                mc.displayGuiScreen(GuiFirstMenu(this))
                return
            } else mc.displayGuiScreen(GuiMainMenu())
        }

        //if (username.isFocused && !loggedIn) {
        //    username.textboxKeyTyped(typedChar, keyCode)
        //}
        //if (password.isFocused && !loggedIn) {
        //    password.textboxKeyTyped(typedChar, keyCode)
        //}
        //if ((username.text.isEmpty() || password.text.isEmpty()) && uid.isFocused && !loggedIn) {
        //    uid.textboxKeyTyped(typedChar, keyCode)
        //}

        super.keyTyped(typedChar, keyCode)
    }

}