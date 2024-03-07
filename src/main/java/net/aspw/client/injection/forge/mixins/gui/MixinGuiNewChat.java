package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

/**
 * The type Mixin gui new chat.
 */
@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {

    private final HashMap<String, String> stringCache = new HashMap<>();
    private float displayPercent;
    private int newLines;
    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;
    @Shadow
    private int scrollPos;
    @Shadow
    private boolean isScrolled;
    @Shadow
    @Final
    private List<ChatLine> chatLines;
    private int line;
    private Interface anInterface;

    /**
     * Gets line count.
     *
     * @return the line count
     */
    @Shadow
    public abstract int getLineCount();

    /**
     * Gets chat open.
     *
     * @return the chat open
     */
    @Shadow
    public abstract boolean getChatOpen();

    /**
     * Gets chat scale.
     *
     * @return the chat scale
     */
    @Shadow
    public abstract float getChatScale();

    /**
     * Gets chat width.
     *
     * @return the chat width
     */
    @Shadow
    public abstract int getChatWidth();

    /**
     * Delete chat line.
     *
     * @param p_deleteChatLine_1_ the p delete chat line 1
     */
    @Shadow
    public abstract void deleteChatLine(int p_deleteChatLine_1_);

    /**
     * Scroll.
     *
     * @param p_scroll_1_ the p scroll 1
     */
    @Shadow
    public abstract void scroll(int p_scroll_1_);

    /**
     * Print chat message with optional deletion.
     *
     * @param chatComponent the chat component
     * @param chatLineId    the chat line id
     */
    @Shadow
    public abstract void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId);

    private void checkHud() {
        if (anInterface == null)
            anInterface = Launch.moduleManager.getModule(Interface.class);
    }

    @Redirect(method = "deleteChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getChatLineID()I"))
    private int checkIfChatLineIsNull(ChatLine instance) {
        if (instance == null) return -1;
        return instance.getChatLineID();
    }

    /**
     * Print chat message.
     *
     * @param chatComponent the chat component
     * @author As_pw
     * @reason Print
     */
    @Overwrite
    public void printChatMessage(IChatComponent chatComponent) {
        checkHud();
        printChatMessageWithOptionalDeletion(chatComponent, this.line);
    }

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private void resetPercentage(CallbackInfo ci) {
        displayPercent = 0F;
    }

    /**
     * Draw chat.
     *
     * @param updateCounter the update counter
     * @author As_pw
     * @reason Draw
     */
    @Overwrite
    public void drawChat(int updateCounter) {
        checkHud();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -12, 0);

        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = this.getLineCount();
            boolean flag = false;
            int j = 0;
            int k = this.drawnChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            if (k > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }

                if (this.isScrolled || !anInterface.getState()) {
                    displayPercent = 1F;
                } else if (displayPercent < 1F && anInterface.getChatAnimationValue().get()) {
                    displayPercent += anInterface.getChatAnimationSpeedValue().get() / 10F * RenderUtils.deltaTime;
                    displayPercent = MathHelper.clamp_float(displayPercent, 0F, 1F);
                }
                if (displayPercent < 1F && !anInterface.getChatAnimationValue().get()) {
                    displayPercent += 10F * RenderUtils.deltaTime;
                    displayPercent = MathHelper.clamp_float(displayPercent, 0F, 1F);
                }

                float t = displayPercent;
                float animationPercent = MathHelper.clamp_float(1F - (--t) * t * t * t, 0F, 1F);

                float f1 = this.getChatScale();
                int l = MathHelper.ceiling_float_int((float) this.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                if (anInterface.getState())
                    GlStateManager.translate(0F, (1F - animationPercent) * 9F * this.getChatScale(), 0F);
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);

                int i1;
                int j1;
                int l1;
                for (i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    int lineBeingDrawn = i1 + this.scrollPos;
                    if (chatline != null) {
                        j1 = updateCounter - chatline.getUpdatedCounter();
                        if (j1 < 200 || flag) {
                            double d0 = (double) j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 *= 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                            d0 *= d0;
                            l1 = (int) (255.0D * d0);
                            if (flag) {
                                l1 = 255;
                            }

                            l1 = (int) ((float) l1 * f);
                            ++j;

                            //Animation part
                            if (l1 > 3) {
                                int i2 = 0;
                                int j2 = -i1 * 9;

                                if (anInterface.getState() && anInterface.getChatRectValue().get() || !anInterface.getState()) {
                                    if (lineBeingDrawn <= newLines && !flag)
                                        RenderUtils.drawRect(i2, j2 - 9, i2 + l + 4, j2, new Color(0F, 0F, 0F, animationPercent * ((float) d0 / 2F)).getRGB());
                                    else
                                        RenderUtils.drawRect(i2, j2 - 9, i2 + l + 4, j2, l1 / 2 << 24);
                                }

                                GlStateManager.resetColor();
                                GlStateManager.color(1F, 1F, 1F, 1F);

                                String s = fixString(chatline.getChatComponent().getFormattedText());
                                GlStateManager.enableBlend();
                                if (anInterface.getState() && lineBeingDrawn <= newLines)
                                    this.mc.fontRendererObj.drawString(s, (float) i2, (float) (j2 - 8), new Color(1F, 1F, 1F, animationPercent * (float) d0).getRGB(), true);
                                else
                                    this.mc.fontRendererObj.drawString(s, (float) i2, (float) (j2 - 8), 16777215 + (l1 << 24), true);
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                if (flag) {
                    i1 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = k * i1 + k;
                    j1 = j * i1 + j;
                    int j3 = this.scrollPos * j1 / k;
                    int k1 = j1 * j1 / l2;
                    if (l2 != j1) {
                        l1 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 13382451 : 3355562;
                        RenderUtils.drawRect(0, -j3, 2, -j3 - k1, l3 + (l1 << 24));
                        RenderUtils.drawRect(2, -j3, 1, -j3 - k1, 13421772 + (l1 << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();
    }

    private String fixString(String str) {
        if (stringCache.containsKey(str)) return stringCache.get(str);

        str = str.replaceAll("\uF8FF", "");

        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if ((int) c > (33 + 65248) && (int) c < (128 + 65248))
                sb.append(Character.toChars((int) c - 65248));
            else
                sb.append(c);
        }

        String result = sb.toString();
        stringCache.put(str, result);

        return result;
    }

    @ModifyVariable(method = "setChatLine", at = @At("STORE"), ordinal = 0)
    private List<IChatComponent> setNewLines(List<IChatComponent> original) {
        newLines = original.size() - 1;
        return original;
    }

    /**
     * Gets chat component.
     *
     * @param p_146236_1_ the p 146236 1
     * @param p_146236_2_ the p 146236 2
     * @return the chat component
     * @author As_pw
     * @reason ChatComponent
     */
    @Overwrite
    public IChatComponent getChatComponent(int p_146236_1_, int p_146236_2_) {
        checkHud();

        if (!this.getChatOpen()) {
            return null;
        } else {
            ScaledResolution sc = new ScaledResolution(this.mc);
            int scaleFactor = sc.getScaleFactor();
            float chatScale = this.getChatScale();
            int mX = p_146236_1_ / scaleFactor - 3;
            int mY = p_146236_2_ / scaleFactor - 27 - (12);
            mX = MathHelper.floor_float((float) mX / chatScale);
            mY = MathHelper.floor_float((float) mY / chatScale);
            if (mX >= 0 && mY >= 0) {
                int lineCount = Math.min(this.getLineCount(), this.drawnChatLines.size());
                if (mX <= MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale()) && mY < this.mc.fontRendererObj.FONT_HEIGHT * lineCount + lineCount) {
                    int line = mY / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
                    if (line >= 0 && line < this.drawnChatLines.size()) {
                        ChatLine chatLine = this.drawnChatLines.get(line);
                        int maxWidth = 0;

                        for (IChatComponent iterator : chatLine.getChatComponent()) {
                            if (iterator instanceof ChatComponentText) {
                                maxWidth += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText) iterator).getChatComponentText_TextValue(), false));
                                if (maxWidth > mX) {
                                    return iterator;
                                }
                            }
                        }
                    }

                }
            }
            return null;
        }
    }
}