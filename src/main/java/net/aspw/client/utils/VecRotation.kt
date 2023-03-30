package net.aspw.client.utils

import net.aspw.client.utils.block.PlaceInfo
import net.minecraft.util.Vec3

data class VecRotation(var yaw: Float, var pitch: Float) {
    data class VecRotation(val vec: Vec3, val rotation: Rotation)
    data class PlaceRotation(val placeInfo: PlaceInfo, val rotation: Rotation)
}