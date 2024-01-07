package net.aspw.client.util;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.targets.AntiBots;
import net.aspw.client.features.module.impl.targets.AntiTeams;
import net.aspw.client.util.render.ColorUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.Vec3;

import java.util.Objects;

/**
 * The type Entity utils.
 */
public final class EntityUtils extends MinecraftInstance {

    /**
     * The constant targetInvisible.
     */
    public static boolean targetInvisible = true;
    /**
     * The constant targetPlayer.
     */
    public static boolean targetPlayer = true;
    /**
     * The constant targetMobs.
     */
    public static boolean targetMobs = true;
    /**
     * The constant targetAnimals.
     */
    public static boolean targetAnimals = true;
    /**
     * The constant targetDead.
     */
    public static boolean targetDead = false;

    /**
     * Is selected boolean.
     *
     * @param entity         the entity
     * @param canAttackCheck the can attack check
     * @return the boolean
     */
    public static boolean isSelected(final Entity entity, final boolean canAttackCheck) {
        if (entity instanceof EntityLivingBase && (targetDead || entity.isEntityAlive()) && entity != mc.thePlayer) {
            if (targetInvisible || !entity.isInvisible()) {
                if (targetPlayer && entity instanceof EntityPlayer) {
                    final EntityPlayer entityPlayer = (EntityPlayer) entity;

                    if (canAttackCheck) {
                        if (AntiBots.isBot(entityPlayer))
                            return false;

                        if (isFriend(entityPlayer))
                            return false;

                        if (entityPlayer.isSpectator())
                            return false;

                        final AntiTeams antiTeams = Client.moduleManager.getModule(AntiTeams.class);
                        return !Objects.requireNonNull(antiTeams).getState() || !antiTeams.isInYourTeam(entityPlayer);
                    }

                    return true;
                }

                return targetMobs && isMob(entity) || targetAnimals && isAnimal(entity);

            }
        }
        return false;
    }

    public static boolean isLookingOnEntities(Entity entity, double maxAngleDifference) {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            return false;
        }

        float playerRotation = player.rotationYawHead;
        float playerPitch = player.rotationPitch;

        double maxAngleDifferenceRadians = Math.toRadians(maxAngleDifference);

        Vec3 lookVec = new Vec3(
                -Math.sin(Math.toRadians(playerRotation)),
                -Math.sin(Math.toRadians(playerPitch)),
                Math.cos(Math.toRadians(playerRotation))
        ).normalize();

        Vec3 playerPos = player.getPositionEyes(0.0f);
        Vec3 entityPos = entity.getPositionEyes(0.0f);

        Vec3 directionToEntity = entityPos.subtract(playerPos).normalize();
        double dotProductThreshold = lookVec.dotProduct(directionToEntity);

        return dotProductThreshold > Math.cos(maxAngleDifferenceRadians);
    }

    /**
     * Is friend boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    public static boolean isFriend(final Entity entity) {
        return entity instanceof EntityPlayer && entity.getName() != null &&
                Client.fileManager.friendsConfig.isFriend(ColorUtils.stripColor(entity.getName()));
    }

    /**
     * Is animal boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    public static boolean isAnimal(final Entity entity) {
        return entity instanceof EntityAnimal || entity instanceof EntitySquid || entity instanceof EntityGolem ||
                entity instanceof EntityBat;
    }

    /**
     * Is mob boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    public static boolean isMob(final Entity entity) {
        return entity instanceof EntityMob || entity instanceof EntityVillager || entity instanceof EntitySlime ||
                entity instanceof EntityGhast || entity instanceof EntityDragon;
    }

    /**
     * Gets name.
     *
     * @param networkPlayerInfoIn the network player info in
     * @return the name
     */
    public static String getName(final NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() :
                ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    /**
     * Gets ping.
     *
     * @param entityPlayer the entity player
     * @return the ping
     */
    public static int getPing(final EntityPlayer entityPlayer) {
        if (entityPlayer == null)
            return 0;

        final NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(entityPlayer.getUniqueID());

        return networkPlayerInfo == null ? 0 : networkPlayerInfo.getResponseTime();
    }
}