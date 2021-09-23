package mapwriter.map;


public class UserPresetMarker {

    private String presetGroup;
    private String presetMarkerName;
    private int color;
    private int groupNumber;
    private int markerNumber;

    public UserPresetMarker( String presetGroup, String presetMarkerName, int color, int groupNumber, int markerNumber) {
        this.presetGroup=presetGroup;
        this.presetMarkerName = presetMarkerName;
        this.color = color;
        this.groupNumber=groupNumber;
        this.markerNumber=markerNumber;
    }

    public String getPresetGroup() { return this.presetGroup; }

    public String getPresetMarkerName() {return this.presetMarkerName; }

    public int getGroupNumber() { return this.groupNumber; }

    public int getMarkerNumber() { return this.markerNumber; }

    public int getColor() { return this.color; }

}
