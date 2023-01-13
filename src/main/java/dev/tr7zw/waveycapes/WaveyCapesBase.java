package dev.tr7zw.waveycapes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.tr7zw.waveycapes.config.Config;
import dev.tr7zw.waveycapes.config.ConfigUpgrader;
import dev.tr7zw.waveycapes.config.CustomConfigScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class WaveyCapesBase {

    public static WaveyCapesBase INSTANCE;
    public static final Logger LOGGER = LogManager.getLogger("Capes");
    public static Config config;
    private final File settingsFile = new File("config", "capes.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean optifinePresent = false;
    
    public void init() {
        INSTANCE = this;
        if (settingsFile.exists()) {
            try {
                config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
        } else {
            if(ConfigUpgrader.upgradeConfig(config)) {
                writeConfig();
            }
        }
        initSupportHooks();
    }
    
    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public GuiScreen createConfigScreen(GuiScreen parent) {
        return new ConfigScreen(parent);
    }
    
    public static class ConfigScreen extends CustomConfigScreen {

        public ConfigScreen(GuiScreen lastScreen) {
            super(lastScreen, "text.wc.title");
        }
        
        @Override
        public void initialize() {
            List<GuiButton> options = new ArrayList<>();
            options.add(getEnumOption("text.wc.setting.capestyle", CapeStyle.class, () -> config.capeStyle, (v) -> config.capeStyle = v));
            options.add(getEnumOption("text.wc.setting.windmode", WindMode.class, () -> config.windMode, (v) -> config.windMode = v));
            options.add(getEnumOption("text.wc.setting.capemovement", CapeMovement.class, () -> config.capeMovement, (v) -> config.capeMovement = v));

            addOptionsList(options);

        }

        @Override
        public void save() {
            INSTANCE.writeConfig();
        }
        
    }
    
    public abstract void initSupportHooks();
    
    /**
     * Checks if a class exists or not
     * @param name
     * @return
     */
    protected static boolean doesClassExist(String name) {
        try {
            if(Class.forName(name) != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {}
        return false;
    }
    
}
