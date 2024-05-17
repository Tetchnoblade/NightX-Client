package net.aspw.client.features.module.impl.player

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.block.BlockUtils.getBlock
import net.aspw.client.utils.block.BlockUtils.getCenterDistance
import net.aspw.client.utils.block.BlockUtils.isFullBlock
import net.aspw.client.utils.extensions.getBlock
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.ListValue
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import java.awt.Color
import java.util.*

@ModuleInfo(name = "Breaker", category = ModuleCategory.PLAYER)
class Breaker : Module() {

    /**
     * SETTINGS
     */

    private val rangeValue = FloatValue("Range", 5F, 1F, 7F, "m")
    private val actionValue = ListValue("Action", arrayOf("Destroy", "Use"), "Destroy")
    private val instantValue = BoolValue("Instant", false)
    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Packet")
    private val rotationsValue = BoolValue("Rotations", true)
    private val surroundingsValue = BoolValue("Surroundings", false)
    private val hypixelValue = BoolValue("Hypixel", false)

    /**
     * VALUES
     */

    private var pos: BlockPos? = null
    private var oldPos: BlockPos? = null
    private var blockHitDelay = 0
    private var currentDamage = 0F
    private val killAura = Launch.moduleManager.getModule(KillAura::class.java)!!
    private val scaffold = Launch.moduleManager.getModule(Scaffold::class.java)!!
    private val legitScaffold = Launch.moduleManager.getModule(LegitScaffold::class.java)!!

    override val tag: String
        get() = if (hypixelValue.get()) "Watchdog" else "Normal"

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (killAura.state && killAura.target != null || scaffold.state || legitScaffold.state) {
            currentDamage = 0F
            return
        }

        if (pos == null || Block.getIdFromBlock(getBlock(pos)) != 26 || getCenterDistance(pos!!) > rangeValue.get())
            pos = find()

        var currentPos = pos
        var rotations = RotationUtils.faceBlock(currentPos)

        if (pos == null) return

        var surroundings = false

        if (surroundingsValue.get()) {
            val eyes = mc.thePlayer.getPositionEyes(1F)
            val blockPos = mc.theWorld.rayTraceBlocks(
                eyes, rotations?.vec, false,
                false, true
            ).blockPos

            if (blockPos != null && blockPos.getBlock() !is BlockAir) {
                if (currentPos?.x != blockPos.x || currentPos.y != blockPos.y || currentPos.z != blockPos.z)
                    surroundings = true

                pos = blockPos
                currentPos = pos ?: return
                rotations = RotationUtils.faceBlock(currentPos) ?: return
            }
        }

        if (hypixelValue.get()) {
            if (Block.getIdFromBlock(getBlock(currentPos)) == 26) {
                val blockPos = currentPos?.up()
                if (getBlock(blockPos) !is BlockAir) {
                    if (currentPos?.x != blockPos?.x || currentPos?.y != blockPos?.y || currentPos?.z != blockPos?.z)
                        surroundings = true

                    pos = blockPos
                    currentPos = pos ?: return
                    rotations = RotationUtils.faceBlock(currentPos) ?: return
                }
            }
        }

        if (oldPos != null && oldPos != currentPos) {
            currentDamage = 0F
        }

        oldPos = currentPos

        if (blockHitDelay > 0) {
            blockHitDelay--
            return
        }

        if (rotationsValue.get())
            RotationUtils.setTargetRotation(rotations?.rotation!!)

        when {
            actionValue.get().equals("destroy", true) || surroundings -> {
                if (instantValue.get()) {
                    mc.netHandler.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            currentPos, EnumFacing.DOWN
                        )
                    )

                    when (swingValue.get().lowercase(Locale.getDefault())) {
                        "normal" -> mc.thePlayer.swingItem()
                        "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
                    }

                    mc.netHandler.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            currentPos, EnumFacing.DOWN
                        )
                    )
                    currentDamage = 0F
                    return
                }

                val block = currentPos?.getBlock() ?: return

                if (currentDamage == 0F) {
                    mc.netHandler.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            currentPos, EnumFacing.DOWN
                        )
                    )

                    if (mc.thePlayer.capabilities.isCreativeMode ||
                        block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos) >= 1.0F
                    ) {
                        when (swingValue.get().lowercase(Locale.getDefault())) {
                            "normal" -> mc.thePlayer.swingItem()
                            "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
                        }
                        mc.playerController.onPlayerDestroyBlock(pos, EnumFacing.DOWN)

                        currentDamage = 0F
                        pos = null
                        return
                    }
                }

                when (swingValue.get().lowercase(Locale.getDefault())) {
                    "normal" -> mc.thePlayer.swingItem()
                    "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
                }

                currentDamage += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, currentPos)
                mc.theWorld.sendBlockBreakProgress(mc.thePlayer.entityId, currentPos, (currentDamage * 10F).toInt() - 1)

                if (currentDamage >= 1F) {
                    mc.netHandler.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            currentPos, EnumFacing.DOWN
                        )
                    )
                    mc.playerController.onPlayerDestroyBlock(currentPos, EnumFacing.DOWN)
                    blockHitDelay = 4
                    currentDamage = 0F
                    pos = null
                }
            }

            actionValue.get().equals("use", true) -> if (mc.playerController.onPlayerRightClick(
                    mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem, pos, EnumFacing.DOWN,
                    Vec3(currentPos?.x?.toDouble()!!, currentPos.y.toDouble(), currentPos.z.toDouble())
                )
            ) {
                when (swingValue.get().lowercase(Locale.getDefault())) {
                    "normal" -> mc.thePlayer.swingItem()
                    "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
                }

                blockHitDelay = 4
                currentDamage = 0F
                pos = null
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        currentDamage = 0F
    }

    override fun onDisable() {
        currentDamage = 0F
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(pos ?: return, Color.WHITE, true)
    }

    private fun find(): BlockPos? {
        val radius = rangeValue.get().toInt() + 1

        var nearestBlockDistance = Double.MAX_VALUE
        var nearestBlock: BlockPos? = null

        for (x in radius downTo -radius + 1) {
            for (y in radius downTo -radius + 1) {
                for (z in radius downTo -radius + 1) {
                    val blockPos = BlockPos(mc.thePlayer).add(x, y, z)
                    val block = getBlock(blockPos) ?: continue

                    if (Block.getIdFromBlock(block) != 26) continue

                    val distance = getCenterDistance(blockPos)
                    if (distance > rangeValue.get()) continue
                    if (nearestBlockDistance < distance) continue
                    if (!isHitable(blockPos) && !surroundingsValue.get()) continue

                    nearestBlockDistance = distance
                    nearestBlock = blockPos
                }
            }
        }
        return nearestBlock
    }

    private fun isHitable(blockPos: BlockPos): Boolean {
        return !isFullBlock(blockPos.down()) || !isFullBlock(blockPos.up()) || !isFullBlock(blockPos.north()) || !isFullBlock(
            blockPos.east()
        ) || !isFullBlock(blockPos.south()) || !isFullBlock(blockPos.west())
    }
}