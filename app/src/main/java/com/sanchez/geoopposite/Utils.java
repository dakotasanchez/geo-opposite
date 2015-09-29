package com.sanchez.geoopposite;

/*
Dakota Sanchez
Summer 2014
 */

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;

public class Utils {

    private static NumberFormat formatter = NumberFormat.getInstance();

    public static double[] getOppositeCoordinates(double lat, double lon) {
        double newLon = lon < 0.0 ? lon + 180.0 : lon - 180.0;
        return new double[] { -1.0 * lat , truncate(newLon) };
    }

    // Remove some precision to have consistent precision throughout app
    public static double truncate(double in) {
        formatter.setMinimumFractionDigits(7);
        formatter.setMaximumFractionDigits(7);
        double ret = in;
        try {
            ret = formatter.parse(formatter.format(in)).doubleValue();
        } catch (ParseException e) {
            Log.e("G.O. Utils", e.getMessage());
        }
        return ret;
    }
}
