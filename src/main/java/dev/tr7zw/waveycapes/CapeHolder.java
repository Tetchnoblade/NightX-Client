package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.sim.StickSimulation;
import dev.tr7zw.waveycapes.sim.StickSimulation.Point;
import dev.tr7zw.waveycapes.sim.StickSimulation.Stick;
import net.minecraft.entity.player.EntityPlayer;

public interface CapeHolder {
    public StickSimulation getSimulation();

    public default void updateSimulation(EntityPlayer abstractClientPlayer, int partCount) {
        StickSimulation simulation = getSimulation();
        boolean dirty = false;
        if (simulation.points.size() != partCount) {
            simulation.points.clear();
            simulation.sticks.clear();
            for (int i = 0; i < partCount; i++) {
                Point point = new Point();
                point.position.y = -i;
                point.locked = i == 0;
                simulation.points.add(point);
                if (i > 0) {
                    simulation.sticks.add(new Stick(simulation.points.get(i - 1), point, 1f));
                }
            }
            dirty = true;
        }
        if (dirty) {
            for (int i = 0; i < 10; i++) // quickly doing a few simulation steps to get the cape int a stable configuration
                simulate(abstractClientPlayer);
        }
    }

    default void simulate(EntityPlayer abstractClientPlayer) {
        StickSimulation simulation = getSimulation();
        if (simulation.points.isEmpty()) {
            return;
        }
        simulation.points.get(0).prevPosition.copy(simulation.points.get(0).position);
        simulation.points.get(0).position.x += 400;
        simulation.points.get(0).position.y = 0;
        simulation.simulate();
    }

}
