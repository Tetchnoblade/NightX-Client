package dev.tr7zw.waveycapes.sim;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.util.Mth;

/**
 * Java port of https://www.youtube.com/watch?v=PGk0rnyTa1U by Sebastian Lague
 * Has some changes like maximizing bends, only designed to simulate a single
 * "rope"(cape). Point 0 is the part fixed to the player
 *
 */
public class StickSimulation {

    public List<Point> points = new ArrayList<>();
    public List<Stick> sticks = new ArrayList<>();
    public float gravity = 20f;
    public int numIterations = 30;
    private float maxBend = 5;

    public void simulate() {
        gravity = WaveyCapesBase.config.gravity;
        //maxBend = WaveyCapesBase.config.maxBend;

        float deltaTime = 50f/1000f; // fixed timescale
        Vector2 down = new Vector2(0, gravity * deltaTime);
        Vector2 tmp = new Vector2(0, 0);
        for (Point p : points) {
            if (!p.locked) {
                tmp.copy(p.position);
                // p.position.add(p.position).subtract(p.prevPosition);
                p.position.subtract(down);
                p.prevPosition.copy(tmp);
            }
        }

        // prevent the cape from clipping into the player
        Point basePoint = points.get(0);
        for (Point p : points) {
            if (p != basePoint && p.position.x - basePoint.position.x > 0) {
                p.position.x = basePoint.position.x - 0.1f;
            }
        }

        // Doesnt work like it should at all, but it prevents some folding into itself, so it stays for now
        for (int i = points.size() - 2; i >= 1; i--) {
            double angle = getAngle(points.get(i).position, points.get(i - 1).position, points.get(i + 1).position);
            angle *= 57.2958;
            if (angle > 360) {
                angle -= 360;
            }
            if (angle < -360) {
                angle += 360;
            }
            double abs = Math.abs(angle);
            if (abs < 180 - maxBend) {
                Vector2 replacement = getReplacement(points.get(i).position, points.get(i - 1).position, angle,
                        180 - maxBend + 1);
                points.get(i + 1).position = replacement;
            }
            if (abs > 180 + maxBend) {
                Vector2 replacement = getReplacement(points.get(i).position, points.get(i - 1).position, angle,
                        180 + maxBend - 1);
                points.get(i + 1).position = replacement;
            }
        }

        // move into correct direction
        for (int i = 0; i < numIterations; i++) {
            for (int x = sticks.size() - 1; x >= 0; x--) {
                Stick stick = sticks.get(x);
                Vector2 stickCentre = stick.pointA.position.clone().add(stick.pointB.position).div(2);
                Vector2 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
                if (!stick.pointA.locked) {
                    stick.pointA.position = stickCentre.clone().add(stickDir.clone().mul(stick.length / 2));
                }
                if (!stick.pointB.locked) {
                    stick.pointB.position = stickCentre.clone().subtract(stickDir.clone().mul(stick.length / 2));
                }
            }
        }
        // fix in the position/length, this prevents it from acting like a spring/stretchy
        for (int x = 0; x < sticks.size(); x++) {
            Stick stick = sticks.get(x);
            Vector2 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
            if (!stick.pointB.locked) {
                stick.pointB.position = stick.pointA.position.clone().subtract(stickDir.mul(stick.length));
            }
        }
    }

    private Vector2 getReplacement(Vector2 middle, Vector2 prev, double angle, double target) {
        double theta = target / 57.2958;
        float x = prev.x - middle.x;
        float y = prev.y - middle.y;
        if (angle < 0) {
            theta *= -1;
        }
        double cs = Math.cos(theta);
        double sn = Math.sin(theta);
        return new Vector2((float) ((x * cs) - (y * sn) + middle.x), (float) ((x * sn) + (y * cs) + middle.y));
    }

    private double getAngle(Vector2 middle, Vector2 prev, Vector2 next) {
        return Math.atan2(next.y - middle.y, next.x - middle.x) - Math.atan2(prev.y - middle.y, prev.x - middle.x);
    }

    public static class Point {
        public Vector2 position = new Vector2(0, 0);
        public Vector2 prevPosition = new Vector2(0, 0);
        public boolean locked;
        
        public float getLerpX(float delta) {
            return Mth.lerp(delta, prevPosition.x, position.x);
        }
        
        public float getLerpY(float delta) {
            return Mth.lerp(delta, prevPosition.y, position.y);
        }
    }

    public static class Stick {
        public Point pointA, pointB;
        public float length;

        public Stick(Point pointA, Point pointB, float length) {
            this.pointA = pointA;
            this.pointB = pointB;
            this.length = length;
        }

    }

    public static class Vector2 {
        public float x, y;

        public Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Vector2 clone() {
            return new Vector2(x, y);
        }

        public void copy(Vector2 vec) {
            this.x = vec.x;
            this.y = vec.y;
        }

        public Vector2 add(Vector2 vec) {
            this.x += vec.x;
            this.y += vec.y;
            return this;
        }

        public Vector2 subtract(Vector2 vec) {
            this.x -= vec.x;
            this.y -= vec.y;
            return this;
        }

        public Vector2 div(float amount) {
            this.x /= amount;
            this.y /= amount;
            return this;
        }

        public Vector2 mul(float amount) {
            this.x *= amount;
            this.y *= amount;
            return this;
        }

        public Vector2 normalize() {
            float f = (float) Math.sqrt(this.x * this.x + this.y * this.y);
            if (f < 1.0E-4F) {
                this.x = 0;
                this.y = 0;
            } else {
                this.x /= f;
                this.y /= f;
            }
            return this;
        }

        @Override
        public String toString() {
            return "Vector2 [x=" + x + ", y=" + y + "]";
        }

    }

}