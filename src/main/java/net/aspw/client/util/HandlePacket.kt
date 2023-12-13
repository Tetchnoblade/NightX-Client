package net.aspw.client.util

import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.server.*

object HandlePacket : MinecraftInstance() {
    fun handlePacket(packet: Packet<INetHandlerPlayClient?>) {
        val netHandler = mc.netHandler

        when (packet) {
            is S00PacketKeepAlive -> {
                netHandler.handleKeepAlive(packet)
            }

            is S01PacketJoinGame -> {
                netHandler.handleJoinGame(packet)
            }

            is S02PacketChat -> {
                netHandler.handleChat(packet)
            }

            is S03PacketTimeUpdate -> {
                netHandler.handleTimeUpdate(packet)
            }

            is S04PacketEntityEquipment -> {
                netHandler.handleEntityEquipment(packet)
            }

            is S05PacketSpawnPosition -> {
                netHandler.handleSpawnPosition(packet)
            }

            is S06PacketUpdateHealth -> {
                netHandler.handleUpdateHealth(packet)
            }

            is S07PacketRespawn -> {
                netHandler.handleRespawn(packet)
            }

            is S08PacketPlayerPosLook -> {
                netHandler.handlePlayerPosLook(packet)
            }

            is S09PacketHeldItemChange -> {
                netHandler.handleHeldItemChange(packet)
            }

            is S10PacketSpawnPainting -> {
                netHandler.handleSpawnPainting(packet)
            }

            is S0APacketUseBed -> {
                netHandler.handleUseBed(packet)
            }

            is S0BPacketAnimation -> {
                netHandler.handleAnimation(packet)
            }

            is S0CPacketSpawnPlayer -> {
                netHandler.handleSpawnPlayer(packet)
            }

            is S0DPacketCollectItem -> {
                netHandler.handleCollectItem(packet)
            }

            is S0EPacketSpawnObject -> {
                netHandler.handleSpawnObject(packet)
            }

            is S0FPacketSpawnMob -> {
                netHandler.handleSpawnMob(packet)
            }

            is S11PacketSpawnExperienceOrb -> {
                netHandler.handleSpawnExperienceOrb(packet)
            }

            is S12PacketEntityVelocity -> {
                netHandler.handleEntityVelocity(packet)
            }

            is S13PacketDestroyEntities -> {
                netHandler.handleDestroyEntities(packet)
            }

            is S14PacketEntity -> {
                netHandler.handleEntityMovement(packet)
            }

            is S18PacketEntityTeleport -> {
                netHandler.handleEntityTeleport(packet)
            }

            is S19PacketEntityStatus -> {
                netHandler.handleEntityStatus(packet)
            }

            is S19PacketEntityHeadLook -> {
                netHandler.handleEntityHeadLook(packet)
            }

            is S1BPacketEntityAttach -> {
                netHandler.handleEntityAttach(packet)
            }

            is S1CPacketEntityMetadata -> {
                netHandler.handleEntityMetadata(packet)
            }

            is S1DPacketEntityEffect -> {
                netHandler.handleEntityEffect(packet)
            }

            is S1EPacketRemoveEntityEffect -> {
                netHandler.handleRemoveEntityEffect(packet)
            }

            is S1FPacketSetExperience -> {
                netHandler.handleSetExperience(packet)
            }

            is S20PacketEntityProperties -> {
                netHandler.handleEntityProperties(packet)
            }

            is S21PacketChunkData -> {
                netHandler.handleChunkData(packet)
            }

            is S22PacketMultiBlockChange -> {
                netHandler.handleMultiBlockChange(packet)
            }

            is S23PacketBlockChange -> {
                netHandler.handleBlockChange(packet)
            }

            is S24PacketBlockAction -> {
                netHandler.handleBlockAction(packet)
            }

            is S25PacketBlockBreakAnim -> {
                netHandler.handleBlockBreakAnim(packet)
            }

            is S26PacketMapChunkBulk -> {
                netHandler.handleMapChunkBulk(packet)
            }

            is S27PacketExplosion -> {
                netHandler.handleExplosion(packet)
            }

            is S28PacketEffect -> {
                netHandler.handleEffect(packet)
            }

            is S29PacketSoundEffect -> {
                netHandler.handleSoundEffect(packet)
            }

            is S2APacketParticles -> {
                netHandler.handleParticles(packet)
            }

            is S2BPacketChangeGameState -> {
                netHandler.handleChangeGameState(packet)
            }

            is S2CPacketSpawnGlobalEntity -> {
                netHandler.handleSpawnGlobalEntity(packet)
            }

            is S2DPacketOpenWindow -> {
                netHandler.handleOpenWindow(packet)
            }

            is S2EPacketCloseWindow -> {
                netHandler.handleCloseWindow(packet)
            }

            is S2FPacketSetSlot -> {
                netHandler.handleSetSlot(packet)
            }

            is S30PacketWindowItems -> {
                netHandler.handleWindowItems(packet)
            }

            is S31PacketWindowProperty -> {
                netHandler.handleWindowProperty(packet)
            }

            is S32PacketConfirmTransaction -> {
                netHandler.handleConfirmTransaction(packet)
            }

            is S33PacketUpdateSign -> {
                netHandler.handleUpdateSign(packet)
            }

            is S34PacketMaps -> {
                netHandler.handleMaps(packet)
            }

            is S35PacketUpdateTileEntity -> {
                netHandler.handleUpdateTileEntity(packet)
            }

            is S36PacketSignEditorOpen -> {
                netHandler.handleSignEditorOpen(packet)
            }

            is S37PacketStatistics -> {
                netHandler.handleStatistics(packet)
            }

            is S38PacketPlayerListItem -> {
                netHandler.handlePlayerListItem(packet)
            }

            is S39PacketPlayerAbilities -> {
                netHandler.handlePlayerAbilities(packet)
            }

            is S3APacketTabComplete -> {
                netHandler.handleTabComplete(packet)
            }

            is S3BPacketScoreboardObjective -> {
                netHandler.handleScoreboardObjective(packet)
            }

            is S3CPacketUpdateScore -> {
                netHandler.handleUpdateScore(packet)
            }

            is S3DPacketDisplayScoreboard -> {
                netHandler.handleDisplayScoreboard(packet)
            }

            is S3EPacketTeams -> {
                netHandler.handleTeams(packet)
            }

            is S3FPacketCustomPayload -> {
                netHandler.handleCustomPayload(packet)
            }

            is S40PacketDisconnect -> {
                netHandler.handleDisconnect(packet)
            }

            is S41PacketServerDifficulty -> {
                netHandler.handleServerDifficulty(packet)
            }

            is S42PacketCombatEvent -> {
                netHandler.handleCombatEvent(packet)
            }

            is S43PacketCamera -> {
                netHandler.handleCamera(packet)
            }

            is S44PacketWorldBorder -> {
                netHandler.handleWorldBorder(packet)
            }

            is S45PacketTitle -> {
                netHandler.handleTitle(packet)
            }

            is S46PacketSetCompressionLevel -> {
                netHandler.handleSetCompressionLevel(packet)
            }

            is S47PacketPlayerListHeaderFooter -> {
                netHandler.handlePlayerListHeaderFooter(packet)
            }

            is S48PacketResourcePackSend -> {
                netHandler.handleResourcePack(packet)
            }

            is S49PacketUpdateEntityNBT -> {
                netHandler.handleEntityNBT(packet)
            }

            else -> {
                throw IllegalArgumentException("Unable to match packet type to handle: ${packet.javaClass}")
            }
        }
    }
}