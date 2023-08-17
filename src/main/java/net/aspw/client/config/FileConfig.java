package net.aspw.client.config;

import java.io.File;
import java.io.IOException;

/**
 * The type File config.
 */
public abstract class FileConfig {

    private final File file;

    /**
     * Instantiates a new File config.
     *
     * @param file the file
     */
    public FileConfig(final File file) {
        this.file = file;
    }

    /**
     * Load config.
     *
     * @throws IOException the io exception
     */
    protected abstract void loadConfig() throws IOException;

    /**
     * Save config.
     *
     * @throws IOException the io exception
     */
    protected abstract void saveConfig() throws IOException;

    /**
     * Create config.
     *
     * @throws IOException the io exception
     */
    public void createConfig() throws IOException {
        file.createNewFile();
    }

    /**
     * Has config boolean.
     *
     * @return the boolean
     */
    public boolean hasConfig() {
        return file.exists();
    }

    /**
     * Gets file.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }
}
