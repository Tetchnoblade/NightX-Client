package net.aspw.client.config.configs;

import com.google.gson.*;
import me.liuli.elixir.account.CrackedAccount;
import me.liuli.elixir.account.MinecraftAccount;
import me.liuli.elixir.account.MojangAccount;
import me.liuli.elixir.manage.AccountSerializer;
import net.aspw.client.config.FileConfig;
import net.aspw.client.config.FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Accounts config.
 */
public class AccountsConfig extends FileConfig {
    private final List<MinecraftAccount> accounts = new ArrayList<>();

    /**
     * Instantiates a new Accounts config.
     *
     * @param file the file
     */
    public AccountsConfig(final File file) {
        super(file);
    }

    @Override
    protected void loadConfig() throws IOException {
        clearAccounts();

        final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(getFile())));

        if (jsonElement instanceof JsonNull)
            return;

        for (final JsonElement accountElement : jsonElement.getAsJsonArray()) {
            final JsonObject accountObject = accountElement.getAsJsonObject();

            try {
                // Import Elixir account format

                accounts.add(AccountSerializer.INSTANCE.fromJson(accountElement.getAsJsonObject()));
            } catch (JsonSyntaxException | IllegalStateException e) {
                // Import old account format

                JsonElement name = accountObject.get("name");
                JsonElement password = accountObject.get("password");
                JsonElement inGameName = accountObject.get("inGameName");

                if (inGameName.isJsonNull() && password.isJsonNull()) {
                    final MojangAccount mojangAccount = new MojangAccount();

                    mojangAccount.setEmail(name.getAsString());
                    mojangAccount.setName(inGameName.getAsString());
                    mojangAccount.setPassword(password.getAsString());

                    accounts.add(mojangAccount);
                } else {
                    final CrackedAccount crackedAccount = new CrackedAccount();

                    crackedAccount.setName(name.getAsString());

                    accounts.add(crackedAccount);
                }
            }
        }
    }

    @Override
    protected void saveConfig() throws IOException {
        final JsonArray jsonArray = new JsonArray();

        for (final MinecraftAccount minecraftAccount : accounts) {
            jsonArray.add(AccountSerializer.INSTANCE.toJson(minecraftAccount));
        }

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonArray));
        printWriter.close();
    }

    /**
     * Add cracked account.
     *
     * @param name the name
     */
    public void addCrackedAccount(final String name) {
        final CrackedAccount crackedAccount = new CrackedAccount();
        crackedAccount.setName(name);

        if (accountExists(crackedAccount))
            return;

        accounts.add(crackedAccount);
    }

    /**
     * Add mojang account.
     *
     * @param name     the name
     * @param password the password
     */
    public void addMojangAccount(final String name, final String password) {
        final MojangAccount mojangAccount = new MojangAccount();
        mojangAccount.setName(name);
        mojangAccount.setPassword(password);

        if (accountExists(mojangAccount))
            return;

        accounts.add(mojangAccount);
    }

    /**
     * Add account.
     *
     * @param account the account
     */
    public void addAccount(final MinecraftAccount account) {
        accounts.add(account);
    }

    /**
     * Remove account.
     *
     * @param selectedSlot the selected slot
     */
    public void removeAccount(final int selectedSlot) {
        accounts.remove(selectedSlot);
    }


    /**
     * Remove account.
     *
     * @param account the account
     */
    public void removeAccount(MinecraftAccount account) {
        accounts.remove(account);
    }

    /**
     * Account exists boolean.
     *
     * @param newAccount the new account
     * @return the boolean
     */
    public boolean accountExists(final MinecraftAccount newAccount) {
        for (final MinecraftAccount minecraftAccount : accounts)
            if (minecraftAccount.getClass().getName().equals(newAccount.getClass().getName()) && minecraftAccount.getName().equals(newAccount.getName()))
                return true;
        return false;
    }

    /**
     * Clear accounts.
     */
    public void clearAccounts() {
        accounts.clear();
    }

    /**
     * Gets accounts.
     *
     * @return the accounts
     */
    public List<MinecraftAccount> getAccounts() {
        return accounts;
    }
}
    