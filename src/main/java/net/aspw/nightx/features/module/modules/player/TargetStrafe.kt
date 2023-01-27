package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventState
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.combat.KillAura
import net.aspw.nightx.features.module.modules.movement.Flight
import net.aspw.nightx.features.module.modules.movement.Speed
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import org.lwjgl.input.Keyboard
import java.util.*

@ModuleInfo(name = "TargetStrafe", spacedName = "Target Strafe", category = ModuleCategory.PLAYER)
class TargetStrafe : Module() {
    private val modeValue = ListValue("KeyMode", arrayOf("Jump", "None"), "Jump")
    private val safewalk = BoolValue("SafeWalk", true)
    val behind = BoolValue("Behind", false)
    val thirdPerson = BoolValue("ThirdPerson", false)
    val killAura = NightX.moduleManager.getModule(KillAura::class.java)
    val speed = NightX.moduleManager.getModule(Speed::class.java)
    val longJump = NightX.moduleManager.getModule(LongJump::class.java)
    val flight = NightX.moduleManager.getModule(Flight::class.java)

    var direction = 1
    var lastView = 0
    var hasChangedThirdPerson = true

    override fun onEnable() {
        hasChangedThirdPerson = true
        lastView = mc.gameSettings.thirdPersonView
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {

        if (thirdPerson.get()) { // smart change back lol
            if (canStrafe) {
                if (hasChangedThirdPerson) lastView = mc.gameSettings.thirdPersonView
                mc.gameSettings.thirdPersonView = 1
                hasChangedThirdPerson = false
            } else if (!hasChangedThirdPerson) {
                mc.gameSettings.thirdPersonView = lastView
                hasChangedThirdPerson = true
            }
        }

        if (event.eventState == EventState.PRE) {
            if (mc.thePlayer.isCollidedHorizontally || safewalk.get() && checkVoid() && !flight!!.state)
                this.direction = -this.direction
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (canStrafe) {
            strafe(event, MovementUtils.getSpeed(event.x, event.z))

            if (safewalk.get() && checkVoid() && !flight!!.state)
                event.isSafeWalk = true
        }
    }

    fun strafe(event: MoveEvent, moveSpeed: Double) {
        if (killAura?.target == null) return
        val target = killAura.target

        val rotYaw = RotationUtils.getRotationsEntity(killAura.target).yaw

        if (mc.thePlayer.getDistanceToEntity(target) <= 1.5)
            MovementUtils.setSpeed(event, moveSpeed, rotYaw, direction.toDouble(), 0.0)
        else
            MovementUtils.setSpeed(event, moveSpeed, rotYaw, direction.toDouble(), 1.0)

        if (behind.get()) {
            val xPos: Double = target!!.posX + -Math.sin(Math.toRadians(target.rotationYaw.toDouble())) * -2
            val zPos: Double = target.posZ + Math.cos(Math.toRadians(target.rotationYaw.toDouble())) * -2
            event.x = (moveSpeed * -MathHelper.sin(
                Math.toRadians(RotationUtils.getRotations1(xPos, target.posY, zPos)[0].toDouble())
                    .toFloat()
            ))
            event.z = (moveSpeed * MathHelper.cos(
                Math.toRadians(RotationUtils.getRotations1(xPos, target.posY, zPos)[0].toDouble())
                    .toFloat()
            ))
        } else {
            if (mc.thePlayer.getDistanceToEntity(target) <= 1.5)
                MovementUtils.setSpeed(event, moveSpeed, rotYaw, direction.toDouble(), 0.0)
            else
                MovementUtils.setSpeed(event, moveSpeed, rotYaw, direction.toDouble(), 1.0)
        }
    }


    val keyMode: Boolean
        get() = when (modeValue.get().lowercase(Locale.getDefault())) {
            "jump" -> Keyboard.isKeyDown(Keyboard.KEY_SPACE)
            "none" -> mc.thePlayer.movementInput.moveStrafe != 0f || mc.thePlayer.movementInput.moveForward != 0f
            else -> false
        }

    val canStrafe: Boolean
        get() = (state && (speed!!.state || flight!!.state || longJump!!.state) && killAura!!.state && killAura.target != null && !mc.thePlayer.isSneaking && keyMode && mc.gameSettings.keyBindForward.isKeyDown && !mc.gameSettings.keyBindRight.isKeyDown && !mc.gameSettings.keyBindLeft.isKeyDown && !mc.gameSettings.keyBindBack.isKeyDown)

    private fun checkVoid(): Boolean {
        for (x in -1..0) {
            for (z in -1..0) {
                if (isVoid(x, z)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isVoid(X: Int, Z: Int): Boolean {
        if (mc.thePlayer.posY < 0.0) {
            return true
        }
        var off = 0
        while (off < mc.thePlayer.posY.toInt() + 2) {
            val bb: AxisAlignedBB = mc.thePlayer.entityBoundingBox.offset(X.toDouble(), (-off).toDouble(), Z.toDouble())
            if (mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer as Entity, bb).isEmpty()) {
                off += 2
                continue
            }
            return false
            off += 2
        }
        return true
    }
}