package mapwriter.map;

public class SearchMarker {
    private String markerName;
    private String coordinates;
    private int distance;
    private int distanceColor;
    private String compassPoint;
    private int stringColor;



    public SearchMarker(String markerName, String coordinates, int distance, int distanceColor,
                        String compassPoint, int stringColor) {
        this.markerName = markerName;
        this.coordinates =coordinates;
        this.distance =distance;
        this.distanceColor =distanceColor;
        this.compassPoint =compassPoint;
        this.stringColor =stringColor;
    }

    public SearchMarker(String markerName, String coordinates, int stringColor ) {
        this.markerName = markerName;
        this.coordinates =coordinates;
        this.stringColor =stringColor;

    }

    public String getMarkerName() { return markerName; }

    public String getCoordinates() { return coordinates; }

    public int getDistance() { return distance; }

    public int getDistanceColor() { return distanceColor; }

    public String getCompassPoint() { return compassPoint; }

    public int getStringColor() { return stringColor; }
}