package net.aspw.client.config.configs;

import com.google.gson.*;
import net.aspw.client.Launch;
import net.aspw.client.config.FileConfig;
import net.aspw.client.config.FileManager;
import net.aspw.client.features.api.MacroManager;
import net.aspw.client.features.module.Module;
import net.aspw.client.value.Value;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 * The type Values config.
 */
public class ValuesConfig extends FileConfig {

    /**
     * Instantiates a new Values config.
     *
     * @param file the file
     */
    public ValuesConfig(final File file) {
        super(file);
    }

    @Override
    protected void loadConfig() throws IOException {
        final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(getFile())));

        if (jsonElement instanceof JsonNull)
            return;

        final JsonObject jsonObject = (JsonObject) jsonElement;

        final Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, JsonElement> entry = iterator.next();
            if (entry.getKey().equalsIgnoreCase("macros")) {
                final JsonArray jsonValue = entry.getValue().getAsJsonArray();
                for (final JsonElement macroElement : jsonValue) {
                    final JsonObject macroObject = macroElement.getAsJsonObject();
                    final JsonElement keyValue = macroObject.get("key");
                    final JsonElement commandValue = macroObject.get("command");

                    MacroManager.INSTANCE.addMacro(keyValue.getAsInt(), commandValue.getAsString());
                }
            } else {

                final Module module = Launch.moduleManager.getModule(entry.getKey());

                if (module != null) {
                    final JsonObject jsonModule = (JsonObject) entry.getValue();

                    for (final Value moduleValue : module.getValues()) {
                        final JsonElement element = jsonModule.get(moduleValue.getName());

                        if (element != null) moduleValue.fromJson(element);
                    }
                }
            }
        }
    }

    @Override
    protected void saveConfig() throws IOException {
        final JsonObject jsonObject = new JsonObject();

        final JsonArray jsonMacros = new JsonArray();
        MacroManager.INSTANCE.getMacroMapping().forEach((k, v) -> {
            final JsonObject jsonMacro = new JsonObject();
            jsonMacro.addProperty("key", k);
            jsonMacro.addProperty("command", v);
            jsonMacros.add(jsonMacro);
        });
        jsonObject.add("macros", jsonMacros);

        final JsonObject jsonFeatures = new JsonObject();

        jsonObject.add("features", jsonFeatures);

        Launch.moduleManager.getModules().stream().filter(module -> !module.getValues().isEmpty()).forEach(module -> {
            final JsonObject jsonModule = new JsonObject();
            module.getValues().forEach(value -> jsonModule.add(value.getName(), value.toJson()));
            jsonObject.add(module.getName(), jsonModule);
        });

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject));
        printWriter.close();
    }
}
