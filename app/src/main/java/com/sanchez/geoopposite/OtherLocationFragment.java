package com.sanchez.geoopposite;

/*
Dakota Sanchez
Summer 2014
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OtherLocationFragment extends Fragment {

    private static final String TAG = OtherLocationFragment.class.getSimpleName();

    // For retrieving autocomplete locations
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    // Possibly unsafe to store in var for future
    private static final String API_KEY = "AIzaSyB2Q4JgxW7hLb88fU-IKkxle-J-uxcq_0g";

    private AutoCompleteTextView cityACTextView;
    private EditText latEditText;
    private EditText longEditText;
    private Button enterButton1;
    private Button enterButton2;

    public OtherLocationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        cityACTextView = (AutoCompleteTextView)rootView.findViewById(R.id.city_AC_textview);
        cityACTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        cityACTextView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));

        cityACTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShowEnterButton().execute(enterButton1);
            }
        });

        latEditText = (EditText)rootView.findViewById(R.id.lat_edit_text);
        latEditText.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        latEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShowEnterButton().execute(enterButton2);
            }
        });

        longEditText = (EditText)rootView.findViewById(R.id.long_edit_text);
        longEditText.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        longEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShowEnterButton().execute(enterButton2);
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
                    launchMap(Utils.getOppositeCoordinates(Double.valueOf(lat), Double.valueOf(lon)));
                }
            }
        });

        return rootView;
    }

    private void launchMap(double[] args) {
        //Toast.makeText(getActivity(), "Opposite = " + args[0] + ", " + args[1], Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), getResources().getString(R.string.fetching_data), Toast.LENGTH_LONG).show();
        Intent i = new Intent(getActivity(), MapActivity.class);
        i.putExtra(MapActivity.EXTRA_COORDINATES, args);
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
                Thread.sleep(2000);
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
                launchMap(Utils.getOppositeCoordinates(result.get(0), result.get(1)));
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Provide interface to updating list of locations for AutoCompleteTextView
    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResouceId) {
            super(context, textViewResouceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        if(resultList != null) {
                            filterResults.count = resultList.size();
                        }
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    // Return ArrayList of potential location matches to input string
    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
}
