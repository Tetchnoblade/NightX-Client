package net.aspw.client.injection.access;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;

public interface IEntity {
    int getNextStepDistance();

    void setNextStepDistance(int var1);

    int getFire();

    void setFire(int var1);

    AxisAlignedBB getBoundingBox();

    boolean isOverOfMaterial(Material var1);

    net.minecraft.util.Vec3 getVectorForRotation(float var1, float var2);

    float getCameraYaw();

    float getCameraPitch();
}
