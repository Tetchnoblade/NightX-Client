package net.aspw.client.injection.forge.mixins.entity;

import com.mojang.authlib.GameProfile;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import net.aspw.client.Client;
import net.aspw.client.event.*;
import net.aspw.client.features.api.PacketManager;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.combat.TPAura;
import net.aspw.client.features.module.impl.exploit.AntiDesync;
import net.aspw.client.features.module.impl.exploit.AntiHunger;
import net.aspw.client.features.module.impl.exploit.PortalMenu;
import net.aspw.client.features.module.impl.movement.Flight;
import net.aspw.client.features.module.impl.movement.NoSlow;
import net.aspw.client.features.module.impl.movement.SilentSneak;
import net.aspw.client.features.module.impl.movement.Sprint;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.injection.access.IEntityPlayerSP;
import net.aspw.client.protocol.Protocol;
import net.aspw.client.util.CooldownHelper;
import net.aspw.client.util.MovementUtils;
import net.aspw.client.util.Rotation;
import net.aspw.client.util.RotationUtils;
import net.aspw.client.visual.client.GuiTeleportation;
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui;
import net.aspw.client.visual.client.clickgui.tab.NewUi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * The type Mixin entity player sp.
 */
@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer implements IEntityPlayerSP {

    /**
     * The Server sprint state.
     */
    @Shadow
    public boolean serverSprintState;
    /**
     * The Sprinting ticks left.
     */
    @Shadow
    public int sprintingTicksLeft;
    /**
     * The Time in portal.
     */
    @Shadow
    public float timeInPortal;
    /**
     * The Prev time in portal.
     */
    @Shadow
    public float prevTimeInPortal;
    /**
     * The Movement input.
     */
    @Shadow
    public MovementInput movementInput;
    /**
     * The Horse jump power.
     */
    @Shadow
    public float horseJumpPower;
    /**
     * The Horse jump power counter.
     */
    @Shadow
    public int horseJumpPowerCounter;
    /**
     * The Send queue.
     */
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;
    /**
     * The Sprint toggle timer.
     */
    @Shadow
    protected int sprintToggleTimer;
    /**
     * The Mc.
     */
    @Shadow
    protected Minecraft mc;
    @Shadow
    private boolean serverSneakState;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    public int positionUpdateTicks;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Unique
    private boolean lastOnGround;

    public MixinEntityPlayerSP() {
        super(null, null);
    }

    /**
     * Play sound.
     *
     * @param name   the name
     * @param volume the volume
     * @param pitch  the pitch
     */
    @Shadow
    public abstract void playSound(String name, float volume, float pitch);

    /**
     * Sets sprinting.
     *
     * @param sprinting the sprinting
     */
    @Shadow
    public abstract void setSprinting(boolean sprinting);

    /**
     * Push out of blocks boolean.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @return the boolean
     */
    @Shadow
    protected abstract boolean pushOutOfBlocks(double x, double y, double z);

    /**
     * Send player abilities.
     */
    @Shadow
    public abstract void sendPlayerAbilities();

    /**
     * Send horse jump.
     */
    @Shadow
    protected abstract void sendHorseJump();

    /**
     * Is riding horse boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isRidingHorse();

    @Shadow
    public abstract boolean isSneaking();

    /**
     * Is current view entity boolean.
     *
     * @return the boolean
     * @author As_pw
     * @reason Fix Video
     */
    @Overwrite
    protected boolean isCurrentViewEntity() {
        final Flight flight = Objects.requireNonNull(Client.moduleManager.getModule(Flight.class));

        return (mc.getRenderViewEntity() != null && mc.getRenderViewEntity().equals(this)) || (Client.moduleManager != null && flight.getState());
    }

    /**
     * On update walking player.
     *
     * @author As_pw
     * @reason Update Event
     */
    @Overwrite
    public void onUpdateWalkingPlayer() {
        try {
            MotionEvent event = new MotionEvent(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
            Client.eventManager.callEvent(event);

            PacketManager.update();

            final AntiHunger antiHunger = Objects.requireNonNull(Client.moduleManager.getModule(AntiHunger.class));

            final SilentSneak sneak = Objects.requireNonNull(Client.moduleManager.getModule(SilentSneak.class));
            final boolean fakeSprint = antiHunger.getState() || (sneak.getState() && (!MovementUtils.isMoving()));

            ActionEvent actionEvent = new ActionEvent(this.isSprinting() && !fakeSprint, this.isSneaking());

            boolean sprinting = actionEvent.getSprinting();
            boolean sneaking = actionEvent.getSneaking();

            if (sprinting != this.serverSprintState) {
                if (sprinting)
                    this.sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, C0BPacketEntityAction.Action.START_SPRINTING));
                else
                    this.sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, C0BPacketEntityAction.Action.STOP_SPRINTING));

                this.serverSprintState = sprinting;
            }

            if (sneaking != this.serverSneakState && (!sneak.getState() || sneak.modeValue.get().equalsIgnoreCase("Legit"))) {
                if (sneaking)
                    this.sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, C0BPacketEntityAction.Action.START_SNEAKING));
                else
                    this.sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, C0BPacketEntityAction.Action.STOP_SNEAKING));

                this.serverSneakState = sneaking;
            }

            if (this.isCurrentViewEntity()) {
                float yaw = event.getYaw();
                float pitch = event.getPitch();
                float lastReportedYaw = RotationUtils.serverRotation.getYaw();
                float lastReportedPitch = RotationUtils.serverRotation.getPitch();

                if (RotationUtils.targetRotation != null) {
                    yaw = RotationUtils.targetRotation.getYaw();
                    pitch = RotationUtils.targetRotation.getPitch();
                }

                final AntiDesync antiDesync = Objects.requireNonNull(Client.moduleManager.getModule(AntiDesync.class));
                double xDiff = event.getX() - this.lastReportedPosX;
                double yDiff = event.getY() - this.lastReportedPosY;
                double zDiff = event.getZ() - this.lastReportedPosZ;
                double yawDiff = yaw - lastReportedYaw;
                double pitchDiff = pitch - lastReportedPitch;
                boolean moved = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff > (antiDesync.getState() ? 0D : 9.0E-4D) || this.positionUpdateTicks >= 20;
                boolean rotated = yawDiff != 0.0D || pitchDiff != 0.0D;

                if (this.ridingEntity == null) {
                    if (moved && rotated) {
                        this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(), event.getY(), event.getZ(), yaw, pitch, event.getOnGround()));
                    } else if (moved) {
                        this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(event.getX(), event.getY(), event.getZ(), event.getOnGround()));
                    } else if (rotated) {
                        this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, event.getOnGround()));
                    } else {
                        this.sendQueue.addToSendQueue(new C03PacketPlayer(event.getOnGround()));
                    }
                } else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D, this.motionZ, yaw, pitch, event.getOnGround()));
                    moved = false;
                }

                ++this.positionUpdateTicks;

                if (moved) {
                    this.lastReportedPosX = event.getX();
                    this.lastReportedPosY = event.getY();
                    this.lastReportedPosZ = event.getZ();
                    this.positionUpdateTicks = 0;
                }

                if (rotated) {
                    this.lastReportedYaw = yaw;
                    this.lastReportedPitch = pitch;
                }
            }

            if (this.isCurrentViewEntity())
                lastOnGround = event.getOnGround();

            event.setEventState(EventState.POST);

            Client.eventManager.callEvent(event);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "swingItem", at = @At("HEAD"))
    private void swingItem(CallbackInfo callbackInfo) {
        CooldownHelper.INSTANCE.resetLastAttackedTicks();
        if (Objects.requireNonNull(Client.moduleManager.getModule(Interface.class)).getSwingSoundValue().get()) {
            Client.tipSoundManager.getSwingSound().asyncPlay(Client.moduleManager.getSwingSoundPower());
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        PushOutEvent event = new PushOutEvent();
        if (this.noClip) event.cancelEvent();
        Client.eventManager.callEvent(event);

        if (event.isCancelled())
            callbackInfoReturnable.setReturnValue(false);
    }

    /**
     * @author As_pw
     * @reason Fix Gui
     */
    @Overwrite
    public void onLivingUpdate() {
        Client.eventManager.callEvent(new UpdateEvent());
        if (mc.currentScreen instanceof NewUi || mc.currentScreen instanceof ClickGui || mc.currentScreen instanceof GuiTeleportation) {
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
            mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
            mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
            mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
        }

        final KillAura killAura = Objects.requireNonNull(Client.moduleManager.getModule(KillAura.class));
        final TPAura tpAura = Objects.requireNonNull(Client.moduleManager.getModule(TPAura.class));

        if (!Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x") && (mc.thePlayer.isBlocking() || mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isUsingItem() || killAura.getState() && killAura.getTarget() != null && !killAura.getAutoBlockModeValue().get().equals("None") && !killAura.getAutoBlockModeValue().get().equals("Fake") || tpAura.getState() && tpAura.isBlocking())) {
            int packetId = 29;
            UserConnection userConnection = Via.getManager().getConnectionManager().getConnections().iterator().next();
            PacketWrapper packet = PacketWrapper.create(packetId, null, userConnection);
            PacketUtil.sendToServer(packet, Protocol1_8To1_9.class, true, true);
        }

        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;

            if (this.sprintingTicksLeft == 0) {
                this.setSprinting(false);
            }
        }

        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }

        this.prevTimeInPortal = this.timeInPortal;

        final PortalMenu portalMenu = Objects.requireNonNull(Client.moduleManager.getModule(PortalMenu.class));

        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()
                    && !portalMenu.getState()) {
                this.mc.displayGuiScreen(null);
            }

            if (this.timeInPortal == 0.0F) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
            }

            this.timeInPortal += 0.0125F;

            if (this.timeInPortal >= 1.0F) {
                this.timeInPortal = 1.0F;
            }

            this.inPortal = false;
        } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667F;

            if (this.timeInPortal > 1.0F) {
                this.timeInPortal = 1.0F;
            }
        } else {
            if (this.timeInPortal > 0.0F) {
                this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F) {
                this.timeInPortal = 0.0F;
            }
        }

        if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
        }

        boolean flag = this.movementInput.jump;
        boolean flag1 = this.movementInput.sneak;
        float f = 0.8F;
        boolean flag2 = this.movementInput.moveForward >= f;
        this.movementInput.updatePlayerMoveState();

        final NoSlow noSlow = Objects.requireNonNull(Client.moduleManager.getModule(NoSlow.class));

        if (getHeldItem() != null && (this.isUsingItem() || (getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isBlocking() && !this.isRiding()))) {
            final SlowDownEvent slowDownEvent = new SlowDownEvent(0.2F, 0.2F);
            Client.eventManager.callEvent(slowDownEvent);
            this.movementInput.moveStrafe *= slowDownEvent.getStrafe();
            this.movementInput.moveForward *= slowDownEvent.getForward();
            this.sprintToggleTimer = 0;
        }

        this.pushOutOfBlocks(this.posX - (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + (double) this.width * 0.35D);
        this.pushOutOfBlocks(this.posX - (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - (double) this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - (double) this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + (double) this.width * 0.35D);

        final Sprint sprint = Objects.requireNonNull(Client.moduleManager.getModule(Sprint.class));

        boolean flag3 = (float) this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;

        if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= f && !this.isSprinting() && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                this.sprintToggleTimer = 7;
            } else {
                this.setSprinting(true);
            }
        }

        if (!this.isSprinting() && this.movementInput.moveForward >= f && flag3 && (noSlow.getState() || !this.isUsingItem()) && !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.isKeyDown())
            this.setSprinting(true);

        final Scaffold scaffold = Objects.requireNonNull(Client.moduleManager.getModule(Scaffold.class));

        if ((scaffold.getState() && scaffold.getCanTower() && scaffold.sprintModeValue.get().equalsIgnoreCase("Off")) || (scaffold.getState() && scaffold.sprintModeValue.get().equalsIgnoreCase("Off")) || !sprint.getAllDirectionsValue().get() && RotationUtils.targetRotation != null && RotationUtils.getRotationDifference(new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)) > 30)
            this.setSprinting(false);

        if (this.isSprinting() && ((!(sprint.getState() && sprint.getAllDirectionsValue().get()) && this.movementInput.moveForward < f) || mc.thePlayer.isCollidedHorizontally || !flag3))
            this.setSprinting(false);

        if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
                if (!this.capabilities.isFlying) {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities();
                }
            } else if (!flag && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }

        if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
                this.motionY -= this.capabilities.getFlySpeed() * 3.0F;
            }

            if (this.movementInput.jump) {
                this.motionY += this.capabilities.getFlySpeed() * 3.0F;
            }
        }

        if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0F;
                }
            }

            if (flag && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            } else if (!flag && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0F;
            } else if (flag) {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter < 10) {
                    this.horseJumpPower = (float) this.horseJumpPowerCounter * 0.1F;
                } else {
                    this.horseJumpPower = 0.8F + 2.0F / (float) (this.horseJumpPowerCounter - 9) * 0.1F;
                }
            }
        } else {
            this.horseJumpPower = 0.0F;
        }

        super.onLivingUpdate();

        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }
}

