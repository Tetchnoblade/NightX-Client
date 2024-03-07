package net.aspw.client.utils;

import net.aspw.client.event.EntityKilledEvent;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Listenable;

public class StatisticsUtils implements Listenable {

    @EventTarget
    public void onTargetKilled(EntityKilledEvent e) {
        e.getTargetEntity();
    }

    public static void addDeaths() {
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
