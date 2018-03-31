package fr.iutmindfuck.velovinformationcenter.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import fr.iutmindfuck.velovinformationcenter.R;
import fr.iutmindfuck.velovinformationcenter.data.Station;
import fr.iutmindfuck.velovinformationcenter.requests.DataRequest;
import fr.iutmindfuck.velovinformationcenter.requests.TaskCallbackHandler;
import fr.iutmindfuck.velovinformationcenter.views.StationListAdapter;

public class MainActivity extends AppCompatActivity implements TaskCallbackHandler {

    private ArrayList<Station> stations;
    private StationListAdapter adapter;

    private ImageView splashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        splashScreen = findViewById(R.id.splashScreen);

        stations = new ArrayList<>();
        adapter = new StationListAdapter(this, stations);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(adapterView.getContext());
                builder.setView(R.layout.more_info_layout);

                AlertDialog dialog = builder.create();
                dialog.show();
                setDialogContent(dialog, (Station) adapterView.getItemAtPosition(i));
            }
        });

        new DataRequest(this).execute("Lyon");
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                new DataRequest(this).execute("Lyon");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTaskStart() { }
    @Override
    public void onTaskCompleted(Object data) {
        splashScreen.setVisibility(View.GONE);

        if (data instanceof Boolean && !(boolean)data)
        {
            Toast.makeText(this, "Pas de connexion internet... Connectez vous puis "
                                            + "rafraichissez la page", Toast.LENGTH_LONG).show();
            return;
        }

        stations.clear();
        stations.addAll((ArrayList<Station>) data);
        adapter.notifyDataSetChanged();
    }

    /* Alert */

    private void setDialogContent(AlertDialog dialog, Station station) {
        ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_address)))
                .setText(getString(R.string.address_text, station.getAddress()));
        ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_name)))
                .setText(station.getName());
        ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_available_bikes)))
                .setText(getString(R.string.available_bikes_text, station.getAvailableBikes()));
        ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_available_places)))
                .setText(getString(R.string.available_places_text, station.getAvailableBikeStands()));
        ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_update)))
                .setText(station.getDurationSinceLastUpdate());

        if (!station.isOpen())
            ((ImageView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_name)))
                    .setImageResource(R.mipmap.logo_status_off);

        if (station.haveBonus())
            ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_bonus)))
                    .setText(getString(R.string.bonus_text, "OUI"));
        else
            ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_bonus)))
                    .setText(getString(R.string.bonus_text, "NON"));

        if (station.havePaymentTerminal())
            ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_payment)))
                    .setText(getString(R.string.payment_text, "OUI"));
        else
            ((TextView) Objects.requireNonNull(dialog.findViewById(R.id.more_info_payment)))
                    .setText(getString(R.string.payment_text, "NON"));
    }
}
