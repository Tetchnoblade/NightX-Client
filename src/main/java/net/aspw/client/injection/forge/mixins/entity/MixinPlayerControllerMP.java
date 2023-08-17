package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Client;
import net.aspw.client.event.AttackEvent;
import net.aspw.client.event.ClickWindowEvent;
import net.aspw.client.util.CooldownHelper;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The type Mixin player controller mp.
 */
@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V"))
    private void attackEntity(EntityPlayer entityPlayer, Entity targetEntity, CallbackInfo callbackInfo) {
        if (targetEntity == null)
            return;

        Client.eventManager.callEvent(new AttackEvent(targetEntity));
        CooldownHelper.INSTANCE.resetLastAttackedTicks();
    }

    @Inject(method = "windowClick", at = @At("HEAD"))
    private void windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn, CallbackInfoReturnable<ItemStack> callbackInfo) {
        final ClickWindowEvent event = new ClickWindowEvent(windowId, slotId, mouseButtonClicked, mode);
        Client.eventManager.callEvent(event);

        if (event.isCancelled())
            callbackInfo.cancel();
    }
}