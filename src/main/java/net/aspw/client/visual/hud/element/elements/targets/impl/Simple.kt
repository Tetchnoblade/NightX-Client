package net.aspw.client.visual.hud.element.elements.targets.impl

import net.aspw.client.utils.extensions.darker
import net.aspw.client.utils.render.BlendUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.elements.Target
import net.aspw.client.visual.hud.element.elements.targets.TargetStyle
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs

class Simple(inst: Target) : TargetStyle("Simple", inst, true) {
    private var lastTarget: EntityPlayer? = null

    override fun drawTarget(entity: EntityPlayer) {
        val font = Fonts.minecraftFont
        val healthString = "${decimalFormat2.format(entity.health)} "

        if (entity != lastTarget || easingHealth < 0 || easingHealth > entity.maxHealth ||
            abs(easingHealth - entity.health) < 0.01
        ) {
            easingHealth = entity.health
        }
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()

        // Draw rect box
        RenderUtils.drawRect(6F, -1F, width - 6F, 32F, targetInstance.bgColor.rgb)

        // Health bar
        val barLength = 69F * (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(
            37F,
            26F,
            40F + 69F,
            30F,
            getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth).darker(0.3F)).rgb
        )
        RenderUtils.drawRect(
            37F,
            26F,
            40F + barLength,
            30F,
            getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth)).rgb
        )

        updateAnim(entity.health)
        // Name
        Fonts.minecraftFont.drawStringWithShadow(entity.name, 37F, 4F, getColor(-1).rgb)

        // HP
        GL11.glPushMatrix()
        GL11.glScalef(1F, 1F, 1F)
        font.drawStringWithShadow(healthString + "HP", 37F, 16.6F, Color(255, 255, 255).rgb)
        GL11.glPopMatrix()

        GlStateManager.resetColor()
        RenderUtils.drawEntityOnScreen(22, 30, 15, entity)

        lastTarget = entity
    }

    override fun handleBlur(entity: EntityPlayer) {
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.quickDrawRect(0F, 0F, width, 32F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

    }

    override fun handleShadowCut(entity: EntityPlayer) = handleBlur(entity)

    override fun handleShadow(entity: EntityPlayer) {
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()

        RenderUtils.newDrawRect(0F, 0F, width, 32F, shadowOpaque.rgb)
    }

    override fun getBorder(entity: EntityPlayer?): Border {
        entity ?: return Border(0F, 0F, 118F, 32F)
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()
        return Border(0F, 0F, width, 32F)
    }
}