package net.ccbluex.liquidbounce.injection.forge.mixins.client;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlowdown;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {

    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D, ordinal = 0))
    public double noSlowSneakStrafe(double constant) {
        return (LiquidBounce.moduleManager != null
                && LiquidBounce.moduleManager.getModule(NoSlowdown.class) != null
                && LiquidBounce.moduleManager.getModule(NoSlowdown.class).getState()) ? LiquidBounce.moduleManager.getModule(NoSlowdown.class).getSneakStrafeMultiplier().get() : 0.3D;
    }

    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D, ordinal = 1))
    public double noSlowSneakForward(double constant) {
        return (LiquidBounce.moduleManager != null
                && LiquidBounce.moduleManager.getModule(NoSlowdown.class) != null
                && LiquidBounce.moduleManager.getModule(NoSlowdown.class).getState()) ? LiquidBounce.moduleManager.getModule(NoSlowdown.class).getSneakForwardMultiplier().get() : 0.3D;
    }

}
