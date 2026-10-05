package com.railway.model;

public class Station {

    private int stationId;
    private String stationCode;
    private String stationName;

    public Station(int stationId, String stationCode, String stationName) {
        this.stationId = stationId;
        this.stationCode = stationCode;
        this.stationName = stationName;
    }

    public int getStationId() {
        return stationId;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getStationName() {
        return stationName;
    }
}
