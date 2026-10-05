package com.railway.model;

public class Route {

    private int routeId;
    private String routeName;
    private int sourceStationId;
    private int destinationStationId;
    private String sourceStation;
    private String destinationStation;

    public Route(
            int routeId,
            String routeName,
            int sourceStationId,
            int destinationStationId,
            String sourceStation,
            String destinationStation) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.sourceStationId = sourceStationId;
        this.destinationStationId = destinationStationId;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
    }

    public Route(int routeId, String routeName) {
        this(routeId, routeName, 0, 0, "", "");
    }

    public int getRouteId() {
        return routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public int getSourceStationId() {
        return sourceStationId;
    }

    public int getDestinationStationId() {
        return destinationStationId;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }
}
