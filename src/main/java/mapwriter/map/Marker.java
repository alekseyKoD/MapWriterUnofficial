package mapwriter.map;

import java.awt.Point;
import mapwriter.Render;
import mapwriter.map.mapmode.MapMode;
import mapwriter.map.MarkerManager;


public class Marker {
	public String name;
	public String groupName;
	public int x;
	public int y;
	public int z;
	public int dimension;
	public int colour;
	
	public Point.Double screenPos = new Point.Double(0, 0);


	private static int[] colours = new int[] {
    		0xff0000, 0x00ff00, 0x0000ff, 0xffff00, 0xff00ff, 0x00ffff,
    		0xff8000, 0x8000ff,0x964b00,0x006400};

	// static so that current index is shared between all markers
    private static int colourIndex = 0;
	
	public Marker(String name, String groupName, int x, int y, int z, int dimension, int colour) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
		this.colour = colour;
		this.groupName = groupName;
	}

	public void setMarkerName(String name) { this.name = name; }

	public void setGroupName(String newGroupName){ this.groupName=newGroupName; }

	public void setCoordX(int x) { this.x = x; }

	public void setCoordY(int y) { this.y = y; }

	public void setCoordZ(int z) { this.z = z; }

	public void setDimension(int dimension) { this.dimension = dimension; }

	public void setColour(int colour) { this.colour = colour; }

	public String getString() {
		return String.format("%s %s (%d, %d, %d) %d %06x",
				this.name, this.groupName, this.x, this.y, this.z, this.dimension, this.colour & 0xffffff);
	}
	
	public static int getCurrentColour() {
    	return 0xff000000 | colours[colourIndex];
    }

	public static int[] getColours(){return colours; }
	
    public void colourNext(MarkerManager markerManager, Marker marker) {
    	colourIndex = (getIndexFromColor(marker.colour)+ 1) % colours.length;
		this.colour = getCurrentColour();
		markerManager.selectedColor=this.colour;


    }
    
    public void colourPrev(MarkerManager markerManager, Marker marker) {

    	colourIndex = (getIndexFromColor(marker.colour) + colours.length - 1) % colours.length;
		this.colour = getCurrentColour();
		markerManager.selectedColor=this.colour;

    }

	public int getIndexFromColor(int color){

		for(int i=0;i<colours.length;i++){
			if((0xff000000 |colours[i]) == color){
				return i;
			}
		}
		return 0;
	}




    public void draw(MapMode mapMode, MapView mapView, int borderColour) {
		double scale = mapView.getDimensionScaling(this.dimension);
		Point.Double p = mapMode.getClampedScreenXY(mapView, this.x * scale, this.z * scale);
		this.screenPos.setLocation(p.x + mapMode.xTranslation, p.y + mapMode.yTranslation);
		
		// draw a coloured rectangle centered on the calculated (x, y)
		double mSize = mapMode.markerSize;
		double halfMSize = mapMode.markerSize / 2.0;
		Render.setColour(borderColour);
		Render.drawRect(p.x - halfMSize, p.y - halfMSize, mSize, mSize);
		Render.setColour(this.colour);
		Render.drawRect(p.x - halfMSize + 0.5, p.y - halfMSize + 0.5, mSize - 1.0, mSize - 1.0);
	}

	// arraylist.contains was producing unexpected results in some situations
	// rather than figure out why i'll just control how two markers are compared
	@Override
	public boolean equals(final Object o) {
		if (this == o) { return true; }
		if (o instanceof Marker) {
			Marker m = (Marker) o;
			return (name == m.name) && (groupName == m.groupName) && (x == m.x) && (y == m.y) && (z == m.z) && (dimension == m.dimension);
		}
		return false;
	}


}