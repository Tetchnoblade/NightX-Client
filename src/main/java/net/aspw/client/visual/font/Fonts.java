package net.aspw.client.visual.font;

import com.google.gson.*;
import net.aspw.client.Client;
import net.aspw.client.utils.ClientUtils;
import net.aspw.client.utils.misc.HttpUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Fonts {

    @FontDetails(fontName = "Minecraft Font")
    public static final FontRenderer minecraftFont = Minecraft.getMinecraft().fontRendererObj;
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

    public static void loadFonts() {
        long l = System.currentTimeMillis();

        ClientUtils.getLogger().info("Loading Fonts.");

        downloadFonts();

        font35 = new GameFontRenderer(getFont("Roboto-Medium.ttf", 35));
        font40 = new GameFontRenderer(getFont("Roboto-Medium.ttf", 40));
        font72 = new GameFontRenderer(getFont("Roboto-Medium.ttf", 72));
        fontSmall = new GameFontRenderer(getFont("Roboto-Medium.ttf", 30));
        fontTiny = new GameFontRenderer(getFont("Roboto-Medium.ttf", 24));
        fontLarge = new GameFontRenderer(getFont("Roboto-Medium.ttf", 60));
        fontSFUI35 = new GameFontRenderer(getFont("sfui.ttf", 35));
        fontSFUI37 = new GameFontRenderer(getFont("sfui.ttf", 37));
        fontSFUI40 = new GameFontRenderer(getFont("sfui.ttf", 40));
        jelloRegular40 = new GameFontRenderer(getFont("jelloregular.ttf", 40));
        fontBold180 = new GameFontRenderer(getFont("Roboto-Bold.ttf", 180));
        fontTahoma = new GameFontRenderer(getFont("TahomaBold.ttf", 35));
        fontTahoma30 = new GameFontRenderer(getFont("TahomaBold.ttf", 30));
        fontTahoma38 = new GameFontRenderer(getFont("TahomaBold.ttf", 38));
        fontTahomaSmall = new TTFFontRenderer(getFont("Tahoma.ttf", 11));
        fontBangers = new GameFontRenderer(getFont("Bangers-Regular.ttf", 45));
        fontPixel = new GameFontRenderer(getFont("Pixel.ttf", 48));
        mojangles = new GameFontRenderer(getFont("Mojangles.ttf", 60));
        mojanglesBold = new GameFontRenderer(getFont("MojanglesBold.ttf", 60));
        niSans = new GameFontRenderer(getFont("NiSans.ttf", 60));

        try {
            CUSTOM_FONT_RENDERERS.clear();

            final File fontsFile = new File(Client.fileManager.fontsDir, "fonts.json");

            if (fontsFile.exists()) {
                final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(fontsFile)));

                if (jsonElement instanceof JsonNull)
                    return;

                final JsonArray jsonArray = (JsonArray) jsonElement;

                for (final JsonElement element : jsonArray) {
                    if (element instanceof JsonNull)
                        return;

                    final JsonObject fontObject = (JsonObject) element;

                    CUSTOM_FONT_RENDERERS.add(new GameFontRenderer(getFont(fontObject.get("fontFile").getAsString(), fontObject.get("fontSize").getAsInt())));
                }
            } else {
                fontsFile.createNewFile();

                final PrintWriter printWriter = new PrintWriter(new FileWriter(fontsFile));
                printWriter.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonArray()));
                printWriter.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        ClientUtils.getLogger().info("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }

    private static void downloadFonts() {
        try {
            final File outputFile = new File(Client.fileManager.fontsDir, "roboto.zip");
            final File sfuiFile = new File(Client.fileManager.fontsDir, "sfui.ttf");
            final File jelloFile = new File(Client.fileManager.fontsDir, "jelloregular.ttf");
            final File prodSansFile = new File(Client.fileManager.fontsDir, "Roboto-Medium.ttf");
            final File prodBoldFile = new File(Client.fileManager.fontsDir, "Roboto-Bold.ttf");
            final File tahomaFile = new File(Client.fileManager.fontsDir, "TahomaBold.ttf");
            final File tahomaReFile = new File(Client.fileManager.fontsDir, "Tahoma.ttf");
            final File bangersFile = new File(Client.fileManager.fontsDir, "Bangers-Regular.ttf");
            final File pixelFile = new File(Client.fileManager.fontsDir, "Pixel.ttf");

            if (!outputFile.exists() || !sfuiFile.exists() || !jelloFile.exists() || !prodSansFile.exists() || !prodBoldFile.exists() || !tahomaFile.exists() || !tahomaReFile.exists() || !bangersFile.exists() || !pixelFile.exists()) {
                ClientUtils.getLogger().info("Downloading fonts...");
                HttpUtils.download(Client.CLIENT_FONTS, outputFile);
                ClientUtils.getLogger().info("Extract fonts...");
                extractZip(outputFile.getPath(), Client.fileManager.fontsDir.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static Font getFont(final String fontName, final int size) {
        try {
            final InputStream inputStream = new FileInputStream(new File(Client.fileManager.fontsDir, fontName));
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();

            return new Font("default", Font.PLAIN, size);
        }
    }

    private static void extractZip(final String zipFile, final String outputFolder) {
        final byte[] buffer = new byte[1024];

        try {
            final File folder = new File(outputFolder);

            if (!folder.exists()) folder.mkdir();

            final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(outputFolder + File.separator + zipEntry.getName());
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int i;
                while ((i = zipInputStream.read(buffer)) > 0)
                    fileOutputStream.write(buffer, 0, i);

                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}