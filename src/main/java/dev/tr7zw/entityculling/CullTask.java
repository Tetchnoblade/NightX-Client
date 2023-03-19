package dev.tr7zw.entityculling;

import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import com.logisticscraft.occlusionculling.util.Vec3d;
import dev.tr7zw.entityculling.access.Cullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

public class CullTask implements Runnable {

    public boolean requestCull = false;

    private final OcclusionCullingInstance culling;
    private final Minecraft client = Minecraft.getMinecraft();
    private final Set<String> unCullable;
    public long lastTime = 0;

    private final Vec3d lastPos = new Vec3d(0, 0, 0);
    private final Vec3d aabbMin = new Vec3d(0, 0, 0);
    private final Vec3d aabbMax = new Vec3d(0, 0, 0);

    public CullTask(OcclusionCullingInstance culling, Set<String> unCullable) {
        this.culling = culling;
        this.unCullable = unCullable;
    }

    @Override
    public void run() {
        while (client != null) {
            try {
                Thread.sleep(10);

                if (EntityCullingModBase.enabled && client.theWorld != null && client.thePlayer != null && client.thePlayer.ticksExisted > 10 && client.getRenderViewEntity() != null) {
                    Vec3 cameraMC = null;
                    cameraMC = getCameraPos();
                    if (requestCull || !(cameraMC.xCoord == lastPos.x && cameraMC.yCoord == lastPos.y && cameraMC.zCoord == lastPos.z)) {
                        long start = System.currentTimeMillis();
                        requestCull = false;
                        lastPos.set(cameraMC.xCoord, cameraMC.yCoord, cameraMC.zCoord);
                        Vec3d camera = lastPos;
                        culling.resetCache();
                        boolean noCulling = client.thePlayer.isSpectator() || client.gameSettings.thirdPersonView != 0;
                        Iterator<TileEntity> iterator = client.theWorld.loadedTileEntityList.iterator();
                        TileEntity entry;
                        while (iterator.hasNext()) {
                            try {
                                entry = iterator.next();
                            } catch (NullPointerException | ConcurrentModificationException ex) {
                                break;
                            }
                            if (unCullable.contains(entry.getBlockType().getUnlocalizedName())) {
                                continue;
                            }
                            Cullable cullable = (Cullable) entry;
                            if (!cullable.isForcedVisible()) {
                                if (noCulling) {
                                    cullable.setCulled(false);
                                    continue;
                                }
                                BlockPos pos = entry.getPos();
                                if (pos.distanceSq(cameraMC.xCoord, cameraMC.yCoord, cameraMC.zCoord) < 64 * 64) { // 64 is the fixed max tile view distance
                                    aabbMin.set(pos.getX(), pos.getY(), pos.getZ());
                                    aabbMax.set(pos.getX() + 1d, pos.getY() + 1d, pos.getZ() + 1d);
                                    boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
                                    cullable.setCulled(!visible);
                                }

                            }
                        }
                        Entity entity = null;
                        Iterator<Entity> iterable = client.theWorld.getLoadedEntityList().iterator();
                        while (iterable.hasNext()) {
                            try {
                                entity = iterable.next();
                            } catch (NullPointerException | ConcurrentModificationException ex) {
                                break;
                            }
                            if (entity == null || !(entity instanceof Cullable)) {
                                continue;
                            }
                            Cullable cullable = (Cullable) entity;
                            if (!cullable.isForcedVisible()) {
                                if (noCulling || isSkippableArmorstand(entity)) {
                                    cullable.setCulled(false);
                                    continue;
                                }
                                if (entity.getPositionVector().squareDistanceTo(cameraMC) > 128 * 128) {
                                    cullable.setCulled(false);
                                    continue;
                                }
                                AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
                                if (boundingBox.maxX - boundingBox.minX > 50 || boundingBox.maxY - boundingBox.minY > 50 || boundingBox.maxZ - boundingBox.minZ > 50) {
                                    cullable.setCulled(false);
                                    continue;
                                }
                                aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                                aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                                boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
                                cullable.setCulled(!visible);
                            }
                        }
                        lastTime = (System.currentTimeMillis() - start);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Vec3 getCameraPos() {
        if (client.gameSettings.thirdPersonView == 0) {
            return client.getRenderViewEntity().getPositionEyes(0);
        }
        return client.getRenderViewEntity().getPositionEyes(0);
    }

    private boolean isSkippableArmorstand(Entity entity) {
        return entity instanceof EntityArmorStand && ((EntityArmorStand) entity).hasMarker();
    }
}
