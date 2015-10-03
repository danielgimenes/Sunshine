package br.com.dgimenes.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" +
                Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        double maxTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double minTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        String shortDesc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

        String highAndLow = formatHighLows(maxTemp, minTemp);

        return Utility.formatDate(date) +
                " - " + shortDesc +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        TextView forecastView = (TextView) view.findViewById(R.id.list_view_forecast_textview);
        TextView highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        TextView lowView = (TextView) view.findViewById(R.id.list_item_low_textview);

        view.setTag(new ViewHolder(lowView, highView, forecastView, dateView, iconView));

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
            This is where we fill-in the views with the contents of the cursor.
         */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);

        String dayName = Utility.getFriendlyDayString(context,
                cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
        viewHolder.dateView.setText(dayName);

        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.forecastView.setText(forecast);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highView.setText(Utility.formatTemperature(high, isMetric));

        // TODO Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowView.setText(Utility.formatTemperature(low, isMetric));
    }

    public static class ViewHolder {
        private TextView lowView;
        private TextView highView;
        private TextView forecastView;
        private TextView dateView;
        private ImageView iconView;

        public ViewHolder(TextView lowView, TextView highView, TextView forecastView,
                          TextView dateView, ImageView iconView) {
            this.lowView = lowView;
            this.highView = highView;
            this.forecastView = forecastView;
            this.dateView = dateView;
            this.iconView = iconView;
        }
    }
}