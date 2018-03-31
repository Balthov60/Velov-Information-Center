package fr.iutmindfuck.velovinformationcenter.data;


import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

public class Station implements Serializable {
    private String id;
    private String name;
    private String address;

    private double latitude;
    private double longitude;

    private Boolean havePaymentTerminal;
    private Boolean haveBonus;
    private Boolean isOpen;

    private int bikeStands;
    private int availableBikes;
    private int availableBikeStands;

    private long lastUpdate;

    public Station(JSONObject object)
    {
        this.name = object.optString("name");
        this.address = object.optString("address");

        JSONObject position = object.optJSONObject("position");
        this.latitude = position.optDouble("lat");
        this.longitude = position.optDouble("lng");

        this.havePaymentTerminal = object.optBoolean("banking");
        this.haveBonus = object.optBoolean("bonus");
        this.isOpen = (object.optString("status").equals("OPEN"));

        this.bikeStands = object.optInt("bike_stands");
        this.availableBikes = object.optInt("available_bikes");
        this.availableBikeStands = object.optInt("available_bike_stands");

        this.lastUpdate = object.optLong("last_update");

        if (name.contains(" - "))
        {
            this.id = name.split(" - ")[0];
            this.name = name.split(" - ")[1];
        }
        else if (name.contains("- "))
        {
            this.id = name.split("- ")[0];

            this.name = name.split("- ")[1];
        }
    }
    public String getDurationSinceLastUpdate()
    {
        long millis = System.currentTimeMillis() - lastUpdate;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60));

        return String.format(Locale.getDefault(), "Mise à jour il y a %d minutes et %d secondes", minute, second);
    }

    public String getId()
    {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Boolean havePaymentTerminal() {
        return havePaymentTerminal;
    }

    public void setHavePaymentTerminal(Boolean havePaymentTerminal) {
        this.havePaymentTerminal = havePaymentTerminal;
    }

    public Boolean haveBonus() {
        return haveBonus;
    }

    public void setHaveBonus(Boolean haveBonus) {
        this.haveBonus = haveBonus;
    }

    public Boolean isOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public int getBikeStands() {
        return bikeStands;
    }

    public void setBikeStands(int bikeStands) {
        this.bikeStands = bikeStands;
    }

    public int getAvailableBikes() {
        return availableBikes;
    }

    public void setAvailableBikes(int availableBikes) {
        this.availableBikes = availableBikes;
    }

    public int getAvailableBikeStands() {
        return availableBikeStands;
    }

    public void setAvailableBikeStands(int availableBikeStands) {
        this.availableBikeStands = availableBikeStands;
    }

    @Override
    public String toString() {
        return name + " " + address + " " + isOpen
                + "\n" + "(" + latitude + ", " + longitude + ")"
                + "\n" + bikeStands + " " + availableBikes + " " + availableBikeStands
                + "\n" + haveBonus + " " + havePaymentTerminal + " " + lastUpdate + " ago";
    }
}
