package net.aspw.client.features.module.impl.combat;

import net.aspw.client.Client;
import net.aspw.client.event.*;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.utils.ClientUtils;
import net.aspw.client.utils.EntityUtils;
import net.aspw.client.utils.render.ColorUtils;
import net.aspw.client.value.BoolValue;
import net.aspw.client.value.FloatValue;
import net.aspw.client.value.IntegerValue;
import net.aspw.client.value.ListValue;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.world.WorldSettings;

import java.util.*;
import java.util.stream.Stream;

@ModuleInfo(name = "AntiBot", spacedName = "Anti Bot", category = ModuleCategory.COMBAT)
public class AntiBot extends Module {
    private final BoolValue czechHekValue = new BoolValue("CzechMatrix", false);
    private final BoolValue czechHekPingCheckValue = new BoolValue("PingCheck", true, () -> czechHekValue.get());
    private final BoolValue czechHekGMCheckValue = new BoolValue("GamemodeCheck", true, () -> czechHekValue.get());
    private final BoolValue tabValue = new BoolValue("Tab", true);
    private final ListValue tabModeValue = new ListValue("TabMode", new String[]{"Equals", "Contains"}, "Contains");
    private final BoolValue entityIDValue = new BoolValue("EntityID", false);
    private final BoolValue colorValue = new BoolValue("Color", false);
    private final BoolValue livingTimeValue = new BoolValue("LivingTime", false);
    private final IntegerValue livingTimeTicksValue = new IntegerValue("LivingTimeTicks", 40, 1, 200);
    private final BoolValue groundValue = new BoolValue("Ground", false);
    private final BoolValue airValue = new BoolValue("Air", false);
    private final BoolValue invalidGroundValue = new BoolValue("InvalidGround", false);
    private final BoolValue swingValue = new BoolValue("Swing", false);
    private final BoolValue healthValue = new BoolValue("Health", false);
    private final BoolValue invalidHealthValue = new BoolValue("InvalidHealth", false);
    private final FloatValue minHealthValue = new FloatValue("MinHealth", 0F, 0F, 100F);
    private final FloatValue maxHealthValue = new FloatValue("MaxHealth", 20F, 0F, 100F);
    private final BoolValue derpValue = new BoolValue("Derp", false);
    private final BoolValue wasInvisibleValue = new BoolValue("WasInvisible", false);
    private final BoolValue armorValue = new BoolValue("Armor", false);
    private final BoolValue pingValue = new BoolValue("Ping", false);
    private final BoolValue needHitValue = new BoolValue("NeedHit", false);
    private final BoolValue duplicateInWorldValue = new BoolValue("DuplicateInWorld", false);
    private final BoolValue drvcValue = new BoolValue("ReverseCheck", true, () -> duplicateInWorldValue.get());
    private final BoolValue duplicateInTabValue = new BoolValue("DuplicateInTab", false);
    private final BoolValue experimentalNPCDetection = new BoolValue("ExperimentalNPCDetection", false);
    private final BoolValue illegalName = new BoolValue("IllegalName", false);
    private final BoolValue removeFromWorld = new BoolValue("RemoveFromWorld", false);
    private final IntegerValue removeIntervalValue = new IntegerValue("Remove-Interval", 20, 1, 100, " tick");
    private final BoolValue debugValue = new BoolValue("Debug", false);

    private final List<Integer> ground = new ArrayList<>();
    private final List<Integer> air = new ArrayList<>();
    private final Map<Integer, Integer> invalidGround = new HashMap<>();
    private final List<Integer> swing = new ArrayList<>();
    private final List<Integer> invisible = new ArrayList<>();
    private final List<Integer> hitted = new ArrayList<>();

    private boolean wasAdded = (mc.thePlayer != null);

    public static boolean isBot(final EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer) || entity == mc.thePlayer)
            return false;

        final AntiBot antiBot = Client.moduleManager.getModule(AntiBot.class);

        if (antiBot == null || !antiBot.getState())
            return false;

        if (antiBot.experimentalNPCDetection.get() && (entity.getDisplayName().getUnformattedText().toLowerCase().contains("npc") || entity.getDisplayName().getUnformattedText().toLowerCase().contains("cit-")))
            return true;

        if (antiBot.illegalName.get() && (entity.getName().contains(" ") || entity.getDisplayName().getUnformattedText().contains(" ")))
            return true;

        if (antiBot.colorValue.get() && !entity.getDisplayName().getFormattedText()
                .replace("§r", "").contains("§"))
            return true;

        if (antiBot.livingTimeValue.get() && entity.ticksExisted < antiBot.livingTimeTicksValue.get())
            return true;

        if (antiBot.groundValue.get() && !antiBot.ground.contains(entity.getEntityId()))
            return true;

        if (antiBot.airValue.get() && !antiBot.air.contains(entity.getEntityId()))
            return true;

        if (antiBot.swingValue.get() && !antiBot.swing.contains(entity.getEntityId()))
            return true;

        if (antiBot.invalidHealthValue.get() && entity.getHealth() == Double.NaN)
            return true;

        if (antiBot.healthValue.get() && (entity.getHealth() > antiBot.maxHealthValue.get() || entity.getHealth() < antiBot.minHealthValue.get()))
            return true;

        if (antiBot.entityIDValue.get() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1))
            return true;

        if (antiBot.derpValue.get() && (entity.rotationPitch > 90F || entity.rotationPitch < -90F))
            return true;

        if (antiBot.wasInvisibleValue.get() && antiBot.invisible.contains(entity.getEntityId()))
            return true;

        if (antiBot.armorValue.get()) {
            final EntityPlayer player = (EntityPlayer) entity;

            if (player.inventory.armorInventory[0] == null && player.inventory.armorInventory[1] == null &&
                    player.inventory.armorInventory[2] == null && player.inventory.armorInventory[3] == null)
                return true;
        }

        if (antiBot.pingValue.get()) {
            EntityPlayer player = (EntityPlayer) entity;

            if (mc.getNetHandler().getPlayerInfo(player.getUniqueID()) != null && mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime() == 0)
                return true;
        }

        if (antiBot.needHitValue.get() && !antiBot.hitted.contains(entity.getEntityId()))
            return true;

        if (antiBot.invalidGroundValue.get() && antiBot.invalidGround.getOrDefault(entity.getEntityId(), 0) >= 10)
            return true;

        if (antiBot.tabValue.get()) {
            final boolean equals = antiBot.tabModeValue.get().equalsIgnoreCase("Equals");
            final String targetName = ColorUtils.stripColor(entity.getDisplayName().getFormattedText());

            if (targetName != null) {
                for (final NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    final String networkName = ColorUtils.stripColor(EntityUtils.getName(networkPlayerInfo));

                    if (networkName == null)
                        continue;

                    if (equals ? targetName.equals(networkName) : targetName.contains(networkName))
                        return false;
                }

                return true;
            }
        }

        if (antiBot.duplicateInWorldValue.get()) {
            if (antiBot.drvcValue.get() && reverse(mc.theWorld.loadedEntityList.stream())
                    .filter(currEntity -> currEntity instanceof EntityPlayer && ((EntityPlayer) currEntity)
                            .getDisplayNameString().equals(((EntityPlayer) currEntity).getDisplayNameString()))
                    .count() > 1)
                return true;

            if (mc.theWorld.loadedEntityList.stream()
                    .filter(currEntity -> currEntity instanceof EntityPlayer && ((EntityPlayer) currEntity)
                            .getDisplayNameString().equals(((EntityPlayer) currEntity).getDisplayNameString()))
                    .count() > 1)
                return true;
        }

        if (antiBot.duplicateInTabValue.get()) {
            if (mc.getNetHandler().getPlayerInfoMap().stream()
                    .filter(networkPlayer -> entity.getName().equals(ColorUtils.stripColor(EntityUtils.getName(networkPlayer))))
                    .count() > 1)
                return true;
        }

        return entity.getName().isEmpty() || entity.getName().equals(mc.thePlayer.getName());
    }

    private static <T> Stream<T> reverse(Stream<T> stream) { // from Don't Panic!
        LinkedList<T> stack = new LinkedList<>();
        stream.forEach(stack::push);
        return stack.stream();
    }

    @Override
    public void onDisable() {
        clearAll();
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        if (removeFromWorld.get() && mc.thePlayer.ticksExisted > 0 && mc.thePlayer.ticksExisted % removeIntervalValue.get() == 0) {
            List<EntityPlayer> ent = new ArrayList<>();
            for (EntityPlayer entity : mc.theWorld.playerEntities) {
                if (entity != mc.thePlayer && isBot(entity))
                    ent.add(entity);
            }
            if (ent.isEmpty()) return;
            for (EntityPlayer e : ent) {
                mc.theWorld.removeEntity(e);
                if (debugValue.get())
                    ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§fRemoved §r" + e.getName() + " §fdue to it being a bot.");
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        final Packet<?> packet = event.getPacket();

        if (czechHekValue.get()) {
            if (packet instanceof S41PacketServerDifficulty) wasAdded = false;
            if (packet instanceof S38PacketPlayerListItem) {
                final S38PacketPlayerListItem packetListItem = (S38PacketPlayerListItem) event.getPacket();
                final S38PacketPlayerListItem.AddPlayerData data = packetListItem.getEntries().get(0);

                if (data.getProfile() != null && data.getProfile().getName() != null) {
                    if (!wasAdded)
                        wasAdded = data.getProfile().getName().equals(mc.thePlayer.getName());
                    else if (!mc.thePlayer.isSpectator() && !mc.thePlayer.capabilities.allowFlying && (!czechHekPingCheckValue.get() || data.getPing() != 0) && (!czechHekGMCheckValue.get() || data.getGameMode() != WorldSettings.GameType.NOT_SET)) {
                        event.cancelEvent();
                        if (debugValue.get())
                            ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§fPrevented §r" + data.getProfile().getName() + " §ffrom spawning.");
                    }
                }
            }
        }

        if (packet instanceof S14PacketEntity) {
            final S14PacketEntity packetEntity = (S14PacketEntity) event.getPacket();
            final Entity entity = packetEntity.getEntity(mc.theWorld);

            if (entity instanceof EntityPlayer) {
                if (packetEntity.getOnGround() && !ground.contains(entity.getEntityId()))
                    ground.add(entity.getEntityId());

                if (!packetEntity.getOnGround() && !air.contains(entity.getEntityId()))
                    air.add(entity.getEntityId());

                if (packetEntity.getOnGround()) {
                    if (entity.prevPosY != entity.posY)
                        invalidGround.put(entity.getEntityId(), invalidGround.getOrDefault(entity.getEntityId(), 0) + 1);
                } else {
                    final int currentVL = invalidGround.getOrDefault(entity.getEntityId(), 0) / 2;

                    if (currentVL <= 0)
                        invalidGround.remove(entity.getEntityId());
                    else
                        invalidGround.put(entity.getEntityId(), currentVL);
                }

                if (entity.isInvisible() && !invisible.contains(entity.getEntityId()))
                    invisible.add(entity.getEntityId());
            }
        }

        if (packet instanceof S0BPacketAnimation) {
            final S0BPacketAnimation packetAnimation = (S0BPacketAnimation) event.getPacket();
            final Entity entity = mc.theWorld.getEntityByID(packetAnimation.getEntityID());

            if (entity instanceof EntityLivingBase && packetAnimation.getAnimationType() == 0 && !swing.contains(entity.getEntityId()))
                swing.add(entity.getEntityId());
        }
    }

    @EventTarget
    public void onAttack(final AttackEvent e) {
        final Entity entity = e.getTargetEntity();

        if (entity instanceof EntityLivingBase && !hitted.contains(entity.getEntityId()))
            hitted.add(entity.getEntityId());
    }

    @EventTarget
    public void onWorld(final WorldEvent event) {
        clearAll();
    }

    private void clearAll() {
        hitted.clear();
        swing.clear();
        ground.clear();
        invalidGround.clear();
        invisible.clear();
    }

}
