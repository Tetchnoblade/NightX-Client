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
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "TargetStrafe", spacedName = "Target Strafe", category = ModuleCategory.PLAYER)
class TargetStrafe : Module() {
    val radius = FloatValue("Radius", 1.5f, 0.1f, 4.0f, "m")
    private val modeValue = ListValue("KeyMode", arrayOf("Jump", "None"), "Jump")
    private val safewalk = BoolValue("SafeWalk", true)
    val thirdPerson = BoolValue("ThirdPerson", false)
    private val expMode = BoolValue("LimitSpeed", false)
    private lateinit var killAura: KillAura
    private lateinit var speed: Speed
    private lateinit var flight: Flight
    private lateinit var longJump: LongJump

    var direction = 1
    var lastView = 0
    var hasChangedThirdPerson = true

    var hasModifiedMovement = false

    override fun onInitialize() {
        killAura = NightX.moduleManager.getModule(KillAura::class.java) as KillAura
        speed = NightX.moduleManager.getModule(Speed::class.java) as Speed
        flight = NightX.moduleManager.getModule(Flight::class.java) as Flight
        longJump = NightX.moduleManager.getModule(LongJump::class.java) as LongJump
    }

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
            if (mc.thePlayer.isCollidedHorizontally)
                this.direction = -this.direction

            if (mc.gameSettings.keyBindLeft.pressed)
                this.direction = 1

            if (mc.gameSettings.keyBindRight.pressed)
                this.direction = -1
        }
    }

    @EventTarget(priority = 2)
    fun onMove(event: MoveEvent) {
        if (canStrafe) {
            if (!hasModifiedMovement) strafe(event, MovementUtils.getSpeed(event.x, event.z))

            if (safewalk.get() && checkVoid())
                event.isSafeWalk = true
        }
        hasModifiedMovement = false
    }

    fun strafe(event: MoveEvent, moveSpeed: Double) {
        if (killAura.target == null) return

        val target = killAura.target!!
        val rotYaw = RotationUtils.getRotationsEntity(target).yaw

        val forward = if (mc.thePlayer.getDistanceToEntity(target) <= radius.get()) 0.0 else 1.0
        val strafe = direction.toDouble()
        var modifySpeed = if (expMode.get()) maximizeSpeed(target, moveSpeed, killAura.rangeValue.get()) else moveSpeed

        MovementUtils.setSpeed(event, modifySpeed, rotYaw, strafe, forward)
        hasModifiedMovement = true
    }

    fun getData(): Array<Float> {
        if (killAura.target == null) return arrayOf(0F, 0F, 0F)

        val target = killAura.target!!
        val rotYaw = RotationUtils.getRotationsEntity(target).yaw

        val forward = if (mc.thePlayer.getDistanceToEntity(target) <= radius.get()) 0F else 1F
        val strafe = direction.toFloat()

        return arrayOf(rotYaw, strafe, forward)
    }

    fun getMovingYaw(): Float {
        val dt = getData()
        return MovementUtils.getRawDirectionRotation(dt[0], dt[1], dt[2])
    }

    fun getMovingDir(): Double {
        val dt = getData()
        return MovementUtils.getDirectionRotation(dt[0], dt[1], dt[2])
    }

    private fun maximizeSpeed(ent: EntityLivingBase, speed: Double, range: Float): Double {
        mc.thePlayer ?: return 0.0
        return if (mc.thePlayer.getDistanceToEntity(ent) <= radius.get()) speed.coerceIn(0.0, range.toDouble() / 20.0) else speed
    }

    val keyMode: Boolean
        get() = when (modeValue.get().toLowerCase()) {
            "jump" -> Keyboard.isKeyDown(Keyboard.KEY_SPACE)
            "none" -> mc.thePlayer.movementInput.moveStrafe != 0f || mc.thePlayer.movementInput.moveForward != 0f
            else -> false
        }

    val canStrafe: Boolean
        get() = (state && (speed.state || flight.state || longJump.state) && killAura.state && killAura.target != null && !mc.thePlayer.isSneaking && keyMode)

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
        }
        return true
    }
}