package net.aspw.client.visual.font.smooth;

import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public abstract class FontLoaders {

    public static CFontRenderer SF15 = new CFontRenderer(FontLoaders.getSF(15), true, true);
    public static CFontRenderer SF16 = new CFontRenderer(FontLoaders.getSF(16), true, true);
    public static CFontRenderer SF18 = new CFontRenderer(FontLoaders.getSF(18), true, true);
    public static CFontRenderer SF20 = new CFontRenderer(FontLoaders.getSF(20), true, true);
    public static CFontRenderer SF21 = new CFontRenderer(FontLoaders.getSF(21), true, true);

    private static Font getSF(int size) {
        Font font;
        try {
            InputStream is = MinecraftInstance.mc.getResourceManager()
                    .getResource(new ResourceLocation("client/font/sfui.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }
}