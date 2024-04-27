package net.aspw.client.features.module.impl.combat

import net.aspw.client.Launch
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.movement.Flight
import net.aspw.client.features.module.impl.movement.LongJump
import net.aspw.client.features.module.impl.player.Blink
import net.aspw.client.features.module.impl.player.Freecam
import net.aspw.client.features.module.impl.player.LegitScaffold
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.features.module.impl.targets.AntiBots
import net.aspw.client.features.module.impl.targets.AntiTeams
import net.aspw.client.protocol.api.ProtocolFixer
import net.aspw.client.utils.*
import net.aspw.client.utils.extensions.getDistanceToEntityBox
import net.aspw.client.utils.extensions.getNearestPointBB
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.utils.timer.TickTimer
import net.aspw.client.utils.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.Vec3
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
    private val coolDownCheck = BoolValue("Cooldown-Check", false)
    private val clickOnly = BoolValue("Click-Only", false)

    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 12, 1, 20, { !coolDownCheck.get() }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 10, 1, 20, { !coolDownCheck.get() }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }

    // Range
    private val rangeValue: FloatValue = object : FloatValue("Target-Range", 4.5f, 0f, 9f, "m") {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = attackRangeValue.get()
            if (i > newValue) set(i)
        }
    }
    private val attackRangeValue: FloatValue = object : FloatValue("Attack-Range", 3f, 0f, 6f, "m") {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = rangeValue.get()
            if (i < newValue) set(i)
        }
    }

    // Modes
    val rotations = ListValue("RotationMode", arrayOf("Standard", "Zero", "None"), "Standard")

    // Turn Speed
    private val maxTurnSpeed: FloatValue =
        object : FloatValue(
            "MaxTurnSpeed",
            120f,
            0f,
            180f,
            "°",
            {
                !rotations.get().equals("none", true) && !rotations.get()
                    .equals("zero", true)
            }) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val v = minTurnSpeed.get()
                if (v > newValue) set(v)
            }
        }

    private val minTurnSpeed: FloatValue =
        object : FloatValue(
            "MinTurnSpeed",
            100f,
            0f,
            180f,
            "°",
            {
                !rotations.get().equals("none", true) && !rotations.get()
                    .equals("zero", true)
            }) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val v = maxTurnSpeed.get()
                if (v < newValue) set(v)
            }
        }

    private val angleTick = IntegerValue(
        "Angle-Tick",
        1,
        1,
        100
    ) { !rotations.get().equals("none", true) }

    private val resetDelay = IntegerValue(
        "Reset-Delay",
        0,
        0,
        40
    ) { !rotations.get().equals("none", true) }

    private val noInventoryAttackValue = BoolValue("NoInvAttack", false)
    private val wallCheckValue = BoolValue("WallCheck", false)
    private val checkSprintValue = BoolValue("StopSprint", false)
    private val antiBlinkValue = BoolValue("AntiBlink", false)
    private val multiCombo = BoolValue("MultiCombo", false)
    private val amountValue = IntegerValue("Multi-Packet", 5, 0, 20, "x") { multiCombo.get() }

    private val noHitCheck = BoolValue(
        "NoHitCheck",
        false
    ) { !rotations.get().equals("none", true) }

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
        "Distance"
    )
    private val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Single")

    // Bypasses
    private val swingValue = ListValue("Swing", arrayOf("Full", "Smart", "Packet", "None"), "Full")
    private val particleValue =
        ListValue("Particle", arrayOf("Vanilla", "HitOnly", "EveryHit", "Always", "None"), "Vanilla")
    private val sharpnessValue = BoolValue("Sharpness", true) {
        !particleValue.get().equals("none", true) && !particleValue.get().equals("vanilla", true)
    }
    private val criticalsValue = BoolValue("Criticals", true) {
        !particleValue.get().equals("none", true) && !particleValue.get().equals("vanilla", true)
    }

    // AutoBlock
    val autoBlockModeValue =
        ListValue(
            "AutoBlock",
            arrayOf(
                "Vanilla",
                "ReBlock",
                "Perfect",
                "Fake",
                "None"
            ),
            "Fake"
        )

    private val reBlockDelayValue =
        IntegerValue("ReBlock-Delay", 10, 1, 100) { autoBlockModeValue.get().equals("reblock", true) }

    private val interactAutoBlockValue = BoolValue(
        "InteractAutoBlock",
        false
    ) {
        !autoBlockModeValue.get().equals("Fake", true) && !autoBlockModeValue.get()
            .equals("None", true)
    }

    private val fovValue = FloatValue("Fov", 180f, 0f, 180f)

    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val limitedMultiTargetsValue =
        IntegerValue("LimitedMultiTargets", 6, 1, 20) { targetModeValue.get().equals("multi", true) }

    /**
     * MODULE
     */

    // Target
    var target: EntityLivingBase? = null
    var currentTarget: EntityLivingBase? = null
    private var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()

    // Attack delay
    private val reBlockTimer = TickTimer()
    private val attackTimer = MSTimer()
    private val endTimer = TickTimer()
    private var failedHit = false
    private var attackDelay = 0L
    private var clicks = 0

    // Fake Block
    var blockingStatus = false
    var fakeBlock = false

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
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
        reBlockTimer.reset()
        stopBlocking()
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

            when (autoBlockModeValue.get().lowercase()) {
                "reblock" -> {
                    if (blockingStatus) {
                        reBlockTimer.update()
                        if (reBlockTimer.hasTimePassed(reBlockDelayValue.get()) && !reBlockTimer.hasTimePassed(
                                reBlockDelayValue.get() + 12
                            )
                        ) {
                            hitable = false
                            stopBlocking()
                        }
                        if (reBlockTimer.hasTimePassed(reBlockDelayValue.get() + 10))
                            reBlockTimer.reset()
                    } else if (reBlockTimer.hasTimePassed(1)) {
                        reBlockTimer.reset()
                    }
                }

                "vanilla" -> {
                    if (mc.thePlayer.isBlocking || canBlock)
                        startBlocking(target!!, interactAutoBlockValue.get())
                }
            }
        }
    }

    fun update() {
        if (cancelRun)
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
        if (autoBlockModeValue.get().equals("perfect", true)) {
            if (blockingStatus
                && ((packet is C07PacketPlayerDigging
                        && packet.status == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)
                        || packet is C08PacketPlayerBlockPlacement)
            )
                event.cancelEvent()

            if (packet is C09PacketHeldItemChange)
                blockingStatus = false
        }
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        update()

        if (target != null) {
            when (particleValue.get().lowercase()) {
                "hit" -> {
                    if (target?.hurtTime!! > 9) {
                        if (sharpnessValue.get())
                            mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC)
                        if (criticalsValue.get())
                            mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT)
                    }
                }

                "always" -> {
                    if (sharpnessValue.get())
                        mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC)
                    if (criticalsValue.get())
                        mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT)
                }
            }
        }
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        updateKA()
    }

    private fun updateKA() {
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (coolDownCheck.get() && CooldownHelper.getAttackCooldownProgress() < 1f) {
            return
        }

        if (target != null && currentTarget != null) {
            endTimer.update()
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        state = false
        chat("KillAura was disabled")
    }

    /**
     * Render event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (cancelRun) return

        target ?: return

        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) &&
            currentTarget!!.hurtTime <= 10
        ) {
            clicks++
            attackTimer.reset()
            attackDelay = if (coolDownCheck.get())
                TimeUtils.randomClickDelay(20, 20)
            else TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (multiCombo.get()) {
            event.targetEntity ?: return
            repeat(amountValue.get()) {
                if (ProtocolFixer.newerThan1_8())
                    mc.netHandler.addToSendQueue(
                        C02PacketUseEntity(
                            event.targetEntity,
                            C02PacketUseEntity.Action.ATTACK
                        )
                    )

                mc.netHandler.addToSendQueue(C0APacketAnimation())

                if (!ProtocolFixer.newerThan1_8())
                    mc.netHandler.addToSendQueue(
                        C02PacketUseEntity(
                            event.targetEntity,
                            C02PacketUseEntity.Action.ATTACK
                        )
                    )
            }
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
        val multi = targetModeValue.get().equals("Multi", ignoreCase = true)
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Check is not hitable or check failrate
        if (!hitable || failHit) {
            if (failHit)
                failedHit = true
        } else {
            if (!multi) {
                attackEntity(currentTarget!!)
            } else {
                var targets = 0

                for (entity in mc.theWorld.loadedEntityList) {
                    val distance = mc.thePlayer.getDistanceToEntityBox(entity)

                    if (entity is EntityLivingBase && isEnemy(entity) && distance <= rangeValue.get()) {
                        attackEntity(entity)

                        targets += 1

                        if (limitedMultiTargetsValue.get() != 0 && limitedMultiTargetsValue.get() <= targets)
                            break
                    }
                }
            }

            prevTargetEntities.add(currentTarget!!.entityId)

            if (target == currentTarget)
                target = null
        }
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Reset fixed target to null

        // Settings
        val hurtTime = 10
        val fov = fovValue.get()
        val switchMode = targetModeValue.get().equals("Switch", ignoreCase = true)

        // Find possible targets
        val targets = mutableListOf<EntityLivingBase>()

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

        for (entity in targets) {
            if (!updateRotations(entity))
                continue

            target = entity
            found = true

            break
        }

        if (found)
            return

        target = null

        if (prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
        }
    }

    /**
     * Check if [entity] is selected as enemy with current target options and other modules
     */
    private fun isEnemy(entity: Entity?): Boolean {
        if (entity is EntityLivingBase && (EntityUtils.targetDead || isAlive(entity)) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible())
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                if (entity.isSpectator || AntiBots.isBot(entity))
                    return false

                if (EntityUtils.isFriend(entity))
                    return false

                val antiTeams = Launch.moduleManager[AntiTeams::class.java] as AntiTeams

                return !antiTeams.state || !antiTeams.isInYourTeam(entity)
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
        if (mc.thePlayer!!.getDistanceToEntityBox(entity) >= attackRangeValue.get() || reBlockTimer.hasTimePassed(
                reBlockDelayValue.get()
            ) && !reBlockTimer.hasTimePassed(
                reBlockDelayValue.get() + 10
            ) || wallCheckValue.get() && !mc.thePlayer.canEntityBeSeen(currentTarget)
        ) return

        // Call attack event
        Launch.eventManager.callEvent(AttackEvent(entity))

        if (autoBlockModeValue.get().equals("vanilla", true)) {
            if (blockingStatus && canBlock && endTimer.hasTimePassed(1)) {
                blockingStatus = false
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN
                    )
                )
            }
        }

        // Attack target
        if (ProtocolFixer.newerThan1_8())
            mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        when (swingValue.get().lowercase(Locale.getDefault())) {
            "full" -> mc.thePlayer.swingItem()

            "smart" -> {
                mc.thePlayer.isSwingInProgress = true
                mc.netHandler.addToSendQueue(C0APacketAnimation())
            }

            "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
        }

        if (!ProtocolFixer.newerThan1_8())
            mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        when (particleValue.get().lowercase()) {
            "vanilla" -> {
                if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isPotionActive(
                        Potion.blindness
                    ) && !mc.thePlayer.isRiding
                ) mc.thePlayer.onCriticalHit(entity)

                if (EnchantmentHelper.getModifierForCreature(
                        mc.thePlayer.heldItem, entity.creatureAttribute
                    ) > 0F
                ) mc.thePlayer.onEnchantmentCritical(entity)
            }

            "everyhit" -> {
                if (sharpnessValue.get())
                    mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC)
                if (criticalsValue.get())
                    mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT)
            }
        }

        if (checkSprintValue.get())
            mc.thePlayer.attackTargetEntityWithCurrentItem(entity)

        // Start blocking after attack
        if (mc.thePlayer.isBlocking || canBlock)
            startBlocking(entity, interactAutoBlockValue.get())
    }

    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: Entity): Boolean {
        if (rotations.get().equals("none", true)) return true

        val defRotation = getTargetRotation(entity) ?: return false

        if (defRotation != RotationUtils.serverRotation)
            defRotation.yaw = RotationUtils.roundRotation(defRotation.yaw, angleTick.get())

        RotationUtils.setTargetRotation(
            defRotation,
            resetDelay.get()
        )

        return true
    }

    private fun getTargetRotation(entity: Entity): Rotation? {
        val boundingBox = entity.entityBoundingBox
        if (rotations.get().equals("Standard", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return RotationUtils.serverRotation

            val limitedRotation = RotationUtils.serverRotation?.let {
                RotationUtils.limitAngleChange(
                    it,
                    RotationUtils.OtherRotation(
                        boundingBox,
                        RotationUtils.getCenter(entity.entityBoundingBox),
                        false,
                        true,
                        maxRange
                    ), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
                )
            }

            return limitedRotation
        }
        if (rotations.get().equals("Zero", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return RotationUtils.serverRotation

            return RotationUtils.calculate(getNearestPointBB(mc.thePlayer.getPositionEyes(1F), boundingBox))
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

        // Completely disable rotation check if turn speed equals to 0 or NoHitCheck is enabled
        if (maxTurnSpeed.get() <= 0F || noHitCheck.get()) {
            hitable = true
            return
        }

        val raycastedEntity =
            RaycastUtils.raycastEntity(min(maxRange.toDouble(), mc.thePlayer.getDistanceToEntityBox(target!!)) + 1) {
                (it is EntityLivingBase && it !is EntityArmorStand) &&
                        (isEnemy(it))
            }

        if (raycastedEntity is EntityLivingBase && (!EntityUtils.isFriend(raycastedEntity))) {
            currentTarget = raycastedEntity
        }

        hitable = if (maxTurnSpeed.get() > 0F) currentTarget == raycastedEntity else true
    }

    /**
     * Start blocking
     */

    private fun startBlocking(interactEntity: Entity, interact: Boolean) {
        if (blockingStatus) return

        if (interact && !autoBlockModeValue.get().equals("None", true) && !autoBlockModeValue.get()
                .equals("Fake", true)
        ) {
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

        when (autoBlockModeValue.get().lowercase()) {
            "reblock", "vanilla", "perfect" -> {
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                blockingStatus = true
            }

            "fake" -> {
                fakeBlock = true
            }

            "none" -> {
                fakeBlock = false
            }
        }
    }

    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        fakeBlock = false
        blockingStatus = false
        currentTarget = null
        if (endTimer.hasTimePassed(1)) {
            if (canBlock || mc.thePlayer.isBlocking) {
                if (!autoBlockModeValue.get().equals("fake", true) && !autoBlockModeValue.get()
                        .equals("none", true)
                ) {
                    PacketUtils.sendPacketNoEvent(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                            BlockPos.ORIGIN,
                            EnumFacing.DOWN
                        )
                    )
                }
            }
            endTimer.reset()
        }
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        get() = mc.thePlayer.isSpectator || !isAlive(mc.thePlayer) || Launch.moduleManager[Flight::class.java]!!.state && Launch.moduleManager[Flight::class.java]!!.modeValue.get()
            .equals(
                "VerusSmooth",
                true
            ) || Launch.moduleManager[LongJump::class.java]!!.state && Launch.moduleManager[LongJump::class.java]!!.modeValue.get()
            .equals("VerusHigh", true)
                || Launch.moduleManager[Freecam::class.java]!!.state ||
                Launch.moduleManager[Scaffold::class.java]!!.state || Launch.moduleManager[LegitScaffold::class.java]!!.state || Launch.moduleManager[Blink::class.java]!!.state && antiBlinkValue.get() || clickOnly.get() && !mc.gameSettings.keyBindAttack.isKeyDown || mc.thePlayer.isRiding || noInventoryAttackValue.get() && mc.currentScreen is GuiContainer

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0


    /**
     * Check if player is able to block
     */
    private val canBlock: Boolean
        get() = mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    /**
     * Range
     */
    private val maxRange: Float
        get() = max(rangeValue.get(), rangeValue.get())

    /**
     * HUD Tag
     */
    override val tag: String
        get() = targetModeValue.get()
}