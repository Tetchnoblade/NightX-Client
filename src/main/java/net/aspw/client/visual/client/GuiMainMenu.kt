package net.aspw.client.visual.client

import net.aspw.client.Launch
import net.aspw.client.utils.Access
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
        if (Access.canConnect) {
            FontLoaders.SF20.drawStringWithShadow(
                "API Connection: §eOK",
                (this.width - 10F - FontLoaders.SF20.getStringWidth("API Connection: §eOK")).toDouble(),
                32F.toDouble(),
                -1
            )
            FontLoaders.SF20.drawStringWithShadow(
                "Every modules, commands are working correctly",
                (this.width - 10F - FontLoaders.SF20.getStringWidth("Every modules, commands are working correctly")).toDouble(),
                42F.toDouble(),
                -1
            )
        } else {
            FontLoaders.SF20.drawStringWithShadow(
                "API Connection: §cNo",
                (this.width - 10F - FontLoaders.SF20.getStringWidth("API Connection: §cNo")).toDouble(),
                32F.toDouble(),
                -1
            )
            FontLoaders.SF20.drawStringWithShadow(
                "Some modules, commands are not working",
                (this.width - 10F - FontLoaders.SF20.getStringWidth("Some modules, commands are not working")).toDouble(),
                42F.toDouble(),
                -1
            )
        }
        FontLoaders.SF20.drawStringWithShadow(
            Launch.CLIENT_BEST + " Client - " + Launch.CLIENT_PROTOCOL_RANGE,
            4F.toDouble(),
            height - 12F.toDouble(),
            -1
        )
        val uiMessage =
            if (Access.canConnect && Access.isLatest) " §e(Latest)" else if (!Access.canConnect && Access.isLatest) " §c(API Dead)" else " §c(Outdated)"
        FontLoaders.SF20.drawStringWithShadow(
            "Your currently build is " + Launch.CLIENT_VERSION + uiMessage,
            width - 4F - FontLoaders.SF20.getStringWidth("Your currently build is " + Launch.CLIENT_VERSION + uiMessage)
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
            3 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            4 -> mc.shutdown()
            5 -> mc.displayGuiScreen(GuiInfo(this))

            6 -> {
                Access.checkStatus()
                Access.checkStaffList()
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}