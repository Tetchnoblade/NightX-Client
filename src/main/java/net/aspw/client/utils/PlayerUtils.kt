package net.aspw.client.utils

import net.aspw.client.Launch
import net.aspw.client.features.module.impl.player.AutoTool
import net.aspw.client.features.module.impl.player.LegitScaffold
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.features.module.impl.visual.Interface
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatFileWriter
import net.minecraft.util.MovementInputFromOptions
import net.minecraft.util.Vec3
import java.util.*

object PlayerUtils {

    var isSpoofing = false
    var isItemNull = false
    var itemToRender: ItemStack? = null

    private val anInterface = Launch.moduleManager.getModule(Interface::class.java)
    private val scaffold = Launch.moduleManager.getModule(Scaffold::class.java)
    private val legitScaffold = Launch.moduleManager.getModule(LegitScaffold::class.java)
    private val autoTool = Launch.moduleManager.getModule(AutoTool::class.java)

    @JvmStatic
    fun spoofItem(): ItemStack? {
        if (anInterface?.state!! && anInterface.itemVisualSpoofsValue.get()) {
            if (scaffold?.state!!) {
                isSpoofing = true
                isItemNull = MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(scaffold.lastSlot) == null
                return MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(scaffold.lastSlot)
            } else if (legitScaffold?.state!!) {
                isSpoofing = true
                isItemNull = MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(legitScaffold.lastSlot) == null
                return MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(legitScaffold.lastSlot)
            } else if (autoTool?.state!! && autoTool.isBreaking) {
                isSpoofing = true
                isItemNull = MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(autoTool.lastSlot) == null
                return MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(autoTool.lastSlot)
            }
        }
        isSpoofing = false
        isItemNull = itemToRender == null
        return itemToRender
    }

    @JvmStatic
    fun renderGuiSlot(entity: EntityPlayer): Int {
        if (anInterface?.state!! && anInterface.itemVisualSpoofsValue.get()) {
            if (scaffold?.state!!)
                return scaffold.lastSlot
            else if (legitScaffold?.state!!)
                return legitScaffold.lastSlot
            else if (autoTool?.state!! && autoTool.isBreaking)
                return autoTool.lastSlot
        }
        return if (entity == MinecraftInstance.mc.thePlayer) MinecraftInstance.mc.thePlayer.inventory.currentItem else entity.inventory.currentItem
    }

    @JvmStatic
    fun isHeldItemNull(): Boolean {
        return scaffold?.state!! && MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(scaffold.lastSlot) == null || legitScaffold?.state!! && MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(
            legitScaffold.lastSlot
        ) == null || autoTool?.state!! && autoTool.isBreaking && MinecraftInstance.mc.thePlayer.inventory.getStackInSlot(
            autoTool.lastSlot
        ) == null || MinecraftInstance.mc.thePlayer.heldItem == null
    }

    @JvmStatic
    fun cancelEquip(): Boolean {
        return scaffold?.state!! || legitScaffold?.state!!
    }
}