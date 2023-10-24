package net.aspw.client.visual.client

import net.aspw.client.Client
import net.aspw.client.util.network.CheckConnection
import net.aspw.client.util.network.LoginID
import net.aspw.client.visual.client.altmanager.GuiAltManager
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.font.smooth.FontLoaders
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Project

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    var alpha = 255
    private var lastAnimTick: Long = 0L
    private var alrUpdate = false
    private val buttonWidth = 112
    private val buttonHeight = 20
    private var panoramaTimer = 0

    override fun initGui() {
        if (!LoginID.loggedIn) {
            mc.displayGuiScreen(GuiLoginSelection(this))
        }
        this.buttonList.add(
            GuiButton(
                0,
                this.width / 2 - 55,
                this.height / 2 - 80 + 80,
                buttonWidth,
                buttonHeight,
                "SINGLEPLAYER"
            )
        )
        this.buttonList.add(
            GuiButton(
                1,
                this.width / 2 - 55,
                this.height / 2 - 80 + 105 - 2,
                buttonWidth,
                buttonHeight,
                "MULTIPLAYER"
            )
        )
        this.buttonList.add(
            GuiButton(
                2,
                this.width / 2 - 55,
                this.height / 2 - 80 + 130 - 4,
                buttonWidth,
                buttonHeight,
                "ALT MANAGER"
            )
        )
        this.buttonList.add(
            GuiButton(
                3,
                this.width / 2 - 55,
                this.height / 2 - 80 + 155 - 6,
                buttonWidth,
                buttonHeight,
                "INFO MENU"
            )
        )
        this.buttonList.add(
            GuiButton(
                4,
                this.width / 2 - 55,
                this.height / 2 - 80 + 180 - 8,
                buttonWidth,
                buttonHeight,
                "OPTIONS"
            )
        )
        this.buttonList.add(
            GuiButton(
                5,
                this.width / 2 - 55,
                this.height / 2 - 80 + 205 - 10,
                buttonWidth,
                buttonHeight,
                "EXIT"
            )
        )
        super.initGui()
    }

    private var titlePanoramaPaths = arrayOf(
        ResourceLocation("client/background/panorama_0.png"),
        ResourceLocation("client/background/panorama_1.png"),
        ResourceLocation("client/background/panorama_2.png"),
        ResourceLocation("client/background/panorama_3.png"),
        ResourceLocation("client/background/portal.png"),
        ResourceLocation("client/background/panorama_4.png")
    )

    private fun drawPanorama(p_73970_1_: Int, p_73970_2_: Int, p_73970_3_: Float) {
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        GlStateManager.matrixMode(5889)
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        Project.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f)
        GlStateManager.matrixMode(5888)
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.disableCull()
        GlStateManager.depthMask(false)
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val i = 8
        for (j in 0 until i * i) {
            GlStateManager.pushMatrix()
            val f = ((j % i).toFloat() / i.toFloat() - 0.5f) / 64.0f
            val f1 = ((j / i).toFloat() / i.toFloat() - 0.5f) / 64.0f
            val f2 = 0.0f
            GlStateManager.translate(f, f1, f2)
            GlStateManager.rotate(-(this.panoramaTimer.toFloat() + p_73970_3_) * 0.01f, 0.0f, 1.0f, 0.0f)
            for (k in 0..5) {
                GlStateManager.pushMatrix()
                if (k == 1) {
                    GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f)
                }
                if (k == 2) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
                }
                if (k == 3) {
                    GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f)
                }
                if (k == 4) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
                }
                if (k == 5) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
                }
                mc.textureManager.bindTexture(titlePanoramaPaths[k])
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
                val l = 255 / (j + 1)
                val f3 = 0.0f
                worldrenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, l).endVertex()
                worldrenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, l).endVertex()
                worldrenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, l).endVertex()
                worldrenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, l).endVertex()
                tessellator.draw()
                GlStateManager.popMatrix()
            }
            GlStateManager.popMatrix()
            GlStateManager.colorMask(true, true, true, false)
        }
        worldrenderer.setTranslation(0.0, 0.0, 0.0)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.matrixMode(5889)
        GlStateManager.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.popMatrix()
        GlStateManager.depthMask(true)
        GlStateManager.enableCull()
        GlStateManager.enableDepth()
    }

    private fun rotateAndBlurSkybox(p_73968_1_: Float) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.colorMask(true, true, true, false)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        GlStateManager.disableAlpha()
        val i = 3
        for (j in 0 until i) {
            val f = 1.0f / (j + 1).toFloat()
            val k = width
            val l = height
            val f1 = (j - i / 2).toFloat() / 256.0f
            worldrenderer.pos(k.toDouble(), l.toDouble(), zLevel.toDouble()).tex((0.0f + f1).toDouble(), 1.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
            worldrenderer.pos(k.toDouble(), 0.0, zLevel.toDouble()).tex((1.0f + f1).toDouble(), 1.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
            worldrenderer.pos(0.0, 0.0, zLevel.toDouble()).tex((1.0f + f1).toDouble(), 0.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
            worldrenderer.pos(0.0, l.toDouble(), zLevel.toDouble()).tex((0.0f + f1).toDouble(), 0.0)
                .color(1.0f, 1.0f, 1.0f, f).endVertex()
        }
        tessellator.draw()
        GlStateManager.enableAlpha()
        GlStateManager.colorMask(true, true, true, true)
    }

    private fun renderSkybox(p_73971_1_: Int, p_73971_2_: Int, p_73971_3_: Float) {
        mc.framebuffer.unbindFramebuffer()
        GlStateManager.viewport(0, 0, 256, 256)
        drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_)
        this.rotateAndBlurSkybox(p_73971_3_)
        this.rotateAndBlurSkybox(p_73971_3_)
        this.rotateAndBlurSkybox(p_73971_3_)
        this.rotateAndBlurSkybox(p_73971_3_)
        this.rotateAndBlurSkybox(p_73971_3_)
        this.rotateAndBlurSkybox(p_73971_3_)
        this.rotateAndBlurSkybox(p_73971_3_)
        mc.framebuffer.bindFramebuffer(true)
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight)
        val f = if (width > height) 120.0f / width.toFloat() else 120.0f / height.toFloat()
        val f1 = height.toFloat() * f / 256.0f
        val f2 = width.toFloat() * f / 256.0f
        val i = width
        val j = height
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(0.0, j.toDouble(), zLevel.toDouble()).tex((0.5f - f1).toDouble(), (0.5f + f2).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldrenderer.pos(i.toDouble(), j.toDouble(), zLevel.toDouble())
            .tex((0.5f - f1).toDouble(), (0.5f - f2).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldrenderer.pos(i.toDouble(), 0.0, zLevel.toDouble()).tex((0.5f + f1).toDouble(), (0.5f - f2).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        worldrenderer.pos(0.0, 0.0, zLevel.toDouble()).tex((0.5f + f1).toDouble(), (0.5f + f2).toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        tessellator.draw()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!alrUpdate) {
            lastAnimTick = System.currentTimeMillis()
            alrUpdate = true
        }
        GL11.glPushMatrix()
        drawBackground(0)
        ++this.panoramaTimer
        renderSkybox(mouseX, mouseY, partialTicks)
        GlStateManager.enableAlpha()
        FontLoaders.logo18.drawStringWithShadow("a", 60.2F.toDouble(), height - 10.3F.toDouble(), -1)
        FontLoaders.SF20.drawStringWithShadow(
            Client.CLIENT_BEST + " Client",
            4F.toDouble(),
            height - 12F.toDouble(),
            -1
        )
        if (Client.clientVersion.get() == "Release") {
            FontLoaders.SF20.drawStringWithShadow(
                "Your current build is §eLatest Release! §r(§b" + Client.CLIENT_VERSION + "§r)",
                width - 4F - FontLoaders.SF20.getStringWidth("Your current build is §eLatest Release! §r(§b" + Client.CLIENT_VERSION + "§r)")
                    .toDouble(),
                height - 12F.toDouble(),
                -1
            )
        } else if (Client.clientVersion.get() == "Beta") {
            FontLoaders.SF20.drawStringWithShadow(
                "Your current build is §eLatest Beta! §r(§b" + Client.CLIENT_VERSION + "§r)",
                width - 4F - FontLoaders.SF20.getStringWidth("Your current build is §eLatest Beta! §r(§b" + Client.CLIENT_VERSION + "§r)")
                    .toDouble(),
                height - 12F.toDouble(),
                -1
            )
        } else if (Client.clientVersion.get() == "Developer") {
            FontLoaders.SF20.drawStringWithShadow(
                "Your current build is §eLatest Developer! §r(§b" + Client.CLIENT_VERSION + "§r)",
                width - 4F - FontLoaders.SF20.getStringWidth("Your current build is §eLatest Developer! §r(§b" + Client.CLIENT_VERSION + "§r)")
                    .toDouble(),
                height - 12F.toDouble(),
                -1
            )
        }
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
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog1,
            6,
            20,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog2,
            6,
            20 + 9,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog3,
            6,
            20 + 18,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog4,
            6,
            20 + 27,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog5,
            6,
            20 + 36,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog6,
            6,
            20 + 45,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog7,
            6,
            20 + 54,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog8,
            6,
            20 + 63,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog9,
            6,
            20 + 72,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog10,
            6,
            20 + 81,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog11,
            6,
            20 + 90,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog12,
            6,
            20 + 99,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog13,
            6,
            20 + 108,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog14,
            6,
            20 + 117,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog15,
            6,
            20 + 126,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog16,
            6,
            20 + 135,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog17,
            6,
            20 + 144,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog18,
            6,
            20 + 153,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog19,
            6,
            20 + 162,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog20,
            6,
            20 + 171,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog21,
            6,
            20 + 180,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog22,
            6,
            20 + 189,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog23,
            6,
            20 + 198,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog24,
            6,
            20 + 207,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog25,
            6,
            20 + 216,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog26,
            6,
            20 + 225,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog27,
            6,
            20 + 234,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog28,
            6,
            20 + 243,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog29,
            6,
            20 + 252,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog30,
            6,
            20 + 261,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog31,
            6,
            20 + 270,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog32,
            6,
            20 + 279,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog33,
            6,
            20 + 288,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog34,
            6,
            20 + 297,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog35,
            6,
            20 + 306,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog36,
            6,
            20 + 315,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog37,
            6,
            20 + 324,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog38,
            6,
            20 + 333,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog39,
            6,
            20 + 342,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog40,
            6,
            20 + 351,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog41,
            6,
            20 + 360,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog42,
            6,
            20 + 369,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog43,
            6,
            20 + 378,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog44,
            6,
            20 + 387,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog45,
            6,
            20 + 396,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog46,
            6,
            20 + 405,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog47,
            6,
            20 + 414,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog48,
            6,
            20 + 423,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog49,
            6,
            20 + 432,
            -1
        )
        Fonts.minecraftFont.drawString(
            CheckConnection.changeLog50,
            6,
            20 + 441,
            -1
        )
        GlStateManager.scale(1.5f, 1.5f, 1.5f)
        Fonts.minecraftFont.drawString(
            "Changelog",
            4,
            4,
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