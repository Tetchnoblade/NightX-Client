package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "MurderDetector", spacedName = "Murder Detector", description = "", category = ModuleCategory.OTHER)
class MurderDetector : Module() {
    private val showText = BoolValue("Murder-ShowText", true)
    private val showTracer = BoolValue("Murder-ShowTracer", true)
    private val chatValue = BoolValue("Murder-Chat", true)
    private val notifyValue = BoolValue("Murder-Notification", true)

    private var murder1: EntityPlayer? = null
    private var murder2: EntityPlayer? = null

    override fun onDisable() {
        murder1 = null
        murder2 = null
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        murder1 = null
        murder2 = null
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            for (player in mc.theWorld.playerEntities) {
                if (mc.thePlayer.ticksExisted % 2 == 0) return
                if (player.heldItem != null && (player.heldItem.displayName.contains("Knife", ignoreCase = true) || player.heldItem.item == Items.iron_sword || player.heldItem.item == Items.stone_sword || player.heldItem.item == Items.iron_shovel || player.heldItem.item == Items.stick || player.heldItem.item == Items.wooden_axe || player.heldItem.item == Items.wooden_sword || player.heldItem.item == Items.stone_shovel || player.heldItem.item == Items.blaze_rod || player.heldItem.item == Items.diamond_shovel || player.heldItem.item == Items.shears || player.heldItem.item == Items.pumpkin_pie || player.heldItem.item == Items.golden_pickaxe || player.heldItem.item == Items.carrot_on_a_stick || player.heldItem.item == Items.cookie || player.heldItem.item == Items.diamond_axe || player.heldItem.item == Items.golden_sword || player.heldItem.item == Items.diamond_sword || player.heldItem.item == Items.diamond_hoe || player.heldItem.item == Items.shears || player.heldItem.item == Blocks.redstone_torch || player.heldItem.item == Blocks.deadbush || player.heldItem.item == Items.name_tag || player.heldItem.item == Blocks.sponge || player.heldItem.item == Items.boat || player.heldItem.item == Blocks.dragon_egg || player.heldItem.item == Items.prismarine_shard || player.heldItem.item == Items.fish || player.heldItem.item == Blocks.double_plant || player.heldItem.item == Blocks.nether_brick || player.heldItem.item == Items.cooked_beef || player.heldItem.item == Items.speckled_melon || player.heldItem.item == Items.dye || player.heldItem.item == Items.book || player.heldItem.item == Items.quartz || player.heldItem.item == Items.golden_carrot || player.heldItem.item == Items.apple || player.heldItem.item == Items.record_blocks || player.heldItem.item == Blocks.ender_chest)
                ) {
                    if (murder1 == null) {
                        if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                        }
                        if (chatValue.get())
                            chat("§e" + player.name + "§r is Murder!")
                        if (notifyValue.get())
                            Client.hud.addNotification(
                                Notification(
                                    player.name + " is Murder!",
                                    Notification.Type.INFO,
                                    6000L
                                )
                            )
                        murder1 = player
                        return
                    }
                    if (murder2 == null && player != murder1) {
                        if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                        }
                        if (chatValue.get())
                            chat("§e" + player.name + "§r is Murder!")
                        if (notifyValue.get())
                            Client.hud.addNotification(
                                Notification(
                                    player.name + " is Murder!",
                                    Notification.Type.INFO,
                                    6000L
                                )
                            )
                        murder2 = player
                    }
                }
            }
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val sc = ScaledResolution(mc)
        if (showText.get()) {
            Fonts.minecraftFont.drawString(
                if (murder1 != null) "Murder1: §e" + murder1?.name else "Murder1: §cNone",
                sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth(if (murder1 != null) "Murder1: §e" + murder1?.name else "Murder1: §cNone") / 2F,
                66.5F,
                Color(255, 255, 255).rgb,
                true
            )
            Fonts.minecraftFont.drawString(
                if (murder2 != null) "Murder2: §e" + murder2?.name else "Murder2: §cNone",
                sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth(if (murder2 != null) "Murder2: §e" + murder2?.name else "Murder2: §cNone") / 2F,
                77.5F,
                Color(255, 255, 255).rgb,
                true
            )
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (!showTracer.get()) return
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(1f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        GL11.glBegin(GL11.GL_LINES)

        for (player in mc.theWorld.loadedEntityList) {
            if (player == murder1 && player != mc.thePlayer && player != null) {
                var dist = (mc.thePlayer.getDistanceToEntity(murder1) * 2).toInt()
                if (dist > 255) dist = 255
                drawTraces(murder1, Color(255, 255, 255, 240), true)
            }
            if (player == murder2 && player != mc.thePlayer && player != null) {
                var dist = (mc.thePlayer.getDistanceToEntity(murder2) * 2).toInt()
                if (dist > 255) dist = 255
                drawTraces(murder2, Color(255, 255, 255, 240), true)
            }
        }

        GL11.glEnd()

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GlStateManager.resetColor()
    }

    private fun drawTraces(entity: EntityPlayer?, color: Color, drawHeight: Boolean) {
        val x = (entity?.lastTickPosX!! + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosX)
        val y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosY)
        val z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosZ)

        val eyeVector = Vec3(0.0, 0.0, 1.0)
            .rotatePitch((-Math.toRadians(mc.thePlayer.rotationPitch.toDouble())).toFloat())
            .rotateYaw((-Math.toRadians(mc.thePlayer.rotationYaw.toDouble())).toFloat())

        RenderUtils.glColor(color)

        GL11.glVertex3d(
            eyeVector.xCoord,
            mc.thePlayer.getEyeHeight().toDouble() + eyeVector.yCoord - 2,
            eyeVector.zCoord
        )
        if (drawHeight) {
            GL11.glVertex3d(x, y, z)
            GL11.glVertex3d(x, y, z)
            GL11.glVertex3d(x, y + entity.height, z)
        } else {
            GL11.glVertex3d(x, y + entity.height / 2.0, z)
        }
    }
}