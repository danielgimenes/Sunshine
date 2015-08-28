package app.sunshine.udacity.dgimenes.com.br.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_view_forecast_textview, weekForecast);
        ListView forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(adapter);
        return rootView;
    }
}
