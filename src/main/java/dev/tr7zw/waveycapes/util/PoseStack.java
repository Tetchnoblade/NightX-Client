package dev.tr7zw.waveycapes.util;

import java.util.Deque;

import com.google.common.collect.Queues;

public class PoseStack {
    private final Deque<Pose> poseStack;

    public PoseStack() {
        this.poseStack = Queues.newArrayDeque();
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.setIdentity();
        poseStack.add(new Pose(matrix4f, matrix3f));
    }

    public void translate(double d, double e, double f) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiplyWithTranslation((float) d, (float) e, (float) f);
    }

    public void scale(float f, float g, float h) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiply(Matrix4f.createScaleMatrix(f, g, h));
        if (f == g && g == h) {
            if (f > 0.0F)
                return;
            pose.normal.mul(-1.0F);
        }
        float i = 1.0F / f;
        float j = 1.0F / g;
        float k = 1.0F / h;
        float l = Mth.fastInvCubeRoot(i * j * k);
        pose.normal.mul(Matrix3f.createScaleMatrix(l * i, l * j, l * k));
    }

    public void mulPose(Quaternion quaternion) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiply(quaternion);
        pose.normal.mul(quaternion);
    }

    public void pushPose() {
        Pose pose = this.poseStack.getLast();
        this.poseStack.addLast(new Pose(pose.pose.copy(), pose.normal.copy()));
    }

    public void popPose() {
        this.poseStack.removeLast();
    }

    public Pose last() {
        return this.poseStack.getLast();
    }

    public boolean clear() {
        return (this.poseStack.size() == 1);
    }

    public void setIdentity() {
        Pose pose = this.poseStack.getLast();
        pose.pose.setIdentity();
        pose.normal.setIdentity();
    }

    public void mulPoseMatrix(Matrix4f matrix4f) {
        ((Pose) this.poseStack.getLast()).pose.multiply(matrix4f);
    }

    public static final class Pose {
        final Matrix4f pose;

        final Matrix3f normal;

        Pose(Matrix4f matrix4f, Matrix3f matrix3f) {
            this.pose = matrix4f;
            this.normal = matrix3f;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }
    }
}