package com.sanchez.geoopposite;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.sanchez.geoopposite.R;

public class MapActivity extends Activity {

    public static final String EXTRA_COORDINATES = "com.sanchez.geoopposite.extra_coordinates";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // grab values passed from MainFragment
        double[] coords = getIntent().getDoubleArrayExtra(EXTRA_COORDINATES);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.containerMap);

        if(fragment == null) {
            fragment = MapFragment.newInstance(coords);
            fm.beginTransaction()
                    .add(R.id.containerMap, fragment)
                    .commit();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
