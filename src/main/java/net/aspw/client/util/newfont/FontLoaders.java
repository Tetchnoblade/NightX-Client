package net.aspw.client.util.newfont;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public abstract class FontLoaders {

    // other
    public static CFontRenderer logo10 = new CFontRenderer(FontLoaders.getLogo(10), true, true);
    public static CFontRenderer logo18 = new CFontRenderer(FontLoaders.getLogo(18), true, true);
    public static CFontRenderer logo28 = new CFontRenderer(FontLoaders.getLogo(28), true, true);


    // roboto2
    public static CFontRenderer clickgui11 = new CFontRenderer(FontLoaders.getClickgui(11), true, true);
    public static CFontRenderer clickgui14 = new CFontRenderer(FontLoaders.getClickgui(14), true, true);
    public static CFontRenderer clickgui16 = new CFontRenderer(FontLoaders.getClickgui(16), true, true);
    public static CFontRenderer clickgui17 = new CFontRenderer(FontLoaders.getClickgui(17), true, true);
    public static CFontRenderer clickgui18 = new CFontRenderer(FontLoaders.getClickgui(18), true, true);
    public static CFontRenderer clickgui19 = new CFontRenderer(FontLoaders.getClickgui(19), true, true);
    public static CFontRenderer clickgui20 = new CFontRenderer(FontLoaders.getClickgui(20), true, true);
    public static CFontRenderer clickgui21 = new CFontRenderer(FontLoaders.getClickgui(21), true, true);
    public static CFontRenderer clickgui22 = new CFontRenderer(FontLoaders.getClickgui(22), true, true);
    public static CFontRenderer clickgui23 = new CFontRenderer(FontLoaders.getClickgui(23), true, true);
    public static CFontRenderer clickgui24 = new CFontRenderer(FontLoaders.getClickgui(24), true, true);

    //raleway2
    public static CFontRenderer kiona9 = new CFontRenderer(FontLoaders.getKiona(9), true, true);
    public static CFontRenderer kiona10 = new CFontRenderer(FontLoaders.getKiona(10), true, true);
    public static CFontRenderer kiona11 = new CFontRenderer(FontLoaders.getKiona(11), true, true);
    public static CFontRenderer kiona12 = new CFontRenderer(FontLoaders.getKiona(12), true, true);
    public static CFontRenderer kiona13 = new CFontRenderer(FontLoaders.getKiona(13), true, true);
    public static CFontRenderer kiona14 = new CFontRenderer(FontLoaders.getKiona(14), true, true);
    public static CFontRenderer kiona16 = new CFontRenderer(FontLoaders.getKiona(16), true, true);
    public static CFontRenderer kiona17 = new CFontRenderer(FontLoaders.getKiona(17), true, true);
    public static CFontRenderer kiona18 = new CFontRenderer(FontLoaders.getKiona(18), true, true);
    public static CFontRenderer kiona19 = new CFontRenderer(FontLoaders.getKiona(19), true, true);
    public static CFontRenderer kiona20 = new CFontRenderer(FontLoaders.getKiona(20), true, true);
    public static CFontRenderer kiona21 = new CFontRenderer(FontLoaders.getKiona(21), true, true);
    public static CFontRenderer kiona23 = new CFontRenderer(FontLoaders.getKiona(23), true, true);
    public static CFontRenderer kiona22 = new CFontRenderer(FontLoaders.getKiona(22), true, true);
    public static CFontRenderer kiona24 = new CFontRenderer(FontLoaders.getKiona(24), true, true);
    public static CFontRenderer kiona26 = new CFontRenderer(FontLoaders.getKiona(26), true, true);
    public static CFontRenderer kiona28 = new CFontRenderer(FontLoaders.getKiona(28), true, true);

    // check
    public static CFontRenderer logos16 = new CFontRenderer(FontLoaders.getLog2go(16), true, true);
    public static CFontRenderer logos35 = new CFontRenderer(FontLoaders.getLog2go(35), true, true);

    // NovICON
    public static CFontRenderer logog18 = new CFontRenderer(FontLoaders.getLoggo(18), true, true);
    public static CFontRenderer logog36 = new CFontRenderer(FontLoaders.getLoggo(36), true, true);
    public static CFontRenderer logog38 = new CFontRenderer(FontLoaders.getLoggo(38), true, true);

    // Tahoma2
    public static CFontRenderer Tahoma9= new CFontRenderer(FontLoaders.getTahoma(9), true, true);
    public static CFontRenderer Tahoma11 = new CFontRenderer(FontLoaders.getTahoma(11), true, true);
    public static CFontRenderer Tahoma13 = new CFontRenderer(FontLoaders.getTahoma(13), true, true);
    public static CFontRenderer Tahoma14 = new CFontRenderer(FontLoaders.getTahoma(14), true, true);
    public static CFontRenderer Tahoma16 = new CFontRenderer(FontLoaders.getTahoma(16), true, true);
    public static CFontRenderer Tahoma18 = new CFontRenderer(FontLoaders.getTahoma(18), true, true);
    public static CFontRenderer Tahoma17 = new CFontRenderer(FontLoaders.getTahoma(17), true, true);
    public static CFontRenderer Tahoma19 = new CFontRenderer(FontLoaders.getTahoma(19), true, true);
    public static CFontRenderer Tahoma21 = new CFontRenderer(FontLoaders.getTahoma(21), true, true);
    public static CFontRenderer Tahoma22 = new CFontRenderer(FontLoaders.getTahoma(22), true, true);
    public static CFontRenderer Tahoma24 = new CFontRenderer(FontLoaders.getTahoma(24), true, true);
    public static CFontRenderer Tahoma23 = new CFontRenderer(FontLoaders.getTahoma(23), true, true);
    public static CFontRenderer Tahoma20 = new CFontRenderer(FontLoaders.getTahoma(20), true, true);

    // Tahoma3
    public static CFontRenderer TahomaBold8 = new CFontRenderer(FontLoaders.getTahomaBold(8), true, true);
    public static CFontRenderer TahomaBold9 = new CFontRenderer(FontLoaders.getTahomaBold(9), true, true);
    public static CFontRenderer TahomaBold10 = new CFontRenderer(FontLoaders.getTahomaBold(10), true, true);
    public static CFontRenderer TahomaBold11 = new CFontRenderer(FontLoaders.getTahomaBold(11), true, true);
    public static CFontRenderer TahomaBold12 = new CFontRenderer(FontLoaders.getTahomaBold(12), true, true);
    public static CFontRenderer TahomaBold13 = new CFontRenderer(FontLoaders.getTahomaBold(13), true, true);
    public static CFontRenderer TahomaBold14 = new CFontRenderer(FontLoaders.getTahomaBold(14), true, true);
    public static CFontRenderer TahomaBold16 = new CFontRenderer(FontLoaders.getTahomaBold(16), true, true);
    public static CFontRenderer TahomaBold17 = new CFontRenderer(FontLoaders.getTahomaBold(17), true, true);
    public static CFontRenderer TahomaBold18 = new CFontRenderer(FontLoaders.getTahomaBold(18), true, true);
    public static CFontRenderer TahomaBold19 = new CFontRenderer(FontLoaders.getTahomaBold(19), true, true);
    public static CFontRenderer TahomaBold20 = new CFontRenderer(FontLoaders.getTahomaBold(20), true, true);
    public static CFontRenderer TahomaBold21 = new CFontRenderer(FontLoaders.getTahomaBold(21), true, true);
    public static CFontRenderer TahomaBold22 = new CFontRenderer(FontLoaders.getTahomaBold(22), true, true);
    public static CFontRenderer TahomaBold23 = new CFontRenderer(FontLoaders.getTahomaBold(23), true, true);
    public static CFontRenderer TahomaBold24 = new CFontRenderer(FontLoaders.getTahomaBold(24), true, true);

    // Exh
    public static CFontRenderer icon35 = new CFontRenderer(FontLoaders.getExhibition(35), true, true);


    public static CFontRenderer SF16 = new CFontRenderer(FontLoaders.getSF(16), true, true);
    public static CFontRenderer SF17 = new CFontRenderer(FontLoaders.getSF(17), true, true);
    public static CFontRenderer SF18 = new CFontRenderer(FontLoaders.getSF(18), true, true);
    public static CFontRenderer SF19 = new CFontRenderer(FontLoaders.getSF(19), true, true);
    public static CFontRenderer SF20 = new CFontRenderer(FontLoaders.getSF(20), true, true);
    public static CFontRenderer SF21 = new CFontRenderer(FontLoaders.getSF(21), true, true);
    public static CFontRenderer SF22 = new CFontRenderer(FontLoaders.getSF(22), true, true);
    public static CFontRenderer SF23 = new CFontRenderer(FontLoaders.getSF(23), true, true);
    public static CFontRenderer SF24 = new CFontRenderer(FontLoaders.getSF(24), true, true);

    private static Font getSF(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
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

    private static Font getExhibition(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("client/font/Icons.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }


    private static Font getTahoma(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("client/font/Tahoma.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getTahomaBold(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("client/font/TahomaBold.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getLog2go(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("client/font/marks.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getLoggo(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("client/font/novicon.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getLogo(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("client/font/other.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getClickgui(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("client/font/latobold.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    private static Font getKiona(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("client/font/raleway.ttf")).getInputStream();
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