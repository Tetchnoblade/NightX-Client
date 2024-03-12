package net.aspw.client.utils;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.targets.AntiBots;
import net.aspw.client.features.module.impl.targets.AntiTeams;
import net.aspw.client.utils.render.ColorUtils;
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

                        final AntiTeams antiTeams = Launch.moduleManager.getModule(AntiTeams.class);
                        return !Objects.requireNonNull(antiTeams).getState() || !antiTeams.isInYourTeam(entityPlayer);
                    }

                    return true;
                }

                return targetMobs && isMob(entity) || targetAnimals && isAnimal(entity);

            }
        }
        return false;
    }

    /**
     * Is friend boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    public static boolean isFriend(final Entity entity) {
        return entity instanceof EntityPlayer && entity.getName() != null &&
                Launch.fileManager.friendsConfig.isFriend(ColorUtils.stripColor(entity.getName()));
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
}