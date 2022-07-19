package com.example.reappearance01;

public class PathSavedData {
    public String fromName;
    public String fromFullAddress;
    public LocationLatLngEntity fromLocation;
    public String toName;
    public String toFullAddress;
    public LocationLatLngEntity toLocation;

    public PathSavedData(String fromName, String fromFullAddress, LocationLatLngEntity fromLocation, String toName, String toFullAddress, LocationLatLngEntity toLocation) {
        this.fromName = fromName;
        this.fromFullAddress = fromFullAddress;
        this.fromLocation = fromLocation;
        this.toName = toName;
        this.toFullAddress = toFullAddress;
        this.toLocation = toLocation;
    }
}
