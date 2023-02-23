package net.aspw.client.injection.forge.mixins.cape;

import dev.tr7zw.waveycapes.CapeHolder;
import dev.tr7zw.waveycapes.sim.StickSimulation;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public class PlayerMixin implements CapeHolder {

    private StickSimulation stickSimulation = new StickSimulation();

    @Override
    public StickSimulation getSimulation() {
        return stickSimulation;
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void moveCloakUpdate(CallbackInfo info) {
        if ((Object) this instanceof EntityPlayer) {
            simulate((EntityPlayer) (Object) this);
        }
    }

}
