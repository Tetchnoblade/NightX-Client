package de.enzaxd.viaforge.loader;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import de.enzaxd.viaforge.ViaForge;

import java.io.File;
import java.util.logging.Logger;

public class BackwardsLoader implements ViaBackwardsPlatform {
    private final File file;

    public BackwardsLoader(final File file) {
        this.init(this.file = new File(file, "ViaBackwards"));
    }

    @Override
    public Logger getLogger() {
        return ViaForge.getInstance().getjLogger();
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isOutdated() {
        return false;
    }

    @Override
    public File getDataFolder() {
        return new File(this.file, "config.yml");
    }
}
