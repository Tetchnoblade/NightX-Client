package net.aspw.nightx.features.module.modules.combat

import de.enzaxd.viaforge.ViaForge
import net.aspw.nightx.NightX
import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.exploit.Disabler
import net.aspw.nightx.features.module.modules.misc.Teams
import net.aspw.nightx.features.module.modules.player.Blink
import net.aspw.nightx.features.module.modules.player.TargetStrafe
import net.aspw.nightx.features.module.modules.render.Freecam
import net.aspw.nightx.features.module.modules.world.Scaffold
import net.aspw.nightx.utils.*
import net.aspw.nightx.utils.extensions.getDistanceToEntityBox
import net.aspw.nightx.utils.misc.RandomUtils
import net.aspw.nightx.utils.timer.MSTimer
import net.aspw.nightx.utils.timer.TimeUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.world.WorldSettings
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@ModuleInfo(
    name = "KillAura", spacedName = "Kill Aura",
    category = ModuleCategory.COMBAT
)
class KillAura : Module() {

    /**
     * OPTIONS
     */

    // CPS - Attack speed
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 12, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 10, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)

    // Range
    val rangeValue = FloatValue("Range", 6.0f, 1f, 8f, "m")
    private val throughWallsRangeValue = FloatValue("ThroughWallsRange", 6.0f, 0f, 8f, "m")
    private val rangeSprintReducementValue = FloatValue("RangeSprintReducement", 0.02f, 0f, 0.4f, "m")

    // Modes
    private val rotations = ListValue("RotationMode", arrayOf("Vanilla", "Full", "Spin", "None"), "Full")

    private val spinHurtTimeValue =
        IntegerValue("Spin-HitHurtTime", 10, 0, 10, { rotations.get().equals("spin", true) })

    // Spin Speed
    private val maxSpinSpeed: FloatValue =
        object : FloatValue("MaxSpinSpeed", 180f, 0f, 180f, "°", { rotations.get().equals("spin", true) }) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val v = minSpinSpeed.get()
                if (v > newValue) set(v)
            }
        }

    private val minSpinSpeed: FloatValue =
        object : FloatValue("MinSpinSpeed", 180f, 0f, 180f, "°", { rotations.get().equals("spin", true) }) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val v = maxSpinSpeed.get()
                if (v < newValue) set(v)
            }
        }

    // Turn Speed
    private val maxTurnSpeed: FloatValue =
        object : FloatValue("MaxTurnSpeed", 120f, 0f, 180f, "°", { !rotations.get().equals("none", true) }) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val v = minTurnSpeed.get()
                if (v > newValue) set(v)
            }
        }

    private val minTurnSpeed: FloatValue =
        object : FloatValue("MinTurnSpeed", 100f, 0f, 180f, "°", { !rotations.get().equals("none", true) }) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val v = maxTurnSpeed.get()
                if (v < newValue) set(v)
            }
        }

    private val roundTurnAngle = BoolValue("RoundAngle", false, { !rotations.get().equals("none", true) })
    private val roundAngleDirs = IntegerValue(
        "RoundAngle-Directions",
        15,
        2,
        100,
        { !rotations.get().equals("none", true) && roundTurnAngle.get() })

    private val noSendRot = BoolValue("NoSendRotation", true, { rotations.get().equals("spin", true) })
    private val noHitCheck = BoolValue("NoHitCheck", false, { !rotations.get().equals("none", true) })
    private val blinkCheck = BoolValue("BlinkCheck", false)

    private val priorityValue = ListValue(
        "Priority",
        arrayOf(
            "Health",
            "Distance",
            "Direction",
            "LivingTime",
            "Armor",
            "HurtResistance",
            "HurtTime",
            "HealthAbsorption",
            "RegenAmplifier"
        ),
        "Direction"
    )
    val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Single")

    //reverted in old LB. idk why they removed it.
    private val switchDelayValue =
        IntegerValue("SwitchDelay", 1000, 1, 2000, "ms", { targetModeValue.get().equals("switch", true) })

    // Bypass
    private val swingValue = BoolValue("Swing", true)
    private val keepSprintValue = BoolValue("NoKeepSprint", false)

    // AutoBlock
    val autoBlockModeValue =
        ListValue("AutoBlock", arrayOf("None", "Interact", "Packet", "AfterTick", "NCP", "OldHypixel"), "Packet")

    private val displayAutoBlockSettings =
        BoolValue("Open", true, { !autoBlockModeValue.get().equals("None", true) })
    private val interactAutoBlockValue = BoolValue(
        "InteractAutoBlock",
        false,
        { !autoBlockModeValue.get().equals("None", true) && displayAutoBlockSettings.get() })
    private val verusAutoBlockValue = BoolValue(
        "UnBlock-Exploit",
        false,
        { !autoBlockModeValue.get().equals("None", true) && displayAutoBlockSettings.get() })
    private val abThruWallValue = BoolValue(
        "ThroughAutoBlock",
        true,
        { !autoBlockModeValue.get().equals("None", true) && displayAutoBlockSettings.get() })

    // smart autoblock stuff
    private val smartAutoBlockValue = BoolValue(
        "SmartAutoBlock",
        false,
        { !autoBlockModeValue.get().equals("None", true) && displayAutoBlockSettings.get() }) // thanks czech
    private val smartABItemValue = BoolValue(
        "SmartAutoBlock-ItemCheck",
        true,
        {
            !autoBlockModeValue.get()
                .equals("None", true) && smartAutoBlockValue.get() && displayAutoBlockSettings.get()
        })
    private val smartABFacingValue = BoolValue(
        "SmartAutoBlock-FacingCheck",
        true,
        {
            !autoBlockModeValue.get()
                .equals("None", true) && smartAutoBlockValue.get() && displayAutoBlockSettings.get()
        })
    private val smartABRangeValue = FloatValue(
        "SmartAB-Range",
        3.5F,
        3F,
        8F,
        "m",
        {
            !autoBlockModeValue.get()
                .equals("None", true) && smartAutoBlockValue.get() && displayAutoBlockSettings.get()
        })
    private val smartABTolerationValue = FloatValue(
        "SmartAB-Toleration",
        0F,
        0F,
        2F,
        {
            !autoBlockModeValue.get()
                .equals("None", true) && smartAutoBlockValue.get() && displayAutoBlockSettings.get()
        })

    private val afterTickPatchValue = BoolValue(
        "AfterTickPatch",
        true,
        { autoBlockModeValue.get().equals("AfterTick", true) && displayAutoBlockSettings.get() })
    private val blockRate = IntegerValue(
        "BlockRate",
        100,
        1,
        100,
        "%",
        { !autoBlockModeValue.get().equals("None", true) && displayAutoBlockSettings.get() })

    // Raycast
    private val raycastValue = BoolValue("PostAttack", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false)
    private val livingRaycastValue = BoolValue("LivingRayCast", false)

    // Bypass
    private val aacValue = BoolValue("AAC", false)

    private val silentRotationValue = BoolValue("SilentRotation", true, { !rotations.get().equals("none", true) })
    val rotationStrafeValue = ListValue("Strafe", arrayOf("Off", "Strict", "Silent"), "Off")

    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)

    // Predict
    private val predictValue = BoolValue("Predict", false)

    private val maxPredictSize: FloatValue =
        object : FloatValue("MaxPredictSize", 1.5f, 0.1f, 5f, { predictValue.get() }) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val v = minPredictSize.get()
                if (v > newValue) set(v)
            }
        }

    private val minPredictSize: FloatValue =
        object : FloatValue("MinPredictSize", 1f, 0.1f, 5f, { predictValue.get() }) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val v = maxPredictSize.get()
                if (v < newValue) set(v)
            }
        }

    private val randomCenterValue = BoolValue("RandomCenter", false, { !rotations.get().equals("none", true) })
    private val randomCenterNewValue =
        BoolValue("NewCalc", true, { !rotations.get().equals("none", true) && randomCenterValue.get() })
    private val minRand: FloatValue = object : FloatValue(
        "MinMultiply",
        0.8f,
        0f,
        2f,
        "x",
        { !rotations.get().equals("none", true) && randomCenterValue.get() }) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxRand.get()
            if (v < newValue) set(v)
        }
    }
    private val maxRand: FloatValue = object : FloatValue(
        "MaxMultiply",
        0.8f,
        0f,
        2f,
        "x",
        { !rotations.get().equals("none", true) && randomCenterValue.get() }) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minRand.get()
            if (v > newValue) set(v)
        }
    }
    private val outborderValue = BoolValue("Outborder", false)

    // Bypass
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val noInventoryAttackValue = BoolValue("NoInvAttack", false)
    private val noInventoryDelayValue = IntegerValue("NoInvDelay", 200, 0, 500, "ms", { noInventoryAttackValue.get() })
    private val limitedMultiTargetsValue =
        IntegerValue("LimitedMultiTargets", 0, 0, 50, { targetModeValue.get().equals("multi", true) })

    // idk
    private val noScaffValue = BoolValue("NoScaffold", true)
    private val debugValue = BoolValue("Debug", false)

    // Visuals
    private val circleValue = BoolValue("Circle", false)
    private val accuracyValue = IntegerValue("Accuracy", 0, 0, 59, { circleValue.get() })
    private val red = IntegerValue("Red", 255, 0, 255, { circleValue.get() })
    private val green = IntegerValue("Green", 0, 0, 255, { circleValue.get() })
    private val blue = IntegerValue("Blue", 255, 0, 255, { circleValue.get() })
    private val alpha = IntegerValue("Alpha", 200, 0, 255, { circleValue.get() })


    /**
     * MODULE
     */

    // Target
    var target: EntityLivingBase? = null
    var currentTarget: EntityLivingBase? = null
    var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()

    private var markEntity: EntityLivingBase? = null

    // Attack delay
    private val attackTimer = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0

    // Container Delay
    private var containerOpen = -1L

    // Fake block status
    var blockingStatus = false
    var verusBlocking = false
    var fakeBlock = false

    var smartBlocking = false
    private val canSmartBlock: Boolean
        get() = !smartAutoBlockValue.get() || smartBlocking

    var spinYaw = 0F

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
        verusBlocking = false
        smartBlocking = false
    }

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        target = null
        currentTarget = null
        hitable = false
        prevTargetEntities.clear()
        attackTimer.reset()
        clicks = 0

        stopBlocking()
        if (verusBlocking && !blockingStatus && !mc.thePlayer.isBlocking) {
            verusBlocking = false
            if (verusAutoBlockValue.get())
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN
                    )
                )
        }
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()

            // AutoBlock
            if (autoBlockModeValue.get().equals("AfterTick", true) && canBlock)
                startBlocking(currentTarget!!, hitable)
        }

        if (rotationStrafeValue.get().equals("Off", true))
            update()
    }


    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val targetStrafe = NightX.moduleManager.getModule(TargetStrafe::class.java)!!
        if (rotationStrafeValue.get().equals("Off", true) && !targetStrafe.state)
            return

        update()

        if (currentTarget != null && RotationUtils.targetRotation != null) {
            when (rotationStrafeValue.get().lowercase(Locale.getDefault())) {
                "strict" -> {
                    val (yaw) = RotationUtils.targetRotation ?: return
                    var strafe = event.strafe
                    var forward = event.forward
                    val friction = event.friction

                    var f = strafe * strafe + forward * forward

                    if (f >= 1.0E-4F) {
                        f = MathHelper.sqrt_float(f)

                        if (f < 1.0F)
                            f = 1.0F

                        f = friction / f
                        strafe *= f
                        forward *= f

                        val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                        val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                        mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                        mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
                    }
                    event.cancelEvent()
                }

                "silent" -> {
                    update()

                    RotationUtils.targetRotation.applyStrafeToPlayer(event)
                    event.cancelEvent()
                }
            }
        }
    }

    fun update() {
        if (cancelRun || (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get()))
        )
            return

        // Update target
        updateTarget()

        if (target == null) {
            stopBlocking()
            return
        }

        // Target
        currentTarget = target

        if (!targetModeValue.get().equals("Switch", ignoreCase = true) && isEnemy(currentTarget))
            target = currentTarget
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (verusBlocking
            && ((packet is C07PacketPlayerDigging
                    && packet.status == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)
                    || packet is C08PacketPlayerBlockPlacement)
            && verusAutoBlockValue.get()
        )
            event.cancelEvent()

        if (packet is C09PacketHeldItemChange)
            verusBlocking = false
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        updateKA()

        smartBlocking = false
        if (smartAutoBlockValue.get() && target != null) {
            val smTarget = target!!
            if (!smartABItemValue.get() || (smTarget.heldItem != null && smTarget.heldItem.item != null && (smTarget.heldItem.item is ItemSword || smTarget.heldItem.item is ItemAxe))) {
                if (mc.thePlayer.getDistanceToEntityBox(smTarget) < smartABRangeValue.get()) {
                    if (smartABFacingValue.get()) {
                        if (smTarget.rayTrace(
                                smartABRangeValue.get().toDouble(),
                                1F
                            ).typeOfHit == MovingObjectPosition.MovingObjectType.MISS
                        ) {
                            val eyesVec = smTarget.getPositionEyes(1F)
                            val lookVec = smTarget.getLook(1F)
                            val pointingVec = eyesVec.addVector(
                                lookVec.xCoord * smartABRangeValue.get(),
                                lookVec.yCoord * smartABRangeValue.get(),
                                lookVec.zCoord * smartABRangeValue.get()
                            )
                            val border = mc.thePlayer.collisionBorderSize + smartABTolerationValue.get()
                            val bb = mc.thePlayer.entityBoundingBox.expand(
                                border.toDouble(),
                                border.toDouble(),
                                border.toDouble()
                            )
                            smartBlocking = bb.calculateIntercept(
                                eyesVec,
                                pointingVec
                            ) != null || bb.intersectsWith(smTarget.entityBoundingBox)
                        }
                    } else
                        smartBlocking = true
                }
            }
        }

        if (mc.thePlayer.isBlocking || blockingStatus)
            verusBlocking = true
        else if (verusBlocking) {
            verusBlocking = false
            if (verusAutoBlockValue.get())
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN
                    )
                )
        }
    }

    private fun updateKA() {
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())
        ) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        if (target != null && currentTarget != null) {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        }
    }

    /**
     * Render event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (circleValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(1F)
            GL11.glColor4f(
                red.get().toFloat() / 255.0F,
                green.get().toFloat() / 255.0F,
                blue.get().toFloat() / 255.0F,
                alpha.get().toFloat() / 255.0F
            )
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 60 - accuracyValue.get()) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    Math.cos(i * Math.PI / 180.0).toFloat() * rangeValue.get(),
                    (Math.sin(i * Math.PI / 180.0).toFloat() * rangeValue.get())
                )
            }

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
        }

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())
        ) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        target ?: return

        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) &&
            currentTarget!!.hurtTime <= hurtTimeValue.get()
        ) {
            clicks++
            attackTimer.reset()
            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }

    /**
     * Handle entity move
     */
    @EventTarget
    fun onEntityMove(event: EntityMovementEvent) {
        val movedEntity = event.movedEntity

        if (target == null || movedEntity != currentTarget)
            return

        updateHitable()
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return

        // Settings
        val failRate = failRateValue.get()
        val swing = swingValue.get()
        val multi = targetModeValue.get().equals("Multi", ignoreCase = true)
        val openInventory = aacValue.get() && mc.currentScreen is GuiInventory
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Close inventory when open
        if (openInventory)
            mc.netHandler.addToSendQueue(C0DPacketCloseWindow())

        // Check is not hitable or check failrate
        if (!hitable || failHit) {
            if (swing || failHit)
                mc.thePlayer.swingItem()
        } else {
            // Attack
            if (!multi) {
                attackEntity(currentTarget!!)
            } else {
                var targets = 0

                for (entity in mc.theWorld.loadedEntityList) {
                    val distance = mc.thePlayer.getDistanceToEntityBox(entity)

                    if (entity is EntityLivingBase && isEnemy(entity) && distance <= getRange(entity)) {
                        attackEntity(entity)

                        targets += 1

                        if (limitedMultiTargetsValue.get() != 0 && limitedMultiTargetsValue.get() <= targets)
                            break
                    }
                }
            }

            prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)

            if (target == currentTarget)
                target = null
        }

        if (targetModeValue.get()
                .equals("Switch", ignoreCase = true) && attackTimer.hasTimePassed((switchDelayValue.get()).toLong())
        ) {
            if (switchDelayValue.get() != 0) {
                prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
                attackTimer.reset()
            }
        }

        // Open inventory
        if (openInventory)
            mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Reset fixed target to null
        var searchTarget = null

        // Settings
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()
        val switchMode = targetModeValue.get().equals("Switch", ignoreCase = true)

        // Find possible targets
        val targets = mutableListOf<EntityLivingBase>()
        val lookingTargets = mutableListOf<EntityLivingBase>()

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity !is EntityLivingBase || !isEnemy(entity) || (switchMode && prevTargetEntities.contains(entity.entityId))/* || (!focusEntityName.isEmpty() && !focusEntityName.contains(entity.name.toLowerCase()))*/)
                continue

            val distance = mc.thePlayer.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= maxRange && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime)
                targets.add(entity)
        }

        // Sort targets by priority
        when (priorityValue.get().lowercase(Locale.getDefault())) {
            "distance" -> targets.sortBy { mc.thePlayer.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> targets.sortBy { it.health } // Sort by health
            "direction" -> targets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> targets.sortBy { -it.ticksExisted } // Sort by existence
            "hurtresistance" -> targets.sortBy { it.hurtResistantTime } // Sort by armor hurt time
            "hurttime" -> targets.sortBy { it.hurtTime } // Sort by hurt time
            "healthabsorption" -> targets.sortBy { it.health + it.absorptionAmount } // Sort by full health with absorption effect
            "regenamplifier" -> targets.sortBy {
                if (it.isPotionActive(Potion.regeneration)) it.getActivePotionEffect(
                    Potion.regeneration
                ).amplifier else -1
            }
        }

        var found = false

        // Find best target
        for (entity in targets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue

            // Set target to current entity
            target = entity
            found = true
            break
        }

        if (found) {
            if (rotations.get().equals("spin", true)) {
                spinYaw += RandomUtils.nextFloat(minSpinSpeed.get(), maxSpinSpeed.get())
                spinYaw = MathHelper.wrapAngleTo180_float(spinYaw)
                val rot = Rotation(spinYaw, 90F)
                RotationUtils.setTargetRotation(rot, 0)
            }
            return
        }

        if (searchTarget != null) {
            if (target != searchTarget) target = searchTarget
            return
        } else {
            target = null
        }

        // Cleanup last targets when no target found and try again
        if (prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
        }
    }

    /**
     * Check if [entity] is selected as enemy with current target options and other modules
     */
    fun isEnemy(entity: Entity?): Boolean {
        if (entity is EntityLivingBase && (EntityUtils.targetDead || isAlive(entity)) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible())
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                if (entity.isSpectator || AntiBot.isBot(entity))
                    return false

                if (EntityUtils.isFriend(entity))
                    return false

                val teams = NightX.moduleManager[Teams::class.java] as Teams

                return !teams.state || !teams.isInYourTeam(entity)
            }

            return EntityUtils.targetMobs && EntityUtils.isMob(entity) || EntityUtils.targetAnimals &&
                    EntityUtils.isAnimal(entity)
        }

        return false
    }

    /**
     * Attack [entity]
     */
    private fun attackEntity(entity: EntityLivingBase) {
        // Stop blocking
        if (mc.thePlayer.isBlocking || blockingStatus)
            stopBlocking()

        // Call attack event
        NightX.eventManager.callEvent(AttackEvent(entity))

        markEntity = entity

        // Get rotation and send packet if possible
        if (rotations.get().equals("spin", true) && !noSendRot.get()) {
            val targetedRotation = getTargetRotation(entity) ?: return
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C05PacketPlayerLook(
                    targetedRotation.yaw,
                    targetedRotation.pitch,
                    mc.thePlayer.onGround
                )
            )

            if (debugValue.get())
                ClientUtils.displayChatMessage("[KillAura] Silent rotation change.")
        }

        // Attack target
        if (swingValue.get() && ViaForge.getInstance().version <= 47) // version fix
            mc.thePlayer.swingItem()

        mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        if (swingValue.get() && ViaForge.getInstance().version > 47)
            mc.thePlayer.swingItem()

        if (keepSprintValue.get()) {
            if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR)
                mc.thePlayer.attackTargetEntityWithCurrentItem(entity)
        }

        if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, entity.creatureAttribute) > 0F) {
            mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC)
        }

        // Start blocking after attack
        if ((!afterTickPatchValue.get() || !autoBlockModeValue.get()
                .equals("AfterTick", true)) && (mc.thePlayer.isBlocking || canBlock)
        )
            startBlocking(entity, interactAutoBlockValue.get())
    }

    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: Entity): Boolean {
        if (rotations.get().equals("none", true)) return true

        val disabler = NightX.moduleManager.getModule(Disabler::class.java)!!
        val modify = disabler.canModifyRotation

        if (modify) return true // just ignore then

        var defRotation = getTargetRotation(entity) ?: return false

        if (defRotation != RotationUtils.serverRotation && roundTurnAngle.get())
            defRotation.yaw = RotationUtils.roundRotation(defRotation.yaw, roundAngleDirs.get())

        if (silentRotationValue.get()) {
            RotationUtils.setTargetRotation(
                defRotation,
                if (aacValue.get() && !rotations.get().equals("Spin", ignoreCase = true)) 15 else 0
            )
        } else {
            defRotation.toPlayer(mc.thePlayer!!)
        }

        return true
    }

    private fun getTargetRotation(entity: Entity): Rotation? {
        var boundingBox = entity.entityBoundingBox
        if (rotations.get().equals("Vanilla", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return RotationUtils.serverRotation

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange,
                RandomUtils.nextFloat(minRand.get(), maxRand.get()),
                randomCenterNewValue.get()
            ) ?: return null

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation, rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            return limitedRotation
        }
        if (rotations.get().equals("Spin", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return RotationUtils.serverRotation

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                false,
                false,
                false,
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return null

            return rotation
        }
        if (rotations.get().equals("Full", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                RotationUtils.OtherRotation(
                    boundingBox, RotationUtils.getCenter(entity.entityBoundingBox), predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(), maxRange
                ), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            return limitedRotation
        }
        return RotationUtils.serverRotation
    }

    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        if (rotations.get().equals("none", true)) {
            hitable = true
            return
        }

        val disabler = NightX.moduleManager.getModule(Disabler::class.java)!!

        // Modify hit check for some situations
        if (rotations.get().equals("spin", true)) {
            hitable = target!!.hurtTime <= spinHurtTimeValue.get()
            return
        }

        // Completely disable rotation check if turn speed equals to 0 or NoHitCheck is enabled
        if (maxTurnSpeed.get() <= 0F || noHitCheck.get() || disabler.canModifyRotation) {
            hitable = true
            return
        }

        val reach = min(maxRange.toDouble(), mc.thePlayer.getDistanceToEntityBox(target!!)) + 1

        if (raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach) {
                (!livingRaycastValue.get() || it is EntityLivingBase && it !is EntityArmorStand) &&
                        (isEnemy(it) || raycastIgnoredValue.get() || aacValue.get() && mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                            it,
                            it.entityBoundingBox
                        ).isNotEmpty())
            }

            if (raycastValue.get() && raycastedEntity is EntityLivingBase
                && (!EntityUtils.isFriend(raycastedEntity))
            )
                currentTarget = raycastedEntity

            hitable = if (maxTurnSpeed.get() > 0F) currentTarget == raycastedEntity else true
        } else
            hitable = RotationUtils.isFaced(currentTarget, reach)
    }

    /**
     * Start blocking
     */


    private fun startBlocking(interactEntity: Entity, interact: Boolean) {
        if (!canSmartBlock || autoBlockModeValue.get()
                .equals("none", true) || !(blockRate.get() > 0 && Random().nextInt(100) <= blockRate.get())
        )
            return

        if (!abThruWallValue.get() && interactEntity is EntityLivingBase) {
            val entityLB = interactEntity
            if (!entityLB.canEntityBeSeen(mc.thePlayer!!)) {
                fakeBlock = true
                return
            }
        }

        if (autoBlockModeValue.get().equals("ncp", true)) {
            PacketUtils.sendPacketNoEvent(
                C08PacketPlayerBlockPlacement(
                    BlockPos(-1, -1, -1),
                    255,
                    null,
                    0.0f,
                    0.0f,
                    0.0f
                )
            )
            return
        }

        if (autoBlockModeValue.get().equals("interact", true)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, true)
        }

        if (autoBlockModeValue.get().equals("oldhypixel", true)) {
            PacketUtils.sendPacketNoEvent(
                C08PacketPlayerBlockPlacement(
                    BlockPos(-1, -1, -1),
                    255,
                    mc.thePlayer.inventory.getCurrentItem(),
                    0.0f,
                    0.0f,
                    0.0f
                )
            )
            blockingStatus = true
            return
        }

        if (interact) {
            //mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, interactEntity.positionVector))
            val positionEye = mc.renderViewEntity?.getPositionEyes(1F)

            val expandSize = interactEntity.collisionBorderSize.toDouble()
            val boundingBox = interactEntity.entityBoundingBox.expand(expandSize, expandSize, expandSize)

            val (yaw, pitch) = RotationUtils.targetRotation ?: Rotation(
                mc.thePlayer!!.rotationYaw,
                mc.thePlayer!!.rotationPitch
            )
            val yawCos = cos(-yaw * 0.017453292F - Math.PI.toFloat())
            val yawSin = sin(-yaw * 0.017453292F - Math.PI.toFloat())
            val pitchCos = -cos(-pitch * 0.017453292F)
            val pitchSin = sin(-pitch * 0.017453292F)
            val range = min(maxRange.toDouble(), mc.thePlayer!!.getDistanceToEntityBox(interactEntity)) + 1
            val lookAt = positionEye!!.addVector(yawSin * pitchCos * range, pitchSin * range, yawCos * pitchCos * range)

            val movingObject = boundingBox.calculateIntercept(positionEye, lookAt) ?: return
            val hitVec = movingObject.hitVec

            mc.netHandler.addToSendQueue(
                C02PacketUseEntity(
                    interactEntity, Vec3(
                        hitVec.xCoord - interactEntity.posX,
                        hitVec.yCoord - interactEntity.posY,
                        hitVec.zCoord - interactEntity.posZ
                    )
                )
            )
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, C02PacketUseEntity.Action.INTERACT))
        }

        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
        blockingStatus = true
    }

    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        fakeBlock = false

        if (blockingStatus) {
            if (autoBlockModeValue.get().equals("oldhypixel", true))
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos(1.0, 1.0, 1.0),
                        EnumFacing.DOWN
                    )
                )
            else
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN
                    )
                )
            if (autoBlockModeValue.get().equals("interact", true))
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, false)
        }
        blockingStatus = false
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        get() = mc.thePlayer.isSpectator || !isAlive(mc.thePlayer)
                || (blinkCheck.get() && NightX.moduleManager[Blink::class.java]!!.state) || NightX.moduleManager[Freecam::class.java]!!.state ||
                (noScaffValue.get() && NightX.moduleManager[Scaffold::class.java]!!.state)

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0 ||
            aacValue.get() && entity.hurtTime > 5


    /**
     * Check if player is able to block
     */
    private val canBlock: Boolean
        get() = mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    /**
     * Range
     */
    private val maxRange: Float
        get() = max(rangeValue.get(), throughWallsRangeValue.get())

    private fun getRange(entity: Entity) =
        (if (mc.thePlayer.getDistanceToEntityBox(entity) >= throughWallsRangeValue.get()) rangeValue.get() else throughWallsRangeValue.get()) - if (mc.thePlayer.isSprinting) rangeSprintReducementValue.get() else 0F

    /**
     * HUD Tag
     */
    override val tag: String
        get() = targetModeValue.get()
}