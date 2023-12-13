package net.aspw.client.visual.hud.element.elements.targets.impl

import net.aspw.client.util.extensions.darker
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

class New(inst: TargetHud) : TargetStyle("New", inst, true) {
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

        updateAnim(entity.health)

        // Draw rect box
        RenderUtils.drawRect(-12F, -1F, width - 3F, 56F, targetHudInstance.bgColor.rgb)

        // Health bar
        val healthLength = 69F * (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(
            36F,
            32F,
            41.5F + 69F,
            35.5F,
            targetHudInstance.barColor.darker(0.3f)
        )
        RenderUtils.drawRect(
            36F,
            32F,
            (easingHealth / entity.maxHealth).coerceIn(0F, entity.maxHealth) * (healthLength + 41.5F),
            35.5F,
            targetHudInstance.barColor.rgb
        )

        // Armor bar
        val armorLength = 68.5F * (entity.totalArmorValue.toFloat() / 20F).coerceIn(0F, 1F)
        RenderUtils.drawRect(
            36F,
            36F,
            42F + 68.5F,
            39.5F,
            getColor(Color.white.darker()).rgb
        )
        RenderUtils.drawRect(
            36F,
            36F,
            (easingHealth / entity.maxHealth).coerceIn(0F, entity.maxHealth) * (armorLength + 42F),
            39.5F,
            getColor(Color.white).rgb
        )

        updateAnim(entity.health)
        // Name
        font.drawStringWithShadow(entity.name, 36F, 4F, Color(210, 210, 210).rgb)

        // HP
        GL11.glPushMatrix()
        GL11.glScalef(1.5F, 1.5F, 1.5F)
        font.drawStringWithShadow("$healthString❤", 24F, 11F, targetHudInstance.barColor.rgb)
        GL11.glPopMatrix()

        GlStateManager.resetColor()
        RenderUtils.drawEntityOnScreen(11, 53, 26, entity)

        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f - targetHudInstance.getFadeProgress())
        RenderHelper.enableGUIStandardItemLighting()

        val renderItem = mc.renderItem

        var x = 34
        val y = 39

        for (index in 3 downTo 0) {
            val stack = entity.inventory.armorInventory[index] ?: continue

            if (stack.item == null)
                continue

            renderItem.renderItemAndEffectIntoGUI(stack, x, y)
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
        RenderUtils.quickDrawRect(-12F, -1F, width - 3F, 56F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun handleShadowCut(entity: EntityPlayer) = handleBlur(entity)

    override fun handleShadow(entity: EntityPlayer) {
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()

        RenderUtils.newDrawRect(-12F, -1F, width - 3F, 56F, shadowOpaque.rgb)
    }

    override fun getBorder(entity: EntityPlayer?): Border {
        entity ?: return Border(0F, 0F, 118F, 32F)
        val width = (38 + Fonts.minecraftFont.getStringWidth(entity.name))
            .coerceAtLeast(118)
            .toFloat()
        return Border(-12F, -1F, width - 3F, 56F)
    }
}