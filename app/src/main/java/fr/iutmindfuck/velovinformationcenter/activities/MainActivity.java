package fr.iutmindfuck.velovinformationcenter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import fr.iutmindfuck.velovinformationcenter.R;
import fr.iutmindfuck.velovinformationcenter.data.Station;
import fr.iutmindfuck.velovinformationcenter.requests.DataRequest;
import fr.iutmindfuck.velovinformationcenter.requests.TaskCallbackHandler;
import fr.iutmindfuck.velovinformationcenter.views.StationListAdapter;

public class MainActivity extends AppCompatActivity implements TaskCallbackHandler {

    private ArrayList<Station> stations;
    private ArrayList<Station> favoriteStations;
    private StationListAdapter basicAdapter;
    private StationListAdapter favoriteAdapter;
    private ListView favoriteList;
    private ListView basicList;

    private ImageView splashScreen;
    private View decorView;
    private ActionBar actionBar;

    private Boolean splashScreenActive;
    private Boolean splashScreenMinimumTimeOver;
    private Boolean requestEnded = false;

    private Boolean dataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullscreen();
        setList();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (stations.isEmpty()) return;
        File file = new File(getFilesDir(), "stations.save");

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(stations);
            oos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /* Initialisation */

    private void fullscreen()
    {
        splashScreen = findViewById(R.id.splashScreen);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();

        splashScreenActive = true;
        splashScreenMinimumTimeOver = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashScreenMinimumTimeOver = true;
                if (requestEnded)
                    leaveSplashScreen();
            }
        }, 2000);
    }
    private void setList()
    {
        favoriteList = findViewById(R.id.listViewFavorite);
        basicList = findViewById(R.id.listView);

        favoriteStations = new ArrayList<>();
        stations = new ArrayList<>();

        favoriteAdapter = new StationListAdapter(this, favoriteStations);
        basicAdapter = new StationListAdapter(this, stations);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(adapterView.getContext());
                builder.setView(R.layout.more_info_layout);

                AlertDialog dialog = builder.create();
                dialog.show();
                setDialogContent(dialog, (Station) adapterView.getItemAtPosition(i));
            }
        };

        registerForContextMenu(basicList);
        basicList.setAdapter(basicAdapter);
        basicList.setOnItemClickListener(listener);

        registerForContextMenu(favoriteList);
        favoriteList.setAdapter(favoriteAdapter);
        favoriteList.setOnItemClickListener(listener);

        requestEnded = false;
        new DataRequest(this).execute("Lyon");
    }
    private void leaveSplashScreen()
    {
        splashScreen.setVisibility(View.GONE);
        decorView.setSystemUiVisibility(View.VISIBLE);
        actionBar.show();

        splashScreenActive = false;
    }

    /* Menu */

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

            case R.id.action_map:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("stations", stations);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
        if (v.getId() == R.id.listViewFavorite) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.favorite_menu_list, menu);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;

        switch(item.getItemId()) {
            case R.id.action_favorite:
                addItemToFavorite(stations.get(index).getId());
                Toast.makeText(this, "Ajouté au favoris !", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_remove_favorite:
                removeItemFromFavorite(favoriteStations.get(index).getId());
                Toast.makeText(this, "Supprimé des favoris !", Toast.LENGTH_LONG).show();
                updateFavoriteList();
                return true;

            case R.id.action_navigate:
                Toast.makeText(this, "navigation", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /* Shared Preferences */

    public void addItemToFavorite(String id)
    {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> favorites = getFavoritesStationsID();
        if (favorites.contains(id)) return;

        favorites.add(id);
        editor.putStringSet("Lyon", favorites);
        editor.apply();
    }
    public void removeItemFromFavorite(String id)
    {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> favorites = getFavoritesStationsID();
        if (!favorites.contains(id)) return;

        favorites.remove(id);
        editor.putStringSet("Lyon", favorites);
        editor.apply();
    }
    public Set<String> getFavoritesStationsID()
    {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet("Lyon",new HashSet<String>());
    }

    /* Task */

    @Override
    public void onTaskStart() { }
    @Override
    public void onTaskCompleted(Object data) {
        requestEnded = true;

        if (data instanceof Boolean && !(boolean)data)
        {
            Toast.makeText(this, "Pas de connexion internet... Connectez vous puis "
                                            + "rafraichissez la page", Toast.LENGTH_LONG).show();

            if (!dataLoaded) useOfflineData();
        }
        else
        {
            if (!splashScreenActive) Toast.makeText(this, "Données mise a jour", Toast.LENGTH_LONG).show();
            dataLoaded = true;
            stations.clear();
            stations.addAll((ArrayList<Station>) data);
        }

        basicAdapter.notifyDataSetChanged();
        updateFavoriteList();

        if (splashScreenActive && splashScreenMinimumTimeOver) leaveSplashScreen();
    }

    private void useOfflineData() {
        File file = new File(getFilesDir(), "stations.save");
        if (!file.exists()) return;

        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            stations.clear();
            stations.addAll((ArrayList<Station>) ois.readObject());
            ois.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    private void updateFavoriteList() {
        favoriteStations.clear();
        Set<String> favoriteIds = getFavoritesStationsID();

        for (Station station : stations)
            if (favoriteIds.contains(station.getId()))
                favoriteStations.add(station);

        favoriteAdapter.notifyDataSetChanged();
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

    /* Interactions */

    public void displayAllStations(View view) {
        favoriteList.setVisibility(View.GONE);
        basicList.setVisibility(View.VISIBLE);
    }
    public void displayMyStations(View view) {
        favoriteList.setVisibility(View.VISIBLE);
        basicList.setVisibility(View.GONE);

        updateFavoriteList();
    }
}
