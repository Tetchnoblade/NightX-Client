package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.PacketUtils;
import net.ccbluex.liquidbounce.utils.PosLookInstance;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "LongJump", spacedName = "Long Jump", description = "", category = ModuleCategory.MOVEMENT)
public class LongJump extends Module {

    private final ListValue modeValue = new ListValue("Mode", new String[]{"NCP", "Damage", "AACv1", "AACv2", "AACv3", "AACv4", "Mineplex", "Mineplex2", "Mineplex3", "RedeskyMaki", "Redesky", "InfiniteRedesky", "MatrixFlag", "VerusDmg", "Pearl"}, "NCP");
    private final BoolValue autoJumpValue = new BoolValue("AutoJump", false);

    private final FloatValue ncpBoostValue = new FloatValue("NCPBoost", 3F, 1F, 10F, () -> modeValue.get().equalsIgnoreCase("ncp"));

    private final FloatValue matrixBoostValue = new FloatValue("MatrixFlag-Boost", 2F, 0F, 3F, () -> modeValue.get().equalsIgnoreCase("matrixflag"));
    private final FloatValue matrixHeightValue = new FloatValue("MatrixFlag-Height", 3F, 0F, 10F, () -> modeValue.get().equalsIgnoreCase("matrixflag"));
    private final BoolValue matrixSilentValue = new BoolValue("MatrixFlag-Silent", false, () -> modeValue.get().equalsIgnoreCase("matrixflag"));
    private final ListValue matrixBypassModeValue = new ListValue("MatrixFlag-BypassMode", new String[]{"Motion", "Clip", "None"}, "Clip", () -> modeValue.get().equalsIgnoreCase("matrixflag"));
    private final BoolValue matrixDebugValue = new BoolValue("MatrixFlag-Debug", false, () -> modeValue.get().equalsIgnoreCase("matrixflag"));

    private final BoolValue redeskyTimerBoostValue = new BoolValue("Redesky-TimerBoost", false, () -> modeValue.get().equalsIgnoreCase("redesky"));
    private final FloatValue redeskyTimerBoostStartValue = new FloatValue("Redesky-TimerBoostStart", 1.85F, 0.05F, 10F, () -> modeValue.get().equalsIgnoreCase("redesky") && redeskyTimerBoostValue.get());
    private final FloatValue redeskyTimerBoostEndValue = new FloatValue("Redesky-TimerBoostEnd", 1.0F, 0.05F, 10F, () -> modeValue.get().equalsIgnoreCase("redesky") && redeskyTimerBoostValue.get());
    private final IntegerValue redeskyTimerBoostSlowDownSpeedValue = new IntegerValue("Redesky-TimerBoost-SlowDownSpeed", 2, 1, 10, () -> modeValue.get().equalsIgnoreCase("redesky") && redeskyTimerBoostValue.get());
    private final BoolValue redeskyGlideAfterTicksValue = new BoolValue("Redesky-GlideAfterTicks", false, () -> modeValue.get().equalsIgnoreCase("redesky"));
    private final IntegerValue redeskyTickValue = new IntegerValue("Redesky-Ticks", 21, 1, 25, () -> modeValue.get().equalsIgnoreCase("redesky"));
    private final FloatValue redeskyYMultiplier = new FloatValue("Redesky-YMultiplier", 0.77F, 0.1F, 1F, () -> modeValue.get().equalsIgnoreCase("redesky"));
    private final FloatValue redeskyXZMultiplier = new FloatValue("Redesky-XZMultiplier", 0.9F, 0.1F, 1F, () -> modeValue.get().equalsIgnoreCase("redesky"));
    private final ListValue verusDmgModeValue = new ListValue("VerusDmg-DamageMode", new String[]{"Instant", "InstantC06", "Jump"}, "Instant", () -> modeValue.get().equalsIgnoreCase("verusdmg"));
    private final FloatValue verusBoostValue = new FloatValue("VerusDmg-Boost", 1.5F, 0F, 10F, () -> modeValue.get().equalsIgnoreCase("verusdmg"));
    private final FloatValue verusHeightValue = new FloatValue("VerusDmg-Height", 0.42F, 0F, 10F, () -> modeValue.get().equalsIgnoreCase("verusdmg"));
    private final FloatValue verusTimerValue = new FloatValue("VerusDmg-Timer", 1F, 0.05F, 10F, () -> modeValue.get().equalsIgnoreCase("verusdmg"));

    private final FloatValue pearlBoostValue = new FloatValue("Pearl-Boost", 4.25F, 0F, 10F, () -> modeValue.get().equalsIgnoreCase("pearl"));
    private final FloatValue pearlHeightValue = new FloatValue("Pearl-Height", 0.42F, 0F, 10F, () -> modeValue.get().equalsIgnoreCase("pearl"));
    private final FloatValue pearlTimerValue = new FloatValue("Pearl-Timer", 1F, 0.05F, 10F, () -> modeValue.get().equalsIgnoreCase("pearl"));

    private final FloatValue damageBoostValue = new FloatValue("Damage-Boost", 4.25F, 0F, 10F, () -> modeValue.get().equalsIgnoreCase("damage"));
    private final FloatValue damageHeightValue = new FloatValue("Damage-Height", 0.42F, 0F, 10F, () -> modeValue.get().equalsIgnoreCase("damage"));
    private final FloatValue damageTimerValue = new FloatValue("Damage-Timer", 1F, 0.05F, 10F, () -> modeValue.get().equalsIgnoreCase("damage"));
    private final BoolValue damageNoMoveValue = new BoolValue("Damage-NoMove", false, () -> modeValue.get().equalsIgnoreCase("damage"));
    private final BoolValue damageARValue = new BoolValue("Damage-AutoReset", false, () -> modeValue.get().equalsIgnoreCase("damage"));
    private final MSTimer dmgTimer = new MSTimer();
    private final PosLookInstance posLookInstance = new PosLookInstance();
    private boolean jumped;
    private boolean canBoost;
    private boolean teleported;
    private boolean canMineplexBoost;
    private int ticks = 0;
    private float currentTimer = 1F;
    private boolean verusDmged, hpxDamage, damaged = false;
    private int verusJumpTimes = 0;
    private int pearlState = 0;
    private double lastMotX, lastMotY, lastMotZ;
    private boolean flagged = false;
    private boolean hasFell = false;

    private void debug(String message) {
        if (matrixDebugValue.get())
            ClientUtils.displayChatMessage(message);
    }

    public void onEnable() {
        if (mc.thePlayer == null) return;
        if (modeValue.get().equalsIgnoreCase("redesky") && redeskyTimerBoostValue.get())
            currentTimer = redeskyTimerBoostStartValue.get();

        ticks = 0;
        verusDmged = false;
        hpxDamage = false;
        damaged = false;
        flagged = false;
        hasFell = false;
        pearlState = 0;
        verusJumpTimes = 0;

        dmgTimer.reset();
        posLookInstance.reset();

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        if (modeValue.get().equalsIgnoreCase("verusdmg")) {
            if (verusDmgModeValue.get().equalsIgnoreCase("Instant")) {
                if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 4, 0).expand(0, 0, 0)).isEmpty()) {
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y + 4, mc.thePlayer.posZ, false));
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, false));
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));
                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                }
            } else if (verusDmgModeValue.get().equalsIgnoreCase("InstantC06")) {
                if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 4, 0).expand(0, 0, 0)).isEmpty()) {
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y + 4, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, y, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                }
            } else if (verusDmgModeValue.get().equalsIgnoreCase("Jump")) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    verusJumpTimes = 1;
                }
            }
        }

        if (modeValue.get().equalsIgnoreCase("matrixflag")) {
            if (matrixBypassModeValue.get().equalsIgnoreCase("none")) {
                debug("no less flag enabled.");
                hasFell = true;
                return;
            }
            if (mc.thePlayer.onGround) {
                if (matrixBypassModeValue.get().equalsIgnoreCase("clip")) {
                    mc.thePlayer.setPosition(x, y + 0.01, z);
                    debug("clipped");
                }
                if (matrixBypassModeValue.get().equalsIgnoreCase("motion"))
                    mc.thePlayer.jump();
            } else if (mc.thePlayer.fallDistance > 0F) {
                hasFell = true;
                debug("falling detected");
            }
        }
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (modeValue.get().equalsIgnoreCase("matrixflag")) {
            if (hasFell) {
                if (!flagged && !matrixSilentValue.get()) {
                    MovementUtils.strafe(matrixBoostValue.get());
                    mc.thePlayer.motionY = matrixHeightValue.get();
                    debug("triggering");
                }
            } else {
                if (matrixBypassModeValue.get().equalsIgnoreCase("motion")) {
                    mc.thePlayer.motionX *= 0.2;
                    mc.thePlayer.motionZ *= 0.2;
                    if (mc.thePlayer.fallDistance > 0) {
                        hasFell = true;
                        debug("activated");
                    }
                }
                if (matrixBypassModeValue.get().equalsIgnoreCase("clip") && mc.thePlayer.motionY < 0F) {
                    hasFell = true;
                    debug("activated");
                }
            }
            return;
        }

        if (modeValue.get().equalsIgnoreCase("verusdmg")) {
            if (mc.thePlayer.hurtTime > 0 && !verusDmged) {
                verusDmged = true;
                MovementUtils.strafe(verusBoostValue.get());
                mc.thePlayer.motionY = verusHeightValue.get();
            }

            if (verusDmgModeValue.get().equalsIgnoreCase("Jump") && verusJumpTimes < 5) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    verusJumpTimes += 1;
                }
                return;
            }

            if (verusDmged)
                mc.timer.timerSpeed = verusTimerValue.get();
            else {
                mc.thePlayer.movementInput.moveForward = 0F;
                mc.thePlayer.movementInput.moveStrafe = 0F;
                if (!verusDmgModeValue.get().equalsIgnoreCase("Jump"))
                    mc.thePlayer.motionY = 0;
            }

            return;
        }

        if (modeValue.get().equalsIgnoreCase("damage")) {
            if (mc.thePlayer.hurtTime > 0 && !damaged) {
                damaged = true;
                MovementUtils.strafe(damageBoostValue.get());
                mc.thePlayer.motionY = damageHeightValue.get();
            }
            if (damaged) {
                mc.timer.timerSpeed = damageTimerValue.get();
                if (damageARValue.get() && mc.thePlayer.hurtTime <= 0) damaged = false;
            }

            return;
        }

        if (modeValue.get().equalsIgnoreCase("pearl")) {
            int enderPearlSlot = getPearlSlot();
            if (pearlState == 0) {
                if (enderPearlSlot == -1) {
                    LiquidBounce.hud.addNotification(new Notification("You don't have any ender pearl!", Notification.Type.ERROR));
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

            if (pearlState == 1 && mc.thePlayer.hurtTime > 0) {
                pearlState = 2;
                MovementUtils.strafe(pearlBoostValue.get());
                mc.thePlayer.motionY = pearlHeightValue.get();
            }

            if (pearlState == 2)
                mc.timer.timerSpeed = pearlTimerValue.get();

            return;
        }

        if (jumped) {
            final String mode = modeValue.get();

            if (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying) {
                jumped = false;
                canMineplexBoost = false;

                if (mode.equalsIgnoreCase("NCP")) {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                }
                return;
            }

            switch (mode.toLowerCase()) {
                case "ncp":
                    MovementUtils.strafe(MovementUtils.getSpeed() * (canBoost ? ncpBoostValue.get() : 1F));
                    canBoost = false;
                    break;
                case "aacv1":
                    mc.thePlayer.motionY += 0.05999D;
                    MovementUtils.strafe(MovementUtils.getSpeed() * 1.08F);
                    break;
                case "aacv2":
                case "mineplex3":
                    mc.thePlayer.jumpMovementFactor = 0.09F;
                    mc.thePlayer.motionY += 0.0132099999999999999999999999999;
                    mc.thePlayer.jumpMovementFactor = 0.08F;
                    MovementUtils.strafe();
                    break;
                case "aacv3":
                    final EntityPlayerSP player = mc.thePlayer;

                    if (player.fallDistance > 0.5F && !teleported) {
                        double value = 3;
                        EnumFacing horizontalFacing = player.getHorizontalFacing();
                        double x = 0;
                        double z = 0;
                        switch (horizontalFacing) {
                            case NORTH:
                                z = -value;
                                break;
                            case EAST:
                                x = +value;
                                break;
                            case SOUTH:
                                z = +value;
                                break;
                            case WEST:
                                x = -value;
                                break;
                        }

                        player.setPosition(player.posX + x, player.posY, player.posZ + z);
                        teleported = true;
                    }
                    break;
                case "mineplex":
                    mc.thePlayer.motionY += 0.0132099999999999999999999999999;
                    mc.thePlayer.jumpMovementFactor = 0.08F;
                    MovementUtils.strafe();
                    break;
                case "mineplex2":
                    if (!canMineplexBoost)
                        break;

                    mc.thePlayer.jumpMovementFactor = 0.1F;

                    if (mc.thePlayer.fallDistance > 1.5F) {
                        mc.thePlayer.jumpMovementFactor = 0F;
                        mc.thePlayer.motionY = -10F;
                    }
                    MovementUtils.strafe();
                    break;
                // add timer to use longjump longer forward without boost
                case "aacv4":
                    mc.thePlayer.jumpMovementFactor = 0.05837456f;
                    mc.timer.timerSpeed = 0.5F;
                    break;
                //simple lmfao
                case "redeskymaki":
                    mc.thePlayer.jumpMovementFactor = 0.15f;
                    mc.thePlayer.motionY += 0.05F;
                    break;
                case "redesky":
                    if (redeskyTimerBoostValue.get()) {
                        mc.timer.timerSpeed = currentTimer;
                    }
                    if (ticks < redeskyTickValue.get()) {
                        mc.thePlayer.jump();
                        mc.thePlayer.motionY *= redeskyYMultiplier.get();
                        mc.thePlayer.motionX *= redeskyXZMultiplier.get();
                        mc.thePlayer.motionZ *= redeskyXZMultiplier.get();
                    } else {
                        if (redeskyGlideAfterTicksValue.get()) {
                            mc.thePlayer.motionY += 0.03F;
                        }
                        if (redeskyTimerBoostValue.get() && currentTimer > redeskyTimerBoostEndValue.get()) {
                            currentTimer = Math.max(0.08F, currentTimer - 0.05F * redeskyTimerBoostSlowDownSpeedValue.get()); // zero-timer protection
                        }
                    }
                    ticks++;
                    break;
                case "infiniteredesky":
                    if (mc.thePlayer.fallDistance > 0.6F)
                        mc.thePlayer.motionY += 0.02F;

                    MovementUtils.strafe((float) Math.min(0.85, Math.max(0.25, MovementUtils.getSpeed() * 1.05878)));
                    break;
            }
        }

        if (autoJumpValue.get() && mc.thePlayer.onGround && MovementUtils.isMoving()) {
            jumped = true;
            mc.thePlayer.jump();
        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        final String mode = modeValue.get();

        if (mode.equalsIgnoreCase("mineplex3")) {
            if (mc.thePlayer.fallDistance != 0)
                mc.thePlayer.motionY += 0.037;
        } else if (mode.equalsIgnoreCase("ncp") && !MovementUtils.isMoving() && jumped) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            event.zeroXZ();
        }

        if ((mode.equalsIgnoreCase("damage") && damageNoMoveValue.get() && !damaged) || (mode.equalsIgnoreCase("verusdmg") && !verusDmged))
            event.zeroXZ();

        if (mode.equalsIgnoreCase("pearl") && pearlState != 2)
            event.cancelEvent();

        if (matrixSilentValue.get() && hasFell && !flagged)
            event.cancelEvent();
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final String mode = modeValue.get();
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();
            if (mode.equalsIgnoreCase("verusdmg") && verusDmgModeValue.get().equalsIgnoreCase("Jump") && verusJumpTimes < 5) {
                packetPlayer.onGround = false;
            }
            if (mode.equalsIgnoreCase("matrixflag")) {
                if (event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook && posLookInstance.equalFlag((C03PacketPlayer.C06PacketPlayerPosLook) event.getPacket())) {
                    posLookInstance.reset();
                    mc.thePlayer.motionX = lastMotX;
                    mc.thePlayer.motionY = lastMotY;
                    mc.thePlayer.motionZ = lastMotZ;
                    debug("should be launched by now");
                } else if (matrixSilentValue.get()) {
                    if (hasFell && !flagged) {
                        if (packetPlayer.isMoving()) {
                            debug("modifying packet: rotate false, onGround false, moving enabled, x, y, z set to expected speed");
                            packetPlayer.onGround = false;
                            double[] data = MovementUtils.getXZDist(matrixBoostValue.get(), packetPlayer.rotating ? packetPlayer.yaw : mc.thePlayer.rotationYaw);
                            lastMotX = data[0];
                            lastMotZ = data[1];
                            lastMotY = matrixHeightValue.get();
                            packetPlayer.x += lastMotX;
                            packetPlayer.y += lastMotY;
                            packetPlayer.z += lastMotZ;
                        }
                    }
                }
            }
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook && mode.equalsIgnoreCase("matrixflag") && hasFell) {
            debug("flag check started");
            flagged = true;
            posLookInstance.set((S08PacketPlayerPosLook) event.getPacket());
            if (!matrixSilentValue.get()) {
                debug("data saved");
                lastMotX = mc.thePlayer.motionX;
                lastMotY = mc.thePlayer.motionY;
                lastMotZ = mc.thePlayer.motionZ;
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onJump(final JumpEvent event) {
        jumped = true;
        canBoost = true;
        teleported = false;

        if (getState()) {
            switch (modeValue.get().toLowerCase()) {
                case "mineplex":
                    event.setMotion(event.getMotion() * 4.08f);
                    break;
                case "mineplex2":
                    if (mc.thePlayer.isCollidedHorizontally) {
                        event.setMotion(2.31f);
                        canMineplexBoost = true;
                        mc.thePlayer.onGround = false;
                    }
                    break;
                case "aacv4":
                    event.setMotion(event.getMotion() * 1.0799F);
                    break;
            }
        }

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

    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.speedInAir = 0.02F;
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}
