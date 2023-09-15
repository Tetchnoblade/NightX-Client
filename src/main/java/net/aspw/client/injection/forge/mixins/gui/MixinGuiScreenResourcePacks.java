package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.util.MinecraftInstance;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The type Mixin gui screen resource packs.
 */
@Mixin(GuiScreenResourcePacks.class)
public class MixinGuiScreenResourcePacks {
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Ljava/util/Collections;reverse(Ljava/util/List;)V", remap = false))
    private void clearHandles(CallbackInfo ci) {
        ResourcePackRepository repository = MinecraftInstance.mc.getResourcePackRepository();
        for (ResourcePackRepository.Entry entry : repository.getRepositoryEntries()) {
            IResourcePack current = repository.getResourcePackInstance();
            if (current == null || !entry.getResourcePackName().equals(current.getPackName()))
                entry.closeResourcePack();
        }
    }
}
