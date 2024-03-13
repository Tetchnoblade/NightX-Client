package net.aspw.client.features.api;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.aspw.client.Launch;
import net.aspw.client.event.*;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.combat.KillAuraRecode;
import net.aspw.client.features.module.impl.combat.TPAura;
import net.aspw.client.features.module.impl.other.BrandSpoofer;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.BetterView;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.utils.*;
import net.aspw.client.utils.timer.MSTimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;

import java.util.Objects;

public class PacketManager extends MinecraftInstance implements Listenable {

    private static final MSTimer packetCountTimer = new MSTimer();
    public static int swing;
    public static boolean isVisualBlocking = false;
    public static int flagTicks;
    public static float eyeHeight;
    public static float lastEyeHeight;
    public static int sendPacketCounts;
    public static int receivePacketCounts;
    private static boolean flagged = false;
    private int preSend = 0;
    private int preReceive = 0;

    public static boolean shouldStopRender(Entity entity) {
        return (EntityUtils.isMob(entity) ||
                EntityUtils.isAnimal(entity) ||
                entity instanceof EntityBoat ||
                entity instanceof EntityMinecart ||
                entity instanceof EntityItemFrame ||
                entity instanceof EntityTNTPrimed ||
                entity instanceof EntityArmorStand) &&
                entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) > 45.0f;
    }

    private int getArmSwingAnimationEnd() {
        return mc.thePlayer.isPotionActive(Potion.digSpeed) ? 5 - mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() :
                (mc.thePlayer.isPotionActive(Potion.digSlowdown) ? 8 + mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier() * 2 : 6);
    }


    @EventTarget
    public void onWorld(WorldEvent event) {
        if (Objects.requireNonNull(Launch.moduleManager.getModule(BetterView.class)).getState())
            RotationUtils.Companion.enableLook();
        flagged = false;
        flagTicks = 1;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        for (Entity en : mc.theWorld.loadedEntityList) {
            if (shouldStopRender(en)) {
                en.renderDistanceWeight = 0.0;
            } else {
                en.renderDistanceWeight = 1.0;
            }
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        mc.leftClickCounter = 0;
        mc.thePlayer.prevRenderArmYaw = mc.thePlayer.rotationYaw;
        mc.thePlayer.prevRenderArmPitch = mc.thePlayer.rotationPitch;
        mc.thePlayer.renderArmYaw = mc.thePlayer.rotationYaw;
        mc.thePlayer.renderArmPitch = mc.thePlayer.rotationPitch;

        float START_HEIGHT = 1.62f;
        float END_HEIGHT;

        lastEyeHeight = eyeHeight;

        boolean isNewSneaking = ProtocolBase.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_13) && !mc.isIntegratedServerRunning();

        if (isNewSneaking)
            END_HEIGHT = 1.32f;
        else END_HEIGHT = 1.54f;

        if (mc.thePlayer.isSneaking()) {
            float delta = END_HEIGHT - eyeHeight;
            if (isNewSneaking)
                delta *= 0.68F;
            else delta *= 0.4F;
            eyeHeight = END_HEIGHT - delta;
        } else if (eyeHeight < START_HEIGHT) {
            float delta = START_HEIGHT - eyeHeight;
            if (isNewSneaking)
                delta *= 0.68F;
            else delta *= 0.4F;
            eyeHeight = START_HEIGHT - delta;
        }

        if (!Objects.requireNonNull(Launch.moduleManager.getModule(BetterView.class)).getState())
            Objects.requireNonNull(Launch.moduleManager.getModule(BetterView.class)).setState(true);
        if (!Objects.requireNonNull(Launch.moduleManager.getModule(BrandSpoofer.class)).getState())
            Objects.requireNonNull(Launch.moduleManager.getModule(BrandSpoofer.class)).setState(true);

        int max = getArmSwingAnimationEnd();
        if (Animations.olderPunching.get() && mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (!mc.thePlayer.isSwingInProgress || mc.thePlayer.swingProgressInt >= max >> 1 || mc.thePlayer.swingProgressInt < 0) {
                mc.thePlayer.isSwingInProgress = true;
                mc.thePlayer.swingProgressInt = -1;
            }
        }

        if (Animations.consoleEating.get() && MinecraftInstance.mc.thePlayer.isUsingItem() && MinecraftInstance.mc.thePlayer.getHeldItem() != null && (MinecraftInstance.mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || MinecraftInstance.mc.thePlayer.getHeldItem().getItem() instanceof ItemBucketMilk || MinecraftInstance.mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion))
            mc.getItemRenderer().resetEquippedProgress();

        if ((Animations.swingAnimValue.get().equals("Smooth") || Animations.swingAnimValue.get().equals("Dash")) && event.getEventState() == EventState.PRE) {
            if (mc.thePlayer.swingProgressInt == 1) {
                swing = 9;
            } else {
                swing = Math.max(0, swing - 1);
            }
        }

        final KillAura killAura = Objects.requireNonNull(Launch.moduleManager.getModule(KillAura.class));
        final TPAura tpAura = Objects.requireNonNull(Launch.moduleManager.getModule(TPAura.class));
        final KillAuraRecode killAuraRecode = Objects.requireNonNull(Launch.moduleManager.getModule(KillAuraRecode.class));

        if (Animations.swingLimitOnlyBlocking.get()) {
            if (mc.thePlayer.swingProgress >= 1f)
                mc.thePlayer.isSwingInProgress = false;
            if (mc.thePlayer.isBlocking() || (killAura.getState() && killAura.getTarget() != null && !killAura.getAutoBlockModeValue().get().equals("None") || tpAura.getState() && tpAura.isBlocking() || killAuraRecode.getState() && killAuraRecode.isBlocking())) {
                if (mc.thePlayer.swingProgress >= Animations.swingLimit.get())
                    mc.thePlayer.isSwingInProgress = false;
            }
        } else if (mc.thePlayer.swingProgress >= Animations.swingLimit.get()) {
            mc.thePlayer.isSwingInProgress = false;
        }

        if (Animations.fankeyBobbing.get() && MovementUtils.isMoving() && mc.thePlayer.onGround && !mc.thePlayer.isSneaking()) {
            mc.thePlayer.cameraYaw = 0.18f;
            mc.thePlayer.cameraPitch = 0.0f;
        }
    }

    @EventTarget
    public void onTeleport(TeleportEvent event) {
        flagged = true;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet.toString().startsWith("net.minecraft.network.play.client.C"))
            preSend++;
        if (packet.toString().startsWith("net.minecraft.network.play.server.S"))
            preReceive++;

        if (packetCountTimer.hasTimePassed(1000L)) {
            sendPacketCounts = preSend;
            receivePacketCounts = preReceive;
            preSend = 0;
            preReceive = 0;
            packetCountTimer.reset();
        }

        if (packet instanceof C03PacketPlayer && flagged) {
            if (mc.thePlayer.ticksExisted % 2 == 0)
                flagTicks++;
            if (flagTicks < 4) {
                if (RotationUtils.targetRotation != null) {
                    event.cancelEvent();
                    PacketUtils.sendPacketNoEvent(
                            new C03PacketPlayer.C06PacketPlayerPosLook(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ,
                                    mc.thePlayer.rotationYaw,
                                    mc.thePlayer.rotationPitch,
                                    mc.thePlayer.onGround
                            )
                    );
                    RotationUtils.Companion.reset();
                }
            } else {
                if (Objects.requireNonNull(Launch.moduleManager.getModule(Interface.class)).getState() && Objects.requireNonNull(Launch.moduleManager.getModule(Interface.class)).getTpDebugValue().get())
                    ClientUtils.displayChatMessage(Launch.CLIENT_CHAT + "tp");
                flagTicks = 1;
                flagged = false;
            }
        }

        if (ProtocolBase.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_10)) {
            if (packet instanceof C08PacketPlayerBlockPlacement) {
                ((C08PacketPlayerBlockPlacement) packet).facingX = 0.5F;
                ((C08PacketPlayerBlockPlacement) packet).facingY = 0.5F;
                ((C08PacketPlayerBlockPlacement) packet).facingZ = 0.5F;
            }
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}