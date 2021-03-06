package org.ramonazaapi.rides.clusters;

import org.ramonazaapi.contacts.ContactInfoWrapper;
import org.ramonazaapi.rides.RidesOptimizer;

/**
 * A cluster based on drawing circles with radius {@link SnakeCluster#RADIUS_ADDITION}
 * and adding all those whose circles are touching.
 * Created by ilan on 10/2/15.
 */
public class SnakeCluster extends RidesCluster {

    public static final double RADIUS_ADDITION = 0.00619;

    public SnakeCluster(ContactInfoWrapper firstContact) {
        super(firstContact);
    }

    @Override
    public boolean passengerLiesInCluster(ContactInfoWrapper toCheck) {
        for (ContactInfoWrapper current : contactsInCluster) {
            if (RidesOptimizer.distBetweenHouses(toCheck, current) <= RADIUS_ADDITION) return true;
        }
        return false;
    }

    @Override
    public void recalculate() {
        return;
    }

    @Override
    public double[] getCenter() {
        double[] center = new double[]{0.0, 0.0};
        int total = 0;
        for (ContactInfoWrapper contact : contactsInCluster) {
            center[0] += contact.getLatitude();
            center[1] += contact.getLongitude();
            total += 1;
        }
        center[0] /= total;
        center[1] /= total;
        return center;
    }
}
