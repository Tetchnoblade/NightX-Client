package net.aspw.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.aspw.client.Client;
import net.aspw.client.config.configs.*;
import net.aspw.client.utils.ClientUtils;
import net.aspw.client.utils.MinecraftInstance;

import java.io.File;
import java.lang.reflect.Field;

public class FileManager extends MinecraftInstance {

    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    public File dir = new File(mc.mcDataDir, Client.CLIENT_FOLDER);
    public final File fontsDir = new File(dir, "fonts");
    public final File settingsDir = new File(dir, "configs");
    public final File soundsDir = new File(dir, "sounds");
    public final File themesDir = new File(dir, "themes");
    public final FileConfig modulesConfig = new ModulesConfig(new File(dir, "toggled.json"));
    public final FileConfig valuesConfig = new ValuesConfig(new File(dir, "value.json"));
    public final AccountsConfig accountsConfig = new AccountsConfig(new File(dir, "alts.json"));
    public final FriendsConfig friendsConfig = new FriendsConfig(new File(dir, "friends.json"));
    public final FileConfig xrayConfig = new XRayConfig(new File(dir, "xray.json"));
    public final FileConfig hudConfig = new HudConfig(new File(dir, "hud.json"));
    public final FileConfig shortcutsConfig = new ShortcutsConfig(new File(dir, "shortcuts.json"));

    /**
     * Constructor of file manager
     * Setup everything important
     */
    public FileManager() {
        setupFolder();
    }

    /**
     * Setup folder
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
     * Load all configs in file manager
     */
    public void loadAllConfigs() {
        for (final Field field : getClass().getDeclaredFields()) {
            if (field.getType() == FileConfig.class) {
                try {
                    if (!field.isAccessible())
                        field.setAccessible(true);

                    final FileConfig fileConfig = (FileConfig) field.get(this);
                    loadConfig(fileConfig);
                } catch (final IllegalAccessException e) {
                    ClientUtils.getLogger().error("Failed to load config file of field " + field.getName() + ".", e);
                }
            }
        }
    }

    /**
     * Load a list of configs
     *
     * @param configs list
     */
    public void loadConfigs(final FileConfig... configs) {
        for (final FileConfig fileConfig : configs)
            loadConfig(fileConfig);
    }

    /**
     * Load one config
     *
     * @param config to load
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
     * Save all configs in file manager
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
     * Save a list of configs
     *
     * @param configs list
     */
    public void saveConfigs(final FileConfig... configs) {
        for (final FileConfig fileConfig : configs)
            saveConfig(fileConfig);
    }

    /**
     * Save one config
     *
     * @param config to save
     */
    public void saveConfig(final FileConfig config) {
        saveConfig(config, false);
    }

    /**
     * Save one config
     *
     * @param config         to save
     * @param ignoreStarting check starting
     */
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