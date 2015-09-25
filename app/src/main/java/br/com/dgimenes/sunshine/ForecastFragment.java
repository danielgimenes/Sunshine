package br.com.dgimenes.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private ArrayAdapter<String> forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] fakeForecastData = {
                "Today - Sunny - 22 / 16",
                "Tomorrow - Sunny - 21 / 17",
                "Saturday - Cloudy - 20 / 15",
                "Sunday - Sunny - 25 / 19",
                "Monday - Rainy - 20 / 12"
        };
        List<String> weekForecast = new ArrayList<>();
        weekForecast.addAll(Arrays.asList(fakeForecastData));

        forecastAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast, R.id.list_view_forecast_textview, weekForecast);
        ListView forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(forecastAdapter);
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecastData = forecastAdapter.getItem(position);
                Intent detailActivityIntent = new Intent(getActivity(), DetailActivity.class);
                detailActivityIntent.putExtra(Intent.EXTRA_TEXT, forecastData);
                startActivity(detailActivityIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        updateWeather();
        super.onStart();
    }

    private void updateWeather() {
        String location = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        new FetchWeatherTask(getActivity(), forecastAdapter).execute(location);
    }
}
