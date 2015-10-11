package br.com.dgimenes.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private String location;

    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";
    private boolean twoPane;
    private boolean detailFragmentAlreadyCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        location = Utility.getPreferredLocation(this);
        if (findViewById(R.id.weather_detail_container) != null) {
            twoPane = true;

            if (!detailFragmentAlreadyCreated || savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, new DetailFragment(),
                                DETAIL_FRAGMENT_TAG)
                        .commit();
                detailFragmentAlreadyCreated = true;
            }
        } else {
            twoPane = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
        String newLocation = Utility.getPreferredLocation(this);
        if (location != null && !newLocation.equals(location)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);
            if (null != ff) {
                ff.onLocationChanged();
            }
            location = newLocation;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        if (id == R.id.action_prefered_location) {
            String location = Utility.getPreferredLocation(this);
            Uri locationUri = Uri.parse("geo:0,0").buildUpon()
                    .appendQueryParameter("q", location).build();
            Intent preferredLocationIntent = new Intent(Intent.ACTION_VIEW);
            preferredLocationIntent.setData(locationUri);
            if (preferredLocationIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(preferredLocationIntent);
            } else {
                Log.e(LOG_TAG, "no map app to receive the intent");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
