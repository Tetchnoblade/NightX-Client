package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.Render2DEvent;
import net.aspw.nightx.event.Render3DEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.utils.ClientUtils;
import net.aspw.nightx.utils.render.ColorUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.utils.render.shader.shaders.OutlineShader;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;

import java.awt.*;

@ModuleInfo(name = "ItemESP", spacedName = "Item ESP", category = ModuleCategory.RENDER)
public class ItemESP extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Box", "ShaderOutline"}, "Box");
    private final IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 255, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private final BoolValue colorRainbow = new BoolValue("Rainbow", false);

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (modeValue.get().equalsIgnoreCase("Box")) {
            final Color color = colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());

            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (!(entity instanceof EntityItem || entity instanceof EntityArrow))
                    continue;

                RenderUtils.drawEntityBox(entity, color, true);
            }
        }
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        if (modeValue.get().equalsIgnoreCase("ShaderOutline")) {
            OutlineShader.OUTLINE_SHADER.startDraw(event.getPartialTicks());

            try {
                for (final Entity entity : mc.theWorld.loadedEntityList) {
                    if (!(entity instanceof EntityItem || entity instanceof EntityArrow))
                        continue;

                    mc.getRenderManager().renderEntityStatic(entity, event.getPartialTicks(), true);
                }
            } catch (final Exception ex) {
                ClientUtils.getLogger().error("An error occurred while rendering all item entities for shader esp", ex);
            }

            OutlineShader.OUTLINE_SHADER.stopDraw(colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 1F, 1F);
        }
    }
}
