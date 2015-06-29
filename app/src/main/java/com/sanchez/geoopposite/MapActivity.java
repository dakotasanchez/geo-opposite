package com.sanchez.geoopposite;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends Activity implements OnMapReadyCallback{

    public static final String EXTRA_COORDINATES = "com.sanchez.geoopposite.extra_coordinates";
    private double[] coords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getActionBar().setTitle(R.string.antipodal_point);

        // grab values passed from MainFragment
        coords = getIntent().getDoubleArrayExtra(EXTRA_COORDINATES);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng destination = new LatLng(coords[0], coords[1]);

        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 10));

        map.addMarker(new MarkerOptions().position(destination));
    }
}
