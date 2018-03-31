package fr.iutmindfuck.velovinformationcenter.requests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import fr.iutmindfuck.velovinformationcenter.activities.MainActivity;
import fr.iutmindfuck.velovinformationcenter.data.Station;


public class DataRequest extends AsyncTask<Object, Void, String> {

    private TaskCallbackHandler callback;
    private static final String API_KEY = "0f62c15020515cad7dd04a09f7c0d1e4e35c00c9";

    public DataRequest(TaskCallbackHandler callback)
    {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onTaskStart();
    }

    @Override
    protected String doInBackground(Object[] objects)
    {
        ConnectivityManager cm = (ConnectivityManager) ((MainActivity)callback).getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Objects.requireNonNull(cm).getActiveNetworkInfo() == null) return null;

        try
        {
            URL url = new URL("https://api.jcdecaux.com/vls/v1/stations?contract=" +
                    objects[0] + "&apiKey=" + API_KEY);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK)
            {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;

                while ((line = br.readLine()) != null)
                {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String data)
    {
        if (data == null) {
            callback.onTaskCompleted(false);
            return;
        }

        ArrayList<Station> stations = new ArrayList<Station>();
        try
        {
            JSONArray array = new JSONArray(data);
            JSONObject object;

            for (int i = 0; i < array.length(); i++)
            {
                object = array.getJSONObject(i);
                stations.add(new Station(object));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        callback.onTaskCompleted(stations);
    }
}
