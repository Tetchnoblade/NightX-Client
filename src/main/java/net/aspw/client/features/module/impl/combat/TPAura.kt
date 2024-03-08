package net.aspw.client.features.module.impl.combat

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.protocol.ProtocolBase
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.pathfinder.MainPathFinder
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C0APacketAnimation
import java.util.*


@ModuleInfo(
    name = "TPAura", spacedName = "TP Aura",
    category = ModuleCategory.COMBAT
)
class TPAura : Module() {

    /*
     * Values
     */
    private val apsValue = IntegerValue("CPS", 6, 1, 10)
    private val maxTargetsValue = IntegerValue("MaxTarget", 1, 1, 8)
    private val rangeValue = IntegerValue("Range", 30, 10, 70, "m")
    private val fovValue = FloatValue("Fov", 180F, 0F, 180F, "°")
    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val rotationValue = BoolValue("Rotations", true)
    private val autoBlock = BoolValue("AutoBlock", true)

    /*
     * Variables
     */
    private val clickTimer = MSTimer()
    var isBlocking = false
    private var lastTarget: EntityLivingBase? = null

    private val attackDelay: Long
        get() = 1000L / apsValue.get().toLong()

    override fun onDisable() {
        isBlocking = false
        clickTimer.reset()
        lastTarget = null
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        state = false
        chat("TPAura was disabled")
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (lastTarget != null && rotationValue.get())
            RotationUtils.faceLook(lastTarget!!, 80f, 120f)

        if (!clickTimer.hasTimePassed(attackDelay)) return

        runAttack()
        clickTimer.reset()
    }

    private fun runAttack() {
        if (mc.thePlayer == null || mc.theWorld == null) return

        val targets = arrayListOf<EntityLivingBase>()
        var entityCount = 0

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityLivingBase && EntityUtils.isSelected(entity, true) && mc.thePlayer.getDistanceToEntity(
                    entity
                ) <= rangeValue.get()
            ) {
                if (fovValue.get() < 180F && RotationUtils.getRotationDifference(entity) > fovValue.get())
                    continue

                if (entityCount >= maxTargetsValue.get())
                    break

                if (autoBlock.get())
                    isBlocking = true
                targets.add(entity)
                entityCount++
            }
        }

        if (targets.isEmpty()) {
            lastTarget = null
            isBlocking = false
            return
        }

        targets.sortBy { it.health }

        targets.forEach {
            if (mc.thePlayer == null || mc.theWorld == null) return

            val path = MainPathFinder.computePath(
                net.aspw.client.utils.pathfinder.Vec3(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ
                ), net.aspw.client.utils.pathfinder.Vec3(it.posX, it.posY, it.posZ)
            )

            for (vec in path) PacketUtils.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    vec.x,
                    vec.y,
                    vec.z,
                    true
                )
            )

            lastTarget = it

            if (ProtocolBase.getManager().targetVersion.newerThan(ProtocolVersion.v1_8))
                mc.netHandler.addToSendQueue(C02PacketUseEntity(it, C02PacketUseEntity.Action.ATTACK))

            when (swingValue.get().lowercase(Locale.getDefault())) {
                "normal" -> mc.thePlayer.swingItem()
                "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
            }

            if (!ProtocolBase.getManager().targetVersion.newerThan(ProtocolVersion.v1_8))
                mc.netHandler.addToSendQueue(C02PacketUseEntity(it, C02PacketUseEntity.Action.ATTACK))

            path.reverse()

            for (vec in path) PacketUtils.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    vec.x,
                    vec.y,
                    vec.z,
                    true
                )
            )
        }
    }
}