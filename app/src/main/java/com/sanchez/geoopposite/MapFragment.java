package com.sanchez.geoopposite;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {

    private static final String EXTRA_COORDINATES = "com.sanchez.geoopposite.extra_coordinates";

    private double[] coordinates;

    // return a new instance of MapFragment with packaged arguments
    public static MapFragment newInstance(double[] coords) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putDoubleArray(EXTRA_COORDINATES, coords);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // retrieve arguments passed from MapActivity (that it got from MainFragment)
            coordinates = getArguments().getDoubleArray(EXTRA_COORDINATES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        return rootView;
    }
}
