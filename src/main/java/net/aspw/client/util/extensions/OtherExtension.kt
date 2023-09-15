package net.aspw.client.util.extensions

import net.aspw.client.util.MinecraftInstance
import net.aspw.client.util.Rotation
import net.aspw.client.util.RotationUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos.MutableBlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import java.util.*

fun Vec3.multiply(value: Double): Vec3 {
    return Vec3(this.xCoord * value, this.yCoord * value, this.zCoord * value)
}

fun AxisAlignedBB.getLookingTargetRange(
    thePlayer: EntityPlayerSP,
    rotation: Rotation? = null,
    range: Double = 6.0
): Double {
    val eyes = thePlayer.getPositionEyes(1F)
    val movingObj = this.calculateIntercept(
        eyes,
        (rotation ?: RotationUtils.targetRotation)?.toDirection()?.multiply(range)?.add(eyes)
    ) ?: return Double.MAX_VALUE
    return movingObj.hitVec.distanceTo(eyes)
}

fun AxisAlignedBB.expands(v: Double, modifyYDown: Boolean = true, modifyYUp: Boolean = true): AxisAlignedBB {
    return AxisAlignedBB(
        this.minX - v,
        this.minY - (if (modifyYDown) v else 0.0),
        this.minZ - v,
        this.maxX + v,
        this.maxY + (if (modifyYUp) v else 0.0),
        this.maxZ + v
    )
}

fun AxisAlignedBB.getBlockStatesIncluded(): List<IBlockState> {
    val tmpArr = LinkedList<IBlockState>()
    val minX = MathHelper.floor_double(this.minX)
    val minY = MathHelper.floor_double(this.minY)
    val minZ = MathHelper.floor_double(this.minZ)
    val maxX = MathHelper.floor_double(this.maxX)
    val maxY = MathHelper.floor_double(this.maxY)
    val maxZ = MathHelper.floor_double(this.maxZ)
    val mc = MinecraftInstance.mc
    val mbp = MutableBlockPos(minX, minY, minZ)

    for (x in minX..maxX) {
        for (y in minY..maxY) {
            for (z in maxZ..maxX) {
                mbp.set(x, y, z)
                if (mc.theWorld.isAirBlock(mbp)) continue
                tmpArr.add(mc.theWorld.getBlockState(mbp))
            }
        }
    }

    return tmpArr
}