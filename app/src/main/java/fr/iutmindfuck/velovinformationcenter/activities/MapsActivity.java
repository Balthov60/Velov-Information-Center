package fr.iutmindfuck.velovinformationcenter.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import fr.iutmindfuck.velovinformationcenter.R;
import fr.iutmindfuck.velovinformationcenter.data.Station;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ArrayList<Station> stations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stations = (ArrayList<Station>) getIntent().getSerializableExtra("stations");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng lyon = new LatLng(45.760, 4.8357);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lyon));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(1f));

        for (Station station : stations)
        {
            LatLng latLng = new LatLng(station.getLatitude(), station.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(station.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(GetColorForMarker(station)))
                    .snippet(getString(R.string.available_places_text, station.getAvailableBikeStands())
                            + " et " + getString(R.string.available_bikes_text, station.getAvailableBikes()))
            );
        }
    }
    private float GetColorForMarker(Station station)
    {
        float color = BitmapDescriptorFactory.HUE_GREEN;

        if (station.getAvailableBikes() <= 5)
        {
            color = BitmapDescriptorFactory.HUE_ORANGE;
        }
        else if (station.getAvailableBikeStands() <= 5)
        {
            color = BitmapDescriptorFactory.HUE_AZURE;
        }

        if (station.getAvailableBikeStands() <= 5 && station.getAvailableBikes() <= 5)
        {
            color = BitmapDescriptorFactory.HUE_RED;
        }

        return color;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
        return false;
    }

}
