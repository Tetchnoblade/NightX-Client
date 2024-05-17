package net.aspw.client.injection.forge.mixins.entity;

import com.mojang.authlib.GameProfile;
import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.combat.KeepSprint;
import net.aspw.client.utils.CooldownHelper;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {

    @Shadow
    public PlayerCapabilities capabilities;
    @Shadow
    public InventoryPlayer inventory;
    @Shadow
    protected int flyToggleTimer;
    @Unique
    private ItemStack cooldownStack;
    @Unique
    private int cooldownStackSlot;

    @Override
    @Shadow
    public abstract ItemStack getHeldItem();

    @Shadow
    public abstract GameProfile getGameProfile();

    @Shadow
    protected abstract boolean canTriggerWalking();

    @Shadow
    protected abstract String getSwimSound();

    @Shadow
    public abstract FoodStats getFoodStats();

    @Shadow
    public abstract boolean isUsingItem();

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

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void onAttackTargetEntityWithCurrentItem(CallbackInfo callbackInfo) {
        final KeepSprint keepSprint = Objects.requireNonNull(Launch.moduleManager.getModule(KeepSprint.class));
        if (keepSprint.getState()) {
            this.motionX = this.motionX / 0.6;
            this.motionZ = this.motionZ / 0.6;
            if (MinecraftInstance.mc.thePlayer.moveForward > 0)
                this.setSprinting(true);
        }
    }
}