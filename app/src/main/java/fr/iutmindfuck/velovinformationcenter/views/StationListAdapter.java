package fr.iutmindfuck.velovinformationcenter.views;


import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import fr.iutmindfuck.velovinformationcenter.R;
import fr.iutmindfuck.velovinformationcenter.data.Station;

public class StationListAdapter extends ArrayAdapter<Station> {

    private final List<Station> stations;
    private final Context context;

    public StationListAdapter(Context context, List<Station> stations) {
        super(context, 0, stations);
        this.stations = stations;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent)
    {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext())
                                 .inflate(R.layout.station_item_layout, parent, false);

        initView(convertView, position);
        return convertView;
    }

    private void initView(View convertView, final int position) {
        StationItemViewHolder viewHolder = (StationItemViewHolder) convertView.getTag();
        if(viewHolder == null) {
            viewHolder = new StationItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        final Station station = getItem(position);
        if (station == null) return;

        if (!station.isOpen())
            viewHolder.logo.setImageResource(R.mipmap.logo_status_off);

        String name = station.getName();
        if (name.contains(" - "))
            name = name.split(" - ")[1];
        if (name.contains("- "))
            name = name.split("- ")[1];
        viewHolder.name.setText(name);
        viewHolder.address.setText(station.getAddress());

        String temp = context.getString(R.string.available_bikes_text, station.getAvailableBikes());
        viewHolder.availableBikes.setText(temp);

        temp = context.getString(R.string.available_places_text, station.getAvailableBikeStands());
        viewHolder.availablePlaces.setText(temp);
    }
}
