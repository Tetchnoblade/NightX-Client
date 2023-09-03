package net.aspw.client.visual.hud.element.elements.targets.impl

import net.aspw.client.util.render.RenderUtils
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.elements.TargetHud
import net.aspw.client.visual.hud.element.elements.targets.TargetStyle
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer

class Slowly(inst: TargetHud) : TargetStyle("Slowly", inst, true) {

    override fun drawTarget(entity: EntityPlayer) {
        val font = Fonts.minecraftFont
        val healthString = "${decimalFormat2.format(entity.health)} ❤"
        val length = 60.coerceAtLeast(font.getStringWidth(entity.name)).coerceAtLeast(font.getStringWidth(healthString))
            .toFloat() + 10F

        updateAnim(entity.health)

        RenderUtils.drawRect(0F, 0F, 32F + length, 36F, targetHudInstance.bgColor.rgb)

        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null)
            drawHead(
                mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin,
                1,
                1,
                30,
                30,
                1F - targetHudInstance.getFadeProgress()
            )

        font.drawStringWithShadow(entity.name, 33F, 2F, getColor(-1).rgb)
        font.drawStringWithShadow(
            healthString,
            length + 31F - font.getStringWidth(healthString).toFloat(),
            22F,
            targetHudInstance.barColor.rgb
        )

        RenderUtils.drawRect(
            0F,
            32F,
            (easingHealth / entity.maxHealth).coerceIn(0F, entity.maxHealth) * (length + 32F),
            36F,
            targetHudInstance.barColor.rgb
        )
    }

    override fun handleBlur(entity: EntityPlayer) {
        val font = Fonts.minecraftFont
        val healthString = "${decimalFormat2.format(entity.health)} ❤"
        val length = 60.coerceAtLeast(font.getStringWidth(entity.name)).coerceAtLeast(font.getStringWidth(healthString))
            .toFloat() + 10F

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.quickDrawRect(0F, 0F, 32F + length, 36F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun handleShadowCut(entity: EntityPlayer) = handleBlur(entity)

    override fun handleShadow(entity: EntityPlayer) {
        val font = Fonts.minecraftFont
        val healthString = "${decimalFormat2.format(entity.health)} ❤"
        val length = 60.coerceAtLeast(font.getStringWidth(entity.name)).coerceAtLeast(font.getStringWidth(healthString))
            .toFloat() + 10F

        RenderUtils.newDrawRect(0F, 0F, 32F + length, 36F, shadowOpaque.rgb)
    }

    override fun getBorder(entity: EntityPlayer?): Border {
        entity ?: return Border(0F, 0F, 102F, 36F)
        val font = Fonts.minecraftFont
        val healthString = "${decimalFormat2.format(entity.health)} ❤"
        val length = 60.coerceAtLeast(font.getStringWidth(entity.name)).coerceAtLeast(font.getStringWidth(healthString))
            .toFloat() + 10F
        return Border(0F, 0F, 32F + length, 36F)
    }

}