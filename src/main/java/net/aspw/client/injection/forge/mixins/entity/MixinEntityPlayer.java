package net.aspw.client.injection.forge.mixins.entity;

import com.mojang.authlib.GameProfile;
import net.aspw.client.Client;
import net.aspw.client.features.module.impl.minigames.TestModule2;
import net.aspw.client.features.module.impl.movement.Flight;
import net.aspw.client.features.module.impl.movement.LongJump;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.player.BowLongJump;
import net.aspw.client.util.CooldownHelper;
import net.aspw.client.util.MinecraftInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
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
     * The Fly toggle timer.
     */
    @Shadow
    protected int flyToggleTimer;

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

    /**
     * The Inventory.
     */
    @Shadow
    public InventoryPlayer inventory;
    private ItemStack cooldownStack;
    private int cooldownStackSlot;
    private final ItemStack[] mainInventory = new ItemStack[36];
    private final ItemStack[] armorInventory = new ItemStack[4];

    /**
     * @author As_pw
     * @reason FakeY
     */
    @Overwrite
    public float getEyeHeight() {
        final Minecraft mc = MinecraftInstance.mc;
        final LongJump longJump = Objects.requireNonNull(Client.moduleManager.getModule(LongJump.class));
        final Flight flight = Objects.requireNonNull(Client.moduleManager.getModule(Flight.class));
        final Speed speed = Objects.requireNonNull(Client.moduleManager.getModule(Speed.class));
        final BowLongJump bowLongJump = Objects.requireNonNull(Client.moduleManager.getModule(BowLongJump.class));
        final TestModule2 testModule2 = Objects.requireNonNull(Client.moduleManager.getModule(TestModule2.class));
        if (longJump.getState() && longJump.getFakeYValue().get()) {
            float f2 = 1.62F;
            final double y = longJump.getY();
            f2 = (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - y));
            return f2;
        }
        if (flight.getState() && flight.getFakeYValue().get()) {
            float f2 = 1.62F;
            final double y = flight.getY();
            f2 = (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - y));
            return f2;
        }
        if (speed.getState() && speed.getFakeYValue().get()) {
            float f2 = 1.62F;
            final double y = speed.getY();
            f2 = (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - y));
            return f2;
        }
        if (testModule2.getState() && testModule2.getStage() < 6) {
            float f2 = 1.62F;
            final double y = testModule2.getFakeY();
            f2 = (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - y));
            return f2;
        }
        if (bowLongJump.getState() && bowLongJump.getFakeYValue().get()) {
            float f2 = 1.62F;
            final double y = bowLongJump.getY();
            f2 = (float) (1.62F - (mc.thePlayer.lastTickPosY + (((mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks)) - y));
            return f2;
        } else {
            float f = this.getDefaultEyeHeight();
            if (this.isPlayerSleeping()) {
                f = 0.2F;
            }

            if (this.isSneaking()) {
                f -= 0.08F;
            }
            return f;
        }
    }

    /**
     * @author As_pw
     * @reason Improves
     */
    @Inject(method = "dropItem", at = @At("HEAD"))
    private void dropItem(ItemStack p_dropItem_1_, boolean p_dropItem_2_, boolean p_dropItem_3_, CallbackInfoReturnable<EntityItem> cir) {
        for (int i = 0; i < this.mainInventory.length; ++i) {
            if (this.mainInventory[i] != null) {
                this.mainInventory[i] = null;
            }
        }

        for (int j = 0; j < this.armorInventory.length; ++j) {
            if (this.armorInventory[j] != null) {
                this.armorInventory[j] = null;
            }
        }
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void injectCooldown(final CallbackInfo callbackInfo) {
        if (getGameProfile() == MinecraftInstance.mc.thePlayer.getGameProfile()) {
            CooldownHelper.INSTANCE.incrementLastAttackedTicks();
            CooldownHelper.INSTANCE.updateGenericAttackSpeed(getHeldItem());

            if (cooldownStackSlot != inventory.currentItem || !ItemStack.areItemStacksEqual(cooldownStack, getHeldItem())) {
                CooldownHelper.INSTANCE.resetLastAttackedTicks();
            }

            cooldownStack = getHeldItem();
            cooldownStackSlot = inventory.currentItem;
        }
    }
}