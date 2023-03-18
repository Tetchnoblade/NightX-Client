package dev.tr7zw.entityculling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;

import java.util.Collections;
import java.util.HashSet;

public abstract class EntityCullingModBase {

    public static EntityCullingModBase instance = new EntityCullingMod();
    public OcclusionCullingInstance culling;
    public static boolean enabled = true;
    public CullTask cullTask;
    private Thread cullThread;
    protected boolean pressed = false;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public int renderedBlockEntities = 0;
    public int skippedBlockEntities = 0;
    public int renderedEntities = 0;
    public int skippedEntities = 0;

    public void onInitialize() {
        culling = new OcclusionCullingInstance(128, new Provider());
        cullTask = new CullTask(culling, new HashSet<>(Collections.singletonList("tile.beacon")));

        cullThread = new Thread(cullTask, "CullThread");
        cullThread.setUncaughtExceptionHandler((thread, ex) -> {
            System.out.println("The CullingThread has crashed! Please report the following stacktrace!");
            ex.printStackTrace();
        });
        cullThread.start();
        initModloader();
    }

    public void worldTick() {
        cullTask.requestCull = true;
    }

    public void clientTick() {
        if (pressed)
            return;
        pressed = true;
        enabled = !enabled;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (enabled) {
            if (player != null) {
                player.addChatMessage(new ChatComponentText("Culling on"));
            }
        }
        cullTask.requestCull = true;
    }

    public abstract void initModloader();

}
