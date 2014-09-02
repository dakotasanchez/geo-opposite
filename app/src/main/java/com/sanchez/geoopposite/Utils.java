package com.sanchez.geoopposite;

/*
Dakota Sanchez
Summer 2014
 */

import java.text.DecimalFormat;

public class Utils {

    public static double[] getOppositeCoordinates(double lat, double lon) {
        double newLon = lon < 0.0 ? lon + 180.0 : lon - 180.0;
        return new double[] { -1.0 * lat , truncate(newLon) };
    }

    // Remove some precision to have consistent precision throughout app
    public static double truncate(double in) {
        return Double.valueOf(new DecimalFormat("#.#######").format(in));
    }

}
