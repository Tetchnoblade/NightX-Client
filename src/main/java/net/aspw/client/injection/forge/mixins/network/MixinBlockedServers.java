package net.aspw.client.injection.forge.mixins.network;

import com.mojang.patchy.BlockedServers;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockedServers.class)
public abstract class MixinBlockedServers {
    /**
     * @author As_pw
     * @reason PATCH
     */
    @Overwrite
    public static boolean isBlockedServer(String server) {
        return Minecraft.getMinecraft().isIntegratedServerRunning();
    }
}