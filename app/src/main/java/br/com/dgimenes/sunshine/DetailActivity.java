/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.dgimenes.sunshine;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.dgimenes.sunshine.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String[] FORECAST_COLUMNS = {
                // In this case the id needs to be fully qualified with a table name, since
                // the content provider joins the location & weather tables in the background
                // (both have an _id column)
                // On the one hand, that's annoying.  On the other, you can search the weather table
                // using the location set by the user, which is only in the Location table.
                // So the convenience is worth it.
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG
        };

        // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
        // must change.
        static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;
        static final int COL_LOCATION_SETTING = 5;
        static final int COL_WEATHER_CONDITION_ID = 6;
        static final int COL_COORD_LAT = 7;
        static final int COL_COORD_LONG = 8;

        private static final int DETAIL_LOADER_ID = 0;
        private final String SHARE_HASHTAG = "#SunshineApp";
        private Uri forecast;
        private ShareActionProvider shareActionProvider;
        private TextView forecastTextView;

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                forecast = Uri.parse(intent.getDataString());
                forecastTextView = (TextView) rootView.findViewById(R.id.forecast_textview);

                getActivity().getLoaderManager().initLoader(DETAIL_LOADER_ID, null,
                        new LoaderManager.LoaderCallbacks<Cursor>() {
                            @Override
                            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                                return new CursorLoader(getActivity(), forecast,
                                        FORECAST_COLUMNS,
                                        null,
                                        null,
                                        null);
                            }

                            @Override
                            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                                if (!data.moveToFirst()) {
                                    return;
                                }
                                forecastTextView.setText(convertCursorRowToUXFormat(data)
                                        + " " + SHARE_HASHTAG);
                            }

                            @Override
                            public void onLoaderReset(Loader<Cursor> loader) {
                            }
                        });
            }
            return rootView;
        }

        private String formatHighLows(double high, double low) {
            boolean isMetric = Utility.isMetric(getContext());
            String highLowStr = Utility.formatTemperature(high, isMetric) + "/" +
                    Utility.formatTemperature(low, isMetric);
            return highLowStr;
        }

        private String convertCursorRowToUXFormat(Cursor cursor) {
            // get row indices for our cursor
            double maxTemp = cursor.getDouble(COL_WEATHER_MAX_TEMP);
            double minTemp = cursor.getDouble(COL_WEATHER_MIN_TEMP);
            long date = cursor.getLong(COL_WEATHER_DATE);
            String shortDesc = cursor.getString(COL_WEATHER_DESC);

            String highAndLow = formatHighLows(maxTemp, minTemp);

            return Utility.formatDate(date) +
                    " - " + shortDesc +
                    " - " + highAndLow;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);
            MenuItem item = menu.findItem(R.id.action_share);

            shareActionProvider = ((ShareActionProvider) MenuItemCompat.getActionProvider(item));
            if (forecast != null) {
                shareActionProvider.setShareIntent(createForecastIntent());
            }
        }

        private Intent createForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, forecastTextView.getText());
            return shareIntent;
        }
    }
}
