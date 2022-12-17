package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.JumpEvent
import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.utils.block.BlockUtils.getBlock
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.ListValue
import net.minecraft.block.BlockPane
import net.minecraft.util.BlockPos
import java.util.*

@ModuleInfo(name = "HighJump", spacedName = "High Jump", category = ModuleCategory.PLAYER)
class HighJump : Module() {
    private val heightValue = FloatValue("Height", 5f, 1.0f, 10f, "m")
    private val modeValue =
        ListValue("Mode", arrayOf("Vanilla", "Damage", "AACv3", "DAC", "Mineplex"), "Vanilla")
    private val glassValue = BoolValue("OnlyGlassPane", false)
    var tick = 0

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (glassValue.get() && getBlock(
                BlockPos(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ
                )
            ) !is BlockPane
        ) return
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "damage" -> if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) mc.thePlayer.motionY += (0.42f * heightValue.get()).toDouble()
            "aacv3" -> if (!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.059
            "dac" -> if (!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.049999
            "mineplex" -> if (!mc.thePlayer.onGround) MovementUtils.strafe(0.35f)
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        if (glassValue.get() && getBlock(
                BlockPos(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ
                )
            ) !is BlockPane
        ) return
        if (!mc.thePlayer.onGround) {
            if ("mineplex".equals(modeValue.get(), ignoreCase = true)) {
                mc.thePlayer.motionY += if (mc.thePlayer.fallDistance == 0f) 0.0499 else 0.05
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (glassValue.get() && getBlock(
                BlockPos(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ
                )
            ) !is BlockPane
        ) return
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "vanilla" -> event.motion = event.motion * heightValue.get()
            "mineplex" -> event.motion = 0.47f
        }
    }

    override val tag: String
        get() = modeValue.get()
}