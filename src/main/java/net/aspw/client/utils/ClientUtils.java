package net.aspw.client.utils;

import com.google.gson.JsonObject;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The type Client utils.
 */
public final class ClientUtils extends MinecraftInstance {

    private static final Logger logger = LogManager.getLogger("Client");

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Display chat message.
     *
     * @param message the message
     */
    public static void displayChatMessage(final String message) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);

        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }
}