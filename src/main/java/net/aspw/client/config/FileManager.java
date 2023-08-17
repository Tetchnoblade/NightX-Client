package net.aspw.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.aspw.client.Client;
import net.aspw.client.config.configs.*;
import net.aspw.client.util.ClientUtils;
import net.aspw.client.util.MinecraftInstance;

import java.io.File;
import java.lang.reflect.Field;

/**
 * The type File manager.
 */
public class FileManager extends MinecraftInstance {

    /**
     * The constant PRETTY_GSON.
     */
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    /**
     * The Dir.
     */
    public File dir = new File(mc.mcDataDir, Client.CLIENT_FOLDER);
    /**
     * The Fonts dir.
     */
    public final File fontsDir = new File(dir, "fonts");
    /**
     * The Settings dir.
     */
    public final File settingsDir = new File(dir, "configs");
    /**
     * The Sounds dir.
     */
    public final File soundsDir = new File(dir, "sounds");
    /**
     * The Themes dir.
     */
    public final File themesDir = new File(dir, "themes");
    /**
     * The Modules config.
     */
    public final FileConfig modulesConfig = new ModulesConfig(new File(dir, "toggled.json"));
    /**
     * The Values config.
     */
    public final FileConfig valuesConfig = new ValuesConfig(new File(dir, "value.json"));
    /**
     * The Accounts config.
     */
    public final AccountsConfig accountsConfig = new AccountsConfig(new File(dir, "alts.json"));
    /**
     * The Friends config.
     */
    public final FriendsConfig friendsConfig = new FriendsConfig(new File(dir, "friends.json"));
    /**
     * The Hud config.
     */
    public final FileConfig hudConfig = new HudConfig(new File(dir, "hud.json"));

    /**
     * Instantiates a new File manager.
     */
    public FileManager() {
        setupFolder();
    }

    /**
     * Sets folder.
     */
    public void setupFolder() {
        if (!dir.exists())
            dir.mkdir();

        if (!fontsDir.exists())
            fontsDir.mkdir();

        if (!settingsDir.exists())
            settingsDir.mkdir();

        if (!soundsDir.exists())
            soundsDir.mkdir();

        if (!themesDir.exists())
            themesDir.mkdir();
    }

    /**
     * Load configs.
     *
     * @param configs the configs
     */
    public void loadConfigs(final FileConfig... configs) {
        for (final FileConfig fileConfig : configs)
            loadConfig(fileConfig);
    }

    /**
     * Load config.
     *
     * @param config the config
     */
    public void loadConfig(final FileConfig config) {
        if (!config.hasConfig()) {
            ClientUtils.getLogger().info("[FileManager] Skipped loading config: " + config.getFile().getName() + ".");

            saveConfig(config, true);
            return;
        }

        try {
            config.loadConfig();
            ClientUtils.getLogger().info("[FileManager] Loaded config: " + config.getFile().getName() + ".");
        } catch (final Throwable t) {
            ClientUtils.getLogger().error("[FileManager] Failed to load config file: " + config.getFile().getName() + ".", t);
        }
    }

    /**
     * Save all configs.
     */
    public void saveAllConfigs() {
        for (final Field field : getClass().getDeclaredFields()) {
            if (field.getType() == FileConfig.class) {
                try {
                    if (!field.isAccessible())
                        field.setAccessible(true);

                    final FileConfig fileConfig = (FileConfig) field.get(this);
                    saveConfig(fileConfig);
                } catch (final IllegalAccessException e) {
                    ClientUtils.getLogger().error("[FileManager] Failed to save config file of field " +
                            field.getName() + ".", e);
                }
            }
        }
    }

    /**
     * Save config.
     *
     * @param config the config
     */
    public void saveConfig(final FileConfig config) {
        saveConfig(config, false);
    }

    private void saveConfig(final FileConfig config, final boolean ignoreStarting) {
        if (!ignoreStarting && Client.INSTANCE.isStarting())
            return;

        try {
            if (!config.hasConfig())
                config.createConfig();

            config.saveConfig();
            ClientUtils.getLogger().info("[FileManager] Saved config: " + config.getFile().getName() + ".");
        } catch (final Throwable t) {
            ClientUtils.getLogger().error("[FileManager] Failed to save config file: " +
                    config.getFile().getName() + ".", t);
        }
    }
}