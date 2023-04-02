package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render2DEvent
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.block.BlockUtils.canBeClicked
import net.aspw.client.utils.block.BlockUtils.getBlock
import net.aspw.client.utils.render.ColorUtils.rainbow
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.visual.font.Fonts
import net.minecraft.block.Block
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "BlockOverlay", spacedName = "Block Overlay", category = ModuleCategory.VISUAL)
class BlockOverlay : Module() {
    private val colorRedValue = IntegerValue("R", 154, 0, 255)
    private val colorGreenValue = IntegerValue("G", 115, 0, 255)
    private val colorBlueValue = IntegerValue("B", 175, 0, 255)
    private val colorRainbow = BoolValue("Rainbow", false)
    val infoValue = BoolValue("Info", false)

    val currentBlock: BlockPos?
        get() {
            val blockPos = mc.objectMouseOver?.blockPos ?: return null

            if (canBeClicked(blockPos) && mc.theWorld.worldBorder.contains(blockPos))
                return blockPos

            return null
        }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val blockPos = currentBlock ?: return
        val block = mc.theWorld.getBlockState(blockPos).block ?: return
        val partialTicks = event.partialTicks
        val color = if (colorRainbow.get()) rainbow(0.4F) else Color(
            colorRedValue.get(),
            colorGreenValue.get(), colorBlueValue.get(), (0.4F * 255).toInt()
        )

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        RenderUtils.glColor(color)
        GL11.glLineWidth(2F)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)

        block.setBlockBoundsBasedOnState(mc.theWorld, blockPos)

        val x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks
        val y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks
        val z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks

        val axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld, blockPos)
            .expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)
            .offset(-x, -y, -z)

        RenderUtils.drawSelectionBoundingBox(axisAlignedBB)
        RenderUtils.drawFilledBox(axisAlignedBB)
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.resetColor()
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (infoValue.get()) {
            val blockPos = currentBlock ?: return
            val block = getBlock(blockPos) ?: return

            val info = "${block.localizedName} §7ID: ${Block.getIdFromBlock(block)}"
            val scaledResolution = ScaledResolution(mc)

            GlStateManager.resetColor()
            Fonts.fontSFUI40.drawCenteredString(
                info,
                scaledResolution.scaledWidth / 2F,
                scaledResolution.scaledHeight / 2F + 6F,
                Color.WHITE.rgb
            )
        }
    }
}