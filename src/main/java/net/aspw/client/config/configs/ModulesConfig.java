package net.aspw.client.config.configs;

import com.google.gson.*;
import net.aspw.client.Client;
import net.aspw.client.config.FileConfig;
import net.aspw.client.config.FileManager;
import net.aspw.client.features.module.Module;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class ModulesConfig extends FileConfig {

    /**
     * Constructor of config
     *
     * @param file of config
     */
    public ModulesConfig(final File file) {
        super(file);
    }

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Override
    protected void loadConfig() throws IOException {
        final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(getFile())));

        if (jsonElement instanceof JsonNull)
            return;

        final Iterator<Map.Entry<String, JsonElement>> entryIterator = jsonElement.getAsJsonObject().entrySet().iterator();
        while (entryIterator.hasNext()) {
            final Map.Entry<String, JsonElement> entry = entryIterator.next();
            final Module module = Client.moduleManager.getModule(entry.getKey());

            if (module != null) {
                final JsonObject jsonModule = (JsonObject) entry.getValue();

                module.setState(jsonModule.get("State").getAsBoolean());
                module.setKeyBind(jsonModule.get("KeyBind").getAsInt());

                if (jsonModule.has("Array"))
                    module.setArray(jsonModule.get("Array").getAsBoolean());
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
        final JsonObject jsonObject = new JsonObject();

        for (final Module module : Client.moduleManager.getModules()) {
            final JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("State", module.getState());
            jsonMod.addProperty("KeyBind", module.getKeyBind());
            jsonMod.addProperty("Array", module.getArray());
            final JsonArray jsonAD = new JsonArray();
            jsonObject.add(module.getName(), jsonMod);
        }

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject));
        printWriter.close();
    }
}
