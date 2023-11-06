package net.aspw.client.util

class Vector3d(val x: Double, val y: Double, val z: Double) {

    fun add(x: Double, y: Double, z: Double): Vector3d {
        return Vector3d(this.x + x, this.y + y, this.z + z)
    }

    fun add(vector: Vector3d): Vector3d {
        return add(vector.x, vector.y, vector.z)
    }

    fun subtract(x: Double, y: Double, z: Double): Vector3d {
        return add(-x, -y, -z)
    }

    fun subtract(vector: Vector3d): Vector3d {
        return add(-vector.x, -vector.y, -vector.z)
    }

    fun length(): Double {
        return Math.sqrt(x * x + y * y + z * z)
    }

    fun multiply(v: Double): Vector3d {
        return Vector3d(x * v, y * v, z * v)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is Vector3d) return false
        val vector = obj
        return Math.floor(x) == Math.floor(vector.x) && Math.floor(y) == Math.floor(vector.y) && Math.floor(
            z
        ) == Math.floor(vector.z)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
}