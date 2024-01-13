package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.util.AnimationUtils;
import net.aspw.client.util.MinecraftInstance;
import net.aspw.client.util.render.RenderUtils;
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

/**
 * The type Mixin gui ingame forge.
 */
@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends MixinGuiInGame {

    /**
     * The X scale.
     */
    public float xScale = 0F;

    /**
     * Pre boolean.
     *
     * @param type the type
     * @return the boolean
     */
    @Shadow(remap = false)
    protected abstract boolean pre(ElementType type);

    /**
     * Post.
     *
     * @param type the type
     */
    @Shadow(remap = false)
    protected abstract void post(ElementType type);

    /**
     * Render player list.
     *
     * @param width  the width
     * @param height the height
     * @author As_pw
     * @reason PlayerList
     */
    @Overwrite(remap = false)
    protected void renderPlayerList(int width, int height) {
        final Minecraft mc = MinecraftInstance.mc;
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