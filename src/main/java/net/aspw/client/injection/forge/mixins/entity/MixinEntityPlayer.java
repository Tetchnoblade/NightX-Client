package net.aspw.client.injection.forge.mixins.entity;

import com.mojang.authlib.GameProfile;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.aspw.client.Launch;
import net.aspw.client.features.api.PacketManager;
import net.aspw.client.features.module.impl.combat.KeepSprint;
import net.aspw.client.features.module.impl.movement.Flight;
import net.aspw.client.features.module.impl.movement.LongJump;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.player.BowJump;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.utils.CooldownHelper;
import net.aspw.client.utils.MinecraftInstance;
import net.aspw.client.utils.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * The type Mixin entity player.
 */
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {

    /**
     * The Capabilities.
     */
    @Shadow
    public PlayerCapabilities capabilities;
    /**
     * The Inventory.
     */
    @Shadow
    public InventoryPlayer inventory;
    /**
     * The Fly toggle timer.
     */
    @Shadow
    protected int flyToggleTimer;
    @Unique
    private ItemStack cooldownStack;
    @Unique
    private int cooldownStackSlot;

    @Override
    @Shadow
    public abstract ItemStack getHeldItem();

    /**
     * Gets game profile.
     *
     * @return the game profile
     */
    @Shadow
    public abstract GameProfile getGameProfile();

    /**
     * Can trigger walking boolean.
     *
     * @return the boolean
     */
    @Shadow
    protected abstract boolean canTriggerWalking();

    /**
     * Gets swim sound.
     *
     * @return the swim sound
     */
    @Shadow
    protected abstract String getSwimSound();

    /**
     * Gets food stats.
     *
     * @return the food stats
     */
    @Shadow
    public abstract FoodStats getFoodStats();

    /**
     * Gets item in use duration.
     *
     * @return the item in use duration
     */
    @Shadow
    public abstract int getItemInUseDuration();

    /**
     * Gets item in use.
     *
     * @return the item in use
     */
    @Shadow
    public abstract ItemStack getItemInUse();

    /**
     * Is using item boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isUsingItem();

    /**
     * Is player sleeping boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isPlayerSleeping();

    @Shadow(remap = false)
    public abstract float getDefaultEyeHeight();

    private final ItemStack[] mainInventory = new ItemStack[36];
    private final ItemStack[] armorInventory = new ItemStack[4];

    /**
     * @author As_pw
     * @reason FakeY
     */
    @Override
    @Overwrite
    public float getEyeHeight() {
        final Minecraft mc = MinecraftInstance.mc;
        final LongJump longJump = Objects.requireNonNull(Launch.moduleManager.getModule(LongJump.class));
        final Flight flight = Objects.requireNonNull(Launch.moduleManager.getModule(Flight.class));
        final Speed speed = Objects.requireNonNull(Launch.moduleManager.getModule(Speed.class));
        final BowJump bowJump = Objects.requireNonNull(Launch.moduleManager.getModule(BowJump.class));
        if (this.isPlayerSleeping())
            return 0.2F;
        if (flight.getState() && flight.getFakeYValue().get())
            return (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - flight.getY()));
        if (speed.getState() && speed.getFakeYValue().get())
            return (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - speed.getY()));
        if (longJump.getState() && longJump.getFakeYValue().get())
            return (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - longJump.getY()));
        if (bowJump.getState() && bowJump.getFakeYValue().get())
            return (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - bowJump.getY()));
        return PacketManager.lastEyeHeight + (PacketManager.eyeHeight - PacketManager.lastEyeHeight) * mc.timer.renderPartialTicks;
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void injectCooldown(final CallbackInfo callbackInfo) {
        if (getGameProfile() == MinecraftInstance.mc.thePlayer.getGameProfile()) {
            CooldownHelper.INSTANCE.incrementLastAttackedTicks();
            CooldownHelper.INSTANCE.updateGenericAttackSpeed(getHeldItem());

            if (cooldownStackSlot != inventory.currentItem)
                CooldownHelper.INSTANCE.resetLastAttackedTicks();

            cooldownStack = getHeldItem();
            cooldownStackSlot = inventory.currentItem;
        }
    }

    /**
     * @author As_pw
     * @reason ViaVersion Tweaks
     */
    @Inject(method = "dropItem", at = @At("HEAD"))
    private void dropItem(ItemStack p_dropItem_1_, boolean p_dropItem_2_, boolean p_dropItem_3_, CallbackInfoReturnable<EntityItem> cir) {
        for (int i = 0; i < this.mainInventory.length; ++i) {
            if (!MinecraftInstance.mc.isIntegratedServerRunning() && ProtocolBase.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_16))
                PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
            if (this.mainInventory[i] != null) {
                this.mainInventory[i] = null;
            }
        }

        for (int j = 0; j < this.armorInventory.length; ++j) {
            if (!MinecraftInstance.mc.isIntegratedServerRunning() && ProtocolBase.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_16))
                PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
            if (this.armorInventory[j] != null) {
                this.armorInventory[j] = null;
            }
        }
    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void onAttackTargetEntityWithCurrentItem(CallbackInfo callbackInfo) {
        final KeepSprint keepSprint = Objects.requireNonNull(Launch.moduleManager.getModule(KeepSprint.class));
        if (keepSprint.getState()) {
            this.motionX = this.motionX / 0.6;
            this.motionZ = this.motionZ / 0.6;
        }
    }
}