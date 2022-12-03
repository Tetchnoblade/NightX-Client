package net.aspw.nightx.features.module.modules.movement;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.*;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.features.module.modules.exploit.Damage;
import net.aspw.nightx.ui.client.hud.element.elements.Notification;
import net.aspw.nightx.utils.*;
import net.aspw.nightx.utils.misc.RandomUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.utils.timer.MSTimer;
import net.aspw.nightx.utils.timer.TickTimer;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSlime;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@ModuleInfo(name = "Flight", category = ModuleCategory.MOVEMENT)
public class Flight extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{
            // Motion-based fly modes, or vanilla one.
            "Motion",
            "Creative",
            "Damage",
            "Pearl",

            // Specified fly modes for NCP.
            "NCP",
            "OldNCP",

            // Old AAC fly modes.
            "AAC1.9.10",
            "AAC3.0.5",
            "AAC3.1.6-Gomme",
            "AAC3.3.12",
            "AAC3.3.12-Glide",
            "AAC3.3.13",

            // New fly using exploits.
            "AAC5-Vanilla",
            "Exploit",
            "Zoom",

            // For other servers, mostly outdated.
            "Cubecraft",
            "Rewinside",
            "TeleportRewinside",
            "Funcraft",
            "Mineplex",
            "NeruxVace",
            "Minesucht",

            // Specified fly modes for Verus.
            "Verus",
            "VerusLowHop",

            // Old Spartan fly modes.
            "Spartan",
            "Spartan2",
            "BugSpartan",

            // Old Hypixel modes.
            "Hypixel",
            "BoostHypixel",
            "FreeHypixel",

            // Other anticheats' fly modes.
            "MineSecure",
            "HawkEye",
            "HAC",
            "WatchCat",
            "Slime",

            // Other exploit-based stuffs.
            "Jetpack",
            "KeepAlive",
            "Flag",
            "Clip",
            "Jump",
            "Derp",
            "Collide"
    }, "Motion");

    private final FloatValue vanillaSpeedValue = new FloatValue("Speed", 1F, 0F, 5F, () -> {
        return (modeValue.get().equalsIgnoreCase("motion") || modeValue.get().equalsIgnoreCase("damage") || modeValue.get().equalsIgnoreCase("pearl") || modeValue.get().equalsIgnoreCase("aac5-vanilla") || modeValue.get().equalsIgnoreCase("bugspartan") || modeValue.get().equalsIgnoreCase("keepalive") || modeValue.get().equalsIgnoreCase("derp"));
    });
    private final FloatValue vanillaVSpeedValue = new FloatValue("V-Speed", 0.6F, 0F, 5F, () -> modeValue.get().equalsIgnoreCase("motion"));
    private final FloatValue vanillaMotionYValue = new FloatValue("Y-Motion", 0F, -1F, 1F, () -> modeValue.get().equalsIgnoreCase("motion"));
    private final BoolValue vanillaKickBypassValue = new BoolValue("AntiKick", false, () -> modeValue.get().equalsIgnoreCase("motion") || modeValue.get().equalsIgnoreCase("creative"));
    private final BoolValue groundSpoofValue = new BoolValue("SpoofGround", false, () -> modeValue.get().equalsIgnoreCase("motion") || modeValue.get().equalsIgnoreCase("creative"));

    private final FloatValue ncpMotionValue = new FloatValue("NCPMotion", 0.04F, 0F, 1F, () -> modeValue.get().equalsIgnoreCase("ncp"));

    // Verus
    private final ListValue verusDmgModeValue = new ListValue("Verus-DamageMode", new String[]{"None", "Instant", "InstantC06", "Jump"}, "Instant", () -> modeValue.get().equalsIgnoreCase("verus"));
    private final ListValue verusBoostModeValue = new ListValue("Verus-BoostMode", new String[]{"Static", "Gradual"}, "Gradual", () -> modeValue.get().equalsIgnoreCase("verus") && !verusDmgModeValue.get().equalsIgnoreCase("none"));
    private final BoolValue verusReDamageValue = new BoolValue("Verus-ReDamage", false, () -> modeValue.get().equalsIgnoreCase("verus") && !verusDmgModeValue.get().equalsIgnoreCase("none") && !verusDmgModeValue.get().equalsIgnoreCase("jump"));
    private final IntegerValue verusReDmgTickValue = new IntegerValue("Verus-ReDamage-Ticks", 100, 0, 300, () -> modeValue.get().equalsIgnoreCase("verus") && !verusDmgModeValue.get().equalsIgnoreCase("none") && !verusDmgModeValue.get().equalsIgnoreCase("jump") && verusReDamageValue.get());
    private final FloatValue verusSpeedValue = new FloatValue("Verus-Speed", 1F, 0F, 10F, () -> modeValue.get().equalsIgnoreCase("verus") && !verusDmgModeValue.get().equalsIgnoreCase("none"));
    private final FloatValue verusTimerValue = new FloatValue("Verus-Timer", 1F, 0.1F, 10F, () -> modeValue.get().equalsIgnoreCase("verus") && !verusDmgModeValue.get().equalsIgnoreCase("none"));
    private final IntegerValue verusDmgTickValue = new IntegerValue("Verus-Ticks", 200, 0, 300, () -> modeValue.get().equalsIgnoreCase("verus") && !verusDmgModeValue.get().equalsIgnoreCase("none"));
    private final BoolValue verusVisualValue = new BoolValue("Verus-VisualPos", true, () -> modeValue.get().equalsIgnoreCase("verus"));
    private final FloatValue verusVisualHeightValue = new FloatValue("Verus-VisualHeight", 0.3F, 0F, 1F, () -> modeValue.get().equalsIgnoreCase("verus") && verusVisualValue.get());
    private final BoolValue verusSpoofGround = new BoolValue("Verus-SpoofGround", true, () -> modeValue.get().equalsIgnoreCase("verus"));

    // AAC
    private final BoolValue aac5NofallValue = new BoolValue("AAC5-NoFall", true, () -> modeValue.get().equalsIgnoreCase("aac5-vanilla"));
    private final BoolValue aac5UseC04Packet = new BoolValue("AAC5-UseC04", true, () -> modeValue.get().equalsIgnoreCase("aac5-vanilla"));
    private final ListValue aac5Packet = new ListValue("AAC5-Packet", new String[]{"Original", "Rise", "Other"}, "Original", () -> modeValue.get().equalsIgnoreCase("aac5-vanilla")); // Original is from UnlegitMC/FDPClient.
    private final IntegerValue aac5PursePacketsValue = new IntegerValue("AAC5-Purse", 7, 3, 20, () -> modeValue.get().equalsIgnoreCase("aac5-vanilla"));

    private final IntegerValue clipDelay = new IntegerValue("Clip-DelayTick", 25, 1, 50, () -> modeValue.get().equalsIgnoreCase("clip"));
    private final FloatValue clipH = new FloatValue("Clip-Horizontal", 8F, 0, 10, () -> modeValue.get().equalsIgnoreCase("clip"));
    private final FloatValue clipV = new FloatValue("Clip-Vertical", -1.75F, -10, 10, () -> modeValue.get().equalsIgnoreCase("clip"));
    private final FloatValue clipMotionY = new FloatValue("Clip-MotionY", 0F, -2, 2, () -> modeValue.get().equalsIgnoreCase("clip"));
    private final FloatValue clipTimer = new FloatValue("Clip-Timer", 1F, 0.08F, 10F, () -> modeValue.get().equalsIgnoreCase("clip"));
    private final BoolValue clipGroundSpoof = new BoolValue("Clip-GroundSpoof", true, () -> modeValue.get().equalsIgnoreCase("clip"));
    private final BoolValue clipCollisionCheck = new BoolValue("Clip-CollisionCheck", false, () -> modeValue.get().equalsIgnoreCase("clip"));
    private final BoolValue clipNoMove = new BoolValue("Clip-NoMove", true, () -> modeValue.get().equalsIgnoreCase("clip"));

    // Pearl
    private final ListValue pearlActivateCheck = new ListValue("PearlActiveCheck", new String[]{"Teleport", "Damage"}, "Teleport", () -> modeValue.get().equalsIgnoreCase("pearl"));

    // AAC
    private final FloatValue aacSpeedValue = new FloatValue("AAC1.9.10-Speed", 0.3F, 0F, 1F, () -> modeValue.get().equalsIgnoreCase("aac1.9.10"));
    private final BoolValue aacFast = new BoolValue("AAC3.0.5-Fast", true, () -> modeValue.get().equalsIgnoreCase("aac3.0.5"));
    private final FloatValue aacMotion = new FloatValue("AAC3.3.12-Motion", 10F, 0.1F, 10F, () -> modeValue.get().equalsIgnoreCase("aac3.3.12"));
    private final FloatValue aacMotion2 = new FloatValue("AAC3.3.13-Motion", 10F, 0.1F, 10F, () -> modeValue.get().equalsIgnoreCase("aac3.3.13"));

    private final ListValue hypixelBoostMode = new ListValue("BoostHypixel-Mode", new String[]{"Default", "MorePackets", "NCP"}, "Default", () -> modeValue.get().equalsIgnoreCase("boosthypixel"));
    private final BoolValue hypixelVisualY = new BoolValue("BoostHypixel-VisualY", true, () -> modeValue.get().equalsIgnoreCase("boosthypixel"));
    private final BoolValue hypixelC04 = new BoolValue("BoostHypixel-MoreC04s", false, () -> modeValue.get().equalsIgnoreCase("boosthypixel"));

    // Hypixel
    private final BoolValue hypixelBoost = new BoolValue("Hypixel-Boost", false, () -> modeValue.get().equalsIgnoreCase("hypixel"));
    private final IntegerValue hypixelBoostDelay = new IntegerValue("Hypixel-BoostDelay", 1200, 0, 2000, () -> modeValue.get().equalsIgnoreCase("hypixel"));
    private final FloatValue hypixelBoostTimer = new FloatValue("Hypixel-BoostTimer", 1F, 0F, 5F, () -> modeValue.get().equalsIgnoreCase("hypixel"));

    private final FloatValue mineplexSpeedValue = new FloatValue("MineplexSpeed", 1F, 0.5F, 10F, () -> modeValue.get().equalsIgnoreCase("mineplex"));
    private final IntegerValue neruxVaceTicks = new IntegerValue("NeruxVace-Ticks", 6, 0, 20, () -> modeValue.get().equalsIgnoreCase("neruxvace"));

    private final BoolValue fakeSprintingValue = new BoolValue("FakeSprinting", false, () -> {
        return modeValue.get().toLowerCase().contains("exploit");
    });

    private final BoolValue fakeNoMoveValue = new BoolValue("FakeNoMove", false, () -> {
        return modeValue.get().toLowerCase().contains("exploit");
    });

    // Visuals
    private final BoolValue fakeDmgValue = new BoolValue("FakeDamage", false);
    private final BoolValue bobbingValue = new BoolValue("Bobbing", false);
    private final FloatValue bobbingAmountValue = new FloatValue("BobbingAmount", 0.1F, 0F, 1F, () -> bobbingValue.get());
    private final BoolValue markValue = new BoolValue("Mark", false);
    private final MSTimer flyTimer = new MSTimer();
    private final MSTimer groundTimer = new MSTimer();
    private final MSTimer boostTimer = new MSTimer();
    private final MSTimer mineSecureVClipTimer = new MSTimer();
    private final MSTimer mineplexTimer = new MSTimer();
    private final TickTimer spartanTimer = new TickTimer();
    private final TickTimer verusTimer = new TickTimer();
    private final TickTimer hypixelTimer = new TickTimer();
    private final TickTimer cubecraftTeleportTickTimer = new TickTimer();
    private final TickTimer freeHypixelTimer = new TickTimer();
    private final ArrayList<C03PacketPlayer> aac5C03List = new ArrayList<>();
    public int wdState, wdTick = 0;
    private BlockPos lastPosition;
    private double startY;
    private boolean shouldFakeJump, shouldActive = false;
    private boolean noPacketModify;
    private boolean isBoostActive = false;
    private boolean noFlag;
    private int pearlState = 0;
    private double bypassValue = 0.f;
    private boolean wasDead;
    private int boostTicks, dmgCooldown = 0;
    private int verusJumpTimes = 0;
    private boolean verusDmged, shouldActiveDmg = false;
    private float lastYaw, lastPitch;
    private double moveSpeed = 0.0;
    private int expectItemStack = -1;
    private double aacJump;
    private int aac3delay;
    private int aac3glideDelay;
    private long minesuchtTP;
    private int boostHypixelState = 1;
    private double lastDistance;
    private boolean failedStart = false;
    private float freeHypixelYaw;
    private float freeHypixelPitch;

    private void doMove(double h, double v) {
        if (mc.thePlayer == null) return;

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);

        double expectedX = x + (-Math.sin(yaw) * h);
        double expectedY = y + v;
        double expectedZ = z + (Math.cos(yaw) * h);

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(expectedX, expectedY, expectedZ, mc.thePlayer.onGround));
        mc.thePlayer.setPosition(expectedX, expectedY, expectedZ);
    }

    private void hClip(double x, double y, double z) {
        if (mc.thePlayer == null) return;

        double expectedX = mc.thePlayer.posX + x;
        double expectedY = mc.thePlayer.posY + y;
        double expectedZ = mc.thePlayer.posZ + z;

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(expectedX, expectedY, expectedZ, mc.thePlayer.onGround));
        mc.thePlayer.setPosition(expectedX, expectedY, expectedZ);
    }

    private double[] getMoves(double h, double v) {
        if (mc.thePlayer == null) return new double[]{0.0, 0.0, 0.0};

        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);

        double expectedX = (-Math.sin(yaw) * h);
        double expectedY = v;
        double expectedZ = (Math.cos(yaw) * h);

        return new double[]{expectedX, expectedY, expectedZ};
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        noPacketModify = true;

        verusTimer.reset();
        flyTimer.reset();

        bypassValue = 0.F;

        shouldFakeJump = false;
        shouldActive = true;
        isBoostActive = false;

        expectItemStack = -1;

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        lastYaw = mc.thePlayer.rotationYaw;
        lastPitch = mc.thePlayer.rotationPitch;

        final String mode = modeValue.get();

        boostTicks = 0;
        dmgCooldown = 0;
        pearlState = 0;

        verusJumpTimes = 0;
        verusDmged = false;

        moveSpeed = 0;
        wdState = 0;
        wdTick = 0;

        switch (mode.toLowerCase()) {
            case "ncp":
                mc.thePlayer.motionY = -ncpMotionValue.get();

                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = -0.5D;
                MovementUtils.strafe();
                break;
            case "oldncp":
                if (startY > mc.thePlayer.posY)
                    mc.thePlayer.motionY = -0.000000000000000000000000000000001D;

                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = -0.2D;

                if (mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.posY < (startY - 0.1D))
                    mc.thePlayer.motionY = 0.2D;
                MovementUtils.strafe();
                break;
            case "verus":
                if (verusDmgModeValue.get().equalsIgnoreCase("Instant")) {
                    if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 4, 0).expand(0, 0, 0)).isEmpty()) {
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y + 4, mc.thePlayer.posZ, false));
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, false));
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                        if (verusReDamageValue.get()) dmgCooldown = verusReDmgTickValue.get();
                    }
                } else if (verusDmgModeValue.get().equalsIgnoreCase("InstantC06")) {
                    if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 4, 0).expand(0, 0, 0)).isEmpty()) {
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y + 4, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                        if (verusReDamageValue.get()) dmgCooldown = verusReDmgTickValue.get();
                    }
                } else if (verusDmgModeValue.get().equalsIgnoreCase("Jump")) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        verusJumpTimes = 1;
                    }
                } else {
                    // set dmged = true since there's no damage method
                    verusDmged = true;
                }
                if (verusVisualValue.get())
                    mc.thePlayer.setPosition(mc.thePlayer.posX, y + verusVisualHeightValue.get(), mc.thePlayer.posZ);
                shouldActiveDmg = dmgCooldown > 0;
                break;
            case "bugspartan":
                for (int i = 0; i < 65; ++i) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.049D, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                }
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.1D, z, true));
                mc.thePlayer.motionX *= 0.1D;
                mc.thePlayer.motionZ *= 0.1D;
                mc.thePlayer.swingItem();
                break;
            case "funcraft":
                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump();
                moveSpeed = 1.6;
                break;
            case "zoom":
                NightX.moduleManager.getModule(Damage.class).setState(true);
                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump();
                moveSpeed = 2;
                break;
            case "slime":
                expectItemStack = getSlimeSlot();
                if (expectItemStack == -1) {
                    NightX.hud.addNotification(new Notification("The fly requires slime blocks to be activated properly."));
                    break;
                }

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    wdState = 1;
                }
                break;
            case "boosthypixel":
                if (!mc.thePlayer.onGround) break;

                if (hypixelC04.get()) for (int i = 0; i < 10; i++) //Imagine flagging to NCP.
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

                if (hypixelBoostMode.get().equalsIgnoreCase("ncp")) {
                    for (int i = 0; i < 65; i++) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.049, mc.thePlayer.posZ, false));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    }
                } else {
                    double fallDistance = hypixelBoostMode.get().equalsIgnoreCase("morepackets") ? 3.4025 : 3.0125; //add 0.0125 to ensure we get the fall dmg
                    while (fallDistance > 0) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0000013579, mc.thePlayer.posZ, false));
                        fallDistance -= 0.0624986421;
                    }
                }
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

                if (hypixelVisualY.get()) {
                    mc.thePlayer.jump();
                    mc.thePlayer.posY += 0.42F; // Visual
                }

                boostHypixelState = 1;
                moveSpeed = 0.1D;
                lastDistance = 0D;
                failedStart = false;
                break;
        }

        startY = mc.thePlayer.posY;
        noPacketModify = false;
        aacJump = -3.8D;

        if (mode.equalsIgnoreCase("freehypixel")) {
            freeHypixelTimer.reset();
            mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + 0.42D, mc.thePlayer.posZ);
            freeHypixelYaw = mc.thePlayer.rotationYaw;
            freeHypixelPitch = mc.thePlayer.rotationPitch;
        }

        if (!mode.equalsIgnoreCase("slime") && !mode.equalsIgnoreCase("exploit")
                && !mode.equalsIgnoreCase("bugspartan") && !mode.equalsIgnoreCase("verus") && !mode.equalsIgnoreCase("damage") && !mode.toLowerCase().contains("hypixel")
                && fakeDmgValue.get()) {
            mc.thePlayer.handleStatusUpdate((byte) 2);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        final Speed speed = NightX.moduleManager.getModule(Speed.class);

        if (!speed.getState()) {
            MovementUtils.strafe(0.3f);
        }

        wasDead = false;

        if (mc.thePlayer == null)
            return;

        noFlag = false;

        final String mode = modeValue.get();

        if ((!mode.toUpperCase().startsWith("AAC") && !mode.equalsIgnoreCase("Hypixel") &&
                !mode.equalsIgnoreCase("CubeCraft") && !mode.equalsIgnoreCase("Collide") && !mode.equalsIgnoreCase("Verus") && !mode.equalsIgnoreCase("Jump") && !mode.equalsIgnoreCase("creative")) || (mode.equalsIgnoreCase("pearl") && pearlState != -1)) {
        }

        if (mode.equalsIgnoreCase("AAC5-Vanilla") && !mc.isIntegratedServerRunning()) {
            sendAAC5Packets();
        }

        mc.thePlayer.capabilities.isFlying = false;

        mc.timer.timerSpeed = 1F;
        mc.thePlayer.speedInAir = 0.02F;
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        final float vanillaSpeed = vanillaSpeedValue.get();
        final float vanillaVSpeed = vanillaVSpeedValue.get();

        mc.thePlayer.noClip = false;

        switch (modeValue.get().toLowerCase()) {
            case "motion":
                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.motionY = vanillaMotionYValue.get();
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY += vanillaVSpeed;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY -= vanillaVSpeed;
                    mc.gameSettings.keyBindSneak.pressed = false;
                }
                MovementUtils.strafe(vanillaSpeed);
                handleVanillaKickBypass();
                break;
            case "cubecraft":
                mc.timer.timerSpeed = 0.6F;

                cubecraftTeleportTickTimer.update();
                break;
            case "ncp":
                mc.thePlayer.motionY = -ncpMotionValue.get();

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY = -0.5D;
                    mc.gameSettings.keyBindSneak.pressed = false;
                }
                MovementUtils.strafe();
                break;
            case "oldncp":
                if (startY > mc.thePlayer.posY)
                    mc.thePlayer.motionY = -0.000000000000000000000000000000001D;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY = -0.2D;
                    mc.gameSettings.keyBindSneak.pressed = false;
                }

                if (mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.posY < (startY - 0.1D)) {
                    mc.thePlayer.motionY = 0.2D;
                }
                MovementUtils.strafe();
                break;
            case "clip":
                mc.thePlayer.motionY = clipMotionY.get();
                mc.timer.timerSpeed = clipTimer.get();
                if (mc.thePlayer.ticksExisted % clipDelay.get() == 0) {
                    double[] expectMoves = getMoves((double) clipH.get(), (double) clipV.get());
                    if (!clipCollisionCheck.get() || mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(expectMoves[0], expectMoves[1], expectMoves[2]).expand(0, 0, 0)).isEmpty())
                        hClip(expectMoves[0], expectMoves[1], expectMoves[2]);
                }
                break;
            case "damage":
                mc.thePlayer.capabilities.isFlying = false;
                if (mc.thePlayer.hurtTime <= 0) break;
            case "derp":
            case "aac5-vanilla":
            case "bugspartan":
                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY += vanillaSpeed;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY -= vanillaSpeed;
                    mc.gameSettings.keyBindSneak.pressed = false;
                }
                MovementUtils.strafe(vanillaSpeed);
                break;
            case "verus":
                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                if (!verusDmgModeValue.get().equalsIgnoreCase("Jump") || shouldActiveDmg || verusDmged)
                    mc.thePlayer.motionY = 0;

                if (verusDmgModeValue.get().equalsIgnoreCase("Jump") && verusJumpTimes < 5) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        verusJumpTimes += 1;
                    }
                    return;
                }

                if (shouldActiveDmg) {
                    if (dmgCooldown > 0)
                        dmgCooldown--;
                    else if (verusDmged) {
                        verusDmged = false;
                        double y = mc.thePlayer.posY;
                        if (verusDmgModeValue.get().equalsIgnoreCase("Instant")) {
                            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 4, 0).expand(0, 0, 0)).isEmpty()) {
                                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y + 4, mc.thePlayer.posZ, false));
                                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, false));
                                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));
                                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                            }
                        } else if (verusDmgModeValue.get().equalsIgnoreCase("InstantC06")) {
                            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 4, 0).expand(0, 0, 0)).isEmpty()) {
                                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y + 4, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                            }
                        }
                        dmgCooldown = verusReDmgTickValue.get();
                    }
                }

                if (!verusDmged && mc.thePlayer.hurtTime > 0) {
                    verusDmged = true;
                    boostTicks = verusDmgTickValue.get();
                }

                if (boostTicks > 0) {
                    mc.timer.timerSpeed = verusTimerValue.get();
                    float motion = 0F;

                    if (verusBoostModeValue.get().equalsIgnoreCase("static")) motion = verusSpeedValue.get();
                    else motion = ((float) boostTicks / (float) verusDmgTickValue.get()) * verusSpeedValue.get();
                    boostTicks--;

                    MovementUtils.strafe(motion);
                } else if (verusDmged) {
                    mc.timer.timerSpeed = 1F;
                    MovementUtils.strafe((float) MovementUtils.getBaseMoveSpeed() * 0.6F);
                } else {
                    mc.thePlayer.movementInput.moveForward = 0F;
                    mc.thePlayer.movementInput.moveStrafe = 0F;
                }
                break;
            case "creative":
                mc.thePlayer.capabilities.isFlying = true;

                handleVanillaKickBypass();
                break;
            case "aac1.9.10":
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    aacJump += 0.2D;
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    aacJump -= 0.2D;
                    mc.gameSettings.keyBindSneak.pressed = false;
                }

                if ((startY + aacJump) > mc.thePlayer.posY) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                    mc.thePlayer.motionY = 0.8D;
                    MovementUtils.strafe(aacSpeedValue.get());
                }

                MovementUtils.strafe();
                break;
            case "aac3.0.5":
                if (aac3delay == 2)
                    mc.thePlayer.motionY = 0.1D;
                else if (aac3delay > 2)
                    aac3delay = 0;

                if (aacFast.get()) {
                    if (mc.thePlayer.movementInput.moveStrafe == 0D)
                        mc.thePlayer.jumpMovementFactor = 0.08F;
                    else
                        mc.thePlayer.jumpMovementFactor = 0F;
                }

                aac3delay++;
                break;
            case "aac3.1.6-gomme":
                mc.thePlayer.capabilities.isFlying = true;

                if (aac3delay == 2) {
                    mc.thePlayer.motionY += 0.05D;
                } else if (aac3delay > 2) {
                    mc.thePlayer.motionY -= 0.05D;
                    aac3delay = 0;
                }

                aac3delay++;

                if (!noFlag)
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                if (mc.thePlayer.posY <= 0D)
                    noFlag = true;
                break;
            case "flag":
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + mc.thePlayer.motionX * 999, mc.thePlayer.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? 1.5624 : 0.00000001) - (mc.gameSettings.keyBindSneak.isKeyDown() ? 0.0624 : 0.00000002), mc.thePlayer.posZ + mc.thePlayer.motionZ * 999, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + mc.thePlayer.motionX * 999, mc.thePlayer.posY - 6969, mc.thePlayer.posZ + mc.thePlayer.motionZ * 999, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                mc.thePlayer.setPosition(mc.thePlayer.posX + mc.thePlayer.motionX * 11, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.motionZ * 11);
                mc.thePlayer.motionY = 0F;
                break;
            case "keepalive":
                mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive());

                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY += vanillaSpeed;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY -= vanillaSpeed;
                }
                MovementUtils.strafe(vanillaSpeed);
                break;
            case "minesecure":
                mc.thePlayer.capabilities.isFlying = false;

                if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY = -0.01F;
                }

                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                MovementUtils.strafe(vanillaSpeed);

                if (mineSecureVClipTimer.hasTimePassed(150) && mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 5, mc.thePlayer.posZ, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(0.5D, -1000, 0.5D, false));
                    final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                    final double x = -Math.sin(yaw) * 0.4D;
                    final double z = Math.cos(yaw) * 0.4D;
                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);

                    mineSecureVClipTimer.reset();
                }
                break;
            case "hac":
                mc.thePlayer.motionX *= 0.8;
                mc.thePlayer.motionZ *= 0.8;
            case "hawkeye":
                mc.thePlayer.motionY = mc.thePlayer.motionY <= -0.42 ? 0.42 : -0.42;
                break;
            case "teleportrewinside":
                final Vec3 vectorStart = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                final float yaw = -mc.thePlayer.rotationYaw;
                final float pitch = -mc.thePlayer.rotationPitch;
                final double length = 9.9;
                final Vec3 vectorEnd = new Vec3(
                        Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * length + vectorStart.xCoord,
                        Math.sin(Math.toRadians(pitch)) * length + vectorStart.yCoord,
                        Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * length + vectorStart.zCoord
                );
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(vectorEnd.xCoord, mc.thePlayer.posY + 2, vectorEnd.zCoord, true));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(vectorStart.xCoord, mc.thePlayer.posY + 2, vectorStart.zCoord, true));
                mc.thePlayer.motionY = 0;
                break;
            case "minesucht":
                final double posX = mc.thePlayer.posX;
                final double posY = mc.thePlayer.posY;
                final double posZ = mc.thePlayer.posZ;

                if (!mc.gameSettings.keyBindForward.isKeyDown())
                    break;

                if (System.currentTimeMillis() - minesuchtTP > 99) {
                    final Vec3 vec3 = mc.thePlayer.getPositionEyes(0);
                    final Vec3 vec31 = mc.thePlayer.getLook(0);
                    final Vec3 vec32 = vec3.addVector(vec31.xCoord * 7, vec31.yCoord * 7, vec31.zCoord * 7);

                    if (mc.thePlayer.fallDistance > 0.8) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + 50, posZ, false));
                        mc.thePlayer.fall(100, 100);
                        mc.thePlayer.fallDistance = 0;
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + 20, posZ, true));
                    }

                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(vec32.xCoord, mc.thePlayer.posY + 50, vec32.zCoord, true));
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY, posZ, false));
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(vec32.xCoord, posY, vec32.zCoord, true));
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY, posZ, false));
                    minesuchtTP = System.currentTimeMillis();
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY, posZ, true));
                }
                break;
            case "jetpack":
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY + 0.2D, mc.thePlayer.posZ, -mc.thePlayer.motionX, -0.5D, -mc.thePlayer.motionZ);
                    mc.thePlayer.motionY += 0.15D;
                    mc.thePlayer.motionX *= 1.1D;
                    mc.thePlayer.motionZ *= 1.1D;
                }
                break;
            case "mineplex":
                if (mc.thePlayer.inventory.getCurrentItem() == null) {
                    if (mc.gameSettings.keyBindJump.isKeyDown() && mineplexTimer.hasTimePassed(100)) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.6, mc.thePlayer.posZ);
                        mineplexTimer.reset();
                    }

                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && mineplexTimer.hasTimePassed(100)) {
                        mc.gameSettings.keyBindSneak.pressed = false;
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.6, mc.thePlayer.posZ);
                        mineplexTimer.reset();
                    }

                    final BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1, mc.thePlayer.posZ);
                    final Vec3 vec = new Vec3(blockPos).addVector(0.4F, 0.4F, 0.4F).add(new Vec3(EnumFacing.UP.getDirectionVec()));
                    mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), blockPos, EnumFacing.UP, new Vec3(vec.xCoord * 0.4F, vec.yCoord * 0.4F, vec.zCoord * 0.4F));
                    MovementUtils.strafe(0.27F);

                    mc.timer.timerSpeed = (1 + mineplexSpeedValue.get());
                } else {
                    mc.timer.timerSpeed = 1;
                    setState(false);
                    ClientUtils.displayChatMessage("§f§l[§d§lN§7§lightX§f§l] §aSelect an empty slot to fly.");
                }
                break;
            case "aac3.3.12":
                if (mc.thePlayer.posY < -70)
                    mc.thePlayer.motionY = aacMotion.get();

                mc.timer.timerSpeed = 1F;

                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                    mc.timer.timerSpeed = 0.2F;
                    mc.rightClickDelayTimer = 0;
                }
                break;
            case "aac3.3.12-glide":
                if (!mc.thePlayer.onGround)
                    aac3glideDelay++;

                if (aac3glideDelay == 2)
                    mc.timer.timerSpeed = 1F;

                if (aac3glideDelay == 12)
                    mc.timer.timerSpeed = 0.1F;

                if (aac3glideDelay >= 12 && !mc.thePlayer.onGround) {
                    aac3glideDelay = 0;
                    mc.thePlayer.motionY = .015;
                }
                break;
            case "aac3.3.13":
                if (mc.thePlayer.isDead)
                    wasDead = true;

                if (wasDead || mc.thePlayer.onGround) {
                    wasDead = false;

                    mc.thePlayer.motionY = aacMotion2.get();
                    mc.thePlayer.onGround = false;
                }

                mc.timer.timerSpeed = 1F;

                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                    mc.timer.timerSpeed = 0.2F;
                    mc.rightClickDelayTimer = 0;
                }
                break;
            case "watchcat":
                MovementUtils.strafe(0.15F);
                mc.thePlayer.setSprinting(true);

                if (mc.thePlayer.posY < startY + 2) {
                    mc.thePlayer.motionY = Math.random() * 0.5;
                    break;
                }

                if (startY > mc.thePlayer.posY)
                    MovementUtils.strafe(0F);
                break;
            case "spartan":
                mc.thePlayer.motionY = 0;
                spartanTimer.update();
                if (spartanTimer.hasTimePassed(12)) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 8, mc.thePlayer.posZ, true));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8, mc.thePlayer.posZ, true));
                    spartanTimer.reset();
                }
                break;
            case "spartan2":
                MovementUtils.strafe(0.264F);

                if (mc.thePlayer.ticksExisted % 8 == 0)
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10, mc.thePlayer.posZ, true));
                break;
            case "pearl":
                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;

                int enderPearlSlot = getPearlSlot();
                if (pearlState == 0) {
                    if (enderPearlSlot == -1) {
                        NightX.hud.addNotification(new Notification("You don't have any ender pearl!", Notification.Type.ERROR));
                        pearlState = -1;
                        this.setState(false);
                        return;
                    }

                    if (mc.thePlayer.inventory.currentItem != enderPearlSlot) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(enderPearlSlot));
                    }

                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, 90, mc.thePlayer.onGround));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventoryContainer.getSlot(enderPearlSlot + 36).getStack(), 0, 0, 0));
                    if (enderPearlSlot != mc.thePlayer.inventory.currentItem) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                    pearlState = 1;
                }

                if (pearlActivateCheck.get().equalsIgnoreCase("damage") && pearlState == 1 && mc.thePlayer.hurtTime > 0)
                    pearlState = 2;

                if (pearlState == 2) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY += vanillaSpeed;
                    }
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                        mc.thePlayer.motionY -= vanillaSpeed;
                        mc.gameSettings.keyBindSneak.pressed = false;
                    }
                    MovementUtils.strafe(vanillaSpeed);
                }
                break;
            case "jump":
                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump();
                break;
            case "exploit":
                if (wdState == 0) {
                    mc.thePlayer.motionY = 0.1D;
                    wdState++;
                }

                if (wdState == 1 && wdTick == 3)
                    wdState++;

                if (wdState == 4) {
                    if (!boostTimer.hasTimePassed(500L))
                        mc.timer.timerSpeed = 1.6F;
                    else if (!boostTimer.hasTimePassed(800L))
                        mc.timer.timerSpeed = 1.4F;
                    else if (!boostTimer.hasTimePassed(1000L))
                        mc.timer.timerSpeed = 1.2F;
                    else
                        mc.timer.timerSpeed = 1F;

                    mc.thePlayer.motionY = 0.0001D;
                    MovementUtils.strafe((float) (MovementUtils.getBaseMoveSpeed(1) * (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.81D : 0.77D)));
                }
                break;
            case "neruxvace":
                if (!mc.thePlayer.onGround)
                    aac3glideDelay++;

                if (aac3glideDelay >= neruxVaceTicks.get() && !mc.thePlayer.onGround) {
                    aac3glideDelay = 0;
                    mc.thePlayer.motionY = .015;
                }
                break;
            case "hypixel":
                final int boostDelay = hypixelBoostDelay.get();
                if (hypixelBoost.get() && !flyTimer.hasTimePassed(boostDelay)) {
                    mc.timer.timerSpeed = 1F + (hypixelBoostTimer.get() * ((float) flyTimer.hasTimeLeft(boostDelay) / (float) boostDelay));
                }

                hypixelTimer.update();

                if (hypixelTimer.hasTimePassed(2)) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
                    hypixelTimer.reset();
                }
                break;
            case "freehypixel":
                if (freeHypixelTimer.hasTimePassed(10)) {
                    mc.thePlayer.capabilities.isFlying = true;
                    break;
                } else {
                    mc.thePlayer.rotationYaw = freeHypixelYaw;
                    mc.thePlayer.rotationPitch = freeHypixelPitch;
                    mc.thePlayer.motionX = mc.thePlayer.motionZ = mc.thePlayer.motionY = 0;
                }

                if (startY == new BigDecimal(mc.thePlayer.posY).setScale(3, RoundingMode.HALF_DOWN).doubleValue())
                    freeHypixelTimer.update();
                break;
        }
    }

    @EventTarget // drew
    public void onMotion(final MotionEvent event) {
        if (mc.thePlayer == null) return;

        if (bobbingValue.get()) {
            mc.thePlayer.cameraYaw = bobbingAmountValue.get();
        }

        if (!bobbingValue.get()) {
            mc.thePlayer.cameraYaw = 0.0f;
        }

        if (modeValue.get().equalsIgnoreCase("boosthypixel")) {
            switch (event.getEventState()) {
                case PRE:
                    hypixelTimer.update();

                    if (hypixelTimer.hasTimePassed(2)) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
                        hypixelTimer.reset();
                    }

                    if (!failedStart) mc.thePlayer.motionY = 0D;
                    break;
                case POST:
                    double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                    double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                    lastDistance = Math.sqrt(xDist * xDist + zDist * zDist);
                    break;
            }
        }

        switch (modeValue.get().toLowerCase()) {
            case "funcraft":
                mc.timer.timerSpeed = 1.4f;
                event.setOnGround(true);
                if (!MovementUtils.isMoving())
                    moveSpeed = 0.25;
                if (moveSpeed > 0.25) {
                    moveSpeed -= moveSpeed / 159.0;
                }
                if (event.getEventState() == EventState.PRE) {
                    mc.thePlayer.capabilities.isFlying = false;
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;

                    MovementUtils.strafe((float) moveSpeed);
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8e-6, mc.thePlayer.posZ);
                }
                break;
            case "zoom":
                event.setOnGround(true);
                if (!MovementUtils.isMoving())
                    moveSpeed = 0.25;
                if (moveSpeed > 0.25) {
                    moveSpeed -= moveSpeed / 159.0;
                }
                if (event.getEventState() == EventState.PRE) {
                    mc.thePlayer.capabilities.isFlying = false;
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;

                    MovementUtils.strafe((float) moveSpeed);
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8e-6, mc.thePlayer.posZ);
                }
                break;
            case "exploit":
                if (event.getEventState() == EventState.PRE)
                    wdTick++;
                break;
            case "slime":
                int current = mc.thePlayer.inventory.currentItem;
                if (event.getEventState() == EventState.PRE) {
                    if (wdState == 1 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -1, 0).expand(0, 0, 0)).isEmpty()) {
                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(expectItemStack));
                        wdState = 2;
                    }

                    mc.timer.timerSpeed = 1F;

                    if (wdState == 3 && expectItemStack != -1) {
                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(current));
                        expectItemStack = -1;
                    }

                    if (wdState == 4) {
                        if (MovementUtils.isMoving())
                            MovementUtils.strafe((float) MovementUtils.getBaseMoveSpeed() * 0.938F);
                        else
                            MovementUtils.strafe(0F);

                        mc.thePlayer.motionY = -0.0015F;
                    } else if (wdState < 3) {
                        final Rotation rot = RotationUtils.getRotationFromPosition(mc.thePlayer.posX, mc.thePlayer.posZ, (int) mc.thePlayer.posY - 1);
                        RotationUtils.setTargetRotation(rot);
                        event.setYaw(rot.getYaw());
                        event.setPitch(rot.getPitch());
                    } else
                        event.setY(event.getY() - 0.08);
                } else if (wdState == 2) {
                    if (mc.playerController.onPlayerRightClick(
                            mc.thePlayer, mc.theWorld,
                            mc.thePlayer.inventoryContainer.getSlot(expectItemStack).getStack(),
                            new BlockPos(mc.thePlayer.posX, (int) mc.thePlayer.posY - 2, mc.thePlayer.posZ),
                            EnumFacing.UP,
                            RotationUtils.getVectorForRotation(RotationUtils.getRotationFromPosition(mc.thePlayer.posX, mc.thePlayer.posZ, (int) mc.thePlayer.posY - 1))))
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());

                    wdState = 3;
                }
                break;
        }
    }

    public float coerceAtMost(double value, double max) {
        return (float) Math.min(value, max);
    }

    @EventTarget
    public void onAction(final ActionEvent event) {
        if (modeValue.get().toLowerCase().contains("exploit") && fakeSprintingValue.get())
            event.setSprinting(false);
    }

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        final String mode = modeValue.get();

        if (!markValue.get() || mode.equalsIgnoreCase("Motion") || mode.equalsIgnoreCase("Creative") || mode.equalsIgnoreCase("Damage") || mode.equalsIgnoreCase("AAC5-Vanilla") || mode.equalsIgnoreCase("Derp") || mode.equalsIgnoreCase("KeepAlive"))
            return;

        double y = startY + 2D;

        RenderUtils.drawPlatform(y, mc.thePlayer.getEntityBoundingBox().maxY < y ? new Color(0, 255, 0, 90) : new Color(255, 0, 0, 90), 1);

        switch (mode.toLowerCase()) {
            case "aac1.9.10":
                RenderUtils.drawPlatform(startY + aacJump, new Color(0, 0, 255, 90), 1);
                break;
            case "aac3.3.12":
                RenderUtils.drawPlatform(-70, new Color(0, 0, 255, 90), 1);
                break;
        }
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        final String mode = modeValue.get();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        if (mode.equalsIgnoreCase("Verus") && boostTicks > 0) {
            float width = (float) (verusDmgTickValue.get() - boostTicks) / (float) verusDmgTickValue.get() * 60F;
            RenderUtils.drawRect(scaledRes.getScaledWidth() / 2F - 31F, scaledRes.getScaledHeight() / 2F + 14F, scaledRes.getScaledWidth() / 2F + 31F, scaledRes.getScaledHeight() / 2F + 18F, 0xA0000000);
            RenderUtils.drawRect(scaledRes.getScaledWidth() / 2F - 30F, scaledRes.getScaledHeight() / 2F + 15F, scaledRes.getScaledWidth() / 2F - 30F + width, scaledRes.getScaledHeight() / 2F + 17F, 0xFFFFFFFF);
        }
        if (mode.equalsIgnoreCase("Verus") && shouldActiveDmg) {
            float width = (float) (verusReDmgTickValue.get() - dmgCooldown) / (float) verusReDmgTickValue.get() * 60F;
            RenderUtils.drawRect(scaledRes.getScaledWidth() / 2F - 31F, scaledRes.getScaledHeight() / 2F + 14F + 10F, scaledRes.getScaledWidth() / 2F + 31F, scaledRes.getScaledHeight() / 2F + 18F + 10F, 0xA0000000);
            RenderUtils.drawRect(scaledRes.getScaledWidth() / 2F - 30F, scaledRes.getScaledHeight() / 2F + 15F + 10F, scaledRes.getScaledWidth() / 2F - 30F + width, scaledRes.getScaledHeight() / 2F + 17F + 10F, 0xFFFF1F1F);
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        final String mode = modeValue.get();

        if (noPacketModify)
            return;

        if (packet instanceof S08PacketPlayerPosLook && mode.equalsIgnoreCase("exploit") && wdState == 3) {
            wdState = 4;
            if (boostTimer.hasTimePassed(8000L)) {
                NightX.hud.addNotification(new Notification("Exploit activated.", Notification.Type.SUCCESS));
                boostTimer.reset();
            } else {
                NightX.hud.addNotification(new Notification("Exploit activated.", Notification.Type.SUCCESS));
            }

            if (fakeDmgValue.get() && mc.thePlayer != null)
                mc.thePlayer.handleStatusUpdate((byte) 2);
        }

        if (packet instanceof C09PacketHeldItemChange && mode.equalsIgnoreCase("slime") && wdState < 4)
            event.cancelEvent();

        if (packet instanceof S08PacketPlayerPosLook) {
            if (mode.equalsIgnoreCase("slime") && wdState == 3) {
                wdState = 4;
                if (fakeDmgValue.get() && mc.thePlayer != null)
                    mc.thePlayer.handleStatusUpdate((byte) 2);
            }

            if (mode.equalsIgnoreCase("pearl") && pearlActivateCheck.get().equalsIgnoreCase("teleport") && pearlState == 1)
                pearlState = 2;

            if (mode.equalsIgnoreCase("BoostHypixel")) {
                failedStart = true;
            }
        }

        if (packet instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;

            boolean lastOnGround = packetPlayer.onGround;

            if (mode.equalsIgnoreCase("NCP") || mode.equalsIgnoreCase("Rewinside") ||
                    (mode.equalsIgnoreCase("Mineplex") && mc.thePlayer.inventory.getCurrentItem() == null) || (mode.equalsIgnoreCase("Verus") && verusSpoofGround.get() && verusDmged))
                packetPlayer.onGround = true;

            if (mode.equalsIgnoreCase("Hypixel") || mode.equalsIgnoreCase("BoostHypixel"))
                packetPlayer.onGround = false;

            if (mode.equalsIgnoreCase("Derp")) {
                packetPlayer.yaw = RandomUtils.nextFloat(0F, 360F);
                packetPlayer.pitch = RandomUtils.nextFloat(-90F, 90F);
            }

            if (mode.equalsIgnoreCase("AAC5-Vanilla") && !mc.isIntegratedServerRunning()) {
                if (aac5NofallValue.get()) packetPlayer.onGround = true;
                aac5C03List.add(packetPlayer);
                event.cancelEvent();
                if (aac5C03List.size() > aac5PursePacketsValue.get())
                    sendAAC5Packets();
            }

            if (mode.equalsIgnoreCase("clip") && clipGroundSpoof.get())
                packetPlayer.onGround = true;

            if ((mode.equalsIgnoreCase("motion") || mode.equalsIgnoreCase("creative")) && groundSpoofValue.get())
                packetPlayer.onGround = true;

            if (verusDmgModeValue.get().equalsIgnoreCase("Jump") && verusJumpTimes < 5 && mode.equalsIgnoreCase("Verus")) {
                packetPlayer.onGround = false;
            }

            if (mode.equalsIgnoreCase("exploit")) {
                if (wdState == 2) {
                    packetPlayer.y -= 0.187;
                    wdState++;
                }
                if (wdState > 3) {
                    if (fakeNoMoveValue.get())
                        packetPlayer.setMoving(false);
                }
            }
        }
    }

    private void sendAAC5Packets() {
        float yaw = mc.thePlayer.rotationYaw;
        float pitch = mc.thePlayer.rotationPitch;
        for (C03PacketPlayer packet : aac5C03List) {
            PacketUtils.sendPacketNoEvent(packet);
            if (packet.isMoving()) {
                if (packet.getRotating()) {
                    yaw = packet.yaw;
                    pitch = packet.pitch;
                }
                switch (aac5Packet.get()) {
                    case "Original":
                        if (aac5UseC04Packet.get()) {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, 1e+159, packet.z, true));
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y, packet.z, true));
                        } else {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.x, 1e+159, packet.z, yaw, pitch, true));
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.x, packet.y, packet.z, yaw, pitch, true));
                        }
                        break;
                    case "Rise":
                        if (aac5UseC04Packet.get()) {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, -1e+159, packet.z + 10, true));
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y, packet.z, true));
                        } else {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.x, -1e+159, packet.z + 10, yaw, pitch, true));
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.x, packet.y, packet.z, yaw, pitch, true));
                        }
                        break;
                    case "Other":
                        if (aac5UseC04Packet.get()) {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, 1.7976931348623157E+308, packet.z, true));
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y, packet.z, true));
                        } else {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.x, 1.7976931348623157E+308, packet.z, yaw, pitch, true));
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.x, packet.y, packet.z, yaw, pitch, true));
                        }
                        break;
                }

            }
        }
        aac5C03List.clear();
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        switch (modeValue.get().toLowerCase()) {
            case "pearl":
                if (pearlState != 2 && pearlState != -1) {
                    event.cancelEvent();
                }
                break;
            case "verus":
                if (!verusDmged)
                    if (verusDmgModeValue.get().equalsIgnoreCase("Jump"))
                        event.zeroXZ();
                    else
                        event.cancelEvent();
                break;
            case "clip":
                if (clipNoMove.get()) event.zeroXZ();
                break;
            case "veruslowhop":
                if (!mc.thePlayer.isInWeb && !mc.thePlayer.isInLava() && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.ridingEntity == null) {
                    if (MovementUtils.isMoving()) {
                        mc.gameSettings.keyBindJump.pressed = false;
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionY = 0;
                            MovementUtils.strafe(0.61F);
                            event.setY(0.41999998688698);
                        }
                        MovementUtils.strafe();
                    }
                }
                break;
            case "slime":
                if (wdState < 4)
                    event.zeroXZ();
                break;
            case "cubecraft": {
                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);

                if (cubecraftTeleportTickTimer.hasTimePassed(2)) {
                    event.setX(-Math.sin(yaw) * 2.4D);
                    event.setZ(Math.cos(yaw) * 2.4D);

                    cubecraftTeleportTickTimer.reset();
                } else {
                    event.setX(-Math.sin(yaw) * 0.2D);
                    event.setZ(Math.cos(yaw) * 0.2D);
                }
                break;
            }
            case "boosthypixel":
                if (!MovementUtils.isMoving()) {
                    event.setX(0D);
                    event.setZ(0D);
                    break;
                }

                if (failedStart)
                    break;

                final double amplifier = 1 + (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.2 *
                        (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) : 0);
                final double baseSpeed = 0.29D * amplifier;

                switch (boostHypixelState) {
                    case 1:
                        moveSpeed = (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.56 : 2.034) * baseSpeed;
                        boostHypixelState = 2;
                        break;
                    case 2:
                        moveSpeed *= 2.16D;
                        boostHypixelState = 3;
                        break;
                    case 3:
                        moveSpeed = lastDistance - (mc.thePlayer.ticksExisted % 2 == 0 ? 0.0103D : 0.0123D) * (lastDistance - baseSpeed);

                        boostHypixelState = 4;
                        break;
                    default:
                        moveSpeed = lastDistance - lastDistance / 159.8D;
                        break;
                }

                moveSpeed = Math.max(moveSpeed, 0.3D);

                final double yaw = MovementUtils.getDirection();
                event.setX(-Math.sin(yaw) * moveSpeed);
                event.setZ(Math.cos(yaw) * moveSpeed);
                mc.thePlayer.motionX = event.getX();
                mc.thePlayer.motionZ = event.getZ();
                break;
            case "freehypixel":
                if (!freeHypixelTimer.hasTimePassed(10))
                    event.zero();
                break;
        }
    }

    @EventTarget
    public void onBB(final BlockBBEvent event) {
        if (mc.thePlayer == null) return;

        final String mode = modeValue.get();

        if (event.getBlock() instanceof BlockAir && mode.equalsIgnoreCase("Jump") && event.getY() < startY)
            event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1, startY, event.getZ() + 1));

        if (event.getBlock() instanceof BlockAir && ((mode.equalsIgnoreCase("collide") && !mc.thePlayer.isSneaking()) || mode.equalsIgnoreCase("veruslowhop")))
            event.setBoundingBox(new AxisAlignedBB(-2, -1, -2, 2, 1, 2).offset(event.getX(), event.getY(), event.getZ()));

        if (event.getBlock() instanceof BlockAir && (mode.equalsIgnoreCase("Hypixel") ||
                mode.equalsIgnoreCase("BoostHypixel") || mode.equalsIgnoreCase("Rewinside") ||
                (mode.equalsIgnoreCase("Mineplex") && mc.thePlayer.inventory.getCurrentItem() == null) || (mode.equalsIgnoreCase("Verus") &&
                (verusDmgModeValue.get().equalsIgnoreCase("none") || verusDmged)))
                && event.getY() < mc.thePlayer.posY)
            event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1, mc.thePlayer.posY, event.getZ() + 1));
    }

    @EventTarget
    public void onJump(final JumpEvent e) {
        final String mode = modeValue.get();

        if (mode.equalsIgnoreCase("Hypixel") || mode.equalsIgnoreCase("BoostHypixel") ||
                mode.equalsIgnoreCase("Rewinside") || (mode.equalsIgnoreCase("Mineplex") && mc.thePlayer.inventory.getCurrentItem() == null) || (mode.equalsIgnoreCase("funcraft") && moveSpeed > 0) || (mode.equalsIgnoreCase("exploit") && wdState >= 1) || (mode.equalsIgnoreCase("slime") && wdState >= 1))
            e.cancelEvent();
    }

    @EventTarget
    public void onStep(final StepEvent e) {
        final String mode = modeValue.get();

        if (mode.equalsIgnoreCase("Hypixel") || mode.equalsIgnoreCase("BoostHypixel") ||
                mode.equalsIgnoreCase("Rewinside") || (mode.equalsIgnoreCase("Mineplex") && mc.thePlayer.inventory.getCurrentItem() == null) || mode.equalsIgnoreCase("funcraft") || (mode.equalsIgnoreCase("exploit") && wdState > 2) || mode.equalsIgnoreCase("slime"))
            e.setStepHeight(0F);
    }

    private void handleVanillaKickBypass() {
        if (!vanillaKickBypassValue.get() || !groundTimer.hasTimePassed(1000)) return;

        final double ground = calculateGround();

        for (double posY = mc.thePlayer.posY; posY > ground; posY -= 8D) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));

            if (posY - 8D < ground) break; // Prevent next step
        }

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, ground, mc.thePlayer.posZ, true));


        for (double posY = ground; posY < mc.thePlayer.posY; posY += 8D) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));

            if (posY + 8D > mc.thePlayer.posY) break; // Prevent next step
        }

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        groundTimer.reset();
    }

    // TODO: Make better and faster calculation lol
    private double calculateGround() {
        final AxisAlignedBB playerBoundingBox = mc.thePlayer.getEntityBoundingBox();
        double blockHeight = 1D;

        for (double ground = mc.thePlayer.posY; ground > 0D; ground -= blockHeight) {
            final AxisAlignedBB customBox = new AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ);

            if (mc.theWorld.checkBlockCollision(customBox)) {
                if (blockHeight <= 0.05D)
                    return ground + blockHeight;

                ground += blockHeight;
                blockHeight = 0.05D;
            }
        }

        return 0F;
    }

    private int getPearlSlot() {
        for (int i = 36; i < 45; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemEnderPearl) {
                return i - 36;
            }
        }
        return -1;
    }

    private int getSlimeSlot() {
        for (int i = 36; i < 45; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemBlock) {
                final ItemBlock itemBlock = (ItemBlock) stack.getItem();
                if (itemBlock.getBlock() instanceof BlockSlime)
                    return i - 36;
            }
        }
        return -1;
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}