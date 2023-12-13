package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MoveEvent
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.util.PacketUtils
import net.aspw.client.util.pathfinder.MainPathFinder
import net.aspw.client.util.pathfinder.Vec3
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.value.ListValue
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import org.lwjgl.input.Mouse
import java.util.*

@ModuleInfo(name = "LookTP", spacedName = "Look TP", description = "", category = ModuleCategory.OTHER)
class LookTP : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Vulcan"), "Vanilla")
    private val buttonValue = ListValue("Button", arrayOf("Left", "Right", "Middle"), "Middle")
    private var countTicks = false
    private var startTeleport = false
    private var tpX = 0.0
    private var tpY = 0.0
    private var tpZ = 0.0
    private var ticks = 0
    private val timer = MSTimer()

    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        countTicks = false
        ticks = 0
        startTeleport = false
        tpX = 0.0
        tpY = 0.0
        tpZ = 0.0
    }

    override fun onEnable() {
        countTicks = false
        ticks = 0
        startTeleport = false
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (Mouse.isButtonDown(listOf(*buttonValue.values).indexOf(buttonValue.get())) && timer.hasTimePassed(
                300L
            )
        ) {
            val pos = mc.thePlayer.rayTrace(999.0, 1f).blockPos
            val tpPos = pos.up()
            if (mc.theWorld.getBlockState(pos).block is BlockAir || mc.theWorld.getBlockState(tpPos).block !is BlockAir) return
            tpX = tpPos.x.toDouble() + 0.5
            tpY = tpPos.y.toDouble()
            tpZ = tpPos.z.toDouble() + 0.5
            when (modeValue.get()) {
                "Vanilla" -> {
                    Thread {
                        val path = MainPathFinder.computePath(
                            Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
                            Vec3(tpX, tpY, tpZ)
                        )
                        for (point in path) PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                point.x,
                                point.y,
                                point.z,
                                true
                            )
                        )
                        mc.thePlayer.setPosition(tpX, tpY, tpZ)
                    }.start()
                    Client.hud.addNotification(
                        Notification(
                            "Successfully Teleported to X: $tpX, Y: $tpY, Z: $tpZ",
                            Notification.Type.SUCCESS
                        )
                    )
                    if (Objects.requireNonNull(
                            Client.moduleManager.getModule(
                                Interface::class.java
                            )
                        )?.flagSoundValue?.get()!!
                    ) {
                        Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                    }
                }

                "Vulcan" -> {
                    startTeleport = true
                    Client.hud.addNotification(
                        Notification(
                            "Teleporting to X: $tpX, Y: $tpY, Z: $tpZ soon",
                            Notification.Type.INFO
                        )
                    )
                }
            }
            timer.reset()
        }
        if (startTeleport && modeValue.get() == "Vulcan") {
            if (countTicks) ticks++
            event.zero()
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ)
            if (countTicks && ticks == 1) {
                Thread {
                    val path = MainPathFinder.computePath(
                        Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
                        Vec3(tpX, tpY, tpZ)
                    )
                    for (point in path) PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            point.x,
                            point.y,
                            point.z,
                            true
                        )
                    )
                }.start()
                PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(tpX, tpY, tpZ + 0.1, true))
                PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(tpX, tpY, tpZ - 0.1, true))
                Client.hud.addNotification(
                    Notification(
                        "Successfully Teleported to X: $tpX, Y: $tpY, Z: $tpZ",
                        Notification.Type.SUCCESS
                    )
                )
                if (Objects.requireNonNull(
                        Client.moduleManager.getModule(
                            Interface::class.java
                        )
                    )?.flagSoundValue?.get()!!
                ) {
                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                }
                countTicks = false
                ticks = 0
                startTeleport = false
            } else if (countTicks && ticks > 1) {
                mc.thePlayer.setPosition(tpX, tpY + 1, tpZ)
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (modeValue.get() == "Vulcan") {
            if (startTeleport && event.packet is S08PacketPlayerPosLook && mc.thePlayer.ticksExisted > 20) {
                val s08 = event.packet
                if (mc.thePlayer.getDistanceSq(s08.getX(), s08.getY(), s08.getZ()) < 2 * 2) {
                    event.cancelEvent()
                    countTicks = true
                } else {
                    countTicks = false
                    ticks = 0
                    startTeleport = false
                }
            }
        }
    }
}