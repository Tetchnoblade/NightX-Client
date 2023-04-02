package net.aspw.client.features.module.impl.combat

import net.aspw.client.Client
import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.movement.Flight
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C07PacketPlayerDigging
import java.util.*

@ModuleInfo(name = "Criticals", category = ModuleCategory.COMBAT)
class Criticals : Module() {

    val modeValue = ListValue(
        "Mode",
        arrayOf(
            "NewPacket",
            "Packet",
            "Packet2",
            "NewNCP",
            "NCPPacket",
            "NoGround",
            "Redesky",
            "AACv4",
            "Hop",
            "TPHop",
            "Jump",
            "Edit",
            "MiniPhase",
            "NanoPacket",
            "Non-Calculable",
            "Invalid",
            "VerusSmart"
        ),
        "NCPPacket"
    )
    val delayValue = IntegerValue("Delay", 0, 0, 500, "ms")
    private val jumpHeightValue = FloatValue("JumpHeight", 0.42F, 0.1F, 0.42F)
    private val downYValue = FloatValue("DownY", 0f, 0f, 0.1F)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val onlyAuraValue = BoolValue("OnlyAura", false)

    val msTimer = MSTimer()
    private var readyCrits = false
    private var canCrits = true
    private var counter = 0
    private var attacked = 0

    override fun onEnable() {
        if (modeValue.get().equals("NoGround", ignoreCase = true))
            mc.thePlayer.jump()
        canCrits = true
        counter = 0
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (onlyAuraValue.get() && !Client.moduleManager[KillAura::class.java]!!.state && !Client.moduleManager[TPAura::class.java]!!.state) return

        if (event.targetEntity is EntityLivingBase) {
            val entity = event.targetEntity

            if (!mc.thePlayer.onGround || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb || mc.thePlayer.isInWater ||
                mc.thePlayer.isInLava || mc.thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                Client.moduleManager[Flight::class.java]!!.state || !msTimer.hasTimePassed(
                    delayValue.get().toLong()
                )
            )
                return

            val x = mc.thePlayer.posX
            val y = mc.thePlayer.posY
            val z = mc.thePlayer.posZ

            when (modeValue.get().lowercase(Locale.getDefault())) {
                "newpacket" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.05250000001304, z, true))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.01400000001304, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                }

                "newncp" -> {
                    attacked++
                    if (attacked >= 5) {
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00001058293536, z, false))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00000916580235, z, false))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00000010371854, z, false))
                        attacked = 0
                    }
                }

                "packet" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.0625, z, true))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 1.1E-5, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                }

                "packet2" -> {
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 0.0625,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 0.09858,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 0.04114514,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 0.025,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                }

                "ncppacket" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.11, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.1100013579, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.0000013579, z, false))
                }

                "aacv4" -> {
                    mc.thePlayer.motionZ *= 0
                    mc.thePlayer.motionX *= 0
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 3e-14,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 8e-15,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                }

                "hop" -> {
                    mc.thePlayer.motionY = 0.1
                    mc.thePlayer.fallDistance = 0.1f
                    mc.thePlayer.onGround = false
                }

                "tphop" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.02, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.01, z, false))
                    mc.thePlayer.setPosition(x, y + 0.01, z)
                }

                "jump" -> {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = jumpHeightValue.get().toDouble()
                    } else {
                        mc.thePlayer.motionY -= downYValue.get()
                    }
                }

                "miniphase" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y - 0.0125, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.01275, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y - 0.00025, z, true))
                }

                "nanopacket" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00973333333333, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.001, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y - 0.01200000000007, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y - 0.0005, z, false))
                }

                "non-calculable" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 1E-5, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 1E-7, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y - 1E-6, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y - 1E-4, z, false))
                }

                "invalid" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 1E+27, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y - 1E+68, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 1E+41, z, false))
                }

                "verussmart" -> {
                    counter++
                    if (counter == 1) {
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.001, z, true))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    }
                    if (counter >= 5)
                        counter = 0
                }
            }

            readyCrits = true
            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (onlyAuraValue.get() && !Client.moduleManager[KillAura::class.java]!!.state) return

        val packet = event.packet

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "redesky" -> {
                if (packet is C03PacketPlayer) {
                    val packetPlayer: C03PacketPlayer = packet
                    if (mc.thePlayer.onGround && canCrits) {
                        packetPlayer.y += 0.000001
                        packetPlayer.onGround = false
                    }
                    if (mc.theWorld.getCollidingBoundingBoxes(
                            mc.thePlayer, mc.thePlayer.entityBoundingBox.offset(
                                0.0, (mc.thePlayer.motionY - 0.08) * 0.98, 0.0
                            ).expand(0.0, 0.0, 0.0)
                        ).isEmpty()
                    ) {
                        packetPlayer.onGround = true
                    }
                }
                if (packet is C07PacketPlayerDigging) {
                    if (packet.status == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                        canCrits = false
                    } else if (packet.status == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK || packet.status == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                        canCrits = true
                    }
                }
            }

            "noground" -> {
                if (packet is C03PacketPlayer) {
                    packet.onGround = false
                }
            }

            "edit" -> {
                if (readyCrits) {
                    if (packet is C03PacketPlayer) {
                        packet.onGround = false
                    }
                    readyCrits = false
                }
            }
        }

    }

    override val tag: String
        get() = modeValue.get()
}
