package com.sanchez.geoopposite;

/*
Dakota Sanchez
Summer 2014
 */

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainFragment extends Fragment implements HemisphereDialogFragment.SuperListener{

    private static final String TAG = MainFragment.class.getSimpleName();

    private Button currentLocationButton;
    private Button otherLocationButton;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        currentLocationButton = (Button)rootView.findViewById(R.id.current_location_button);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LaunchMapTask().execute();
                currentLocationButton.setEnabled(false);
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
        Toast.makeText(getActivity(), getResources().getString(R.string.fetching_data), Toast.LENGTH_SHORT).show();

        HemisphereDialogFragment dialogFragment = HemisphereDialogFragment.newInstance(this, args);
        dialogFragment.show(getFragmentManager(), "dialog_get_hemisphere");
    }

    @Override
    public void onHemisphereSelection(double[] coords) {
        Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
    }

    private class LaunchMapTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // TODO Get device location
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Testing
            launchHemisphereDialog(new double[] { 0.0, 0.0});
            // TODO Launch new activity
        }
    }
}