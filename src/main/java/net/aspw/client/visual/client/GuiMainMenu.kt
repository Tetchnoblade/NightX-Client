package net.aspw.client.visual.client

import net.aspw.client.Launch
import net.aspw.client.utils.Access
import net.aspw.client.utils.misc.MiscUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.visual.client.altmanager.GuiAltManager
import net.aspw.client.visual.font.smooth.FontLoaders
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSelectWorld
import net.minecraft.client.gui.GuiYesNoCallback
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    var alpha = 255
    private var lastAnimTick: Long = 0L
    private var alrUpdate = false
    private val buttonWidth = 112
    private val buttonHeight = 20

    override fun initGui() {
        if (Access.isLatest) {
            this.buttonList.add(
                GuiButton(
                    0,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 70,
                    buttonWidth,
                    buttonHeight,
                    "SINGLE PLAYER"
                )
            )
            this.buttonList.add(
                GuiButton(
                    1,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 95 - 2,
                    buttonWidth,
                    buttonHeight,
                    "MULTI PLAYER"
                )
            )
            this.buttonList.add(
                GuiButton(
                    2,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 120 - 4,
                    buttonWidth,
                    buttonHeight,
                    "ALT MANAGER"
                )
            )
            this.buttonList.add(
                GuiButton(
                    3,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 145 - 6,
                    buttonWidth,
                    buttonHeight,
                    "CONFIGURE"
                )
            )
            this.buttonList.add(
                GuiButton(
                    4,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 170 - 8,
                    buttonWidth,
                    buttonHeight,
                    "OPTIONS"
                )
            )
            this.buttonList.add(
                GuiButton(
                    5,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 195 - 10,
                    buttonWidth,
                    buttonHeight,
                    "EXIT"
                )
            )
        } else {
            this.buttonList.add(
                GuiButton(
                    6,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 60,
                    buttonWidth,
                    buttonHeight,
                    "RECONNECT"
                )
            )
            this.buttonList.add(
                GuiButton(
                    7,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 85 - 2,
                    buttonWidth,
                    buttonHeight,
                    "UPDATE LINK"
                )
            )
            this.buttonList.add(
                GuiButton(
                    5,
                    this.width / 2 - 55,
                    this.height / 2 - 80 + 110 - 4,
                    buttonWidth,
                    buttonHeight,
                    "EXIT"
                )
            )
        }
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!alrUpdate) {
            lastAnimTick = System.currentTimeMillis()
            alrUpdate = true
        }
        GL11.glPushMatrix()
        GlStateManager.disableAlpha()
        drawBackground(0)
        RenderUtils.drawImage(
            ResourceLocation("client/background/mainmenu.png"), 0, 0,
            width, height
        )
        GlStateManager.enableAlpha()
        FontLoaders.SF20.drawStringWithShadow(
            Launch.CLIENT_BEST + " Client - " + Launch.CLIENT_PROTOCOL_RANGE,
            4F.toDouble(),
            height - 12F.toDouble(),
            -1
        )
        FontLoaders.SF20.drawStringWithShadow(
            "Made by " + Launch.CLIENT_CREATOR,
            width - 4F - FontLoaders.SF20.getStringWidth("Made by " + Launch.CLIENT_CREATOR)
                .toDouble(),
            height - 23F.toDouble(),
            -1
        )
        FontLoaders.SF20.drawStringWithShadow(
            "Your currently build is §e" + Launch.CLIENT_VERSION,
            width - 4F - FontLoaders.SF20.getStringWidth("Your currently build is §e" + Launch.CLIENT_VERSION)
                .toDouble(),
            height - 12F.toDouble(),
            -1
        )
        GlStateManager.disableAlpha()
        GlStateManager.enableAlpha()
        GL11.glPopMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(GuiSelectWorld(this))
            1 -> mc.displayGuiScreen(GuiMultiplayer(this))
            2 -> mc.displayGuiScreen(GuiAltManager(this))
            3 -> mc.displayGuiScreen(GuiInfo(this))
            4 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            5 -> mc.shutdown()

            6 -> {
                Access.checkStatus()
                Access.checkLatestVersion()
                mc.displayGuiScreen(GuiMainMenu())
            }

            7 -> MiscUtils.showURL(Access.clientGithub)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}