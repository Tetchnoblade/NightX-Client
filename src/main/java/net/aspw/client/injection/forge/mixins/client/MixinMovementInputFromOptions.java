package net.aspw.client.injection.forge.mixins.client;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.movement.NoSlow;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Objects;

/**
 * The type Mixin movement input from options.
 */
@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {

    /**
     * No slow sneak strafe double.
     *
     * @param constant the constant
     * @return the double
     */
    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D, ordinal = 0))
    public double noSlowSneakStrafe(double constant) {
        final NoSlow noSlow = Objects.requireNonNull(Client.moduleManager.getModule(NoSlow.class));

        return (Client.moduleManager != null
                && Client.moduleManager.getModule(NoSlow.class) != null
                && noSlow.getState()) ? noSlow.getSneakStrafeMultiplier().get() : 0.3D;
    }

    /**
     * No slow sneak forward double.
     *
     * @param constant the constant
     * @return the double
     */
    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D, ordinal = 1))
    public double noSlowSneakForward(double constant) {
        final NoSlow noSlow = Objects.requireNonNull(Client.moduleManager.getModule(NoSlow.class));

        return (Client.moduleManager != null
                && Client.moduleManager.getModule(NoSlow.class) != null
                && noSlow.getState()) ? noSlow.getSneakForwardMultiplier().get() : 0.3D;
    }

}
