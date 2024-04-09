package net.aspw.client.visual.client

import net.aspw.client.Launch
import net.aspw.client.utils.APIConnecter
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.visual.client.altmanager.GuiAltManager
import net.aspw.client.visual.font.smooth.FontLoaders
import net.minecraft.client.gui.*
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
                "OPTIONS"
            )
        )
        this.buttonList.add(
            GuiButton(
                4,
                this.width / 2 - 55,
                this.height / 2 - 80 + 170 - 8,
                buttonWidth,
                buttonHeight,
                "EXIT"
            )
        )
        this.buttonList.add(
            GuiButton(
                5,
                (this.width - 112F - FontLoaders.SF21.getStringWidth("Configure")).toInt(),
                8,
                buttonWidth - 42,
                buttonHeight,
                "Configure"
            )
        )
        this.buttonList.add(
            GuiButton(
                6,
                (this.width - 24F - FontLoaders.SF21.getStringWidth("Connect API")).toInt(),
                8,
                buttonWidth - 42,
                buttonHeight,
                "Connect API"
            )
        )
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
        val apiMessage = if (APIConnecter.canConnect) "§eOK" else "§cNo"
        FontLoaders.SF20.drawStringWithShadow(
            "API Connection: $apiMessage",
            (this.width - 10F - FontLoaders.SF20.getStringWidth("API Connection: $apiMessage")).toDouble(),
            32F.toDouble(),
            -1
        )
        FontLoaders.SF20.drawStringWithShadow(
            Launch.CLIENT_BEST + " Client - " + Launch.CLIENT_PROTOCOL_RANGE,
            4F.toDouble(),
            height - 12F.toDouble(),
            -1
        )
        val uiMessage =
            if (APIConnecter.canConnect && APIConnecter.isLatest) " §e(Latest)" else if (!APIConnecter.canConnect && APIConnecter.isLatest) " §c(API Dead)" else " §c(Outdated)"
        FontLoaders.SF20.drawStringWithShadow(
            "Your currently build is " + Launch.CLIENT_VERSION + uiMessage,
            width - 4F - FontLoaders.SF20.getStringWidth("Your currently build is " + Launch.CLIENT_VERSION + uiMessage)
                .toDouble(),
            height - 12F.toDouble(),
            -1
        )
        FontLoaders.SF21.drawStringWithShadow(
            "Changelogs:",
            3F.toDouble(),
            4F.toDouble(),
            -1
        )
        var changeY = 16
        val changeDetails = APIConnecter.changelogs.split("\n")
        for (i in changeDetails) {
            FontLoaders.SF20.drawStringWithShadow(
                i,
                4F.toDouble(),
                changeY.toDouble(),
                -1
            )
            changeY += 11
        }
        FontLoaders.SF21.drawStringWithShadow(
            "Known Bugs:",
            (this.width - 6F - FontLoaders.SF21.getStringWidth("Known Bugs")).toDouble(),
            48F.toDouble(),
            -1
        )
        var bugsY = 60
        val bugDetails = APIConnecter.bugs.split("\n")
        for (i in bugDetails) {
            FontLoaders.SF20.drawStringWithShadow(
                i,
                (this.width - 4F - FontLoaders.SF20.getStringWidth(i)).toDouble(),
                bugsY.toDouble(),
                -1
            )
            bugsY += 11
        }
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
            3 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            4 -> mc.shutdown()
            5 -> mc.displayGuiScreen(GuiInfo(this))

            6 -> {
                APIConnecter.checkStatus()
                APIConnecter.checkChangelogs()
                APIConnecter.checkBugs()
                APIConnecter.checkStaffList()
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}