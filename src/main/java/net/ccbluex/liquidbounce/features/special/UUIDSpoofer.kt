package net.ccbluex.liquidbounce.features.special

import net.ccbluex.liquidbounce.utils.MinecraftInstance

object UUIDSpoofer : MinecraftInstance() {
    var spoofId: String? = null

    @JvmStatic
    fun getUUID(): String = (if (spoofId == null) mc.session.playerID else spoofId!!).replace("-", "")
}