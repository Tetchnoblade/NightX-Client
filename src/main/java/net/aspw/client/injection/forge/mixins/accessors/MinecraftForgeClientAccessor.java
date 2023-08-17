package net.aspw.client.injection.forge.mixins.accessors;

import com.google.common.cache.LoadingCache;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The interface Minecraft forge client accessor.
 */
@Mixin(MinecraftForgeClient.class)
public interface MinecraftForgeClientAccessor {
    /**
     * Gets region cache.
     *
     * @return the region cache
     */
    @SuppressWarnings("UnstableApiUsage")
    @Accessor(remap = false)
    static LoadingCache<Pair<World, BlockPos>, RegionRenderCache> getRegionCache() {
        throw new AssertionError();
    }
}
