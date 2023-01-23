package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.Render3DEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.combat.AntiBot
import net.aspw.nightx.utils.EntityUtils
import net.aspw.nightx.utils.render.ColorUtils
import net.aspw.nightx.utils.render.RenderUtils.*
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.FontValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.visual.font.Fonts
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.roundToInt

@ModuleInfo(name = "NameTags", spacedName = "Name Tags", category = ModuleCategory.RENDER, array = false)
class NameTags : Module() {
    private val healthValue = BoolValue("Health", false)
    private val healthBarValue = BoolValue("Bar", true)
    private val pingValue = BoolValue("Ping", false)
    private val distanceValue = BoolValue("Distance", false)
    private val armorValue = BoolValue("Armor", true)
    private val enchantValue = BoolValue("Enchant", false, { armorValue.get() })
    private val potionValue = BoolValue("Potions", false)
    private val clearNamesValue = BoolValue("ClearNames", false)
    private val fontValue = FontValue("Font", Fonts.fontSFUI40)
    private val fontShadowValue = BoolValue("Shadow", false)
    private val borderValue = BoolValue("Border", false)
    val localValue = BoolValue("LocalPlayer", false)
    val nfpValue = BoolValue("NoFirstPerson", true, { localValue.get() })
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 80, 0, 255)
    private val borderColorRedValue = IntegerValue("Border-R", 0, 0, 255)
    private val borderColorGreenValue = IntegerValue("Border-G", 0, 0, 255)
    private val borderColorBlueValue = IntegerValue("Border-B", 0, 0, 255)
    private val borderColorAlphaValue = IntegerValue("Border-Alpha", 30, 0, 255)
    private val scaleValue = FloatValue("Scale", 1.3F, 1F, 4F, "x")

    private val inventoryBackground = ResourceLocation("textures/gui/container/inventory.png")

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        glPushAttrib(GL_ENABLE_BIT)
        glPushMatrix()

        // Disable lightning and depth test
        glDisable(GL_LIGHTING)
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_LINE_SMOOTH)

        // Enable blend
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        for (entity in mc.theWorld.loadedEntityList) {
            if (!EntityUtils.isSelected(
                    entity,
                    false
                ) && (!localValue.get() || entity != mc.thePlayer || (nfpValue.get() && mc.gameSettings.thirdPersonView == 0))
            )
                continue

            renderNameTag(
                entity as EntityLivingBase,
                if (clearNamesValue.get())
                    ColorUtils.stripColor(entity.getDisplayName().unformattedText) ?: continue
                else
                    entity.getDisplayName().unformattedText
            )
        }

        glPopMatrix()
        glPopAttrib()

        // Reset color
        resetColor()
        glColor4f(1F, 1F, 1F, 1F)
    }

    private fun renderNameTag(entity: EntityLivingBase, tag: String) {
        val fontRenderer = fontValue.get()

        // Modify tag
        val bot = AntiBot.isBot(entity)
        val nameColor = if (bot) "§3" else if (entity.isInvisible) "§6" else if (entity.isSneaking) "§4" else "§7"
        val ping = if (entity is EntityPlayer) EntityUtils.getPing(entity) else 0

        val distanceText =
            if (distanceValue.get()) "§7 [§a${mc.thePlayer.getDistanceToEntity(entity).roundToInt()}§7]" else ""
        val pingText =
            if (pingValue.get() && entity is EntityPlayer) " §7[" + (if (ping > 200) "§c" else if (ping > 100) "§e" else "§a") + ping + "ms§7]" else ""
        val healthText = if (healthValue.get()) "§7 [§f" + entity.health.toInt() + "§c❤§7]" else ""
        val botText = if (bot) " §7[§6§lBot§7]" else ""

        val text = "$nameColor$tag$healthText$distanceText$pingText$botText"

        // Push
        glPushMatrix()

        // Translate to player position
        val timer = mc.timer
        val renderManager = mc.renderManager


        glTranslated( // Translate to player position with render pos and interpolate it
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY + entity.eyeHeight.toDouble() + 0.55,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
        )

        glRotatef(-mc.renderManager.playerViewY, 0F, 1F, 0F)
        glRotatef(mc.renderManager.playerViewX, 1F, 0F, 0F)


        // Scale
        var distance = mc.thePlayer.getDistanceToEntity(entity) * 0.25f

        if (distance < 1F)
            distance = 1F

        val scale = distance / 100f * scaleValue.get()

        glScalef(-scale, -scale, scale)

        //AWTFontRenderer.assumeNonVolatile = true

        // Draw NameTag
        val width = fontRenderer.getStringWidth(text) * 0.5f

        val dist = width + 4F - (-width - 2F)

        glDisable(GL_TEXTURE_2D)
        glEnable(GL_BLEND)

        val bgColor = Color(
            backgroundColorRedValue.get(),
            backgroundColorGreenValue.get(),
            backgroundColorBlueValue.get(),
            backgroundColorAlphaValue.get()
        )
        val borderColor = Color(
            borderColorRedValue.get(),
            borderColorGreenValue.get(),
            borderColorBlueValue.get(),
            borderColorAlphaValue.get()
        )

        if (borderValue.get())
            quickDrawBorderedRect(
                -width - 2F,
                -2F,
                width + 4F,
                fontRenderer.FONT_HEIGHT + 2F + if (healthBarValue.get()) 2F else 0F,
                2F,
                borderColor.rgb,
                bgColor.rgb
            )
        else
            quickDrawRect(
                -width - 2F,
                -2F,
                width + 4F,
                fontRenderer.FONT_HEIGHT + 2F + if (healthBarValue.get()) 2F else 0F,
                bgColor.rgb
            )

        if (healthBarValue.get()) {
            quickDrawRect(
                -width - 2F,
                fontRenderer.FONT_HEIGHT + 3F,
                -width - 2F + dist,
                fontRenderer.FONT_HEIGHT + 4F,
                Color(10, 155, 10).rgb
            )
            quickDrawRect(
                -width - 2F,
                fontRenderer.FONT_HEIGHT + 3F,
                -width - 2F + (dist * (entity.health.toFloat() / entity.maxHealth.toFloat()).coerceIn(0F, 1F)),
                fontRenderer.FONT_HEIGHT + 4F,
                Color(10, 255, 10).rgb
            )
        }

        glEnable(GL_TEXTURE_2D)

        fontRenderer.drawString(
            text, 1F + -width, if (fontRenderer == Fonts.minecraftFont) 1F else 1.5F,
            0xFFFFFF, fontShadowValue.get()
        )

        //AWTFontRenderer.assumeNonVolatile = false

        var foundPotion = false
        if (potionValue.get() && entity is EntityPlayer) {
            val potions =
                (entity.getActivePotionEffects() as Collection<PotionEffect>).map { Potion.potionTypes[it.potionID] }
                    .filter { it.hasStatusIcon() }
            if (!potions.isEmpty()) {
                foundPotion = true

                color(1.0F, 1.0F, 1.0F, 1.0F)
                disableLighting()
                enableTexture2D()

                val minX = (potions.size * -20) / 2

                var index = 0

                glPushMatrix()
                enableRescaleNormal()
                for (potion in potions) {
                    color(1.0F, 1.0F, 1.0F, 1.0F)
                    mc.textureManager.bindTexture(inventoryBackground)
                    val i1 = potion.statusIconIndex
                    drawTexturedModalRect(minX + index * 20, -22, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18, 0F)
                    index++
                }
                disableRescaleNormal()
                glPopMatrix()

                enableAlpha()
                disableBlend()
                enableTexture2D()
            }
        }

        if (armorValue.get() && entity is EntityPlayer) {
            for (index in 0..4) {
                if (entity.getEquipmentInSlot(index) == null)
                    continue

                mc.renderItem.zLevel = -147F
                mc.renderItem.renderItemAndEffectIntoGUI(
                    entity.getEquipmentInSlot(index),
                    -50 + index * 20,
                    if (potionValue.get() && foundPotion) -42 else -22
                )
            }

            enableAlpha()
            disableBlend()
            enableTexture2D()
        }

        if (enchantValue.get() && entity is EntityPlayer) {
            glPushMatrix()
            for (index in 0..4) {
                if (entity.getEquipmentInSlot(index) == null)
                    continue

                mc.renderItem.renderItemOverlays(
                    mc.fontRendererObj,
                    entity.getEquipmentInSlot(index),
                    -50 + index * 20,
                    if (potionValue.get() && foundPotion) -42 else -22
                )
                drawExhiEnchants(
                    entity.getEquipmentInSlot(index),
                    -50f + index * 20f,
                    if (potionValue.get() && foundPotion) -42f else -22f
                )
            }


            // Disable lightning and depth test
            glDisable(GL_LIGHTING)
            glDisable(GL_DEPTH_TEST)

            glEnable(GL_LINE_SMOOTH)

            // Enable blend
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            glPopMatrix()
        }

        // Reset color
        resetColor()
        glColor4f(1F, 1F, 1F, 1F)

        // Pop
        glPopMatrix()
    }
}
