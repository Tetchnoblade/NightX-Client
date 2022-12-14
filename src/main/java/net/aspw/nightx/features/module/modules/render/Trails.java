package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.Render3DEvent;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.utils.render.ColorUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.IntegerValue;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Trails", category = ModuleCategory.RENDER, array = false)
public class Trails extends Module {
    public final BoolValue unlimitedValue = new BoolValue("Unlimited", false);
    public final FloatValue lineWidth = new FloatValue("LineWidth", 5F, 1F, 10F);
    public final IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    public final IntegerValue colorGreenValue = new IntegerValue("G", 80, 0, 255);
    public final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    public final IntegerValue fadeSpeedValue = new IntegerValue("Fade-Speed", 1, 0, 255);
    public final BoolValue colorRainbow = new BoolValue("Rainbow", false);
    private final LinkedList<Dot> positions = new LinkedList<>();

    private double lastX, lastY, lastZ = 0;

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        final Color color = colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());

        synchronized (positions) {
            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            mc.entityRenderer.disableLightmap();
            glLineWidth(lineWidth.get());
            glBegin(GL_LINE_STRIP);

            final double renderPosX = mc.getRenderManager().viewerPosX;
            final double renderPosY = mc.getRenderManager().viewerPosY - 0.5;
            final double renderPosZ = mc.getRenderManager().viewerPosZ;

            List<Dot> removeQueue = new ArrayList<>();

            for (final Dot dot : positions) {
                if (dot.alpha > 0)
                    dot.render(color, renderPosX, renderPosY, renderPosZ, unlimitedValue.get() ? 0 : fadeSpeedValue.get());
                else removeQueue.add(dot);
            }

            for (Dot removeDot : removeQueue)
                positions.remove(removeDot);

            glColor4d(1, 1, 1, 1);
            glEnd();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        synchronized (positions) {
            if (mc.thePlayer.posX != lastX || mc.thePlayer.getEntityBoundingBox().minY != lastY || mc.thePlayer.posZ != lastZ) {
                positions.add(new Dot(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ}));
                lastX = mc.thePlayer.posX;
                lastY = mc.thePlayer.getEntityBoundingBox().minY;
                lastZ = mc.thePlayer.posZ;
            }
        }
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        synchronized (positions) {
            positions.add(new Dot(new double[]{mc.thePlayer.posX,
                    mc.thePlayer.getEntityBoundingBox().minY + (mc.thePlayer.getEyeHeight() * 0.5f),
                    mc.thePlayer.posZ}));
            positions.add(new Dot(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ}));
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        synchronized (positions) {
            positions.clear();
        }
        super.onDisable();
    }

    class Dot {
        private final double[] pos;
        public int alpha = 255;

        public Dot(double[] position) {
            this.pos = position;
        }

        public void render(Color color, double renderPosX, double renderPosY, double renderPosZ, int decreaseBy) {
            Color reColor = ColorUtils.reAlpha(color, alpha);
            RenderUtils.glColor(reColor);
            glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);
            alpha -= decreaseBy;
            if (alpha < 0) alpha = 0;
        }
    }
}