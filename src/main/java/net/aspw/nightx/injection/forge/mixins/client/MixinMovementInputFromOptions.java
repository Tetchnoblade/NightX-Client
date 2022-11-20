package net.aspw.nightx.injection.forge.mixins.client;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.movement.NoSlow;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {

    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D, ordinal = 0))
    public double noSlowSneakStrafe(double constant) {
        return (NightX.moduleManager != null
                && NightX.moduleManager.getModule(NoSlow.class) != null
                && NightX.moduleManager.getModule(NoSlow.class).getState()) ? NightX.moduleManager.getModule(NoSlow.class).getSneakStrafeMultiplier().get() : 0.3D;
    }

    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D, ordinal = 1))
    public double noSlowSneakForward(double constant) {
        return (NightX.moduleManager != null
                && NightX.moduleManager.getModule(NoSlow.class) != null
                && NightX.moduleManager.getModule(NoSlow.class).getState()) ? NightX.moduleManager.getModule(NoSlow.class).getSneakForwardMultiplier().get() : 0.3D;
    }

}
