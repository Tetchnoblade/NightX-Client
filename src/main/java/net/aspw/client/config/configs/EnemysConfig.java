package net.aspw.client.config.configs;

import com.google.gson.*;
import net.aspw.client.config.FileConfig;
import net.aspw.client.config.FileManager;
import net.aspw.client.utils.ClientUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EnemysConfig extends FileConfig {

    private final List<Enemy> enemys = new ArrayList<>();

    /**
     * Constructor of config
     *
     * @param file of config
     */
    public EnemysConfig(final File file) {
        super(file);
    }

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Override
    protected void loadConfig() throws IOException {
        clearEnemys();
        try {
            final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(getFile())));

            if (jsonElement instanceof JsonNull)
                return;

            for (final JsonElement enemyElement : jsonElement.getAsJsonArray()) {
                JsonObject enemyObject = enemyElement.getAsJsonObject();
                addEnemy(enemyObject.get("playerName").getAsString(), enemyObject.get("alias").getAsString());
            }

        } catch (JsonSyntaxException | IllegalStateException ex) {
            //When the JSON Parse fail, the client try to load and update the old config
            ClientUtils.getLogger().info("[FileManager] Try to load old Enemys config...");

            final BufferedReader bufferedReader = new BufferedReader(new FileReader(getFile()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.contains("{") && !line.contains("}")) {
                    line = line.replace(" ", "").replace("\"", "").replace(",", "");

                    if (line.contains(":")) {
                        String[] data = line.split(":");
                        addEnemy(data[0], data[1]);
                    } else
                        addEnemy(line);
                }
            }
            bufferedReader.close();
            ClientUtils.getLogger().info("[FileManager] Loaded old Enemys config...");

            //Save the enemys into a new valid JSON file
            saveConfig();
            ClientUtils.getLogger().info("[FileManager] Saved Enemys to new config...");
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    @Override
    protected void saveConfig() throws IOException {
        final JsonArray jsonArray = new JsonArray();

        for (final Enemy enemy : getEnemys()) {
            JsonObject enemyObject = new JsonObject();
            enemyObject.addProperty("playerName", enemy.getPlayerName());
            enemyObject.addProperty("alias", enemy.getAlias());
            jsonArray.add(enemyObject);
        }

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonArray));
        printWriter.close();
    }

    /**
     * Add enemy to config
     *
     * @param playerName of enemy
     * @return of successfully added enemy
     */
    public boolean addEnemy(final String playerName) {
        return addEnemy(playerName, playerName);
    }

    /**
     * Add enemy to config
     *
     * @param playerName of enemy
     * @param alias      of enemy
     * @return of successfully added enemy
     */
    public boolean addEnemy(final String playerName, final String alias) {
        if (isEnemy(playerName))
            return false;

        enemys.add(new Enemy(playerName, alias));
        return true;
    }

    /**
     * Remove enemy from config
     *
     * @param playerName of enemy
     */
    public boolean removeEnemy(final String playerName) {
        if (!isEnemy(playerName))
            return false;

        enemys.removeIf(enemy -> enemy.getPlayerName().equals(playerName));
        return true;
    }

    /**
     * Check is enemy
     *
     * @param playerName of enemy
     * @return is enemy
     */
    public boolean isEnemy(final String playerName) {
        for (final Enemy enemy : enemys)
            if (enemy.getPlayerName().equals(playerName))
                return true;
        return false;
    }

    /**
     * Clear all enemys from config
     */
    public void clearEnemys() {
        enemys.clear();
    }

    /**
     * Get enemys
     *
     * @return list of enemys
     */
    public List<Enemy> getEnemys() {
        return enemys;
    }

    public class Enemy {

        private final String playerName;
        private final String alias;

        /**
         * @param playerName of enemy
         * @param alias      of enemy
         */
        Enemy(final String playerName, final String alias) {
            this.playerName = playerName;
            this.alias = alias;
        }

        /**
         * @return name of enemy
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * @return alias of enemy
         */
        public String getAlias() {
            return alias;
        }
    }
}
