package net.aspw.client.utils

import net.aspw.client.Launch
import net.aspw.client.event.*
import net.aspw.client.features.module.impl.combat.FastBow
import net.aspw.client.features.module.impl.visual.SilentRotations
import net.aspw.client.utils.misc.RandomUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.*
import org.lwjgl.opengl.Display
import java.util.*
import kotlin.math.*

/**
 * The type Rotation utils.
 */
class RotationUtils : MinecraftInstance(), Listenable {
    /**
     * On tick.
     *
     * @param event the event
     */
    @EventTarget
    fun onTick(event: TickEvent?) {
        if (targetRotation != null) {
            keepLength--
            if (keepLength <= 0) reset()
        }
        if (random.nextGaussian() > 0.8) x = Math.random()
        if (random.nextGaussian() > 0.8) y = Math.random()
        if (random.nextGaussian() > 0.8) z = Math.random()
    }

    /**
     * On packet.
     *
     * @param event the event
     */
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            if (targetRotation != null && !keepCurrentRotation && (targetRotation?.yaw !== serverRotation?.yaw || targetRotation?.pitch !== serverRotation?.pitch)) {
                packet.yaw = targetRotation?.yaw!!
                packet.pitch = targetRotation?.pitch!!
                packet.rotating = true
            }

            if (packet.rotating) serverRotation = Rotation(packet.yaw, packet.pitch)
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (Launch.moduleManager.getModule(SilentRotations::class.java)?.rotating!! && Launch.moduleManager.getModule(
                SilentRotations::class.java
            )?.state!! && Launch.moduleManager.getModule(SilentRotations::class.java)?.customStrafe?.get()!!
        )
            event.yaw = cameraYaw
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (Launch.moduleManager.getModule(SilentRotations::class.java)?.rotating!! && Launch.moduleManager.getModule(
                SilentRotations::class.java
            )?.state!! && Launch.moduleManager.getModule(SilentRotations::class.java)?.customStrafe?.get()!!
        )
            event.yaw = cameraYaw
    }

    override fun handleEvents(): Boolean {
        return true
    }

    companion object {
        private val random = Random()

        /**
         * The constant targetRotation.
         */
        @JvmField
        var targetRotation: Rotation? = null

        /**
         * The constant serverRotation.
         */
        @JvmField
        var serverRotation: Rotation? = Rotation(30f, 30f)

        /**
         * The constant keepCurrentRotation.
         */
        var keepCurrentRotation = false
        private var keepLength = 0
        private var x = random.nextDouble()
        private var y = random.nextDouble()
        private var z = random.nextDouble()

        @JvmField
        var perspectiveToggled = false

        @JvmField
        var cameraYaw = 0f

        @JvmField
        var cameraPitch = 0f

        @JvmField
        var prevCameraYaw = 0f

        @JvmField
        var prevCameraPitch = 0f

        @JvmStatic
        fun overrideMouse(): Boolean {
            if (mc.inGameHasFocus && Display.isActive()) {
                if (!perspectiveToggled) {
                    return true
                }
                mc.mouseHelper.mouseXYChange()
                val f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f
                val f2 = f1 * f1 * f1 * 8.0f
                val f3 = mc.mouseHelper.deltaX * f2
                val f4 = mc.mouseHelper.deltaY * f2
                prevCameraYaw = cameraYaw
                prevCameraPitch = cameraPitch
                cameraYaw += f3 * 0.15f
                cameraPitch -= f4 * 0.15f
                if (cameraPitch > 90) cameraPitch = 90f
                if (cameraPitch < -90) cameraPitch = -90f
            }
            return false
        }

        fun enableLook() {
            if (!Launch.isStarting) {
                perspectiveToggled = true
                if (mc.thePlayer != null) {
                    cameraYaw = mc.thePlayer.rotationYaw
                    cameraPitch = mc.thePlayer.rotationPitch
                }
            }
        }

        fun disableLook() {
            if (!Launch.isStarting) {
                perspectiveToggled = false
                if (mc.thePlayer != null) {
                    mc.thePlayer.rotationYaw = cameraYaw
                    mc.thePlayer.rotationPitch = cameraPitch
                }
            }
        }

        /**
         * Other rotation rotation.
         *
         * @param bb           the bb
         * @param vec          the vec
         * @param predict      the predict
         * @param throughWalls the through walls
         * @param distance     the distance
         * @return the rotation
         */
        fun OtherRotation(
            bb: AxisAlignedBB,
            vec: Vec3,
            predict: Boolean,
            throughWalls: Boolean,
            distance: Float
        ): Rotation {
            val eyesPos = Vec3(
                mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY +
                        mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ
            )
            val eyes = mc.thePlayer.getPositionEyes(1f)
            var vecRotation: VecRotation? = null
            var xSearch = 0.15
            while (xSearch < 0.85) {
                var ySearch = 0.15
                while (ySearch < 1.0) {
                    var zSearch = 0.15
                    while (zSearch < 0.85) {
                        val vec3 = Vec3(
                            bb.minX + (bb.maxX - bb.minX) * xSearch,
                            bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch
                        )
                        val rotation = toRotation(vec3, predict)
                        val vecDist = eyes.distanceTo(vec3)
                        if (vecDist > distance) {
                            zSearch += 0.1
                            continue
                        }
                        if (throughWalls || isVisible(vec3)) {
                            val currentVec = VecRotation(vec3, rotation)
                            if (vecRotation == null || getRotationDifference(currentVec.rotation) < getRotationDifference(
                                    vecRotation.rotation
                                )
                            )
                                vecRotation = currentVec
                        }
                        zSearch += 0.1
                    }
                    ySearch += 0.1
                }
                xSearch += 0.1
            }
            if (predict) eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ)
            val diffX = vec.xCoord - eyesPos.xCoord
            val diffY = vec.yCoord - eyesPos.yCoord
            val diffZ = vec.zCoord - eyesPos.zCoord
            return Rotation(
                MathHelper.wrapAngleTo180_float(
                    Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90f
                ),
                MathHelper.wrapAngleTo180_float(
                    (-Math.toDegrees(
                        atan2(
                            diffY,
                            sqrt(diffX * diffX + diffZ * diffZ)
                        )
                    )).toFloat()
                )
            )
        }

        /**
         * Face block vec rotation.
         *
         * @param blockPos the block pos
         * @return the vec rotation
         */
        fun faceBlock(blockPos: BlockPos?): VecRotation? {
            if (blockPos == null) return null
            var vecRotation: VecRotation? = null
            var xSearch = 0.1
            while (xSearch < 0.9) {
                var ySearch = 0.1
                while (ySearch < 0.9) {
                    var zSearch = 0.1
                    while (zSearch < 0.9) {
                        val eyesPos = Vec3(
                            mc.thePlayer.posX,
                            mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
                            mc.thePlayer.posZ
                        )
                        val posVec = Vec3(blockPos).addVector(xSearch, ySearch, zSearch)
                        val dist = eyesPos.distanceTo(posVec)
                        val diffX = posVec.xCoord - eyesPos.xCoord
                        val diffY = posVec.yCoord - eyesPos.yCoord
                        val diffZ = posVec.zCoord - eyesPos.zCoord
                        val diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ).toDouble()
                        val rotation = Rotation(
                            MathHelper.wrapAngleTo180_float(Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90f),
                            MathHelper.wrapAngleTo180_float(-Math.toDegrees(atan2(diffY, diffXZ)).toFloat())
                        )
                        val rotationVector = getVectorForRotation(rotation)
                        val vector = eyesPos.addVector(
                            rotationVector.xCoord * dist, rotationVector.yCoord * dist,
                            rotationVector.zCoord * dist
                        )
                        val obj = mc.theWorld.rayTraceBlocks(
                            eyesPos, vector, false,
                            false, true
                        )
                        if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                            val currentVec = VecRotation(posVec, rotation)
                            if (vecRotation == null || getRotationDifference(currentVec.rotation) < getRotationDifference(
                                    vecRotation.rotation
                                )
                            ) vecRotation = currentVec
                        }
                        zSearch += 0.1
                    }
                    ySearch += 0.1
                }
                xSearch += 0.1
            }
            return vecRotation
        }

        /**
         * @param target      the target
         * @param silent      the silent
         * @param predict     the predict
         * @param predictSize the predict size
         */
        fun faceLook(target: Entity, minTurnSpeed: Float, maxTurnSpeed: Float) {
            val player = mc.thePlayer
            val posX: Double =
                target.posX + 0.toDouble() - player.posX
            val posY: Double =
                target.entityBoundingBox.minY + target.eyeHeight - 0.15 - player.entityBoundingBox.minY - player.getEyeHeight()
            val posZ: Double =
                target.posZ + 0.toDouble() - player.posZ
            val posSqrt = sqrt(posX * posX + posZ * posZ)
            var velocity = 1f
            velocity = (velocity * velocity + velocity * 2) / 3
            if (velocity > 1) velocity = 1f
            val rotation = Rotation(
                (atan2(posZ, posX) * 180 / Math.PI).toFloat() - 90,
                -Math.toDegrees(atan((velocity * velocity - sqrt(velocity * velocity * velocity * velocity - 0.006f * (0.006f * (posSqrt * posSqrt) + 2 * posY * (velocity * velocity)))) / (0.006f * posSqrt)))
                    .toFloat()
            )
            setTargetRotation(
                limitAngleChange(
                    serverRotation!!,
                    rotation,
                    RandomUtils.nextFloat(minTurnSpeed, maxTurnSpeed)
                )
            )
        }

        /**
         * Face bow.
         *
         * @param target      the target
         * @param silent      the silent
         * @param predict     the predict
         * @param predictSize the predict size
         */
        fun faceBow(target: Entity, silent: Boolean, predict: Boolean, predictSize: Float) {
            val player = mc.thePlayer
            val posX: Double =
                target.posX + (if (predict) (target.posX - target.prevPosX) * predictSize else 0.toDouble()) - (player.posX + if (predict) player.posX - player.prevPosX else 0.toDouble())
            val posY: Double =
                target.entityBoundingBox.minY + (if (predict) (target.entityBoundingBox.minY - target.prevPosY) * predictSize else 0.toDouble()) + target.eyeHeight - 0.15 - (player.entityBoundingBox.minY + if (predict) player.posY - player.prevPosY else 0.toDouble()) - player.getEyeHeight()
            val posZ: Double =
                target.posZ + (if (predict) (target.posZ - target.prevPosZ) * predictSize else 0.toDouble()) - (player.posZ + if (predict) player.posZ - player.prevPosZ else 0.toDouble())
            val posSqrt = sqrt(posX * posX + posZ * posZ)
            var velocity =
                if (Launch.moduleManager.getModule(FastBow::class.java)!!.state) 1f else player.itemInUseDuration / 20f
            velocity = (velocity * velocity + velocity * 2) / 3
            if (velocity > 1) velocity = 1f
            val rotation = Rotation(
                (atan2(posZ, posX) * 180 / Math.PI).toFloat() - 90,
                -Math.toDegrees(atan((velocity * velocity - sqrt(velocity * velocity * velocity * velocity - 0.006f * (0.006f * (posSqrt * posSqrt) + 2 * posY * (velocity * velocity)))) / (0.006f * posSqrt)))
                    .toFloat()
            )
            if (silent) setTargetRotation(rotation) else limitAngleChange(
                Rotation(player.rotationYaw, player.rotationPitch), rotation, (10 +
                        Random().nextInt(6)).toFloat()
            ).toPlayer(mc.thePlayer)
        }

        /**
         * To rotation rotation.
         *
         * @param vec     the vec
         * @param predict the predict
         * @return the rotation
         */
        fun toRotation(vec: Vec3, predict: Boolean): Rotation {
            val eyesPos = Vec3(
                mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY +
                        mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ
            )
            if (predict) eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ)
            val diffX = vec.xCoord - eyesPos.xCoord
            val diffY = vec.yCoord - eyesPos.yCoord
            val diffZ = vec.zCoord - eyesPos.zCoord
            return Rotation(
                MathHelper.wrapAngleTo180_float(
                    Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90f
                ),
                MathHelper.wrapAngleTo180_float(
                    (-Math.toDegrees(
                        atan2(
                            diffY,
                            sqrt(diffX * diffX + diffZ * diffZ)
                        )
                    )).toFloat()
                )
            )
        }

        /**
         * Gets center.
         *
         * @param bb the bb
         * @return the center
         */
        fun getCenter(bb: AxisAlignedBB): Vec3 {
            return Vec3(
                bb.minX + (bb.maxX - bb.minX) * 0.5,
                bb.minY + (bb.maxY - bb.minY) * 0.5,
                bb.minZ + (bb.maxZ - bb.minZ) * 0.5
            )
        }

        /**
         * Round rotation float.
         *
         * @param yaw      the yaw
         * @param strength the strength
         * @return the float
         */
        fun roundRotation(yaw: Float, strength: Int): Float {
            return ((yaw / strength).roundToInt() * strength).toFloat()
        }

        /**
         * Gets rotation difference.
         *
         * @param entity the entity
         * @return the rotation difference
         */
        fun getRotationDifference(entity: Entity): Double {
            val rotation = toRotation(getCenter(entity.entityBoundingBox), true)
            return getRotationDifference(rotation, Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch))
        }

        /**
         * Gets rotation back difference.
         *
         * @param entity the entity
         * @return the rotation back difference
         */
        fun getRotationBackDifference(entity: Entity): Double {
            val rotation = toRotation(getCenter(entity.entityBoundingBox), true)
            return getRotationDifference(rotation, Rotation(mc.thePlayer.rotationYaw - 180, mc.thePlayer.rotationPitch))
        }

        /**
         * Gets rotation difference.
         *
         * @param rotation the rotation
         * @return the rotation difference
         */
        @JvmStatic
        fun getRotationDifference(rotation: Rotation): Double {
            return if (serverRotation == null) 0.0 else getRotationDifference(rotation, serverRotation)
        }

        /**
         * Gets rotation difference.
         *
         * @param a the a
         * @param b the b
         * @return the rotation difference
         */
        private fun getRotationDifference(a: Rotation, b: Rotation?): Double {
            return hypot(getAngleDifference(a.yaw, b!!.yaw).toDouble(), (a.pitch - b.pitch).toDouble())
        }

        /**
         * Limit angle change rotation.
         *
         * @param currentRotation the current rotation
         * @param targetRotation  the target rotation
         * @param turnSpeed       the turn speed
         * @return the rotation
         */
        fun limitAngleChange(currentRotation: Rotation, targetRotation: Rotation, turnSpeed: Float): Rotation {
            val yawDifference = getAngleDifference(targetRotation.yaw, currentRotation.yaw)
            val pitchDifference = getAngleDifference(targetRotation.pitch, currentRotation.pitch)
            return Rotation(
                currentRotation.yaw + if (yawDifference > turnSpeed) turnSpeed else yawDifference.coerceAtLeast(-turnSpeed),
                currentRotation.pitch + if (pitchDifference > turnSpeed) turnSpeed else pitchDifference.coerceAtLeast(-turnSpeed)
            )
        }

        private fun getAngleDifference(a: Float, b: Float): Float {
            return ((a - b) % 360f + 540f) % 360f - 180f
        }

        /**
         * Gets vector for rotation.
         *
         * @param rotation the rotation
         * @return the vector for rotation
         */
        fun getVectorForRotation(rotation: Rotation): Vec3 {
            val yawCos = MathHelper.cos(-rotation.yaw * 0.017453292f - Math.PI.toFloat())
            val yawSin = MathHelper.sin(-rotation.yaw * 0.017453292f - Math.PI.toFloat())
            val pitchCos = -MathHelper.cos(-rotation.pitch * 0.017453292f)
            val pitchSin = MathHelper.sin(-rotation.pitch * 0.017453292f)
            return Vec3((yawSin * pitchCos).toDouble(), pitchSin.toDouble(), (yawCos * pitchCos).toDouble())
        }

        /**
         * Is visible boolean.
         *
         * @param vec3 the vec 3
         * @return the boolean
         */
        private fun isVisible(vec3: Vec3?): Boolean {
            val eyesPos = Vec3(
                mc.thePlayer.posX,
                mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
                mc.thePlayer.posZ
            )
            return mc.theWorld.rayTraceBlocks(eyesPos, vec3) == null
        }

        /**
         * Sets target rotation.
         *
         * @param rotation the rotation
         */
        fun setTargetRotation(rotation: Rotation) {
            setTargetRotation(rotation, 0)
        }

        /**
         * Sets target rotation.
         *
         * @param rotation   the rotation
         * @param keepLength the keep length
         */
        fun setTargetRotation(rotation: Rotation, keepLength: Int) {
            try {
                if (java.lang.Double.isNaN(rotation.yaw.toDouble()) || java.lang.Double.isNaN(rotation.pitch.toDouble()) || rotation.pitch > 90 || rotation.pitch < -90) return
            } catch (ignored: Exception) {
                return
            }
            rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity)
            targetRotation = rotation
            Companion.keepLength = keepLength
        }

        /**
         * Reset.
         */
        fun reset() {
            keepLength = 0
            targetRotation = null
        }

        /**
         * Gets rotations entity.
         *
         * @param entity the entity
         * @return the rotations entity
         */
        fun getRotationsEntity(entity: EntityLivingBase): Rotation {
            return getRotations(entity.posX, entity.posY + entity.eyeHeight - 0.4, entity.posZ)
        }

        /**
         * Gets rotations.
         *
         * @param posX the pos x
         * @param posY the pos y
         * @param posZ the pos z
         * @return the rotations
         */
        fun getRotations(posX: Double, posY: Double, posZ: Double): Rotation {
            val player = mc.thePlayer
            val x = posX - player.posX
            val y = posY - (player.posY + player.getEyeHeight().toDouble())
            val z = posZ - player.posZ
            val dist = MathHelper.sqrt_double(x * x + z * z).toDouble()
            val yaw = (atan2(z, x) * 180.0 / 3.141592653589793).toFloat() - 90.0f
            val pitch = (-(atan2(y, dist) * 180.0 / 3.141592653589793)).toFloat()
            return Rotation(yaw, pitch)
        }

        private fun calculate(from: Vec3?, to: Vec3): Rotation {
            val diff = to.subtract(from)
            val distance = hypot(diff.xCoord, diff.zCoord)
            val yaw = (MathHelper.atan2(diff.zCoord, diff.xCoord) * (180f / Math.PI)).toFloat() - 90.0f
            val pitch = (-(MathHelper.atan2(diff.yCoord, distance) * (180f / Math.PI))).toFloat()
            return Rotation(yaw, pitch)
        }

        fun calculate(to: Vec3): Rotation {
            return calculate(
                mc.thePlayer.positionVector.add(Vec3(0.0, mc.thePlayer.getEyeHeight().toDouble(), 0.0)),
                Vec3(to.xCoord, to.yCoord, to.zCoord)
            )
        }

        /**
         * Gets rotations.
         *
         * @param ent the ent
         * @return the rotations
         */
        fun getRotations(ent: Entity): Rotation {
            val x = ent.posX
            val z = ent.posZ
            val y = ent.posY + (ent.eyeHeight / 2.0f).toDouble()
            return getRotationFromPosition(x, z, y)
        }

        /**
         * Get rotations 1 float [ ].
         *
         * @param posX the pos x
         * @param posY the pos y
         * @param posZ the pos z
         * @return the float [ ]
         */
        fun getRotations1(posX: Double, posY: Double, posZ: Double): FloatArray {
            val player = mc.thePlayer
            val x = posX - player.posX
            val y = posY - (player.posY + player.getEyeHeight().toDouble())
            val z = posZ - player.posZ
            val dist = MathHelper.sqrt_double(x * x + z * z).toDouble()
            val yaw = (atan2(z, x) * 180.0 / Math.PI).toFloat() - 90.0f
            val pitch = -(atan2(y, dist) * 180.0 / Math.PI).toFloat()
            return floatArrayOf(yaw, pitch)
        }

        /**
         * Gets rotation from position.
         *
         * @param x the x
         * @param z the z
         * @param y the y
         * @return the rotation from position
         */
        fun getRotationFromPosition(x: Double, z: Double, y: Double): Rotation {
            val xDiff = x - mc.thePlayer.posX
            val zDiff = z - mc.thePlayer.posZ
            val yDiff = y - mc.thePlayer.posY - 1.2
            val dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff).toDouble()
            val yaw = (atan2(zDiff, xDiff) * 180.0 / Math.PI).toFloat() - 90.0f
            val pitch = (-atan2(yDiff, dist) * 180.0 / Math.PI).toFloat()
            return Rotation(yaw, pitch)
        }
    }
}