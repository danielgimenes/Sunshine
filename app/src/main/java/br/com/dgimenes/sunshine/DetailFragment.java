package br.com.dgimenes.sunshine;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.dgimenes.sunshine.data.WeatherContract;

public class DetailFragment extends Fragment {

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
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
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
    static final int COL_HUMIDITY = 9;
    static final int COL_PRESSURE = 10;
    static final int COL_WIND_SPEED = 11;
    static final int COL_DEGREES = 12;

    private static final int DETAIL_LOADER_ID = 0;
    private final String SHARE_HASHTAG = "#SunshineApp";
    private Uri forecast;
    private ShareActionProvider shareActionProvider;

    private TextView minTextView;
    private TextView maxTextView;
    private TextView forecastTextView;
    private TextView friendlyDateTextView;
    private TextView dateTextView;
    private TextView humidityTextView;
    private TextView windTextView;
    private TextView pressureTextView;
    private ImageView forecastIconTextView;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            forecast = Uri.parse(intent.getDataString());

            minTextView = (TextView) rootView.findViewById(R.id.min_temp_view);
            maxTextView = (TextView) rootView.findViewById(R.id.max_temp_view);
            forecastTextView = (TextView) rootView.findViewById(R.id.forecast_view);
            friendlyDateTextView = (TextView) rootView.findViewById(R.id.friendly_date_view);
            dateTextView = (TextView) rootView.findViewById(R.id.date_view);
            humidityTextView = (TextView) rootView.findViewById(R.id.humidity_view);
            windTextView = (TextView) rootView.findViewById(R.id.wind_view);
            pressureTextView = (TextView) rootView.findViewById(R.id.pressure_view);
            forecastIconTextView = (ImageView) rootView.findViewById(R.id.forecast_icon);

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
                        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                            if (!cursor.moveToFirst()) {
                                return;
                            }
                            Context context = DetailFragment.this.getActivity();

                            friendlyDateTextView.setText(Utility.getDayName(context,
                                    cursor.getLong(COL_WEATHER_DATE)));
                            dateTextView.setText(Utility.getFormattedMonthDay(context,
                                    cursor.getLong(COL_WEATHER_DATE)));

                            boolean isMetric = PreferenceManager
                                    .getDefaultSharedPreferences(context)
                                    .getString(context.getString(R.string.pref_units_key),
                                            context.getString(R.string.pref_default_unit))
                                    .equals(context.getString(R.string.pref_units_metric));
                            maxTextView.setText(Utility.formatTemperature(context,
                                    cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric));
                            minTextView.setText(Utility.formatTemperature(context,
                                    cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric));

                            humidityTextView.setText(
                                    Double.valueOf(cursor.getDouble(COL_HUMIDITY)).toString()
                                            + "%");

                            windTextView.setText(Utility.getFormattedWind(context,
                                    cursor.getLong(COL_WIND_SPEED),
                                    cursor.getLong(COL_DEGREES)));

                            pressureTextView.setText(
                                    Double.valueOf(cursor.getDouble(COL_PRESSURE)).toString());

                            forecastIconTextView.setImageDrawable(getResources().getDrawable(
                                    R.mipmap.ic_launcher));
                            forecastTextView.setText(cursor.getString(COL_WEATHER_DESC));
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
        String highLowStr = Utility.formatTemperature(getActivity(), high, isMetric) + "/" +
                Utility.formatTemperature(getActivity(), low, isMetric);
        return highLowStr;
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