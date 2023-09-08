package net.aspw.client.util.extensions

import net.aspw.client.util.MinecraftInstance
import net.aspw.client.util.Rotation
import net.aspw.client.util.RotationUtils
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3

/**
 * Allows to get the distance between the current entity and [entity] from the nearest corner of the bounding box
 */
fun Entity.getDistanceToEntityBox(entity: Entity) = eyes.distanceTo(getNearestPointBB(eyes, entity.hitBox))

fun getNearestPointBB(eye: Vec3, box: AxisAlignedBB): Vec3 {
    val origin = doubleArrayOf(eye.xCoord, eye.yCoord, eye.zCoord)
    val destMins = doubleArrayOf(box.minX, box.minY, box.minZ)
    val destMaxs = doubleArrayOf(box.maxX, box.maxY, box.maxZ)
    for (i in 0..2) {
        if (origin[i] > destMaxs[i]) origin[i] = destMaxs[i] else if (origin[i] < destMins[i]) origin[i] = destMins[i]
    }
    return Vec3(origin[0], origin[1], origin[2])
}

val Entity.hitBox: AxisAlignedBB
    get() {
        val borderSize = collisionBorderSize.toDouble()
        return entityBoundingBox.expand(borderSize, borderSize, borderSize)
    }

val Entity.eyes: Vec3
    get() = getPositionEyes(1f)

fun getVectorForRotation(pitch: Float, yaw: Float): Vec3 {
    val f = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f)
    val f2 = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f)
    val f3 = -MathHelper.cos(-pitch * 0.017453292f)
    val f4 = MathHelper.sin(-pitch * 0.017453292f)
    return Vec3((f2 * f3).toDouble(), f4.toDouble(), (f * f3).toDouble())
}

fun rayTraceCustom(blockReachDistance: Double, yaw: Float, pitch: Float): MovingObjectPosition? {
    val vec3 = MinecraftInstance.mc.thePlayer.getPositionEyes(1.0f)
    val vec31 = getVectorForRotation(yaw, pitch)
    val vec32 = vec3.addVector(
        vec31.xCoord * blockReachDistance,
        vec31.yCoord * blockReachDistance,
        vec31.zCoord * blockReachDistance
    )
    return MinecraftInstance.mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true)
}

fun Entity.getLookDistanceToEntityBox(entity: Entity = this, rotation: Rotation? = null, range: Double = 10.0): Double {
    val eyes = this.getPositionEyes(1F)
    val end = (rotation ?: RotationUtils.targetRotation).toDirection().multiply(range).add(eyes)
    return entity.entityBoundingBox.calculateIntercept(eyes, end)?.hitVec?.distanceTo(eyes) ?: Double.MAX_VALUE
}

val Entity.rotation: Rotation
    get() = Rotation(rotationYaw, rotationPitch)