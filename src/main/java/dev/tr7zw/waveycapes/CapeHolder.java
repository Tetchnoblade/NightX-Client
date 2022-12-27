package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.sim.StickSimulation;
import dev.tr7zw.waveycapes.sim.StickSimulation.Point;
import dev.tr7zw.waveycapes.sim.StickSimulation.Stick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public interface CapeHolder {
    public StickSimulation getSimulation();
    
    public default void updateSimulation(EntityPlayer abstractClientPlayer, int partCount) {
        StickSimulation simulation = getSimulation();
        boolean dirty = false;
        if(simulation.points.size() != partCount) {
            simulation.points.clear();
            simulation.sticks.clear();
            for (int i = 0; i < partCount; i++) {
                Point point = new Point();
                point.position.y = -i;
                point.locked = i == 0;
                simulation.points.add(point);
                if(i > 0) {
                    simulation.sticks.add(new Stick(simulation.points.get(i-1), point, 1f));
                }
            }
            dirty = true;
        }
        if(dirty) {
            for(int i = 0; i < 10; i++) // quickly doing a few simulation steps to get the cape int a stable configuration
                simulate(abstractClientPlayer);
        }
    }
    
    public default void simulate(EntityPlayer abstractClientPlayer) {
        StickSimulation simulation = getSimulation();
        if(simulation.points.isEmpty()) {
            return; // no cape, nothing to update
        }
        simulation.points.get(0).prevPosition.copy(simulation.points.get(0).position);
        double d = abstractClientPlayer.chasingPosX
                - abstractClientPlayer.posX;
        double m = abstractClientPlayer.chasingPosZ
                - abstractClientPlayer.posZ;
        float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset;
        double o = Math.sin(n * 0.017453292F);
        double p = -Math.cos(n * 0.017453292F);
        float heightMul = WaveyCapesBase.config.heightMultiplier;
        // gives the cape a small swing when jumping/falling to not clip with itself/simulate some air getting under it
        double fallHack = MathHelper.clamp_double((simulation.points.get(0).position.y - (abstractClientPlayer.posY*heightMul)), 0d, 1d); 
        simulation.points.get(0).position.x += (d * o + m * p) + fallHack;
        simulation.points.get(0).position.y = (float) (abstractClientPlayer.posY*heightMul + (abstractClientPlayer.isSneaking() ? -4 : 0));
        simulation.simulate();
    }
    
}
