package net.aspw.client.injection.forge.mixins.block;

import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(BlockRedstoneTorch.class)
public class MixinBlockRedstoneTorch {
    @Shadow
    private static final Map<World, List<BlockRedstoneTorch.Toggle>> toggles = new WeakHashMap<>();
}
