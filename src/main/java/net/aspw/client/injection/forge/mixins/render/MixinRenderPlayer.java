package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.other.PlayerEdit;
import net.aspw.client.features.module.impl.visual.CustomModel;
import net.aspw.client.util.MinecraftInstance;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * The type Mixin render player.
 */
@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    private final ResourceLocation rabbit = new ResourceLocation("client/models/rabbit.png");
    private final ResourceLocation fred = new ResourceLocation("client/models/freddy.png");
    private final ResourceLocation amongus = new ResourceLocation("client/models/amongus.png");

    /**
     * Render living at.
     *
     * @param entityLivingBaseIn the entity living base in
     * @param x                  the x
     * @param y                  the y
     * @param z                  the z
     * @param callbackInfo       the callback info
     */
    @Inject(method = "renderLivingAt", at = @At("HEAD"))
    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z, CallbackInfo callbackInfo) {
        final PlayerEdit playerEdit = Objects.requireNonNull(Client.moduleManager.getModule(PlayerEdit.class));

        if (playerEdit.getState() & entityLivingBaseIn.equals(MinecraftInstance.mc.thePlayer) && PlayerEdit.editPlayerSizeValue.get()) {
            GlStateManager.scale(PlayerEdit.playerSizeValue.get(), PlayerEdit.playerSizeValue.get(), PlayerEdit.playerSizeValue.get());
        }
    }

    /**
     * Gets entity texture.
     *
     * @param entity the entity
     * @param ci     the ci
     */
    @Inject(method = {"getEntityTexture"}, at = {@At("HEAD")}, cancellable = true)
    public void getEntityTexture(AbstractClientPlayer entity, CallbackInfoReturnable<ResourceLocation> ci) {
        final CustomModel customModel = Objects.requireNonNull(Client.moduleManager.getModule(CustomModel.class));

        if (customModel.getState() && (!customModel.getOnlySelf().get() || entity == MinecraftInstance.mc.thePlayer)) {
            if (customModel.getMode().get().contains("Rabbit")) {
                ci.setReturnValue(rabbit);
            }
            if (customModel.getMode().get().contains("Freddy")) {
                ci.setReturnValue(fred);
            }
            if (customModel.getMode().get().contains("Amongus")) {
                ci.setReturnValue(amongus);
            }
        }
    }
}