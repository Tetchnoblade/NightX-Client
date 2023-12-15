package net.aspw.client.visual.hud.element.elements.targets.impl

import net.aspw.client.util.render.RenderUtils
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.elements.TargetHud
import net.aspw.client.visual.hud.element.elements.targets.TargetStyle
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs

class ExhibitionOld(inst: TargetHud) : TargetStyle("ExhibitionOld", inst, true) {
    private var lastTarget: EntityPlayer? = null

    override fun drawTarget(entity: EntityPlayer) {
        val font = Fonts.minecraftFont

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
            35.5F,
            14F,
            35.5f + (easingHealth / entity.maxHealth).coerceIn(0F, entity.maxHealth) * (healthLength + 75F),
            18F,
            Color(245, 251, 1).rgb
        )
        for (i in 0..5)
            RenderUtils.drawRectBasedBorder(
                35.5F + i * 7F,
                14F,
                69.5F + (i + 1) * 7F,
                18F,
                0.5F,
                getColor(Color.black).rgb
            )

        // Name
        font.drawStringWithShadow(entity.name, 36F, 3F, Color(255, 255, 255).rgb)

        // Information
        GL11.glPushMatrix()
        GL11.glScalef(0.5F, 0.5F, 0.5F)
        font.drawStringWithShadow(
            "HP: " + entity.health.toInt() + " | Dist: " + mc.thePlayer.getDistanceToEntity(entity)
                .toInt() + " | " + entity.onGround, 72F, 44F, Color(255, 255, 255).rgb
        )
        GL11.glPopMatrix()

        GlStateManager.resetColor()
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        RenderUtils.drawEntityOnScreen(19, 45, 23, entity)
        GlStateManager.resetColor()

        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f - targetHudInstance.getFadeProgress())
        RenderHelper.enableGUIStandardItemLighting()

        val renderItem = mc.renderItem

        var x = 35
        val y = 28

        for (index in 3 downTo 0) {
            val stack = entity.inventory.armorInventory[index] ?: continue

            if (stack.item == null)
                continue

            renderItem.renderItemAndEffectIntoGUI(stack, x, y)
            RenderUtils.drawExhiEnchants(stack, x.toFloat(), y.toFloat())
            x += 16
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()

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