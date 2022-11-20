package net.aspw.nightx.features.module.modules.misc;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.MotionEvent;
import net.aspw.nightx.event.PacketEvent;
import net.aspw.nightx.event.Render2DEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.ui.font.Fonts;
import net.aspw.nightx.utils.AnimationUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.utils.render.Stencil;
import net.aspw.nightx.utils.timer.MSTimer;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;
import net.aspw.nightx.value.TextValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

@ModuleInfo(name = "AutoHypixel", spacedName = "Auto Hypixel", category = ModuleCategory.MISC)
public class AutoHypixel extends Module {

    public static String gameMode = "NONE";
    private final IntegerValue delayValue = new IntegerValue("Delay", 3000, 0, 5000, "ms");
    private final BoolValue autoGGValue = new BoolValue("Auto-GG", true);
    private final TextValue ggMessageValue = new TextValue("GG-Message", "GG", () -> autoGGValue.get());
    private final BoolValue checkValue = new BoolValue("CheckGameMode", false);
    private final BoolValue antiSnipeValue = new BoolValue("AntiSnipe", false);
    private final BoolValue renderValue = new BoolValue("Render", true);
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Solo", "Teams", "Ranked", "Mega"}, "Solo");
    private final ListValue soloTeamsValue = new ListValue("Solo/Teams-Mode", new String[]{"Normal", "Insane"}, "Insane", () -> modeValue.get().equalsIgnoreCase("solo") || modeValue.get().equalsIgnoreCase("teams"));
    private final ListValue megaValue = new ListValue("Mega-Mode", new String[]{"Normal", "Doubles"}, "Normal", () -> modeValue.get().equalsIgnoreCase("mega"));
    private final MSTimer timer = new MSTimer();
    private final DecimalFormat dFormat = new DecimalFormat("0.0");
    private final String[] strings = new String[]{
            "1st Killer -",
            "1st Place -",
            "died! Want to play again? Click here!",
            "won! Want to play again? Click here!",
            "- Damage Dealt -",
            "1st -",
            "Winning Team -",
            "Winners:",
            "Winner:",
            "Winning Team:",
            " win the game!",
            "1st Place:",
            "Last team standing!",
            "Winner #1 (",
            "Top Survivors",
            "Winners -"};
    public boolean shouldChangeGame, useOtherWord = false;
    private float posY = -20F;

    @Override
    public void onEnable() {
        shouldChangeGame = false;
        timer.reset();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (checkValue.get() && !gameMode.toLowerCase().contains("skywars"))
            return;

        ScaledResolution sc = new ScaledResolution(mc);
        float middleX = sc.getScaledWidth() / 2F;
        String detail = "Next game in " + dFormat.format((float) timer.hasTimeLeft(delayValue.get()) / 1000F) + "s...";
        float middleWidth = Fonts.font40.getStringWidth(detail) / 2F;
        float strength = MathHelper.clamp_float((float) timer.hasTimeLeft(delayValue.get()) / delayValue.get(), 0F, 1F);
        float wid = strength * (5F + middleWidth) * 2F;

        posY = AnimationUtils.animate(shouldChangeGame ? 10F : -20F, posY, 0.25F * 0.05F * RenderUtils.deltaTime);
        if (!renderValue.get() || posY < -15)
            return;

        Stencil.write(true);
        RenderUtils.drawRoundedRect(middleX - 5F - middleWidth, posY, middleX + 5F + middleWidth, posY + 15F, 3F, 0xA0000000);
        Stencil.erase(true);
        RenderUtils.drawRect(middleX - 5F - middleWidth, posY, middleX - 5F - middleWidth + wid, posY + 15F, new Color(0.4F, 0.8F, 0.4F, 0.35F).getRGB());
        Stencil.dispose();

        GlStateManager.resetColor();
        Fonts.fontSFUI40.drawString(detail, middleX - middleWidth - 1F, posY + 4F, -1);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if ((!checkValue.get() || gameMode.toLowerCase().contains("skywars")) && shouldChangeGame && timer.hasTimePassed(delayValue.get())) {
            mc.thePlayer.sendChatMessage("/play " + modeValue.get().toLowerCase() + (modeValue.get().equalsIgnoreCase("ranked") ? "_normal" : modeValue.get().equalsIgnoreCase("mega") ? "_" + megaValue.get().toLowerCase() : "_" + soloTeamsValue.get().toLowerCase()));
            shouldChangeGame = false;
        }
        if (!shouldChangeGame) timer.reset();
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat chat = (S02PacketChat) event.getPacket();
            if (chat.getChatComponent() != null) {
                if (antiSnipeValue.get() && chat.getChatComponent().getUnformattedText().contains("Sending you to")) {
                    event.cancelEvent();
                    return;
                }

                for (String s : strings)
                    if (chat.getChatComponent().getUnformattedText().contains(s)) {
                        //LiquidBounce.hud.addNotification(new Notification("Attempting to send you to the next game in "+dFormat.format((double)delayValue.get()/1000D)+"s.",1000L));
                        if (autoGGValue.get() && chat.getChatComponent().getUnformattedText().contains(strings[3]))
                            mc.thePlayer.sendChatMessage(ggMessageValue.get());
                        shouldChangeGame = true;
                        break;
                    }
            }
        }
    }

}