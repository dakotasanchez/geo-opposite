package com.sanchez.geoopposite;

/*
Dakota Sanchez
Summer 2014
 */

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OtherLocationFragment extends Fragment implements
        HemisphereDialogFragment.SuperListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = OtherLocationFragment.class.getSimpleName();
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_NORTH_AMERICA = new LatLngBounds(new LatLng(18.000000, -64.000000),
            new LatLng(67.000000, -165.000000));

    private GoogleApiClient googleApiClient;
    private PlaceAutocompleteAdapter acAdapter;

    private AutoCompleteTextView cityACTextView;
    private EditText latEditText;
    private EditText longEditText;
    private Button enterButton1;
    private Button enterButton2;

    public OtherLocationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage((FragmentActivity) getActivity(), GOOGLE_API_CLIENT_ID, this)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        // TODO: Check error code and provide solution
        Toast.makeText(getActivity(), "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other_location, container, false);

        if(NavUtils.getParentActivityName(getActivity()) != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getActivity().getActionBar().setTitle(getResources().getString(R.string.title_action_bar));

        setHasOptionsMenu(true);

        acAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1,
                googleApiClient, BOUNDS_NORTH_AMERICA, null);

        cityACTextView = (AutoCompleteTextView)rootView.findViewById(R.id.city_AC_textview);
        cityACTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        cityACTextView.setAdapter(acAdapter);

        cityACTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new ShowEnterButton().execute(enterButton1);
                return false;
            }
        });

        latEditText = (EditText)rootView.findViewById(R.id.lat_edit_text);
        latEditText.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        latEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new ShowEnterButton().execute(enterButton2);
                return false;
            }
        });

        longEditText = (EditText)rootView.findViewById(R.id.long_edit_text);
        longEditText.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        longEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new ShowEnterButton().execute(enterButton2);
                return false;
            }
        });

        enterButton1 = (Button)rootView.findViewById(R.id.enter_button_1);
        enterButton1.setVisibility(View.INVISIBLE);
        enterButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(cityACTextView.getText().toString()).equals("")) {
                    new GetCoordinatesFromLocation().execute(cityACTextView.getText().toString());
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_loc_entered), Toast.LENGTH_LONG).show();
                }
            }
        });

        enterButton2 = (Button)rootView.findViewById(R.id.enter_button_2);
        enterButton2.setVisibility(View.INVISIBLE);
        enterButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lat = latEditText.getText().toString();
                String lon = longEditText.getText().toString();

                if(lat.equals("") && lon.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_coord_entered), Toast.LENGTH_LONG).show();
                } else if(lat.equals("") || lon.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.fill_both_fields), Toast.LENGTH_LONG).show();
                } else {
                    Log.i(TAG,  "Before reversal: " + Double.valueOf(lat) + " " + Double.valueOf(lon));
                    launchHemisphereDialog(Utils.getOppositeCoordinates(Double.valueOf(lat), Double.valueOf(lon)));
                }
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
        Intent i = new Intent(getActivity(), MapActivity.class);
        i.putExtra(MapActivity.EXTRA_COORDINATES, coords);
        // Launch map activity
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Show Enter button after delay
    private class ShowEnterButton extends AsyncTask<Button, Void, Button> {

        @Override
        protected Button doInBackground(Button... buttons) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
            return buttons[0];
        }

        @Override
        protected void onPostExecute(Button button) {
            if(button.getVisibility() == View.INVISIBLE) {
                button.setVisibility(View.VISIBLE);
            }
        }
    }

    // TODO Handle timeout or Geocoder service unavailable...
    private class GetCoordinatesFromLocation extends AsyncTask<String, Void, ArrayList<Double>> {

        @Override
        protected ArrayList<Double> doInBackground(String... location) {
            ArrayList<Double> returnList = new ArrayList<Double>();
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(location[0], 1);
                if(addresses.size() > 0) {
                    Address address = addresses.get(0);
                    returnList.add(address.getLatitude());
                    returnList.add(address.getLongitude());
                    return returnList;
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            if(result != null) {
                Log.i(TAG,  "Before reversal: " + result.get(0) + " " + result.get(1));
                launchHemisphereDialog(Utils.getOppositeCoordinates(result.get(0), result.get(1)));
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
            }
        }
    }
}
