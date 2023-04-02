package net.aspw.client.config.configs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.aspw.client.Client;
import net.aspw.client.config.FileConfig;
import net.aspw.client.config.FileManager;
import net.aspw.client.features.module.impl.visual.XRay;
import net.aspw.client.utils.ClientUtils;
import net.minecraft.block.Block;

import java.io.*;

public class XRayConfig extends FileConfig {

    /**
     * Constructor of config
     *
     * @param file of config
     */
    public XRayConfig(final File file) {
        super(file);
    }

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Override
    protected void loadConfig() throws IOException {
        final XRay xRay = Client.moduleManager.getModule(XRay.class);

        if (xRay == null) {
            ClientUtils.getLogger().error("[FileManager] Failed to find xray module.");
            return;
        }

        final JsonArray jsonArray = new JsonParser().parse(new BufferedReader(new FileReader(getFile()))).getAsJsonArray();

        xRay.getXrayBlocks().clear();

        for (final JsonElement jsonElement : jsonArray) {
            try {
                final Block block = Block.getBlockFromName(jsonElement.getAsString());

                if (xRay.getXrayBlocks().contains(block)) {
                    ClientUtils.getLogger().error("[FileManager] Skipped xray block '" + block.getRegistryName() + "' because the block is already added.");
                    continue;
                }

                xRay.getXrayBlocks().add(block);
            } catch (final Throwable throwable) {
                ClientUtils.getLogger().error("[FileManager] Failed to add block to xray.", throwable);
            }
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    @Override
    protected void saveConfig() throws IOException {
        final XRay xRay = Client.moduleManager.getModule(XRay.class);

        if (xRay == null) {
            ClientUtils.getLogger().error("[FileManager] Failed to find xray module.");
            return;
        }

        final JsonArray jsonArray = new JsonArray();

        for (final Block block : xRay.getXrayBlocks())
            jsonArray.add(FileManager.PRETTY_GSON.toJsonTree(Block.getIdFromBlock(block)));

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonArray));
        printWriter.close();
    }
}
