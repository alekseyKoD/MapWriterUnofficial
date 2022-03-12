package mapwriter.map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import mapwriter.Render;
import mapwriter.map.mapmode.MapMode;


public class Marker {

	private String markerName;
	private int groupIndex;
	private int posX;
	private int posY;
	private int posZ;
	private int dimension;
	private int colour;
	
	public Point.Double screenPos = new Point.Double(0, 0);


	private static final  int[] colours;

	static{
		colours = new int[]{
				0xff0000, 0x00ff00, 0x0000ff, 0xffff00, 0xff00ff, 0x00ffff,
				0xff8000, 0x8000ff, 0x964b00, 0x006400};
	}

	// static so that current index is shared between all markers
    private static int colourIndex = 0;
	
	public Marker(String name, int groupIndex, int posX, int posY, int posZ, int dimension, int colour) {
		this.markerName = name;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.dimension = dimension;
		this.colour = colour;
		this.groupIndex = groupIndex;
	}

	public Marker(List<String> arrayData){
		this.markerName = arrayData.get(0); //marker`s name
		this.groupIndex = Integer.parseInt(arrayData.get(1)); //marker`s group index
		this.posX = Integer.parseInt(arrayData.get(2)); //marker`s X coordinates
		this.posY = Integer.parseInt(arrayData.get(3)); //marker`s Y coordinate
		this.posZ = Integer.parseInt(arrayData.get(4)); //marker`s Z coordinate
		this.dimension = Integer.parseInt(arrayData.get(5)); //marker`s dimension id
		this.colour = Integer.parseInt(arrayData.get(6)); //marker`s color

	}

	public String getMarkerName(){ return this.markerName;}
	public void setMarkerName(String name) { this.markerName = name; }

	public int getGroupIndex(){return this.groupIndex; }
	public void setGroupIndex(Integer newGroupIndex){ this.groupIndex=newGroupIndex; }

	public int getPosX() { return posX; }
	public void setPosX(int posX) { this.posX = posX; }

	public int getPosY() { return posY;	}
	public void setPosY(int posY) { this.posY = posY; }

	public int getPosZ() { return posZ;	}
	public void setPosZ(int posZ) { this.posZ = posZ; }

	public int getDimension() {	return dimension; }
	public void setDimension(int dimension) { this.dimension = dimension; }

	public int getColour() { return colour;	}
	public void setColour(int colour) { this.colour = colour; }

	public int getColourIndex() { return colourIndex; }

	public List<String> convertMarkerToArray(Marker marker){
		List<String> markerL=new ArrayList<String>();
		markerL.add(0,marker.getMarkerName()); //marker Name
		markerL.add(1,String.valueOf(marker.getGroupIndex())); //marker`s group index
		markerL.add(2,String.valueOf(marker.getPosX()) ); // marker`s X coordinates
		markerL.add(3,String.valueOf(marker.getPosY()) ); // marker`s Y coordinates
		markerL.add(4,String.valueOf(marker.getPosZ()) ); // marker`s Z coordinates
		markerL.add(5,String.valueOf(marker.getDimension()) ); // marker`s dimension id
		markerL.add(6,String.valueOf(marker.getColour()) ); // marker`s color
		return markerL;
	}

	/*
	public String getString() {
		return String.format("%s %s (%d, %d, %d) %d %06x",
				this.name, this.groupName, this.posX, this.posY, this.posZ, this.dimension, this.colour & 0xffffff);
	}
	*/
	public static int getCurrentColour() {
    	return 0xff000000 | colours[colourIndex];
    }

	public static int[] getColours(){return colours; }
	
    public void getNextcolour(MarkerManager markerManager, Marker marker) {
    	colourIndex = (getIndexFromColor(marker.colour)+ 1) % colours.length;
		this.colour = getCurrentColour();
		markerManager.selectedColor=this.colour;
	}
    
    public void getPrevColour(MarkerManager markerManager, Marker marker) {

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
		Point.Double p = mapMode.getClampedScreenXY(mapView, this.posX * scale, this.posZ * scale);
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
			return (markerName.equals(m.markerName) ) &&
					(groupIndex == m.groupIndex) &&
					(posX == m.posX) &&
					(posY == m.posY) &&
					(colour==m.colour) &&
					(posZ == m.posZ) &&
					(dimension == m.dimension);
		}
		return false;
	}


}