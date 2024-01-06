package net.aspw.client.features.module.impl.combat

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9
import com.viaversion.viarewind.utils.PacketUtil
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.api.type.Type
import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.player.Freecam
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.features.module.impl.targets.AntiBots
import net.aspw.client.features.module.impl.targets.AntiTeams
import net.aspw.client.protocol.ProtocolBase
import net.aspw.client.util.*
import net.aspw.client.util.extensions.getDistanceToEntityBox
import net.aspw.client.util.extensions.getNearestPointBB
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.util.timer.TickTimer
import net.aspw.client.util.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
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
import net.raphimc.vialoader.util.VersionEnum
import org.lwjgl.opengl.GL11
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
    val rotations = ListValue("RotationMode", arrayOf("Undetectable", "Standard", "Zero", "None"), "Undetectable")

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
    private val checkSprintValue = BoolValue("StopSprint", false)
    private val throughWallsValue = BoolValue(
        "No-Walls",
        false
    ) { rotations.get().equals("undetectable", true) }
    private val randomValue = BoolValue("Random", true) { rotations.get().equals("undetectable", true) }
    private val multiCombo = BoolValue("MultiCombo", false)
    private val amountValue = IntegerValue("Multi-Packet", 5, 0, 20, "x") { multiCombo.get() }

    private val noHitCheck = BoolValue(
        "NoHitCheck",
        false
    ) { !rotations.get().equals("none", true) }

    private val movementFix =
        ListValue("MovementFix", arrayOf("Full", "Semi", "None"), "None") { !rotations.get().equals("none", true) }

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
                "Verus",
                "1.9+",
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
            .equals("None", true) && !autoBlockModeValue.get().equals("ReBlock", true)
    }

    // Bypass
    private val silentRotationValue = BoolValue("SilentRotation", true) { !rotations.get().equals("none", true) }

    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)

    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val limitedMultiTargetsValue =
        IntegerValue("LimitedMultiTargets", 6, 1, 20) { targetModeValue.get().equals("multi", true) }

    // Visuals
    private val waterParticleValue = BoolValue("WaterParticles", true)
    private val espValue = BoolValue("ESP", true)
    private val circleValue = BoolValue("Circle", false)

    /**
     * MODULE
     */

    // Target
    var target: EntityLivingBase? = null
    var currentTarget: EntityLivingBase? = null
    private var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()

    private var markEntity: EntityLivingBase? = null

    // Attack delay
    private val reBlockTimer = TickTimer()
    private val attackTimer = MSTimer()
    private val endTimer = TickTimer()
    private var failedHit = false
    private var attackDelay = 0L
    private var clicks = 0

    // Container Delay
    private var containerOpen = -1L

    // Fake block status
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
        if (autoBlockModeValue.get().equals("reblock", true)) {
            if (blockingStatus) {
                reBlockTimer.update()
                if (reBlockTimer.hasTimePassed(reBlockDelayValue.get()) && !reBlockTimer.hasTimePassed(reBlockDelayValue.get() + 10)) {
                    hitable = false
                    stopBlocking()
                }
                if (reBlockTimer.hasTimePassed(reBlockDelayValue.get() + 10))
                    reBlockTimer.reset()
            } else if (reBlockTimer.hasTimePassed(1))
                reBlockTimer.reset()
        }

        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()
        }
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
    fun onJump(event: JumpEvent) {
        if (movementFix.get() == "Full" && currentTarget != null) {
            mc.thePlayer.isSprinting = false
            event.yaw = RotationUtils.serverRotation?.yaw!!
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (!autoBlockModeValue.get().equals("verus", true)) return
        if (blockingStatus
            && ((packet is C07PacketPlayerDigging
                    && packet.status == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)
                    || packet is C08PacketPlayerBlockPlacement)
        )
            event.cancelEvent()

        if (packet is C09PacketHeldItemChange)
            blockingStatus = false
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        update()

        if (target != null) {
            if (mc.thePlayer.ticksExisted % 6 == 0 && waterParticleValue.get())
                mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.SPELL_MOB_AMBIENT)
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

        if (target != null && RotationUtils.targetRotation != null) {
            when (movementFix.get().lowercase()) {
                "full" -> {
                    val (yaw) = RotationUtils.targetRotation ?: return
                    event.yaw = yaw
                }

                "semi" -> {
                    update()
                    RotationUtils.targetRotation!!.applyStrafeToPlayer(event)
                    event.cancelEvent()
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

    @EventTarget
    fun onWorld(event: WorldEvent) {
        state = false
        Client.hud.addNotification(
            Notification(
                "KillAura was disabled",
                Notification.Type.WARNING
            )
        )
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
                    cos(i * Math.PI / 180.0).toFloat() * rangeValue.get(),
                    (sin(i * Math.PI / 180.0).toFloat() * rangeValue.get())
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

            for (i in 0..360 step 60 - 14) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * 0.8f,
                    (sin(i * Math.PI / 180.0).toFloat() * 0.8f)
                )
            }

            GL11.glEnd()

            GL11.glLineWidth(3f)
            GL11.glBegin(GL11.GL_LINE_LOOP)

            for (i in 0..360 step 60 - 14) { // You can change circle accuracy  (60 - accuracy)
                GL11.glColor3f(
                    0 / 255.0f,
                    255 / 255.0f,
                    255 / 255.0f
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
                if (ProtocolBase.getManager().targetVersion.isNewerThan(VersionEnum.r1_8))
                    mc.netHandler.addToSendQueue(
                        C02PacketUseEntity(
                            event.targetEntity,
                            C02PacketUseEntity.Action.ATTACK
                        )
                    )

                mc.netHandler.addToSendQueue(C0APacketAnimation())

                if (!ProtocolBase.getManager().targetVersion.isNewerThan(VersionEnum.r1_8))
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
        if (mc.thePlayer!!.getDistanceToEntityBox(entity) >= attackRangeValue.get() || reBlockTimer.hasTimePassed(
                reBlockDelayValue.get()
            ) && !reBlockTimer.hasTimePassed(
                reBlockDelayValue.get() + 10
            )
        ) return

        // Call attack event
        Client.eventManager.callEvent(AttackEvent(entity))

        markEntity = entity

        if (autoBlockModeValue.get().equals("vanilla", true) || autoBlockModeValue.get().equals("1.9+", true)) {
            if (blockingStatus && canBlock && endTimer.hasTimePassed(2)) {
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
        if (ProtocolBase.getManager().targetVersion.isNewerThan(VersionEnum.r1_8))
            mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        when (swingValue.get().lowercase(Locale.getDefault())) {
            "full" -> mc.thePlayer.swingItem()

            "smart" -> {
                mc.thePlayer.isSwingInProgress = true
                mc.netHandler.addToSendQueue(C0APacketAnimation())
            }

            "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
        }

        if (!ProtocolBase.getManager().targetVersion.isNewerThan(VersionEnum.r1_8))
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
        if (clickOnly.get() && !mc.gameSettings.keyBindAttack.isKeyDown || mc.thePlayer.isRiding) return false
        if (rotations.get().equals("none", true)) return true

        val defRotation = getTargetRotation(entity) ?: return false

        if (defRotation != RotationUtils.serverRotation)
            defRotation.yaw = RotationUtils.roundRotation(defRotation.yaw, angleTick.get())

        if (silentRotationValue.get()) {
            RotationUtils.setTargetRotation(
                defRotation,
                resetDelay.get()
            )
        } else {
            defRotation.toPlayer(mc.thePlayer!!)
        }

        return true
    }

    private fun getTargetRotation(entity: Entity): Rotation? {
        val boundingBox = entity.entityBoundingBox
        if (rotations.get().equals("Standard", ignoreCase = true)) {
            val limitedRotation = RotationUtils.serverRotation?.let {
                RotationUtils.limitAngleChange(
                    it,
                    RotationUtils.OtherRotation(
                        boundingBox,
                        RotationUtils.getCenter(entity.entityBoundingBox),
                        false,
                        mc.thePlayer!!.getDistanceToEntityBox(entity) < rangeValue.get(),
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
                if (!throughWallsValue.get()) mc.thePlayer!!.getDistanceToEntityBox(entity) < rangeValue.get() else false,
                maxRange,
                if (randomValue.get()) 0.5F else 0F,
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
                .equals("Fake", true) && !autoBlockModeValue.get().equals("ReBlock", true)
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
            "reblock", "vanilla", "verus" -> {
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                blockingStatus = true
            }

            "1.9+" -> {
                if (ProtocolBase.getManager().targetVersion.isNewerThan(VersionEnum.r1_8)) {
                    val useItem =
                        PacketWrapper.create(29, null, Via.getManager().connectionManager.connections.iterator().next())
                    useItem.write(Type.VAR_INT, 1)
                    PacketUtil.sendToServer(useItem, Protocol1_8To1_9::class.java, true, true)
                    blockingStatus = true
                }
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
            rangeValue.get(),
            if (!throughWallsValue.get()) rangeValue.get() else 0.0f
        ) else max(rangeValue.get(), if (!throughWallsValue.get()) rangeValue.get() else 0.0f)

    /**
     * HUD Tag
     */
    override val tag: String
        get() = targetModeValue.get()
}