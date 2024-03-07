package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EntityKilledEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity
import net.minecraft.util.EnumParticleTypes

@ModuleInfo(name = "MoreParticles", spacedName = "More Particles", category = ModuleCategory.VISUAL)
class MoreParticles : Module() {
    private val onKillValue = BoolValue("OnKill", true)
    private val killModeValue = ListValue(
        "Kill-ParticleMode",
        arrayOf("Thunder", "Blood", "Fire", "Criticals", "Sharpness"),
        "Thunder"
    ) { onKillValue.get() }
    private val killTimesValue = IntegerValue("Kill-MultiplyParticles", 1, 1, 10) { onKillValue.get() }
    private val onHitValue = BoolValue("OnHit", false)
    private val hitModeValue = ListValue(
        "Hit-ParticleMode",
        arrayOf("Thunder", "Blood", "Fire", "Criticals", "Sharpness"),
        "Blood"
    ) { onHitValue.get() }
    private val hitTimesValue = IntegerValue("Hit-MultiplyParticles", 3, 1, 10) { onHitValue.get() }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (onHitValue.get()) {
            if (EntityUtils.isSelected(event.targetEntity, true)) {
                displayEffectFor(event.targetEntity as EntityLivingBase, "Hit")
            }
        }
    }

    @EventTarget
    fun onKilled(event: EntityKilledEvent) {
        if (onKillValue.get()) {
            displayEffectFor(event.targetEntity, "Kill")
        }
    }

    private fun displayEffectFor(entity: EntityLivingBase, showMode: String) {
        if (showMode == "Kill") {
            repeat(killTimesValue.get()) {
                when (killModeValue.get().lowercase()) {
                    "thunder" -> {
                        mc.netHandler.handleSpawnGlobalEntity(
                            S2CPacketSpawnGlobalEntity(
                                EntityLightningBolt(
                                    mc.theWorld,
                                    entity.posX,
                                    entity.posY,
                                    entity.posZ
                                )
                            )
                        )
                    }

                    "blood" -> {
                        repeat(10) {
                            mc.effectRenderer.spawnEffectParticle(
                                EnumParticleTypes.BLOCK_CRACK.particleID,
                                entity.posX,
                                entity.posY + entity.height / 2,
                                entity.posZ,
                                entity.motionX + RandomUtils.nextFloat(-0.5f, 0.5f),
                                entity.motionY + RandomUtils.nextFloat(-0.5f, 0.5f),
                                entity.motionZ + RandomUtils.nextFloat(-0.5f, 0.5f),
                                Block.getStateId(Blocks.redstone_block.defaultState)
                            )
                        }
                    }

                    "fire" ->
                        mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.LAVA)

                    "criticals" -> mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT)
                    "sharpness" -> mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC)
                }
            }
        }

        if (showMode == "Hit") {
            repeat(hitTimesValue.get()) {
                when (hitModeValue.get().lowercase()) {
                    "thunder" -> {
                        mc.netHandler.handleSpawnGlobalEntity(
                            S2CPacketSpawnGlobalEntity(
                                EntityLightningBolt(
                                    mc.theWorld,
                                    entity.posX,
                                    entity.posY,
                                    entity.posZ
                                )
                            )
                        )
                    }

                    "blood" -> {
                        repeat(10) {
                            mc.effectRenderer.spawnEffectParticle(
                                EnumParticleTypes.BLOCK_CRACK.particleID,
                                entity.posX,
                                entity.posY + entity.height / 2,
                                entity.posZ,
                                entity.motionX + RandomUtils.nextFloat(-0.5f, 0.5f),
                                entity.motionY + RandomUtils.nextFloat(-0.5f, 0.5f),
                                entity.motionZ + RandomUtils.nextFloat(-0.5f, 0.5f),
                                Block.getStateId(Blocks.redstone_block.defaultState)
                            )
                        }
                    }

                    "fire" ->
                        mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.LAVA)

                    "criticals" -> mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT)
                    "sharpness" -> mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC)
                }
            }
        }
    }
}