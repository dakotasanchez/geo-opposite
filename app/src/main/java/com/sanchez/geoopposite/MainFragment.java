package com.sanchez.geoopposite;

/*
Dakota Sanchez
Summer 2014
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainFragment extends Fragment implements HemisphereDialogFragment.SuperListener{

    private static final String TAG = MainFragment.class.getSimpleName();

    private Button currentLocationButton;
    private Button otherLocationButton;

    private LocationManager locationManager;
    private String provider;
    private Location location = null;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        currentLocationButton = (Button)rootView.findViewById(R.id.current_location_button);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), getResources().getString(R.string.fetching_data),
                        Toast.LENGTH_SHORT).show();
                new LaunchMapTask().execute();
            }
        });

        otherLocationButton = (Button)rootView.findViewById(R.id.other_location_button);
        otherLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), OtherLocationActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }

    private void launchHemisphereDialog(double[] args) {
        HemisphereDialogFragment dialogFragment = HemisphereDialogFragment.newInstance(this, args);
        dialogFragment.show(getFragmentManager(), "dialog_get_hemisphere");
    }

    @Override
    public void onHemisphereSelection(double[] coords) {
        Intent i = new Intent(getActivity(), MapActivity.class);
        i.putExtra(MapActivity.EXTRA_COORDINATES, coords);
        // Launch map activity
        startActivity(i);
    }

    private class LaunchMapTask extends AsyncTask<Void, Void, ArrayList<Double>> {

        @Override
        protected ArrayList<Double> doInBackground(Void... voids) {
            ArrayList<Double> coords = new ArrayList<>();

            try {
                location = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                // do nothing, we'll toast soon
            }
            if(location != null) {
                coords.add(location.getLatitude());
                coords.add(location.getLongitude());
            } else {
                Toast.makeText(getActivity(), "Location unavailable", Toast.LENGTH_SHORT).show();
            }

            return coords;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            if(result != null) {
                Log.i(TAG, "Before reversal: " + result.get(0) + " " + result.get(1));
                launchHemisphereDialog(Utils.getOppositeCoordinates(result.get(0), result.get(1)));
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
            }
        }
    }
}