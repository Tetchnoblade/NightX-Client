package net.aspw.client.visual.font.semi;

import net.aspw.client.utils.ClientUtils;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.client.gui.FontRenderer;

import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Fonts {

    @FontDetails(fontName = "Minecraft Font")
    public static final FontRenderer minecraftFont = MinecraftInstance.mc.fontRendererObj;
    private static final List<GameFontRenderer> CUSTOM_FONT_RENDERERS = new ArrayList<>();
    @FontDetails(fontName = "Roboto Medium", fontSize = 35)
    public static GameFontRenderer font35;
    @FontDetails(fontName = "Roboto Medium", fontSize = 40)
    public static GameFontRenderer font40;
    @FontDetails(fontName = "Roboto Medium", fontSize = 72)
    public static GameFontRenderer font72;
    @FontDetails(fontName = "Roboto Medium", fontSize = 30)
    public static GameFontRenderer fontSmall;
    @FontDetails(fontName = "Roboto Medium", fontSize = 24)
    public static GameFontRenderer fontTiny;
    @FontDetails(fontName = "Roboto Medium", fontSize = 52)
    public static GameFontRenderer fontLarge;
    @FontDetails(fontName = "SFUI Regular", fontSize = 35)
    public static GameFontRenderer fontSFUI35;
    @FontDetails(fontName = "SFUI Regular", fontSize = 37)
    public static GameFontRenderer fontSFUI37;
    @FontDetails(fontName = "SFUI Regular", fontSize = 40)
    public static GameFontRenderer fontSFUI40;
    @FontDetails(fontName = "Jello Regular", fontSize = 40)
    public static GameFontRenderer jelloRegular40;
    @FontDetails(fontName = "Roboto Bold", fontSize = 180)
    public static GameFontRenderer fontBold180;
    @FontDetails(fontName = "Tahoma Bold", fontSize = 38)
    public static GameFontRenderer fontTahoma38;
    @FontDetails(fontName = "Tahoma Bold", fontSize = 35)
    public static GameFontRenderer fontTahoma;
    @FontDetails(fontName = "Tahoma Bold", fontSize = 30)
    public static GameFontRenderer fontTahoma30;
    public static TTFFontRenderer fontTahomaSmall;
    @FontDetails(fontName = "Bangers", fontSize = 45)
    public static GameFontRenderer fontBangers;
    @FontDetails(fontName = "Pixel", fontSize = 48)
    public static GameFontRenderer fontPixel;
    @FontDetails(fontName = "Mojangles", fontSize = 60)
    public static GameFontRenderer mojangles;
    @FontDetails(fontName = "Mojangles Bold", fontSize = 60)
    public static GameFontRenderer mojanglesBold;
    @FontDetails(fontName = "NiSans", fontSize = 60)
    public static GameFontRenderer niSans;
    @FontDetails(fontName = "Icons", fontSize = 52)
    public static GameFontRenderer icons;
    @FontDetails(fontName = "Marks", fontSize = 52)
    public static GameFontRenderer marks;

    public static void loadFonts() {
        long l = System.currentTimeMillis();

        ClientUtils.getLogger().info("Loading Fonts...");

        font35 = new GameFontRenderer(getRobotoMedium("Roboto-Medium.ttf", 35));
        font40 = new GameFontRenderer(getRobotoMedium("Roboto-Medium.ttf", 40));
        font72 = new GameFontRenderer(getRobotoMedium("Roboto-Medium.ttf", 72));
        fontSmall = new GameFontRenderer(getRobotoMedium("Roboto-Medium.ttf", 30));
        fontTiny = new GameFontRenderer(getRobotoMedium("Roboto-Medium.ttf", 24));
        fontLarge = new GameFontRenderer(getRobotoMedium("Roboto-Medium.ttf", 60));
        fontSFUI35 = new GameFontRenderer(getSFUI("sfui.ttf", 35));
        fontSFUI37 = new GameFontRenderer(getSFUI("sfui.ttf", 37));
        fontSFUI40 = new GameFontRenderer(getSFUI("sfui.ttf", 40));
        jelloRegular40 = new GameFontRenderer(getJelloRegular("jelloregular.ttf", 40));
        fontBold180 = new GameFontRenderer(getRobotoBold("Roboto-Bold.ttf", 180));
        fontTahoma = new GameFontRenderer(getTahomaBold("TahomaBold.ttf", 35));
        fontTahoma30 = new GameFontRenderer(getTahomaBold("TahomaBold.ttf", 30));
        fontTahoma38 = new GameFontRenderer(getTahomaBold("TahomaBold.ttf", 38));
        fontTahomaSmall = new TTFFontRenderer(getTahoma("Tahoma.ttf", 11));
        fontBangers = new GameFontRenderer(getBangersRegular("Bangers-Regular.ttf", 45));
        fontPixel = new GameFontRenderer(getPixel("Pixel.ttf", 48));
        mojangles = new GameFontRenderer(getMojangles("Mojangles.ttf", 60));
        mojanglesBold = new GameFontRenderer(getMojanglesBold("MojanglesBold.ttf", 60));
        niSans = new GameFontRenderer(getNiSans("NiSans.ttf", 60));
        icons = new GameFontRenderer(getIcons("Icons.ttf", 52));
        marks = new GameFontRenderer(getMarks("marks.ttf", 52));

        ClientUtils.getLogger().info("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }

    public static FontRenderer getFontRenderer(final String name, final int size) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if (o instanceof FontRenderer) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    if (fontDetails.fontName().equals(name) && fontDetails.fontSize() == size)
                        return (FontRenderer) o;
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (final GameFontRenderer liquidFontRenderer : CUSTOM_FONT_RENDERERS) {
            final Font font = liquidFontRenderer.getDefaultFont().getFont();

            if (font.getName().equals(name) && font.getSize() == size)
                return liquidFontRenderer;
        }

        return minecraftFont;
    }

    public static Object[] getFontDetails(final FontRenderer fontRenderer) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if (o.equals(fontRenderer)) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    return new Object[]{fontDetails.fontName(), fontDetails.fontSize()};
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (fontRenderer instanceof GameFontRenderer) {
            final Font font = ((GameFontRenderer) fontRenderer).getDefaultFont().getFont();

            return new Object[]{font.getName(), font.getSize()};
        }

        return null;
    }

    public static List<FontRenderer> getFonts() {
        final List<FontRenderer> fonts = new ArrayList<>();

        for (final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if (fontObj instanceof FontRenderer) fonts.add((FontRenderer) fontObj);
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS);

        return fonts;
    }

    private static Font getSFUI(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/sfui.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("sfui", Font.PLAIN, size);
        }
    }

    private static Font getNiSans(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/NiSans.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("NiSans", Font.PLAIN, size);
        }
    }

    private static Font getRobotoMedium(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/Roboto-Medium.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("Roboto-Medium", Font.PLAIN, size);
        }
    }

    private static Font getJelloRegular(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/jelloregular.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("jelloregular", Font.PLAIN, size);
        }
    }

    private static Font getRobotoBold(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/Roboto-Bold.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("Roboto-Bold", Font.PLAIN, size);
        }
    }

    private static Font getTahomaBold(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/TahomaBold.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("TahomaBold", Font.PLAIN, size);
        }
    }

    private static Font getTahoma(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/Tahoma.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("Tahoma", Font.PLAIN, size);
        }
    }

    private static Font getBangersRegular(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/Bangers-Regular.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("Bangers-Regular", Font.PLAIN, size);
        }
    }

    private static Font getPixel(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/Pixel.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("Pixel", Font.PLAIN, size);
        }
    }

    private static Font getMojangles(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/Mojangles.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("Mojangles", Font.PLAIN, size);
        }
    }

    private static Font getMojanglesBold(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/MojanglesBold.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("MojanglesBold", Font.PLAIN, size);
        }
    }

    private static Font getIcons(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/Icons.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("Icons", Font.PLAIN, size);
        }
    }

    private static Font getMarks(final String fontName, final int size) {
        try {
            InputStream inputStream = Fonts.class.getResourceAsStream("/assets/minecraft/client/font/marks.ttf");

            if (inputStream == null) {
                throw new FileNotFoundException("Font file not found: " + fontName);
            }

            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            return new Font("marks", Font.PLAIN, size);
        }
    }
}