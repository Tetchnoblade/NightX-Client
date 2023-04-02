package net.aspw.client.injection.forge.mixins.client;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.movement.NoSlow;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {

    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D, ordinal = 0))
    public double noSlowSneakStrafe(double constant) {
        return (Client.moduleManager != null
                && Client.moduleManager.getModule(NoSlow.class) != null
                && Client.moduleManager.getModule(NoSlow.class).getState()) ? Client.moduleManager.getModule(NoSlow.class).getSneakStrafeMultiplier().get() : 0.3D;
    }

    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D, ordinal = 1))
    public double noSlowSneakForward(double constant) {
        return (Client.moduleManager != null
                && Client.moduleManager.getModule(NoSlow.class) != null
                && Client.moduleManager.getModule(NoSlow.class).getState()) ? Client.moduleManager.getModule(NoSlow.class).getSneakForwardMultiplier().get() : 0.3D;
    }

}
