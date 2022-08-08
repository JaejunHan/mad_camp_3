package com.example.reappearance01;

import android.app.Application;
import android.content.Context;

public class GlobalSearchResult extends Application {
    private static Context context;



    private String fromName;
    private String toName;
    private String fromFullAddress;
    private String toFullAddress;
    private LocationLatLngEntity fromLocation;
    private LocationLatLngEntity toLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        fromName = null;
        toName = null;
        fromFullAddress = null;
        toFullAddress = null;
        fromLocation = null;
        toLocation = null;

    }
    public void setContext(Context context) {
        this.context = context;
    }
    public Context getContext() {
        return context;
    }

    public String getFromFullAddress() {
        return fromFullAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public String getToName() {
        return toName;
    }

    public String getToFullAddress() {
        return toFullAddress;
    }

    public LocationLatLngEntity getFromLocation() {
        return fromLocation;
    }

    public LocationLatLngEntity getToLocation() {
        return toLocation;
    }

    public void setFromFullAddress(String fromFullAddress) {
        this.fromFullAddress = fromFullAddress;
    }

    public void setFromLocation(LocationLatLngEntity fromLocation) {
        this.fromLocation = fromLocation;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setToFullAddress(String toFullAddress) {
        this.toFullAddress = toFullAddress;
    }

    public void setToLocation(LocationLatLngEntity toLocation) {
        this.toLocation = toLocation;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }
}
