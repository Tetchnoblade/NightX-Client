package net.aspw.client.visual.client

import net.aspw.client.Client
import net.aspw.client.util.network.Access
import net.aspw.client.util.network.LoginID
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.visual.client.altmanager.GuiAltManager
import net.aspw.client.visual.font.semi.Fonts
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
        if (!LoginID.loggedIn)
            mc.displayGuiScreen(GuiFirstMenu(this))
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
            Client.CLIENT_BEST + " Client",
            4F.toDouble(),
            height - 12F.toDouble(),
            -1
        )
        FontLoaders.SF20.drawStringWithShadow(
            "Made by " + Client.CLIENT_CREATOR,
            width - 4F - FontLoaders.SF20.getStringWidth("Made by " + Client.CLIENT_CREATOR)
                .toDouble(),
            height - 23F.toDouble(),
            -1
        )
        FontLoaders.SF20.drawStringWithShadow(
            "Your currently build is §e" + Client.CLIENT_VERSION,
            width - 4F - FontLoaders.SF20.getStringWidth("Your currently build is §e" + Client.CLIENT_VERSION)
                .toDouble(),
            height - 12F.toDouble(),
            -1
        )
        Fonts.minecraftFont.drawString(
            "< §cAnnouncement §r>",
            width - 4 - Fonts.minecraftFont.getStringWidth("< §cAnnouncement §r>"),
            4,
            -1
        )
        Fonts.minecraftFont.drawString(
            Access.announcement,
            width - 4 - Fonts.minecraftFont.getStringWidth(Access.announcement),
            13,
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
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}