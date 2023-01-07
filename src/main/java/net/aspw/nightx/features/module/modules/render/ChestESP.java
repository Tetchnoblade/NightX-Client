package net.aspw.nightx.features.module.modules.render;

import co.uk.hexeption.utils.OutlineUtils;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.Render2DEvent;
import net.aspw.nightx.event.Render3DEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.features.module.modules.world.StealAura;
import net.aspw.nightx.utils.ClientUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.utils.render.shader.FramebufferShader;
import net.aspw.nightx.utils.render.shader.shaders.GlowShader;
import net.aspw.nightx.utils.render.shader.shaders.OutlineShader;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.ListValue;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.tileentity.*;

import java.awt.*;

import com.sun.jdi.IntegerValue;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "ChestESP", spacedName = "Chest ESP", category = ModuleCategory.RENDER, array = false)
public class ChestESP extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Box", "OtherBox", "Outline", "ShaderOutline", "ShaderGlow", "2D", "WireFrame"}, "OtherBox");

    private final BoolValue chestValue = new BoolValue("Chest", true);
    private final BoolValue enderChestValue = new BoolValue("EnderChest", true);
    private final BoolValue furnaceValue = new BoolValue("Furnace", true);
    private final BoolValue dispenserValue = new BoolValue("Dispenser", true);
    private final BoolValue hopperValue = new BoolValue("Hopper", true);

    private IntegerValue R = new IntegerValue("R", 255, 0, 255);
    private IntegerValue G = new IntegerValue("G", 255, 0, 255);
    private IntegerValue B = new IntegerValue("B", 255, 0, 255);

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        try {
            final String mode = modeValue.get();

            if (mode.equalsIgnoreCase("outline")) {
                ClientUtils.disableFastRender();
                OutlineUtils.checkSetupFBO();
            }

            float gamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 100000.0F;

            for (final TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
                Color color = null;

                if (chestValue.get() && tileEntity instanceof TileEntityChest && !StealAura.INSTANCE.getClickedBlocks().contains(tileEntity.getPos()))
                    color = new Color(R.get(), G.get(), B.get());

                if (enderChestValue.get() && tileEntity instanceof TileEntityEnderChest && !StealAura.INSTANCE.getClickedBlocks().contains(tileEntity.getPos()))
                    color = Color.MAGENTA;

                if (furnaceValue.get() && tileEntity instanceof TileEntityFurnace)
                    color = Color.BLACK;

                if (dispenserValue.get() && tileEntity instanceof TileEntityDispenser)
                    color = Color.BLACK;

                if (hopperValue.get() && tileEntity instanceof TileEntityHopper)
                    color = Color.GRAY;

                if (color == null)
                    continue;

                if (!(tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest)) {
                    RenderUtils.drawBlockBox(tileEntity.getPos(), color, !mode.equalsIgnoreCase("otherbox"));
                    continue;
                }

                switch (mode.toLowerCase()) {
                    case "otherbox":
                    case "box":
                        RenderUtils.drawBlockBox(tileEntity.getPos(), color, !mode.equalsIgnoreCase("otherbox"));
                        break;
                    case "2d":
                        RenderUtils.draw2D(tileEntity.getPos(), color.getRGB(), Color.BLACK.getRGB());
                        break;
                    case "outline":
                        RenderUtils.glColor(color);
                        OutlineUtils.renderOne(3F);
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
                        OutlineUtils.renderTwo();
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
                        OutlineUtils.renderThree();
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
                        OutlineUtils.renderFour(color);
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
                        OutlineUtils.renderFive();

                        OutlineUtils.setColor(Color.WHITE);
                        break;
                    case "wireframe":
                        glPushMatrix();
                        glPushAttrib(GL_ALL_ATTRIB_BITS);
                        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                        glDisable(GL_TEXTURE_2D);
                        glDisable(GL_LIGHTING);
                        glDisable(GL_DEPTH_TEST);
                        glEnable(GL_LINE_SMOOTH);
                        glEnable(GL_BLEND);
                        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
                        RenderUtils.glColor(color);
                        glLineWidth(1.5F);
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
                        glPopAttrib();
                        glPopMatrix();
                        break;
                }
            }

            for (final Entity entity : mc.theWorld.loadedEntityList)
                if (entity instanceof EntityMinecartChest) {
                    switch (mode.toLowerCase()) {
                        case "otherbox":
                        case "box":
                            RenderUtils.drawEntityBox(entity, new Color(R.get(), G.get(), B.get()), !mode.equalsIgnoreCase("otherbox"));
                            break;
                        case "2d":
                            RenderUtils.draw2D(entity.getPosition(), new Color(R.get(), G.get(), B.get()).getRGB(), Color.BLACK.getRGB());
                            break;
                        case "outline": {
                            final boolean entityShadow = mc.gameSettings.entityShadows;
                            mc.gameSettings.entityShadows = false;

                            RenderUtils.glColor(new Color(R.get(), G.get(), 255B.get());
                            OutlineUtils.renderOne(3F);
                            mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                            OutlineUtils.renderTwo();
                            mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                            OutlineUtils.renderThree();
                            mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                            OutlineUtils.renderFour(new Color(R.get(), G.get(), 255B.get());
                            mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                            OutlineUtils.renderFive();

                            OutlineUtils.setColor(Color.WHITE);

                            mc.gameSettings.entityShadows = entityShadow;
                            break;
                        }
                        case "wireframe": {
                            final boolean entityShadow = mc.gameSettings.entityShadows;
                            mc.gameSettings.entityShadows = false;

                            glPushMatrix();
                            glPushAttrib(GL_ALL_ATTRIB_BITS);
                            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                            glDisable(GL_TEXTURE_2D);
                            glDisable(GL_LIGHTING);
                            glDisable(GL_DEPTH_TEST);
                            glEnable(GL_LINE_SMOOTH);
                            glEnable(GL_BLEND);
                            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                            RenderUtils.glColor(new Color(R.get(), G.get(), 255B.get());
                            mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                            RenderUtils.glColor(new Color(R.get(), G.get(), 255B.get());
                            glLineWidth(1.5F);
                            mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                            glPopAttrib();
                            glPopMatrix();

                            mc.gameSettings.entityShadows = entityShadow;
                            break;
                        }
                    }
                }

            RenderUtils.glColor(new Color(255, 255, 255, 255));
            mc.gameSettings.gammaSetting = gamma;
        } catch (Exception ignored) {
        }
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        final String mode = modeValue.get();

        final FramebufferShader shader = mode.equalsIgnoreCase("shaderoutline")
                ? OutlineShader.OUTLINE_SHADER : mode.equalsIgnoreCase("shaderglow")
                ? GlowShader.GLOW_SHADER : null;

        if (shader == null) return;

        shader.startDraw(event.getPartialTicks());

        try {
            final RenderManager renderManager = mc.getRenderManager();

            for (final TileEntity entity : mc.theWorld.loadedTileEntityList) {
                if (!(entity instanceof TileEntityChest))
                    continue;
                if (StealAura.INSTANCE.getClickedBlocks().contains(entity.getPos()))
                    continue;

                TileEntityRendererDispatcher.instance.renderTileEntityAt(
                        entity,
                        entity.getPos().getX() - renderManager.renderPosX,
                        entity.getPos().getY() - renderManager.renderPosY,
                        entity.getPos().getZ() - renderManager.renderPosZ,
                        event.getPartialTicks()
                );
            }

            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (!(entity instanceof EntityMinecartChest))
                    continue;

                renderManager.renderEntityStatic(entity, event.getPartialTicks(), true);
            }
        } catch (final Exception ex) {
            ClientUtils.getLogger().error("An error occurred while rendering all storages for shader esp", ex);
        }

        shader.stopDraw(new Color(R.get(), G.get(), B.get()), mode.equalsIgnoreCase("shaderglow") ? 2.5F : 1.5F, 1F);
    }
}
