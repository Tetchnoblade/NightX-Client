package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.AxisAlignedBB

@ModuleInfo(name = "TargetStrafe", spacedName = "Target Strafe", category = ModuleCategory.MOVEMENT)
class TargetStrafe : Module() {
    val radius = FloatValue("Radius", 1.5f, 0.1f, 4.0f, "m")
    private val modeValue = ListValue("KeyMode", arrayOf("Jump", "None"), "Jump")
    private val safewalk = BoolValue("SafeWalk", true)
    val thirdPerson = BoolValue("ThirdPerson", false)
    private val expMode = BoolValue("LimitSpeed", false)
    private lateinit var killAura: KillAura
    private lateinit var speed: Speed
    private lateinit var fly: Flight

    var direction = 1
    var lastView = 0
    var hasChangedThirdPerson = true

    var hasModifiedMovement = false

    override fun onInitialize() {
        killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        speed = LiquidBounce.moduleManager.getModule(Speed::class.java) as Speed
        fly = LiquidBounce.moduleManager.getModule(Flight::class.java) as Flight
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
            "jump" -> mc.gameSettings.keyBindJump.isKeyDown
            "none" -> mc.thePlayer.movementInput.moveStrafe != 0f || mc.thePlayer.movementInput.moveForward != 0f
            else -> false
        }

    val canStrafe: Boolean
        get() = (state && (speed.state || fly.state) && killAura.state && killAura.target != null && !mc.thePlayer.isSneaking && keyMode)

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