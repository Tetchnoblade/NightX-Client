package net.aspw.client.injection.access;

public interface IEntityPlayerSP {
    boolean moving();

    float getSpeed();

    void setSpeed(double var1);

    void setYaw(double var1);

    void setPitch(double var1);

    float getDirection();

    void setLastReportedPosY(double var1);

    double getlastReportedPosZ();

    double getlastReportedPosX();

    double getlastReportedPosY();

    double getoffGroundTicks();

    double getOnGroundTicks();
}
