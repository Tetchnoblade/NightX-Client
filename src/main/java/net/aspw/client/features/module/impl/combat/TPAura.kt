package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.PacketUtils
import net.aspw.client.util.RotationUtils
import net.aspw.client.util.pathfinder.MainPathFinder
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.Vec3
import java.util.*


@ModuleInfo(
    name = "TPAura", spacedName = "TP Aura", description = "",
    category = ModuleCategory.COMBAT
)
class TPAura : Module() {

    /*
     * Values
     */
    private val apsValue = IntegerValue("CPS", 6, 1, 10)
    private val maxTargetsValue = IntegerValue("MaxTarget", 1, 1, 8)
    private val rangeValue = IntegerValue("Range", 30, 10, 70, "m")
    private val fovValue = FloatValue("FOV", 180F, 0F, 180F, "°")
    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val autoBlock = BoolValue("AutoBlock", true)

    /*
     * Variables
     */
    private val clickTimer = MSTimer()
    private var tpVectors = arrayListOf<Vec3>()
    private var thread: Thread? = null
    var isBlocking = false
    var lastTarget: EntityLivingBase? = null

    private val attackDelay: Long
        get() = 1000L / apsValue.get().toLong()

    override fun onEnable() {
        clickTimer.reset()
        tpVectors.clear()
        lastTarget = null
    }

    override fun onDisable() {
        isBlocking = false
        clickTimer.reset()
        tpVectors.clear()
        lastTarget = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!clickTimer.hasTimePassed(attackDelay)) return

        if (thread == null || !thread!!.isAlive) {
            tpVectors.clear()
            clickTimer.reset()
            thread = Thread { runAttack() }
            thread!!.start()
        } else
            clickTimer.reset()
    }

    private fun runAttack() {
        if (mc.thePlayer == null || mc.theWorld == null) return

        val targets = arrayListOf<EntityLivingBase>()
        var entityCount = 0

        for (entity in mc.theWorld.loadedEntityList)
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

        if (targets.isEmpty()) {
            lastTarget = null
            isBlocking = false
            return
        }

        targets.sortBy { it.health }


        targets.forEach {
            if (mc.thePlayer == null || mc.theWorld == null) return

            val path = MainPathFinder.computePath(
                net.aspw.client.util.pathfinder.Vec3(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ
                ), net.aspw.client.util.pathfinder.Vec3(it.posX, it.posY, it.posZ)
            )

            for (vec in path) PacketUtils.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    vec.x,
                    vec.y,
                    vec.z,
                    false
                )
            )

            lastTarget = it

            when (swingValue.get().lowercase(Locale.getDefault())) {
                "normal" -> mc.thePlayer.swingItem()
                "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
            }

            mc.netHandler.addToSendQueue(C02PacketUseEntity(it, C02PacketUseEntity.Action.ATTACK))

            path.reverse()

            for (vec in path) PacketUtils.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    vec.x,
                    vec.y,
                    vec.z,
                    false
                )
            )
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook)
            clickTimer.reset()
    }
}