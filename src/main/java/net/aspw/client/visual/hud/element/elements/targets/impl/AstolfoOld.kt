package net.aspw.client.visual.hud.element.elements.targets.impl

import net.aspw.client.util.render.RenderUtils
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.elements.TargetHud
import net.aspw.client.visual.hud.element.elements.targets.TargetStyle
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs

class AstolfoOld(inst: TargetHud) : TargetStyle("AstolfoOld", inst, true) {
    private var lastTarget: EntityPlayer? = null

    override fun drawTarget(entity: EntityPlayer) {
        val font = Fonts.minecraftFont
        val healthString = decimalFormat2.format(entity.health)

        if (entity != lastTarget || easingHealth < 0 || easingHealth > entity.maxHealth ||
            abs(easingHealth - entity.health) < 0.01
        ) {
            easingHealth = entity.health
        }
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()

        updateAnim(entity.health)

        // Draw rect box
        RenderUtils.drawRect(3F, -1F, width - 3F, 47F, targetHudInstance.bgColor.rgb)

        // Health bar
        val healthLength = (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(
            36F,
            16.5F,
            36F + (easingHealth / entity.maxHealth).coerceIn(0F, entity.maxHealth) * (healthLength + 74F),
            26F,
            Color(
                245,
                entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + 91,
                1
            ).rgb
        )

        updateAnim(entity.health)
        // Name
        font.drawStringWithShadow(entity.name, 36F, 4F, Color(255, 255, 255).rgb)

        // HP
        GL11.glPushMatrix()
        font.drawStringWithShadow(
            healthString,
            64.5F,
            17F,
            Color(
                245,
                entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + entity.health.toInt() + 91,
                1
            ).rgb
        )
        GL11.glPopMatrix()

        GlStateManager.resetColor()
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        RenderUtils.drawEntityOnScreen(20, 42, 20, entity)
        GlStateManager.resetColor()

        lastTarget = entity
    }

    override fun handleBlur(entity: EntityPlayer) {
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.quickDrawRect(3F, -1F, width - 3F, 47F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun handleShadowCut(entity: EntityPlayer) = handleBlur(entity)

    override fun handleShadow(entity: EntityPlayer) {
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()

        RenderUtils.newDrawRect(3F, -1F, width - 3F, 47F, shadowOpaque.rgb)
    }

    override fun getBorder(entity: EntityPlayer?): Border {
        entity ?: return Border(0F, 0F, 118F, 32F)
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()
        return Border(3F, -1F, width - 3F, 47F)
    }
}