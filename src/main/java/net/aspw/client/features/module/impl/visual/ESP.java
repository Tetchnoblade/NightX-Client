package net.aspw.client.features.module.impl.visual;

import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Render2DEvent;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.utils.EntityUtils;
import net.aspw.client.utils.item.ItemUtils;
import net.aspw.client.utils.render.BlendUtils;
import net.aspw.client.utils.render.ColorUtils;
import net.aspw.client.utils.render.RenderUtils;
import net.aspw.client.value.BoolValue;
import net.aspw.client.value.FloatValue;
import net.aspw.client.value.IntegerValue;
import net.aspw.client.value.ListValue;
import net.aspw.client.visual.font.GameFontRenderer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@ModuleInfo(name = "ESP", category = ModuleCategory.VISUAL)
public final class ESP extends Module {
    public static List collectedEntities = new ArrayList();
    public final BoolValue outline = new BoolValue("Outline", false);
    public final ListValue boxMode = new ListValue("Mode", new String[]{"Box", "Corners"}, "Box");
    public final BoolValue healthBar = new BoolValue("Health-Bar", true);
    public final ListValue hpBarMode = new ListValue("HBar-Mode", new String[]{"Dot", "Line"}, "Line", () -> healthBar.get());
    public final BoolValue absorption = new BoolValue("Render-Absorption", false, () -> healthBar.get() && hpBarMode.get().equalsIgnoreCase("line"));
    public final BoolValue armorBar = new BoolValue("Armor-bar", false);
    public final ListValue armorBarMode = new ListValue("ABar-Mode", new String[]{"Total", "Items"}, "Total", () -> armorBar.get());
    public final BoolValue healthNumber = new BoolValue("HealthNumber", false, () -> healthBar.get());
    public final ListValue hpMode = new ListValue("HP-Mode", new String[]{"Health", "Percent"}, "Health", () -> healthBar.get() && healthNumber.get());
    public final BoolValue armorNumber = new BoolValue("ItemArmorNumber", false, () -> armorBar.get());
    public final BoolValue armorItems = new BoolValue("ArmorItems", false);
    public final BoolValue armorDur = new BoolValue("ArmorDurability", false, () -> armorItems.get());
    public final BoolValue hoverValue = new BoolValue("Details-HoverOnly", false);
    public final BoolValue tagsValue = new BoolValue("Tags", true);
    public final BoolValue tagsBGValue = new BoolValue("Tags-Background", false, () -> tagsValue.get());
    public final BoolValue itemTagsValue = new BoolValue("Item-Tags", false);
    public final BoolValue clearNameValue = new BoolValue("Use-Clear-Name", false);
    public final BoolValue localPlayer = new BoolValue("Local-Player", false);
    public final BoolValue droppedItems = new BoolValue("Dropped-Items", false);
    private final ListValue colorModeValue = new ListValue("Color", new String[]{"Custom", "Rainbow", "Sky", "LiquidSlowly", "Fade", "Mixer"}, "Custom");
    private final IntegerValue colorRedValue = new IntegerValue("Red", 255, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("Green", 255, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
    private final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
    private final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
    private final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);
    private final FloatValue fontScaleValue = new FloatValue("Font-Scale", 1F, 0F, 1F, "x");
    private final BoolValue colorTeam = new BoolValue("Team", false);
    private final IntBuffer viewport;
    private final FloatBuffer modelview;
    private final FloatBuffer projection;
    private final FloatBuffer vector;
    private final int backgroundColor;
    private final int black;

    private final DecimalFormat dFormat = new DecimalFormat("0.0");

    public ESP() {
        this.viewport = GLAllocation.createDirectIntBuffer(16);
        this.modelview = GLAllocation.createDirectFloatBuffer(16);
        this.projection = GLAllocation.createDirectFloatBuffer(16);
        this.vector = GLAllocation.createDirectFloatBuffer(4);
        this.backgroundColor = new Color(0, 0, 0, 120).getRGB();
        this.black = Color.BLACK.getRGB();
    }

    public Color getColor(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

            if (entityLivingBase.hurtTime > 0)
                return Color.RED;

            if (EntityUtils.isFriend(entityLivingBase))
                return Color.BLUE;

            if (colorTeam.get()) {
                final char[] chars = entityLivingBase.getDisplayName().getFormattedText().toCharArray();
                int color = Integer.MAX_VALUE;

                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] != '§' || i + 1 >= chars.length)
                        continue;

                    final int index = GameFontRenderer.getColorIndex(chars[i + 1]);

                    if (index < 0 || index > 15)
                        continue;

                    color = ColorUtils.hexColors[index];
                    break;
                }

                return new Color(color);
            }
        }

        switch (colorModeValue.get()) {
            case "Custom":
                return new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
            case "Rainbow":
                return new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
            case "Sky":
                return RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
            case "LiquidSlowly":
                return ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get());
            case "Mixer":
                return ColorMixer.getMixedColor(0, mixerSecondsValue.get());
            default:
                return ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
        }
    }

    @Override
    public void onDisable() {
        collectedEntities.clear();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        GL11.glPushMatrix();
        this.collectEntities();
        float partialTicks = event.getPartialTicks();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        double scaling = (double) scaleFactor / Math.pow(scaleFactor, 2.0D);
        GL11.glScaled(scaling, scaling, scaling);
        int black = this.black;
        int background = this.backgroundColor;
        RenderManager renderMng = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;
        boolean outline = this.outline.get();
        boolean health = this.healthBar.get();
        boolean armor = this.armorBar.get();
        int i = 0;

        for (int collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; ++i) {
            Entity entity = (Entity) collectedEntities.get(i);
            int color = getColor(entity).getRGB();
            if (RenderUtils.isInViewFrustrum(entity)) {
                double x = RenderUtils.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                double y = RenderUtils.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                double z = RenderUtils.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                double width = (double) entity.width / 1.5D;
                double height = (double) entity.height + (entity.isSneaking() ? -0.3D : 0.2D);
                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                List vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                entityRenderer.setupCameraTransform(partialTicks, 0);
                Vector4d position = null;
                Iterator var38 = vectors.iterator();

                while (var38.hasNext()) {
                    Vector3d vector = (Vector3d) var38.next();
                    vector = this.project2D(scaleFactor, vector.x - renderMng.viewerPosX, vector.y - renderMng.viewerPosY, vector.z - renderMng.viewerPosZ);
                    if (vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
                        if (position == null) {
                            position = new Vector4d(vector.x, vector.y, vector.z, 0.0D);
                        }

                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }

                if (position != null) {
                    entityRenderer.setupOverlayRendering();
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;
                    double endPosY = position.w;
                    if (outline) {
                        if (this.boxMode.get().equals("Box")) {
                            RenderUtils.newDrawRect(posX - 1.0D, posY, posX + 0.5D, endPosY + 0.5D, black);
                            RenderUtils.newDrawRect(posX - 1.0D, posY - 0.5D, endPosX + 0.5D, posY + 0.5D + 0.5D, black);
                            RenderUtils.newDrawRect(endPosX - 0.5D - 0.5D, posY, endPosX + 0.5D, endPosY + 0.5D, black);
                            RenderUtils.newDrawRect(posX - 1.0D, endPosY - 0.5D - 0.5D, endPosX + 0.5D, endPosY + 0.5D, black);
                            RenderUtils.newDrawRect(posX - 0.5D, posY, posX + 0.5D - 0.5D, endPosY, color);
                            RenderUtils.newDrawRect(posX, endPosY - 0.5D, endPosX, endPosY, color);
                            RenderUtils.newDrawRect(posX - 0.5D, posY, endPosX, posY + 0.5D, color);
                            RenderUtils.newDrawRect(endPosX - 0.5D, posY, endPosX, endPosY, color);
                        } else {
                            RenderUtils.newDrawRect(posX + 0.5D, posY, posX - 1.0D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                            RenderUtils.newDrawRect(posX - 1.0D, endPosY, posX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                            RenderUtils.newDrawRect(posX - 1.0D, posY - 0.5D, posX + (endPosX - posX) / 3.0D + 0.5D, posY + 1.0D, black);
                            RenderUtils.newDrawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, posY - 0.5D, endPosX, posY + 1.0D, black);
                            RenderUtils.newDrawRect(endPosX - 1.0D, posY, endPosX + 0.5D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                            RenderUtils.newDrawRect(endPosX - 1.0D, endPosY, endPosX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                            RenderUtils.newDrawRect(posX - 1.0D, endPosY - 1.0D, posX + (endPosX - posX) / 3.0D + 0.5D, endPosY + 0.5D, black);
                            RenderUtils.newDrawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, endPosY - 1.0D, endPosX + 0.5D, endPosY + 0.5D, black);
                            RenderUtils.newDrawRect(posX, posY, posX - 0.5D, posY + (endPosY - posY) / 4.0D, color);
                            RenderUtils.newDrawRect(posX, endPosY, posX - 0.5D, endPosY - (endPosY - posY) / 4.0D, color);
                            RenderUtils.newDrawRect(posX - 0.5D, posY, posX + (endPosX - posX) / 3.0D, posY + 0.5D, color);
                            RenderUtils.newDrawRect(endPosX - (endPosX - posX) / 3.0D, posY, endPosX, posY + 0.5D, color);
                            RenderUtils.newDrawRect(endPosX - 0.5D, posY, endPosX, posY + (endPosY - posY) / 4.0D, color);
                            RenderUtils.newDrawRect(endPosX - 0.5D, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0D, color);
                            RenderUtils.newDrawRect(posX, endPosY - 0.5D, posX + (endPosX - posX) / 3.0D, endPosY, color);
                            RenderUtils.newDrawRect(endPosX - (endPosX - posX) / 3.0D, endPosY - 0.5D, endPosX - 0.5D, endPosY, color);
                        }
                    }

                    boolean living = entity instanceof EntityLivingBase;
                    EntityLivingBase entityLivingBase;
                    float armorValue;
                    float itemDurability;
                    double durabilityWidth;
                    double textWidth;
                    float tagY;
                    if (living) {
                        entityLivingBase = (EntityLivingBase) entity;
                        if (health) {
                            armorValue = entityLivingBase.getHealth();
                            itemDurability = entityLivingBase.getMaxHealth();
                            if (armorValue > itemDurability)
                                armorValue = itemDurability;

                            durabilityWidth = armorValue / itemDurability;
                            textWidth = (endPosY - posY) * durabilityWidth;
                            String healthDisplay = dFormat.format(entityLivingBase.getHealth()) + " §c❤";
                            String healthPercent = ((int) ((entityLivingBase.getHealth() / itemDurability) * 100F)) + "%";
                            if (healthNumber.get() && (!hoverValue.get() || entity == mc.thePlayer || isHovering(posX, endPosX, posY, endPosY, scaledResolution)))
                                drawScaledString(hpMode.get().equalsIgnoreCase("health") ? healthDisplay : healthPercent, posX - 4.0 - mc.fontRendererObj.getStringWidth(hpMode.get().equalsIgnoreCase("health") ? healthDisplay : healthPercent) * fontScaleValue.get(), (endPosY - textWidth) - mc.fontRendererObj.FONT_HEIGHT / 2F * fontScaleValue.get(), fontScaleValue.get(), -1);
                            RenderUtils.newDrawRect(posX - 3.5D, posY - 0.5D, posX - 1.5D, endPosY + 0.5D, background);
                            if (armorValue > 0.0F) {
                                int healthColor = BlendUtils.getHealthColor(armorValue, itemDurability).getRGB();
                                double deltaY = endPosY - posY;
                                if (hpBarMode.get().equalsIgnoreCase("dot") && deltaY >= 60) { // revert back to normal bar if the height is too low
                                    for (double k = 0; k < 10; k++) {
                                        double reratio = MathHelper.clamp_double(armorValue - k * (itemDurability / 10D), 0D, itemDurability / 10D) / (itemDurability / 10D);
                                        double hei = (deltaY / 10D - 0.5) * reratio;
                                        RenderUtils.newDrawRect(posX - 3.0D, endPosY - (deltaY + 0.5) / 10D * k, posX - 2.0D, endPosY - (deltaY + 0.5) / 10D * k - hei, healthColor);
                                    }
                                } else {
                                    RenderUtils.newDrawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - textWidth, healthColor);
                                    tagY = entityLivingBase.getAbsorptionAmount();
                                    if (absorption.get() && tagY > 0.0F)
                                        RenderUtils.newDrawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - (endPosY - posY) / 6.0D * (double) tagY / 2.0D, (new Color(Potion.absorption.getLiquidColor())).getRGB());
                                }
                            }
                        }
                    }

                    if (armor) {
                        if (living) {
                            entityLivingBase = (EntityLivingBase) entity;
                            if (armorBarMode.get().equalsIgnoreCase("items")) {
                                final double constHeight = (endPosY - posY) / 4.0;
                                for (int m = 4; m > 0; m--) {
                                    ItemStack armorStack = entityLivingBase.getEquipmentInSlot(m);
                                    double theHeight = constHeight + 0.25D;
                                    if (armorStack != null && armorStack.getItem() != null) {
                                        RenderUtils.newDrawRect(endPosX + 1.5D, endPosY + 0.5D - theHeight * m, endPosX + 3.5D, endPosY + 0.5D - theHeight * (m - 1), background);
                                        RenderUtils.newDrawRect(endPosX + 2.0D,
                                                endPosY + 0.5D - theHeight * (m - 1) - 0.25D,
                                                endPosX + 3.0D,
                                                endPosY + 0.5D - theHeight * (m - 1) - 0.25D - (constHeight - 0.25D) * MathHelper.clamp_double((double) ItemUtils.getItemDurability(armorStack) / (double) armorStack.getMaxDamage(), 0D, 1D), new Color(0, 255, 255).getRGB());
                                    }
                                }
                            } else {
                                armorValue = (float) entityLivingBase.getTotalArmorValue();
                                double armorWidth = (endPosY - posY) * (double) armorValue / 20.0D;
                                RenderUtils.newDrawRect(endPosX + 1.5D, posY - 0.5D, endPosX + 3.5D, endPosY + 0.5D, background);
                                if (armorValue > 0.0F)
                                    RenderUtils.newDrawRect(endPosX + 2.0D, endPosY, endPosX + 3.0D, endPosY - armorWidth, new Color(0, 255, 255).getRGB());
                            }
                        } else if (entity instanceof EntityItem) {
                            ItemStack itemStack = ((EntityItem) entity).getEntityItem();
                            if (itemStack.isItemStackDamageable()) {
                                int maxDamage = itemStack.getMaxDamage();
                                itemDurability = (float) (maxDamage - itemStack.getItemDamage());
                                durabilityWidth = (endPosY - posY) * (double) itemDurability / (double) maxDamage;
                                if (armorNumber.get() && (!hoverValue.get() || entity == mc.thePlayer || isHovering(posX, endPosX, posY, endPosY, scaledResolution)))
                                    drawScaledString(((int) itemDurability) + "", endPosX + 4.0, (endPosY - durabilityWidth) - mc.fontRendererObj.FONT_HEIGHT / 2F * fontScaleValue.get(), fontScaleValue.get(), -1);
                                RenderUtils.newDrawRect(endPosX + 1.5D, posY - 0.5D, endPosX + 3.5D, endPosY + 0.5D, background);
                                RenderUtils.newDrawRect(endPosX + 2.0D, endPosY, endPosX + 3.0D, endPosY - durabilityWidth, new Color(0, 255, 255).getRGB());
                            }
                        }
                    }

                    if (living && armorItems.get() && (!hoverValue.get() || entity == mc.thePlayer || isHovering(posX, endPosX, posY, endPosY, scaledResolution))) {
                        entityLivingBase = (EntityLivingBase) entity;
                        double yDist = (endPosY - posY) / 4.0D;
                        for (int j = 4; j > 0; j--) {
                            ItemStack armorStack = entityLivingBase.getEquipmentInSlot(j);
                            if (armorStack != null && armorStack.getItem() != null) {
                                renderItemStack(armorStack, endPosX + (armor ? 4.0D : 2.0D), posY + (yDist * (4 - j)) + (yDist / 2.0D) - 5.0D);
                                if (armorDur.get())
                                    drawScaledCenteredString(ItemUtils.getItemDurability(armorStack) + "", endPosX + (armor ? 4.0D : 2.0D) + 4.5D, posY + (yDist * (4 - j)) + (yDist / 2.0D) + 4.0D, fontScaleValue.get(), -1);
                            }
                        }
                    }

                    if (living && tagsValue.get()) {
                        entityLivingBase = (EntityLivingBase) entity;
                        String entName = clearNameValue.get() ? entityLivingBase.getName() : entityLivingBase.getDisplayName().getFormattedText();
                        if (tagsBGValue.get())
                            RenderUtils.newDrawRect(posX + (endPosX - posX) / 2F - (mc.fontRendererObj.getStringWidth(entName) / 2F + 2F) * fontScaleValue.get(), posY - 1F - (mc.fontRendererObj.FONT_HEIGHT + 2F) * fontScaleValue.get(), posX + (endPosX - posX) / 2F + (mc.fontRendererObj.getStringWidth(entName) / 2F + 2F) * fontScaleValue.get(), posY - 1F + 2F * fontScaleValue.get(), 0xA0000000);
                        drawScaledCenteredString(entName, posX + (endPosX - posX) / 2F, posY - 1F - mc.fontRendererObj.FONT_HEIGHT * fontScaleValue.get(), fontScaleValue.get(), -1);
                    }

                    if (itemTagsValue.get()) {
                        if (living) {
                            entityLivingBase = (EntityLivingBase) entity;
                            if (entityLivingBase.getHeldItem() != null && entityLivingBase.getHeldItem().getItem() != null) {
                                String itemName = entityLivingBase.getHeldItem().getDisplayName();
                                if (tagsBGValue.get())
                                    RenderUtils.newDrawRect(posX + (endPosX - posX) / 2F - (mc.fontRendererObj.getStringWidth(itemName) / 2F + 2F) * fontScaleValue.get(), endPosY + 1F - 2F * fontScaleValue.get(), posX + (endPosX - posX) / 2F + (mc.fontRendererObj.getStringWidth(itemName) / 2F + 2F) * fontScaleValue.get(), endPosY + 1F + (mc.fontRendererObj.FONT_HEIGHT + 2F) * fontScaleValue.get(), 0xA0000000);
                                drawScaledCenteredString(itemName, posX + (endPosX - posX) / 2F, endPosY + 1F, fontScaleValue.get(), -1);
                            }
                        } else if (entity instanceof EntityItem) {
                            String entName = ((EntityItem) entity).getEntityItem().getDisplayName();
                            if (tagsBGValue.get())
                                RenderUtils.newDrawRect(posX + (endPosX - posX) / 2F - (mc.fontRendererObj.getStringWidth(entName) / 2F + 2F) * fontScaleValue.get(), endPosY + 1F - 2F * fontScaleValue.get(), posX + (endPosX - posX) / 2F + (mc.fontRendererObj.getStringWidth(entName) / 2F + 2F) * fontScaleValue.get(), endPosY + 1F + (mc.fontRendererObj.FONT_HEIGHT + 2F) * fontScaleValue.get(), 0xA0000000);
                            drawScaledCenteredString(entName, posX + (endPosX - posX) / 2F, endPosY + 1F, fontScaleValue.get(), -1);
                        }
                    }
                }
            }
        }

        GL11.glPopMatrix();
        GlStateManager.enableBlend();
        GlStateManager.resetColor();
        entityRenderer.setupOverlayRendering();
    }

    private boolean isHovering(double minX, double maxX, double minY, double maxY, ScaledResolution sc) {
        return sc.getScaledWidth() / 2 >= minX && sc.getScaledWidth() / 2 < maxX && sc.getScaledHeight() / 2 >= minY && sc.getScaledHeight() / 2 < maxY;
    }

    private void drawScaledString(String text, double x, double y, double scale, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(scale, scale, scale);
        mc.fontRendererObj.drawStringWithShadow(text, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private void drawScaledCenteredString(String text, double x, double y, double scale, int color) {
        drawScaledString(text, x - mc.fontRendererObj.getStringWidth(text) / 2F * scale, y, scale, color);
    }

    private void renderItemStack(ItemStack stack, double x, double y) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void collectEntities() {
        collectedEntities.clear();
        List playerEntities = mc.theWorld.loadedEntityList;
        int i = 0;

        for (int playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; ++i) {
            Entity entity = (Entity) playerEntities.get(i);
            if (EntityUtils.isSelected(entity, false) || (localPlayer.get() && entity instanceof EntityPlayerSP && mc.gameSettings.thirdPersonView != 0) || (droppedItems.get() && entity instanceof EntityItem)) {
                collectedEntities.add(entity);
            }
        }

    }

    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        return GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.vector) ? new Vector3d(this.vector.get(0) / (float) scaleFactor, ((float) Display.getHeight() - this.vector.get(1)) / (float) scaleFactor, this.vector.get(2)) : null;
    }
}
