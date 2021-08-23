package mapwriter.api;

import java.awt.Point;

public interface IMwChunkOverlay {
	public Point   getCoordinates();
	public int     getColor();
	public int 	   getColorFromXY(int X, int Z);
	public float   getFilling();
	public boolean hasBorder();
	public float   getBorderWidth();
	public int     getBorderColor();
	public boolean getPaintChunks();
	public int	   getOverlayGridSize();
}
