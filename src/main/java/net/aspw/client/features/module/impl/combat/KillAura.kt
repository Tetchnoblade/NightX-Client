package net.aspw.client.features.module.impl.combat

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.exploit.Disabler
import net.aspw.client.features.module.impl.other.FreeLook
import net.aspw.client.features.module.impl.player.Freecam
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.features.module.impl.targets.AntiBots
import net.aspw.client.features.module.impl.targets.AntiTeams
import net.aspw.client.features.module.impl.visual.Tracers
import net.aspw.client.protocol.Protocol
import net.aspw.client.util.*
import net.aspw.client.util.extensions.getDistanceToEntityBox
import net.aspw.client.util.extensions.getNearestPointBB
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.util.timer.TickTimer
import net.aspw.client.util.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.*
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin


@ModuleInfo(
    name = "KillAura", spacedName = "Kill Aura", description = "",
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
    private val prevRangeValue: FloatValue =
        object : FloatValue("Preview-Range", 1.5f, 0f, 3f, "m", { !noHitCheck.get() }) {}
    private val rangeValue: FloatValue = object : FloatValue("Range", 3f, 0f, 6f, "m") {}

    // Modes
    val rotations = ListValue("RotationMode", arrayOf("Undetectable", "HvH", "Zero", "None"), "Undetectable")

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

    private val animationValue = BoolValue("Animation", false)
    private val noInventoryAttackValue = BoolValue("NoInvAttack", false)
    private val checkSprintValue = BoolValue("StopSprint", false)
    private val throughWallsValue = BoolValue(
        "No-Walls",
        false
    ) { rotations.get().equals("undetectable", true) }
    private val randomValue = BoolValue("Random", true) { rotations.get().equals("undetectable", true) }
    val movementFix = BoolValue("MovementFix", false) { !rotations.get().equals("none", true) }
    private val silentMovementFix = BoolValue("SilentMovementFix", false) { !rotations.get().equals("none", true) }
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
    private val particleValue = ListValue("Particle", arrayOf("Hit", "EveryHit", "Always", "None"), "Hit")
    private val sharpnessValue = BoolValue("Sharpness", true) { !particleValue.get().equals("none", true) }
    private val criticalsValue = BoolValue("Criticals", true) { !particleValue.get().equals("none", true) }

    // AutoBlock
    val autoBlockModeValue =
        ListValue(
            "AutoBlock",
            arrayOf(
                "Packet",
                "AfterTick",
                "NCP",
                "HurtTime",
                "Click",
                "Hypixel",
                "OldHypixel",
                "OldIntave",
                "Fake",
                "None"
            ),
            "Fake"
        )

    private val interactAutoBlockValue = BoolValue(
        "InteractAutoBlock",
        false
    ) { !autoBlockModeValue.get().equals("Fake", true) && !autoBlockModeValue.get().equals("None", true) }
    private val verusAutoBlockValue = BoolValue(
        "UnBlock-Exploit",
        false
    ) { !autoBlockModeValue.get().equals("Fake", true) && !autoBlockModeValue.get().equals("None", true) }

    // Bypass
    val silentRotationValue = BoolValue("SilentRotation", true) { !rotations.get().equals("none", true) }
    private val toggleFreeLook =
        BoolValue("ToggleFreeLook", false) { !rotations.get().equals("none", true) && !silentRotationValue.get() }

    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)

    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val limitedMultiTargetsValue =
        IntegerValue("LimitedMultiTargets", 6, 1, 20) { targetModeValue.get().equals("multi", true) }

    // Visuals
    private val espValue = BoolValue("Round-ESP", true)
    private val tracerESPValue = BoolValue("Tracer-ESP", false)
    private val simpleESPValue = BoolValue("Simple-ESP", false)
    private val circleValue = BoolValue("Circle", false)

    /**
     * MODULE
     */

    // Target
    var target: EntityLivingBase? = null
    private var currentTarget: EntityLivingBase? = null
    private var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()

    private var markEntity: EntityLivingBase? = null

    // Attack delay
    private val attackTimer = MSTimer()
    private val endTimer = TickTimer()
    private var failedHit = false
    private var attackDelay = 0L
    private var clicks = 0

    // Container Delay
    private var containerOpen = -1L

    // Fake block status
    var blockingStatus = false
    private var verusBlocking = false
    var fakeBlock = false

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
        verusBlocking = false
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
        if (toggleFreeLook.get() && target != null)
            Client.moduleManager.getModule(FreeLook::class.java)!!.state = false

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

        if (toggleFreeLook.get() && !silentRotationValue.get() && target != null && !rotations.get()
                .equals("none", true)
        )
            Client.moduleManager.getModule(FreeLook::class.java)!!.state = true
    }

    fun update() {
        if (cancelRun || (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < 200))
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

            if (target?.hurtTime!! > 7.8) {
                if (animationValue.get())
                    mc.itemRenderer.resetEquippedProgress2()
            }
        }

        if (currentTarget != null && RotationUtils.targetRotation != null) {
            if (movementFix.get()) {
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

                    event.cancelEvent()
                }
            }
            if (silentMovementFix.get()) {
                update()
                RotationUtils.targetRotation!!.applyStrafeToPlayer(event)
                event.cancelEvent()
            }
        }
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        updateKA()

        if (mc.thePlayer.isBlocking || blockingStatus || target != null)
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
        if (clickOnly.get() && !mc.gameSettings.keyBindAttack.isKeyDown || mc.thePlayer.isRiding) return

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < 200)
        ) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
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
                0.toFloat() / 255.0F,
                255.toFloat() / 255.0F,
                255.toFloat() / 255.0F,
                200.toFloat() / 255.0F
            )
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 60 - 40) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * rangeValue.get() - 0.5f,
                    (sin(i * Math.PI / 180.0).toFloat() * rangeValue.get() - 0.5f)
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
                    System.currentTimeMillis() - containerOpen < 200)
        ) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        target ?: return

        if (simpleESPValue.get()) {
            RenderUtils.drawBlockBox(
                BlockPos(target!!.posX.toInt(), target!!.posY.toInt() + 2, target!!.posZ.toInt()),
                Color.WHITE,
                false
            )
        }

        if (tracerESPValue.get()) {
            val tracers = Client.moduleManager.getModule(Tracers::class.java)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(0.5F)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(false)
            GL11.glBegin(GL11.GL_LINES)
            var dist = (mc.thePlayer.getDistanceToEntity(currentTarget) * 2).toInt()
            if (dist > 255) dist = 255
            currentTarget?.let { tracers?.drawTraces(it, Color.WHITE, false) }
            GL11.glEnd()
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(true)
            GL11.glDisable(GL11.GL_BLEND)
            GlStateManager.resetColor()
        }

        if (espValue.get()) {
            if (clickOnly.get() && !mc.gameSettings.keyBindAttack.isKeyDown || mc.thePlayer.isRiding) return
            GL11.glPushMatrix()
            GL11.glTranslated(
                target!!.lastTickPosX + (target!!.posX - target!!.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                target!!.lastTickPosY + (target!!.posY - target!!.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                target!!.lastTickPosZ + (target!!.posZ - target!!.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glRotatef(90F, 1F, 0F, 0F)

            GL11.glLineWidth(3 + 1.25F)
            GL11.glColor3f(0F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_LOOP)

            for (i in 0..360 step 60 - 20) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * 0.8f,
                    (sin(i * Math.PI / 180.0).toFloat() * 0.8f)
                )
            }

            GL11.glEnd()

            GL11.glLineWidth(3f)
            GL11.glBegin(GL11.GL_LINE_LOOP)

            for (i in 0..360 step 60 - 20) { // You can change circle accuracy  (60 - accuracy)
                GL11.glColor3f(
                    255 / 255.0f,
                    255 / 255.0f,
                    0 / 255.0f
                )
                GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * 0.8f,
                    (sin(i * Math.PI / 180.0).toFloat() * 0.8f)
                )
            }

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()

            GlStateManager.resetColor()
            GL11.glColor4f(1F, 1F, 1F, 1F)
        }

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
                PacketUtils.sendPacketNoEvent(C0APacketAnimation())
                PacketUtils.sendPacketNoEvent(C02PacketUseEntity(event.targetEntity, C02PacketUseEntity.Action.ATTACK))
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

                    if (entity is EntityLivingBase && isEnemy(entity) && distance <= rangeValue.get() - 0.5f) {
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

        // Find best target
        for (entity in targets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue
            Client.moduleManager.getModule(BackTrack::class.java)?.loopThroughBacktrackData(entity) {
                if (updateRotations(entity)) {
                    return@loopThroughBacktrackData true
                }
                return@loopThroughBacktrackData false
            }

            // Set target to current entity
            target = entity
            found = true

            break
        }

        if (found)
            return

        target = null

        // Cleanup last targets when no target found and try again
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

                val antiTeams = Client.moduleManager[AntiTeams::class.java] as AntiTeams

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
        // Call attack event
        Client.eventManager.callEvent(AttackEvent(entity))

        markEntity = entity

        // Attack target
        if (Protocol.versionSlider.sliderVersion.getName() != "1.8.x")
            mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        when (swingValue.get().lowercase(Locale.getDefault())) {
            "full" -> mc.thePlayer.swingItem()

            "smart" -> {
                mc.thePlayer.isSwingInProgress = true
                mc.netHandler.addToSendQueue(C0APacketAnimation())
            }

            "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
        }

        if (Protocol.versionSlider.sliderVersion.getName() == "1.8.x")
            mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        when (particleValue.get().lowercase()) {
            "everyhit" -> {
                if (sharpnessValue.get())
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC)
                if (criticalsValue.get())
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT)
            }
        }

        if (checkSprintValue.get())
            mc.thePlayer.attackTargetEntityWithCurrentItem(entity)

        // Start blocking after attack
        if (!autoBlockModeValue.get()
                .equals("AfterTick", true) && (mc.thePlayer.isBlocking || canBlock)
        )
            startBlocking(entity, interactAutoBlockValue.get())

        if (mc.thePlayer.isBlocking || canBlock) {
            if (autoBlockModeValue.get().equals("Hypixel", true))
                startBlocking(entity, (mc.thePlayer.getDistanceToEntityBox(entity) < maxRange))
        }
    }

    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: Entity): Boolean {
        if (clickOnly.get() && !mc.gameSettings.keyBindAttack.isKeyDown || mc.thePlayer.isRiding) return false
        if (rotations.get().equals("none", true)) return true

        val disabler = Client.moduleManager.getModule(Disabler::class.java)!!
        val modify = disabler.canModifyRotation

        if (modify) return true // just ignore then

        val defRotation = getTargetRotation(entity) ?: return false

        if (defRotation != RotationUtils.serverRotation)
            defRotation.yaw = RotationUtils.roundRotation(defRotation.yaw, angleTick.get())

        if (silentRotationValue.get()) {
            RotationUtils.setTargetRotation(
                defRotation,
                0
            )
        } else {
            defRotation.toPlayer(mc.thePlayer!!)
        }

        return true
    }

    private fun getTargetRotation(entity: Entity): Rotation? {
        val boundingBox = entity.entityBoundingBox
        if (rotations.get().equals("HvH", ignoreCase = true)) {
            val limitedRotation = RotationUtils.serverRotation?.let {
                RotationUtils.limitAngleChange(
                    it,
                    RotationUtils.OtherRotation(
                        boundingBox,
                        RotationUtils.getCenter(entity.entityBoundingBox),
                        false,
                        mc.thePlayer!!.getDistanceToEntityBox(entity) < rangeValue.get() - 0.6f,
                        maxRange
                    ), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
                )
            }

            return limitedRotation
        }
        if (rotations.get().equals("Undetectable", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return RotationUtils.serverRotation

            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                false,
                true,
                false,
                if (!throughWallsValue.get()) mc.thePlayer!!.getDistanceToEntityBox(entity) < rangeValue.get() - 0.6f else false,
                maxRange,
                if (randomValue.get()) 20F else 0F,
                false
            ) ?: return null

            val limitedRotation = RotationUtils.serverRotation?.let {
                RotationUtils.limitAngleChange(
                    it, rotation,
                    (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
                )
            }

            return limitedRotation
        }
        if (rotations.get().equals("Zero", ignoreCase = true)) {
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

        val disabler = Client.moduleManager.getModule(Disabler::class.java)!!

        // Completely disable rotation check if turn speed equals to 0 or NoHitCheck is enabled
        if (maxTurnSpeed.get() <= 0F || noHitCheck.get() || disabler.canModifyRotation) {
            hitable = true
            return
        }

        val raycastedEntity =
            RaycastUtils.raycastEntity(min(attackRange.toDouble(), mc.thePlayer.getDistanceToEntityBox(target!!)) + 1) {
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
        if (autoBlockModeValue.get().equals("none", true)) {
            fakeBlock = false
        }

        if (autoBlockModeValue.get().equals("fake", true)) {
            fakeBlock = true
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
            blockingStatus = true
        }

        if (autoBlockModeValue.get().equals("click", true)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, true)
        }

        if (autoBlockModeValue.get().equals("oldintave", true)) {
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
        }

        if (autoBlockModeValue.get().equals("hurttime", true)) {
            if (mc.thePlayer.hurtTime > 1) {
                if (!mc.thePlayer.onGround)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, true)
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, false)
            }
        }

        if (autoBlockModeValue.get().equals("packet", true)) {
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
            blockingStatus = true
        }

        if (autoBlockModeValue.get().equals("hypixel", true)) {
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1))
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
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
        }

        if (interact) {
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
    }

    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        fakeBlock = false
        blockingStatus = false
        if (endTimer.hasTimePassed(2)) {
            if (autoBlockModeValue.get().equals("click", true) || autoBlockModeValue.get()
                    .equals("hurttime", true)
            ) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, false)
            }
            if (autoBlockModeValue.get().equals("oldhypixel", true)) {
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos(1.0, 1.0, 1.0),
                        EnumFacing.DOWN
                    )
                )
            }
            if (!autoBlockModeValue.get().equals("fake", true) && !autoBlockModeValue.get()
                    .equals("none", true) && !autoBlockModeValue.get()
                    .equals("hypixel", true)
            ) {
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN
                    )
                )
            }
            if (toggleFreeLook.get() && Client.moduleManager.getModule(FreeLook::class.java)?.state!!)
                Client.moduleManager.getModule(FreeLook::class.java)!!.state = false
            endTimer.reset()
        }
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        get() = mc.thePlayer.isSpectator || !isAlive(mc.thePlayer)
                || Client.moduleManager[Freecam::class.java]!!.state ||
                (Client.moduleManager[Scaffold::class.java]!!.state)

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
        get() = if (!noHitCheck.get()) max(
            rangeValue.get() + prevRangeValue.get() + 0.9f,
            if (!throughWallsValue.get()) rangeValue.get() + prevRangeValue.get() + 0.9f else 0.0f
        ) else max(rangeValue.get() - 0.3f, if (!throughWallsValue.get()) rangeValue.get() - 0.3f else 0.0f)

    private val attackRange: Float
        get() = if (!noHitCheck.get()) max(
            rangeValue.get() - 1.3f,
            if (!throughWallsValue.get()) rangeValue.get() - 1.3f else 0.0f
        ) else max(
            rangeValue.get() - 0.3f,
            if (!throughWallsValue.get()) rangeValue.get() - 0.3f else 0.0f
        )

    /**
     * HUD Tag
     */
    override val tag: String
        get() = targetModeValue.get()
}