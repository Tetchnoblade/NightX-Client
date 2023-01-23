package net.aspw.nightx.features.module.modules.world;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.*;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.features.module.modules.movement.Speed;
import net.aspw.nightx.injection.access.StaticStorage;
import net.aspw.nightx.utils.*;
import net.aspw.nightx.utils.block.BlockUtils;
import net.aspw.nightx.utils.block.PlaceInfo;
import net.aspw.nightx.utils.misc.RandomUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.utils.timer.MSTimer;
import net.aspw.nightx.utils.timer.TickTimer;
import net.aspw.nightx.utils.timer.TimeUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;
import net.aspw.nightx.visual.font.Fonts;
import net.aspw.nightx.visual.hud.element.elements.Notification;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Scaffold", category = ModuleCategory.WORLD)
public class Scaffold extends Module {

    /**
     * OPTIONS (Tower)
     */
    // Global settings
    private final BoolValue towerEnabled = new BoolValue("EnableTower", true);
    private final ListValue towerModeValue = new ListValue("TowerMode", new String[]{
            "Jump", "Motion", "StableMotion", "ConstantMotion", "MotionTP", "Packet", "Teleport", "AAC3.3.9", "AAC3.6.4", "Verus"
    }, "ConstantMotion", () -> towerEnabled.get());
    private final ListValue towerPlaceModeValue = new ListValue("Tower-PlaceTiming", new String[]{"Pre", "Post"}, "Post");
    private final BoolValue stopWhenBlockAbove = new BoolValue("StopWhenBlockAbove", true, () -> towerEnabled.get());
    private final BoolValue onJumpValue = new BoolValue("OnJump", true, () -> towerEnabled.get());
    private final BoolValue noMoveOnlyValue = new BoolValue("NoMove", true, () -> towerEnabled.get());
    private final BoolValue noMoveFreezeValue = new BoolValue("NoMoveFreezePlayer", true, () -> towerEnabled.get() && noMoveOnlyValue.get());
    private final FloatValue towerTimerValue = new FloatValue("TowerTimer", 1F, 0.1F, 10F, () -> towerEnabled.get());

    // Jump mode
    private final FloatValue jumpMotionValue = new FloatValue("JumpMotion", 0.42F, 0.3681289F, 0.79F, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("Jump"));
    private final IntegerValue jumpDelayValue = new IntegerValue("JumpDelay", 0, 0, 20, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("Jump"));

    // StableMotion
    private final FloatValue stableMotionValue = new FloatValue("StableMotion", 0.41982F, 0.1F, 1F, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("StableMotion"));
    private final BoolValue stableFakeJumpValue = new BoolValue("StableFakeJump", false, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("StableMotion"));
    private final BoolValue stableStopValue = new BoolValue("StableStop", false, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("StableMotion"));
    private final IntegerValue stableStopDelayValue = new IntegerValue("StableStopDelay", 1500, 0, 5000, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("StableMotion") && stableStopValue.get());

    // ConstantMotion
    private final FloatValue constantMotionValue = new FloatValue("ConstantMotion", 0.42F, 0.1F, 1F, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("ConstantMotion"));
    private final FloatValue constantMotionJumpGroundValue = new FloatValue("ConstantMotionJumpGround", 0.79F, 0.76F, 1F, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("ConstantMotion"));

    // Teleport
    private final FloatValue teleportHeightValue = new FloatValue("TeleportHeight", 1.15F, 0.1F, 5F, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("Teleport"));
    private final IntegerValue teleportDelayValue = new IntegerValue("TeleportDelay", 0, 0, 20, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("Teleport"));
    private final BoolValue teleportGroundValue = new BoolValue("TeleportGround", true, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("Teleport"));
    private final BoolValue teleportNoMotionValue = new BoolValue("TeleportNoMotion", false, () -> towerEnabled.get() && towerModeValue.get().equalsIgnoreCase("Teleport"));

    /**
     * OPTIONS (Scaffold)
     */
    // Mode
    public final ListValue modeValue = new ListValue("Mode", new String[]{"Normal", "Rewinside", "Expand"}, "Normal");

    // Delay
    private final BoolValue placeableDelay = new BoolValue("PlaceableDelay", false);
    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 50, 0, 1000, "ms") {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int i = minDelayValue.get();

            if (i > newValue)
                set(i);
        }
    };

    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 50, 0, 1000, "ms") {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int i = maxDelayValue.get();

            if (i < newValue)
                set(i);
        }
    };

    public final FloatValue speedModifierValue = new FloatValue("SpeedModifier", 0.8F, 0, 2F, "x");

    //make sprint compatible with tower.add sprint tricks
    public final ListValue sprintModeValue = new ListValue("SprintMode", new String[]{"Same", "Silent", "Ground", "Air", "Off"}, "Same");
    // Basic stuff
    private final BoolValue swingValue = new BoolValue("Swing", false);
    private final BoolValue downValue = new BoolValue("Down", true);
    private final BoolValue searchValue = new BoolValue("Search", true);
    private final ListValue placeModeValue = new ListValue("PlaceTiming", new String[]{"Pre", "Post"}, "Post");

    // Eagle
    private final BoolValue eagleValue = new BoolValue("Eagle", false);
    private final BoolValue eagleSilentValue = new BoolValue("EagleSilent", false, () -> eagleValue.get());
    private final IntegerValue blocksToEagleValue = new IntegerValue("BlocksToEagle", 0, 0, 10, () -> eagleValue.get());
    private final FloatValue eagleEdgeDistanceValue = new FloatValue("EagleEdgeDistance", 0.2F, 0F, 0.5F, "m", () -> eagleValue.get());

    // Expand
    private final BoolValue omniDirectionalExpand = new BoolValue("OmniDirectionalExpand", true, () -> modeValue.get().equalsIgnoreCase("expand"));
    private final IntegerValue expandLengthValue = new IntegerValue("ExpandLength", 3, 1, 6, " blocks", () -> modeValue.get().equalsIgnoreCase("expand"));

    // Rotations
    private final BoolValue rotationsValue = new BoolValue("Rotations", true);
    private final BoolValue noHitCheckValue = new BoolValue("NoHitCheck", false, () -> rotationsValue.get());
    public final ListValue rotationModeValue = new ListValue("RotationMode", new String[]{"Normal", "AAC", "Static", "Static2", "Static3", "Spin", "Custom"}, "Normal"); // searching reason
    public final ListValue rotationLookupValue = new ListValue("RotationLookup", new String[]{"Normal", "AAC", "Same"}, "Normal");

    private final FloatValue maxTurnSpeed = new FloatValue("MaxTurnSpeed", 120F, 0F, 180F, "°", () -> rotationsValue.get()) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            final float i = minTurnSpeed.get();

            if (i > newValue)
                set(i);
        }
    };

    private final FloatValue minTurnSpeed = new FloatValue("MinTurnSpeed", 80F, 0F, 180F, "°", () -> rotationsValue.get()) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            final float i = maxTurnSpeed.get();

            if (i < newValue)
                set(i);
        }
    };

    private final FloatValue staticPitchValue = new FloatValue("Static-Pitch", 86F, 80F, 90F, "°", () -> rotationModeValue.get().toLowerCase().startsWith("static"));

    private final FloatValue customYawValue = new FloatValue("Custom-Yaw", 135F, -180F, 180F, "°", () -> rotationModeValue.get().equalsIgnoreCase("custom"));
    private final FloatValue customPitchValue = new FloatValue("Custom-Pitch", 86F, -90F, 90F, "°", () -> rotationModeValue.get().equalsIgnoreCase("custom"));

    private final FloatValue speenSpeedValue = new FloatValue("Spin-Speed", 5F, -90F, 90F, "°", () -> rotationModeValue.get().equalsIgnoreCase("spin"));
    private final FloatValue speenPitchValue = new FloatValue("Spin-Pitch", 90F, -90F, 90F, "°", () -> rotationModeValue.get().equalsIgnoreCase("spin"));

    private final BoolValue keepRotOnJumpValue = new BoolValue("KeepRotOnJump", true, () -> (!rotationModeValue.get().equalsIgnoreCase("normal") && !rotationModeValue.get().equalsIgnoreCase("aac")));

    private final BoolValue keepRotationValue = new BoolValue("KeepRotation", true, () -> rotationsValue.get());
    private final IntegerValue keepLengthValue = new IntegerValue("KeepRotationLength", 0, 0, 20, () -> rotationsValue.get() && !keepRotationValue.get());
    private final ListValue placeConditionValue = new ListValue("Place-Condition", new String[]{"Air", "FallDown", "NegativeMotion", "Always"}, "Always");

    private final BoolValue rotationStrafeValue = new BoolValue("RotationStrafe", false);

    // Zitter
    private final BoolValue zitterValue = new BoolValue("Zitter", false, () -> !isTowerOnly());
    private final ListValue zitterModeValue = new ListValue("ZitterMode", new String[]{"Teleport", "Smooth"}, "Smooth", () -> !isTowerOnly() && zitterValue.get());
    private final FloatValue zitterSpeed = new FloatValue("ZitterSpeed", 0.13F, 0.1F, 0.3F, () -> !isTowerOnly() && zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("teleport"));
    private final FloatValue zitterStrength = new FloatValue("ZitterStrength", 0.072F, 0.05F, 0.2F, () -> !isTowerOnly() && zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("teleport"));
    private final IntegerValue zitterDelay = new IntegerValue("ZitterDelay", 100, 0, 500, "ms", () -> !isTowerOnly() && zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("smooth"));

    // Game
    private final FloatValue timerValue = new FloatValue("Timer", 1F, 0.1F, 10F, () -> !isTowerOnly());
    // AutoBlock
    private final ListValue autoBlockMode = new ListValue("AutoBlock", new String[]{"LiteSpoof", "Spoof", "Switch", "Off"}, "LiteSpoof");
    public final FloatValue xzMultiplier = new FloatValue("XZ-Multiplier", 1F, 0F, 4F, "x");
    private final BoolValue customSpeedValue = new BoolValue("CustomSpeed", false);
    private final FloatValue customMoveSpeedValue = new FloatValue("CustomMoveSpeed", 0.2F, 0, 5F, () -> customSpeedValue.get());

    // Safety
    private final BoolValue sameYValue = new BoolValue("KeepY", false);
    private final BoolValue autoJumpValue = new BoolValue("AutoJump", false, () -> !isTowerOnly());
    private final BoolValue smartSpeedValue = new BoolValue("SpeedKeepY", true, () -> !isTowerOnly());
    private final BoolValue safeWalkValue = new BoolValue("SafeWalk", false);
    private final BoolValue airSafeValue = new BoolValue("AirSafe", false, () -> safeWalkValue.get());
    private final BoolValue autoDisableSpeedValue = new BoolValue("AutoDisable-Speed", false);
    private final BoolValue noSpeedPotValue = new BoolValue("NoSpeedPot", false);
    // Visuals
    public final ListValue counterDisplayValue = new ListValue("Counter", new String[]{"Off", "NightX", "Exhibition", "Advanced", "Sigma", "Novoline"}, "NightX");

    private final BoolValue markValue = new BoolValue("Mark", false);
    private final IntegerValue redValue = new IntegerValue("Red", 255, 0, 255, () -> markValue.get());
    private final IntegerValue greenValue = new IntegerValue("Green", 255, 0, 255, () -> markValue.get());
    private final IntegerValue blueValue = new IntegerValue("Blue", 255, 0, 255, () -> markValue.get());
    private final IntegerValue alphaValue = new IntegerValue("Alpha", 120, 0, 255, () -> markValue.get());

    // Delay
    private final MSTimer delayTimer = new MSTimer();
    private final MSTimer towerDelayTimer = new MSTimer();
    private final MSTimer zitterTimer = new MSTimer();
    // Mode stuff
    private final TickTimer timer = new TickTimer();
    /**
     * MODULE
     */

    // Target block
    private PlaceInfo targetPlace, towerPlace;

    // Launch position
    private int launchY;
    private boolean faceBlock;

    // Rotation lock
    private Rotation lockRotation;
    private Rotation lookupRotation;
    private Rotation speenRotation;

    // Auto block slot
    private int slot, lastSlot;

    // Zitter Smooth
    private boolean zitterDirection;
    private long delay;
    // Eagle
    private int placedBlocksWithoutEagle = 0;
    private boolean eagleSneaking;
    // Down
    private boolean shouldGoDown = false;
    // Render thingy
    private float progress = 0;
    private float spinYaw = 0F;
    private long lastMS = 0L;
    private double jumpGround = 0;
    private int verusState = 0;
    private boolean verusJumped = false;

    public boolean isTowerOnly() {
        return (towerEnabled.get() && !onJumpValue.get());
    }

    public boolean towerActivation() {
        return towerEnabled.get() && (!onJumpValue.get() || mc.gameSettings.keyBindJump.isKeyDown()) && (!noMoveOnlyValue.get() || !MovementUtils.isMoving());
    }

    /**
     * Enable module
     */
    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;

        progress = 0;
        spinYaw = 0;
        launchY = (int) mc.thePlayer.posY;
        lastSlot = mc.thePlayer.inventory.currentItem;
        slot = mc.thePlayer.inventory.currentItem;

        faceBlock = false;
        lastMS = System.currentTimeMillis();
    }

    //Send jump packets, bypasses Hypixel.
    private void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }

    /**
     * Move player
     */
    private void move(MotionEvent event) {
        switch (towerModeValue.get().toLowerCase()) {
            case "jump":
                if (mc.thePlayer.onGround && timer.hasTimePassed(jumpDelayValue.get())) {
                    fakeJump();
                    mc.thePlayer.motionY = jumpMotionValue.get();
                    timer.reset();
                }
                break;
            case "motion":
                if (mc.thePlayer.onGround) {
                    fakeJump();
                    mc.thePlayer.motionY = 0.42D;
                } else if (mc.thePlayer.motionY < 0.1D) mc.thePlayer.motionY = -0.3D;
                break;
            case "motiontp":
                if (mc.thePlayer.onGround) {
                    fakeJump();
                    mc.thePlayer.motionY = 0.42D;
                } else if (mc.thePlayer.motionY < 0.23D)
                    mc.thePlayer.setPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ);
                break;
            case "packet":
                if (mc.thePlayer.onGround && timer.hasTimePassed(2)) {
                    fakeJump();
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                            mc.thePlayer.posY + 0.42D, mc.thePlayer.posZ, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                            mc.thePlayer.posY + 0.76D, mc.thePlayer.posZ, false));
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.08D, mc.thePlayer.posZ);
                    timer.reset();
                }
                break;
            case "teleport":
                if (teleportNoMotionValue.get())
                    mc.thePlayer.motionY = 0;

                if ((mc.thePlayer.onGround || !teleportGroundValue.get()) && timer.hasTimePassed(teleportDelayValue.get())) {
                    fakeJump();
                    mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + teleportHeightValue.get(), mc.thePlayer.posZ);
                    timer.reset();
                }
                break;
            case "stablemotion":
                if (stableFakeJumpValue.get())
                    fakeJump();
                mc.thePlayer.motionY = stableMotionValue.get();
                if (stableStopValue.get() && towerDelayTimer.hasTimePassed(stableStopDelayValue.get())) {
                    mc.thePlayer.motionY = -0.28D;
                    towerDelayTimer.reset();
                }
                break;
            case "constantmotion":
                if (mc.thePlayer.onGround) {
                    fakeJump();
                    jumpGround = mc.thePlayer.posY;
                    mc.thePlayer.motionY = constantMotionValue.get();
                }

                if (mc.thePlayer.posY > jumpGround + constantMotionJumpGroundValue.get()) {
                    fakeJump();
                    mc.thePlayer.setPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ);
                    mc.thePlayer.motionY = constantMotionValue.get();
                    jumpGround = mc.thePlayer.posY;
                }
                break;
            case "aac3.3.9":
                if (mc.thePlayer.onGround) {
                    fakeJump();
                    mc.thePlayer.motionY = 0.4001;
                }
                mc.timer.timerSpeed = 1F;

                if (mc.thePlayer.motionY < 0) {
                    mc.thePlayer.motionY -= 0.00000945;
                    mc.timer.timerSpeed = 1.6F;
                }
                break;
            case "aac3.6.4":
                if (mc.thePlayer.ticksExisted % 4 == 1) {
                    mc.thePlayer.motionY = 0.4195464;
                    mc.thePlayer.setPosition(mc.thePlayer.posX - 0.035, mc.thePlayer.posY, mc.thePlayer.posZ);
                } else if (mc.thePlayer.ticksExisted % 4 == 0) {
                    mc.thePlayer.motionY = -0.5;
                    mc.thePlayer.setPosition(mc.thePlayer.posX + 0.035, mc.thePlayer.posY, mc.thePlayer.posZ);
                }
                break;
            case "verus":
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -0.01, 0)).isEmpty() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                    verusState = 0;
                    verusJumped = true;
                }
                if (verusJumped) {
                    MovementUtils.strafe();
                    switch (verusState) {
                        case 0:
                            fakeJump();
                            mc.thePlayer.motionY = 0.41999998688697815;
                            ++verusState;
                            break;
                        case 1:
                            ++verusState;
                            break;
                        case 2:
                            ++verusState;
                            break;
                        case 3:
                            event.setOnGround(true);
                            mc.thePlayer.motionY = 0.0;
                            ++verusState;
                            break;
                        case 4:
                            ++verusState;
                            break;
                    }
                    verusJumped = false;
                }
                verusJumped = true;
                break;
        }
    }

    /**
     * Update event
     *
     * @param event
     */
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        int blockSlot = -1;
        ItemStack itemStack = mc.thePlayer.getHeldItem();

        if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
            if (autoBlockMode.get().equalsIgnoreCase("Off"))
                return;

            blockSlot = InventoryUtils.findAutoBlockBlock();

            if (blockSlot == -1)
                return;

            if (autoBlockMode.get().equalsIgnoreCase("Switch")) {
                mc.thePlayer.inventory.currentItem = blockSlot - 36;
                mc.playerController.updateController();
            }

            if (autoBlockMode.get().equalsIgnoreCase("LiteSpoof")) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(blockSlot - 36));
                itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).getStack();
            }
        }

        if (autoDisableSpeedValue.get() && NightX.moduleManager.getModule(Speed.class).getState()) {
            NightX.moduleManager.getModule(Speed.class).setState(false);
            NightX.hud.addNotification(new Notification("Speed is disabled.", Notification.Type.WARNING));
        }

        if (towerActivation()) {
            shouldGoDown = false;
            mc.gameSettings.keyBindSneak.pressed = false;
            mc.thePlayer.setSprinting(false);
            return;
        }

        mc.timer.timerSpeed = timerValue.get();
        shouldGoDown = downValue.get() && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && getBlocksAmount() > 1;
        if (shouldGoDown)
            mc.gameSettings.keyBindSneak.pressed = false;

        // scaffold custom speed if enabled
        if (customSpeedValue.get())
            MovementUtils.strafe(customMoveSpeedValue.get());

        if (mc.thePlayer.onGround) {
            final String mode = modeValue.get();

            // Rewinside scaffold mode
            if (mode.equalsIgnoreCase("Rewinside")) {
                MovementUtils.strafe(0.2F);
                mc.thePlayer.motionY = 0D;
            }

            // Smooth Zitter
            if (zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("smooth")) {
                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight))
                    mc.gameSettings.keyBindRight.pressed = false;

                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft))
                    mc.gameSettings.keyBindLeft.pressed = false;

                if (zitterTimer.hasTimePassed(zitterDelay.get())) {
                    zitterDirection = !zitterDirection;
                    zitterTimer.reset();
                }

                if (zitterDirection) {
                    mc.gameSettings.keyBindRight.pressed = true;
                    mc.gameSettings.keyBindLeft.pressed = false;
                } else {
                    mc.gameSettings.keyBindRight.pressed = false;
                    mc.gameSettings.keyBindLeft.pressed = true;
                }
            }

            // Eagle
            if (eagleValue.get() && !shouldGoDown) {
                double dif = 0.5D;
                if (eagleEdgeDistanceValue.get() > 0) {
                    for (int i = 0; i < 4; i++) {
                        final BlockPos blockPos = new BlockPos(mc.thePlayer.posX + (i == 0 ? (-1) : i == 1 ? 1 : 0), mc.thePlayer.posY - (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? 0D : 1.0D), mc.thePlayer.posZ + (i == 2 ? -1 : i == 3 ? 1 : 0));
                        final PlaceInfo placeInfo = PlaceInfo.get(blockPos);
                        if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                            double calcDif = i > 1 ? mc.thePlayer.posZ - blockPos.getZ() : mc.thePlayer.posX - blockPos.getX();
                            calcDif -= 0.5D;

                            if (calcDif < 0)
                                calcDif *= -1;
                            calcDif -= 0.5D;

                            if (calcDif < dif)
                                dif = calcDif;
                        }
                    }
                }
                if (placedBlocksWithoutEagle >= blocksToEagleValue.get()) {
                    final boolean shouldEagle = mc.theWorld.getBlockState(
                            new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ)).getBlock() == Blocks.air || dif < eagleEdgeDistanceValue.get();

                    if (eagleSilentValue.get()) {
                        if (eagleSneaking != shouldEagle) {
                            mc.getNetHandler().addToSendQueue(
                                    new C0BPacketEntityAction(mc.thePlayer, shouldEagle ?
                                            C0BPacketEntityAction.Action.START_SNEAKING :
                                            C0BPacketEntityAction.Action.STOP_SNEAKING)
                            );
                        }

                        eagleSneaking = shouldEagle;
                    } else
                        mc.gameSettings.keyBindSneak.pressed = shouldEagle;

                    placedBlocksWithoutEagle = 0;
                } else
                    placedBlocksWithoutEagle++;
            }

            // Zitter
            if (zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("teleport")) {
                MovementUtils.strafe(zitterSpeed.get());


                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw + (zitterDirection ? 90D : -90D));
                mc.thePlayer.motionX -= Math.sin(yaw) * zitterStrength.get();
                mc.thePlayer.motionZ += Math.cos(yaw) * zitterStrength.get();
                zitterDirection = !zitterDirection;
            }
        }

        if (sprintModeValue.get().equalsIgnoreCase("off") || (sprintModeValue.get().equalsIgnoreCase("ground") && !mc.thePlayer.onGround) || (sprintModeValue.get().equalsIgnoreCase("air") && mc.thePlayer.onGround)) {
            mc.thePlayer.setSprinting(false);
        }

        // Auto Jump thingy
        if (shouldGoDown) {
            launchY = (int) mc.thePlayer.posY - 1;
        } else if (!sameYValue.get()) {
            if ((!autoJumpValue.get() && !(smartSpeedValue.get() && NightX.moduleManager.getModule(Speed.class).getState())) || GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.thePlayer.posY < launchY)
                launchY = (int) mc.thePlayer.posY;
            if (autoJumpValue.get() && !NightX.moduleManager.getModule(Speed.class).getState() && MovementUtils.isMoving() && mc.thePlayer.onGround) {
                mc.thePlayer.jump();
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (mc.thePlayer == null)
            return;

        final Packet<?> packet = event.getPacket();

        // Sprint
        if (sprintModeValue.get().equalsIgnoreCase("silent")) {
            if (packet instanceof C0BPacketEntityAction &&
                    (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING || ((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING))
                event.cancelEvent();
        }

        // AutoBlock
        if (packet instanceof C09PacketHeldItemChange) {
            final C09PacketHeldItemChange packetHeldItemChange = (C09PacketHeldItemChange) packet;

            slot = packetHeldItemChange.getSlotId();
        }
    }

    @EventTarget
    //took it from applyrotationstrafe XD. staticyaw comes from bestnub.
    public void onStrafe(final StrafeEvent event) {
        if (lookupRotation != null && rotationStrafeValue.get()) {
            final int dif = (int) ((MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - lookupRotation.getYaw() - 23.5F - 135) + 180) / 45);

            final float yaw = lookupRotation.getYaw();
            final float strafe = event.getStrafe();
            final float forward = event.getForward();
            final float friction = event.getFriction();
            float calcForward = 0F;
            float calcStrafe = 0F;
            /*
            Rotation Dif
            7 \ 0 / 1     +  +  +      +  |  -
            6   +   2     -- F --      +  S  -
            5 / 4 \ 3     -  -  -      +  |  -
            */
            switch (dif) {
                case 0: {
                    calcForward = forward;
                    calcStrafe = strafe;
                    break;
                }
                case 1: {
                    calcForward += forward;
                    calcStrafe -= forward;
                    calcForward += strafe;
                    calcStrafe += strafe;
                    break;
                }
                case 2: {
                    calcForward = strafe;
                    calcStrafe = -forward;
                    break;
                }
                case 3: {
                    calcForward -= forward;
                    calcStrafe -= forward;
                    calcForward += strafe;
                    calcStrafe -= strafe;
                    break;
                }
                case 4: {
                    calcForward = -forward;
                    calcStrafe = -strafe;
                    break;
                }
                case 5: {
                    calcForward -= forward;
                    calcStrafe += forward;
                    calcForward -= strafe;
                    calcStrafe -= strafe;
                    break;
                }
                case 6: {
                    calcForward = -strafe;
                    calcStrafe = forward;
                    break;
                }
                case 7: {
                    calcForward += forward;
                    calcStrafe += forward;
                    calcForward -= strafe;
                    calcStrafe += strafe;
                    break;
                }
            }

            if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
                calcForward *= 0.5f;
            }

            if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
                calcStrafe *= 0.5f;
            }

            float f = calcStrafe * calcStrafe + calcForward * calcForward;

            if (f >= 1.0E-4F) {
                f = MathHelper.sqrt_float(f);

                if (f < 1.0F)
                    f = 1.0F;

                f = friction / f;
                calcStrafe *= f;
                calcForward *= f;

                final float yawSin = MathHelper.sin((float) (yaw * Math.PI / 180F));
                final float yawCos = MathHelper.cos((float) (yaw * Math.PI / 180F));

                mc.thePlayer.motionX += calcStrafe * yawCos - calcForward * yawSin;
                mc.thePlayer.motionZ += calcForward * yawCos + calcStrafe * yawSin;
            }
            event.cancelEvent();
        }
    }

    private boolean shouldPlace() {
        boolean placeWhenAir = placeConditionValue.get().equalsIgnoreCase("air");
        boolean placeWhenFall = placeConditionValue.get().equalsIgnoreCase("falldown");
        boolean placeWhenNegativeMotion = placeConditionValue.get().equalsIgnoreCase("negativemotion");
        boolean alwaysPlace = placeConditionValue.get().equalsIgnoreCase("always");
        return towerActivation() || alwaysPlace || (placeWhenAir && !mc.thePlayer.onGround) || (placeWhenFall && mc.thePlayer.fallDistance > 0) || (placeWhenNegativeMotion && mc.thePlayer.motionY < 0);
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        // No SpeedPot
        if (noSpeedPotValue.get()) {
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                mc.thePlayer.motionX = mc.thePlayer.motionX * 0.8F;
                mc.thePlayer.motionZ = mc.thePlayer.motionZ * 0.8F;
                mc.thePlayer.motionX = mc.thePlayer.motionX * 0.85F;
                mc.thePlayer.motionZ = mc.thePlayer.motionZ * 0.85F;
            }
        }

        // XZReducer
        mc.thePlayer.motionX *= xzMultiplier.get();
        mc.thePlayer.motionZ *= xzMultiplier.get();

        // Lock Rotation
        if (rotationsValue.get() && keepRotationValue.get() && lockRotation != null) {
            if (rotationModeValue.get().equalsIgnoreCase("spin")) {
                spinYaw += speenSpeedValue.get();
                spinYaw = MathHelper.wrapAngleTo180_float(spinYaw);
                speenRotation = new Rotation(spinYaw, speenPitchValue.get());
                RotationUtils.setTargetRotation(speenRotation);
            } else if (lockRotation != null)
                RotationUtils.setTargetRotation(RotationUtils.limitAngleChange(RotationUtils.serverRotation, lockRotation, RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())));
        }

        final String mode = modeValue.get();
        final EventState eventState = event.getEventState();

        // i think patches should be here instead
        for (int i = 0; i < 8; i++) {
            if (mc.thePlayer.inventory.mainInventory[i] != null
                    && mc.thePlayer.inventory.mainInventory[i].stackSize <= 0)
                mc.thePlayer.inventory.mainInventory[i] = null;
        }

        if ((!rotationsValue.get() || noHitCheckValue.get() || faceBlock) && placeModeValue.get().equalsIgnoreCase(eventState.getStateName()) && !towerActivation()) {
            place(false);
        }

        if (eventState == EventState.PRE && !towerActivation()) {
            if (!shouldPlace() || (!autoBlockMode.get().equalsIgnoreCase("Off") ? InventoryUtils.findAutoBlockBlock() == -1 : mc.thePlayer.getHeldItem() == null ||
                    !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)))
                return;

            findBlock(mode.equalsIgnoreCase("expand") && !towerActivation());
        }

        if (targetPlace == null) {
            if (placeableDelay.get())
                delayTimer.reset();
        }

        if (!towerActivation()) {
            verusState = 0;
            towerPlace = null;
            return;
        }

        mc.timer.timerSpeed = towerTimerValue.get();
        if (noMoveOnlyValue.get() && noMoveFreezeValue.get())
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;

        if (towerPlaceModeValue.get().equalsIgnoreCase(eventState.getStateName())) place(true);

        if (eventState == EventState.PRE) {
            towerPlace = null;
            timer.update();

            final boolean isHeldItemBlock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
            if (InventoryUtils.findAutoBlockBlock() != -1 || isHeldItemBlock) {
                launchY = (int) mc.thePlayer.posY;

                if (towerModeValue.get().equalsIgnoreCase("verus") || !stopWhenBlockAbove.get() || BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX,
                        mc.thePlayer.posY + 2, mc.thePlayer.posZ)) instanceof BlockAir) {
                    move(event);
                }

                final BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ);
                if (mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockAir) {
                    if (search(blockPos, true, true) && rotationsValue.get()) {
                        final VecRotation vecRotation = RotationUtils.faceBlock(blockPos);

                        if (vecRotation != null) {
                            RotationUtils.setTargetRotation(RotationUtils.limitAngleChange(RotationUtils.serverRotation, vecRotation.getRotation(), RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())));
                            towerPlace.setVec3(vecRotation.getVec());
                        }
                    }
                }
            }
        }
    }

    /**
     * Search for new target block
     */
    private void findBlock(final boolean expand) {
        final BlockPos blockPosition = shouldGoDown ? (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.6D, mc.thePlayer.posZ)
                : new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.6, mc.thePlayer.posZ).down()) :
                (!towerActivation() && (sameYValue.get() || ((autoJumpValue.get() && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || (smartSpeedValue.get() && NightX.moduleManager.getModule(Speed.class).getState())) && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump))) && launchY <= mc.thePlayer.posY ? (new BlockPos(mc.thePlayer.posX, launchY - 1, mc.thePlayer.posZ)) :
                        (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? new BlockPos(mc.thePlayer)
                                : new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()));

        if (!expand && (!BlockUtils.isReplaceable(blockPosition) || search(blockPosition, !shouldGoDown, false)))
            return;

        if (expand) {
            double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
            int x = omniDirectionalExpand.get() ? (int) Math.round(-Math.sin(yaw)) : mc.thePlayer.getHorizontalFacing().getDirectionVec().getX();
            int z = omniDirectionalExpand.get() ? (int) Math.round(Math.cos(yaw)) : mc.thePlayer.getHorizontalFacing().getDirectionVec().getZ();

            for (int i = 0; i < expandLengthValue.get(); i++) {
                if (search(blockPosition.add(x * i, 0, z * i), false, false))
                    return;
            }
        } else if (searchValue.get()) {
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    if (search(blockPosition.add(x, 0, z), !shouldGoDown, false))
                        return;
        }
    }

    /**
     * Place target block
     */
    private void place(boolean towerActive) {
        if ((towerActive ? towerPlace : targetPlace) == null) {
            if (placeableDelay.get())
                delayTimer.reset();
            return;
        }

        if (!towerActivation() && (!delayTimer.hasTimePassed(delay) || ((sameYValue.get() || ((autoJumpValue.get() || (smartSpeedValue.get() && NightX.moduleManager.getModule(Speed.class).getState())) && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump))) && launchY - 1 != (int) (towerActive ? towerPlace : targetPlace).getVec3().yCoord)))
            return;

        int blockSlot = -1;
        ItemStack itemStack = mc.thePlayer.getHeldItem();

        if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
            if (autoBlockMode.get().equalsIgnoreCase("Off"))
                return;

            blockSlot = InventoryUtils.findAutoBlockBlock();

            if (blockSlot == -1)
                return;

            if (autoBlockMode.get().equalsIgnoreCase("Switch")) {
                mc.thePlayer.inventory.currentItem = blockSlot - 36;
                mc.playerController.updateController();
            }

            if (autoBlockMode.get().equalsIgnoreCase("Spoof")) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(blockSlot - 36));
                itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).getStack();
            }

            if (autoBlockMode.get().equalsIgnoreCase("LiteSpoof")) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(blockSlot - 36));
                itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).getStack();
            }
        }

        // blacklist check
        if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) itemStack.getItem()).getBlock();
            if (InventoryUtils.BLOCK_BLACKLIST.contains(block) || !block.isFullCube() || itemStack.stackSize <= 0)
                return;
        }

        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, (towerActive ? towerPlace : targetPlace).getBlockPos(),
                (towerActive ? towerPlace : targetPlace).getEnumFacing(), (towerActive ? towerPlace : targetPlace).getVec3())) {
            delayTimer.reset();
            delay = (!placeableDelay.get() ? 0L : TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get()));

            if (mc.thePlayer.onGround) {
                final float modifier = speedModifierValue.get();

                mc.thePlayer.motionX *= modifier;
                mc.thePlayer.motionZ *= modifier;
            }

            if (swingValue.get())
                mc.thePlayer.swingItem();
            else
                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }

        // Reset
        if (towerActive)
            this.towerPlace = null;
        else
            this.targetPlace = null;

        if (blockSlot >= 0 && autoBlockMode.get().equalsIgnoreCase("Spoof"))
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    /**
     * Disable scaffold module
     */
    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false;

            if (eagleSneaking)
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight))
            mc.gameSettings.keyBindRight.pressed = false;

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft))
            mc.gameSettings.keyBindLeft.pressed = false;

        lockRotation = null;
        lookupRotation = null;
        mc.timer.timerSpeed = 1F;
        shouldGoDown = false;
        faceBlock = false;

        if (lastSlot != mc.thePlayer.inventory.currentItem && autoBlockMode.get().equalsIgnoreCase("switch")) {
            mc.thePlayer.inventory.currentItem = lastSlot;
            mc.playerController.updateController();
        }

        if (slot != mc.thePlayer.inventory.currentItem && autoBlockMode.get().equalsIgnoreCase("spoof") || slot != mc.thePlayer.inventory.currentItem && autoBlockMode.get().equalsIgnoreCase("litespoof"))
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    /**
     * Entity movement event
     *
     * @param event
     */
    @EventTarget
    public void onMove(final MoveEvent event) {
        if (!safeWalkValue.get() || shouldGoDown)
            return;

        if (airSafeValue.get() || mc.thePlayer.onGround)
            event.setSafeWalk(true);
    }

    @EventTarget
    public void onJump(final JumpEvent event) {
        if (towerActivation())
            event.cancelEvent();
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        progress = (float) (System.currentTimeMillis() - lastMS) / 100F;
        if (progress >= 1) progress = 1;

        String counterMode = counterDisplayValue.get();
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final String info = getBlocksAmount() + " Blocks";
        int infoWidth = Fonts.fontSFUI40.getStringWidth(info);
        int infoWidth2 = Fonts.minecraftFont.getStringWidth(getBlocksAmount() + "");
        if (counterMode.equalsIgnoreCase("exhibition")) {
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) - 1, scaledResolution.getScaledHeight() / 2 - 36, 0xff000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) + 1, scaledResolution.getScaledHeight() / 2 - 36, 0xff000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 35, 0xff000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 37, 0xff000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 36, 0xff00ff00, false);
        }
        if (counterMode.equalsIgnoreCase("advanced")) {
            boolean canRenderStack = (slot >= 0 && slot < 9 && mc.thePlayer.inventory.mainInventory[slot] != null && mc.thePlayer.inventory.mainInventory[slot].getItem() != null && mc.thePlayer.inventory.mainInventory[slot].getItem() instanceof ItemBlock);
            if (canRenderStack) {
                RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledResolution.getScaledWidth() / 2 - 8, scaledResolution.getScaledHeight() / 2 - 25, scaledResolution.getScaledWidth() / 2 - 8);
                renderItemStack(mc.thePlayer.inventory.mainInventory[slot], 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.fontSFUI40.drawCenteredString(info, scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 - 36, -1);
        }

        if (counterMode.equalsIgnoreCase("sigma")) {
            GlStateManager.translate(0, -14F - (progress * 4F), 0);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(0.15F, 0.15F, 0.15F, progress);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.resetColor();
            Fonts.fontSFUI37.drawCenteredString(info, scaledResolution.getScaledWidth() / 2 + 0.1F, scaledResolution.getScaledHeight() - 70, new Color(1F, 1F, 1F, progress).getRGB(), false);
            GlStateManager.translate(0, 14F + (progress * 4F), 0);
        }

        if (counterMode.equalsIgnoreCase("novoline")) {
            if (slot >= 0 && slot < 9 && mc.thePlayer.inventory.mainInventory[slot] != null && mc.thePlayer.inventory.mainInventory[slot].getItem() != null && mc.thePlayer.inventory.mainInventory[slot].getItem() instanceof ItemBlock) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledResolution.getScaledWidth() / 2 - 22, scaledResolution.getScaledHeight() / 2 + 16, scaledResolution.getScaledWidth() / 2 - 22);
                renderItemStack(mc.thePlayer.inventory.mainInventory[slot], 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.minecraftFont.drawString(getBlocksAmount() + " Blocks", scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 + 20, -1, true);
        }

        if (counterMode.equalsIgnoreCase("nightx")) {
            if (slot >= 0 && slot < 9 && mc.thePlayer.inventory.mainInventory[slot] != null && mc.thePlayer.inventory.mainInventory[slot].getItem() != null && mc.thePlayer.inventory.mainInventory[slot].getItem() instanceof ItemBlock) {
                GlStateManager.pushMatrix();
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.minecraftFont.drawString(getBlocksAmount() + " Blocks", scaledResolution.getScaledWidth() / 1.95f, scaledResolution.getScaledHeight() / 2 + 20, -1, true);
        }
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (!markValue.get())
            return;

        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        int x = omniDirectionalExpand.get() ? (int) Math.round(-Math.sin(yaw)) : mc.thePlayer.getHorizontalFacing().getDirectionVec().getX();
        int z = omniDirectionalExpand.get() ? (int) Math.round(Math.cos(yaw)) : mc.thePlayer.getHorizontalFacing().getDirectionVec().getZ();

        for (int i = 0; i < ((modeValue.get().equalsIgnoreCase("Expand") && !towerActivation()) ? expandLengthValue.get() + 1 : 2); i++) {
            final BlockPos blockPos = new BlockPos(
                    mc.thePlayer.posX + x * i,
                    (!towerActivation()
                            && (sameYValue.get() ||
                            ((autoJumpValue.get() ||
                                    (smartSpeedValue.get() && NightX.moduleManager.getModule(Speed.class).getState()))
                                    && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump)))
                            && launchY <= mc.thePlayer.posY) ? launchY - 1 : (mc.thePlayer.posY - (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? 0D : 1.0D) - (shouldGoDown ? 1D : 0)),
                    mc.thePlayer.posZ + z * i);
            final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

            if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                RenderUtils.drawBlockBox(blockPos, new Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()), false);
                break;
            }
        }
    }

    private boolean search(final BlockPos blockPosition, final boolean checks) {
        return search(blockPosition, checks, false);
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    private boolean search(final BlockPos blockPosition, final boolean checks, boolean towerActive) {
        faceBlock = false;

        if (!BlockUtils.isReplaceable(blockPosition))
            return false;


        final boolean staticYawMode = rotationLookupValue.get().equalsIgnoreCase("AAC")
                || (rotationLookupValue.get().equalsIgnoreCase("same") && (rotationModeValue.get().equalsIgnoreCase("AAC")
                || (rotationModeValue.get().contains("Static") && !rotationModeValue.get().equalsIgnoreCase("static3"))));

        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        PlaceRotation placeRotation = null;

        for (final EnumFacing side : StaticStorage.facings()) {
            final BlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtils.canBeClicked(neighbor))
                continue;

            final Vec3 dirVec = new Vec3(side.getDirectionVec());

            for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
                for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                    for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                        final Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        final Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));

                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18D || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null))
                            continue;

                        // face block
                        for (int i = 0; i < (staticYawMode ? 2 : 1); i++) {
                            final double diffX = staticYawMode && i == 0 ? 0 : hitVec.xCoord - eyesPos.xCoord;
                            final double diffY = hitVec.yCoord - eyesPos.yCoord;
                            final double diffZ = staticYawMode && i == 1 ? 0 : hitVec.zCoord - eyesPos.zCoord;

                            final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

                            Rotation rotation = new Rotation(
                                    MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                                    MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
                            );

                            lookupRotation = rotation;

                            if (rotationModeValue.get().equalsIgnoreCase("static") && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown()))
                                rotation = new Rotation(MovementUtils.getScaffoldRotation(mc.thePlayer.rotationYaw, mc.thePlayer.moveStrafing), staticPitchValue.get());

                            if ((rotationModeValue.get().equalsIgnoreCase("static2") || rotationModeValue.get().equalsIgnoreCase("static3")) && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown()))
                                rotation = new Rotation(rotation.getYaw(), staticPitchValue.get());

                            if (rotationModeValue.get().equalsIgnoreCase("custom") && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown()))
                                rotation = new Rotation(mc.thePlayer.rotationYaw + customYawValue.get(), customPitchValue.get());

                            if (rotationModeValue.get().equalsIgnoreCase("spin") && speenRotation != null && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown()))
                                rotation = speenRotation;

                            final Vec3 rotationVector = RotationUtils.getVectorForRotation(rotationLookupValue.get().equalsIgnoreCase("same") ? rotation : lookupRotation);
                            final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4, rotationVector.yCoord * 4, rotationVector.zCoord * 4);
                            final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);

                            if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor)))
                                continue;

                            if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(placeRotation.getRotation()))
                                placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                        }
                    }
                }
            }
        }

        if (placeRotation == null) return false;

        if (rotationsValue.get()) {
            if (minTurnSpeed.get() < 180) {
                final Rotation limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, placeRotation.getRotation(), RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get()));
                if ((int) (10 * MathHelper.wrapAngleTo180_float(limitedRotation.getYaw())) == (int) (10 * MathHelper.wrapAngleTo180_float(placeRotation.getRotation().getYaw()))
                        && (int) (10 * MathHelper.wrapAngleTo180_float(limitedRotation.getPitch())) == (int) (10 * MathHelper.wrapAngleTo180_float(placeRotation.getRotation().getPitch()))) {
                    RotationUtils.setTargetRotation(placeRotation.getRotation(), keepLengthValue.get());
                    lockRotation = placeRotation.getRotation();
                    faceBlock = true;
                } else {
                    RotationUtils.setTargetRotation(limitedRotation, keepLengthValue.get());
                    lockRotation = limitedRotation;
                    faceBlock = false;
                }
            } else {
                RotationUtils.setTargetRotation(placeRotation.getRotation(), keepLengthValue.get());
                lockRotation = placeRotation.getRotation();
                faceBlock = true;
            }

            if (rotationLookupValue.get().equalsIgnoreCase("same"))
                lookupRotation = lockRotation;
        }

        if (towerActive)
            towerPlace = placeRotation.getPlaceInfo();
        else
            targetPlace = placeRotation.getPlaceInfo();

        return true;
    }

    /**
     * @return hotbar blocks amount
     */
    private int getBlocksAmount() {
        int amount = 0;

        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) itemStack.getItem()).getBlock();
                if (!InventoryUtils.BLOCK_BLACKLIST.contains(block) && block.isFullCube())
                    amount += itemStack.stackSize;
            }
        }

        return amount;
    }


}