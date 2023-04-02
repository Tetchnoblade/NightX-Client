package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.utils.AnimationUtils;
import net.aspw.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.PLAYER_LIST;

@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends MixinGuiInGame {

    public float xScale = 0F;

    @Shadow(remap = false)
    abstract boolean pre(ElementType type);

    @Shadow(remap = false)
    abstract void post(ElementType type);

    @Inject(
            method = "renderChat",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", ordinal = 0, remap = false)),
            at = @At(value = "RETURN", ordinal = 0),
            remap = false
    )
    private void fixProfilerSectionNotEnding(int width, int height, CallbackInfo ci) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.mcProfiler.getNameOfLastSection().endsWith("chat"))
            mc.mcProfiler.endSection();
    }

    @Inject(method = "renderExperience", at = @At("HEAD"), remap = false)
    private void enableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.enableAlpha();
    }

    @Inject(method = "renderExperience", at = @At("RETURN"), remap = false)
    private void disableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.disableAlpha();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected void renderPlayerList(int width, int height) {
        final Minecraft mc = Minecraft.getMinecraft();
        ScoreObjective scoreobjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = mc.thePlayer.sendQueue;

        if (!mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null) {
            xScale = AnimationUtils.animate((mc.gameSettings.keyBindPlayerList.isKeyDown() ? 100F : 0F), xScale, Animations.tabAnimations.get().equalsIgnoreCase("none") ? 1F : 0.0125F * RenderUtils.deltaTime);
            float rescaled = xScale / 100F;
            boolean displayable = rescaled > 0F;
            this.overlayPlayerList.updatePlayerList(displayable);
            if (!displayable || pre(PLAYER_LIST)) return;
            GlStateManager.pushMatrix();
            switch (Animations.tabAnimations.get().toLowerCase()) {
                case "zoom":
                    GlStateManager.translate(width / 2F * (1F - rescaled), 0F, 0F);
                    GlStateManager.scale(rescaled, rescaled, rescaled);
                    break;
                case "slide":
                    GlStateManager.scale(1F, rescaled, 1F);
                    break;
                case "none":
                    break;
            }
            this.overlayPlayerList.renderPlayerlist(width, mc.theWorld.getScoreboard(), scoreobjective);
            GlStateManager.popMatrix();
            post(PLAYER_LIST);
        } else {
            this.overlayPlayerList.updatePlayerList(false);
        }
    }
}