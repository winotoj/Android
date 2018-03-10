package com.ipd10.q2traveldb;

import java.util.Date;

/**
 * Created by 1795661 on 1/19/2018.
 */

public class Travel {
    public Travel(int id, String name, String destination, Date departureDate) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.departureDate = departureDate;
    }

    int id;
    String name;
    String destination;
    Date departureDate;

    @Override
    public String toString() {
        return String.format("%s to %s on %s", name, destination,Database.dateFormat.format(departureDate));
    }
}
