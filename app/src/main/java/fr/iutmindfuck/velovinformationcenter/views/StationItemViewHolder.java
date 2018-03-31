package fr.iutmindfuck.velovinformationcenter.views;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.iutmindfuck.velovinformationcenter.R;

class StationItemViewHolder
{
    ImageView logo;
    TextView name;
    TextView address;
    TextView availableBikes;
    TextView availablePlaces;

    StationItemViewHolder(View view) {
        this.logo = view.findViewById(R.id.station_item_logo);
        this.name = view.findViewById(R.id.station_item_name);
        this.address = view.findViewById(R.id.station_item_address);
        this.availableBikes = view.findViewById(R.id.station_item_available_bikes);
        this.availablePlaces = view.findViewById(R.id.station_item_available_places);
    }
}
