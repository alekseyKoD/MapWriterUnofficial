package mapwriter.overlay;

import java.awt.Point;
import java.util.ArrayList;

import mapwriter.Mw;
import mapwriter.api.IMwChunkOverlay;
import mapwriter.api.IMwDataProvider;
import mapwriter.map.MapView;
import mapwriter.map.mapmode.MapMode;
import net.minecraft.util.MathHelper;

public class OverlayGrid implements IMwDataProvider {

	public class ChunkOverlay implements IMwChunkOverlay{

		private Mw mw;
		Point coord;
		float borderWidth = 0.5f;
		int overlayGridSize =OverlayGrid.this.getOverlayGridSize();
		
		public ChunkOverlay(Mw mw, int x, int z) {
			this.mw = mw;
			this.coord = new Point(x, z);
		}
		
		@Override
		public Point getCoordinates() {	return this.coord; }

		@Override
		public int getColor() {	return 0x00ffffff; }

		@Override
		public float getFilling() {	return 1.0f; }

		@Override
		public boolean hasBorder() { return true; }

		@Override
		public float getBorderWidth() { return borderWidth; }

		@Override
		public int getBorderColor() {
			if (this.mw.backgroundTextureMode == 0) {
				return 0x8021140e; //0x6021140e
			} else {
				return 0xff000000;
			}
		}

		@Override
		public int getColorFromXY(int x, int z) { return 0x00ffffff;}

		@Override
		public boolean getPaintChunks() {	return false; }
		@Override
		public int getOverlayGridSize() {  return this.overlayGridSize;   }

	}
	
	@Override
	public ArrayList<IMwChunkOverlay> getChunksOverlay(Mw mw, int dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ, int gridSize) {
		int minChunkX = (MathHelper.ceiling_double_int(minX) / gridSize) - 1;
		int minChunkZ = (MathHelper.ceiling_double_int(minZ) / gridSize) - 1;
		int maxChunkX = (MathHelper.ceiling_double_int(maxX) / gridSize) + 1;
		int maxChunkZ = (MathHelper.ceiling_double_int(maxZ) / gridSize) + 1;
		int cX = (MathHelper.ceiling_double_int(centerX) / gridSize) + 1;
		int cZ = (MathHelper.ceiling_double_int(centerZ) / gridSize) + 1;
		
		int limitMinX = Math.max(minChunkX, cX - 100);
		int limitMaxX = Math.min(maxChunkX, cX + 100);
		int limitMinZ = Math.max(minChunkZ, cZ - 100);
		int limitMaxZ = Math.min(maxChunkZ, cZ + 100);
		
		ArrayList<IMwChunkOverlay> chunks = new ArrayList<IMwChunkOverlay>();
		for (int x = limitMinX; x <= limitMaxX; x++)
			for (int z = limitMinZ; z <= limitMaxZ; z++)
				chunks.add(new ChunkOverlay(mw, x, z));
				
		return chunks;
	}

	@Override
	public String getStatusString(int dim, int bX, int bY, int bZ) { return "";	}

	@Override
	public void onMiddleClick(int dim, int bX, int bZ, MapView mapview){	}

	@Override
	public void onDimensionChanged(int dimension, MapView mapview) {	}

	@Override
	public void onMapCenterChanged(double vX, double vZ, MapView mapview) {

	}

	@Override
	public void onZoomChanged(int level, MapView mapview) {

	}

	@Override
	public void onOverlayActivated(MapView mapview) {
		/*if (mapview.getZoomLevel() > -1) {
			mapview.setZoomLevel(-1);
		}*/
	}

	@Override
	public void onOverlayDeactivated(MapView mapview) {
		
	}

	@Override
	public void onDraw(MapView mapview, MapMode mapmode) {
		
	}

	@Override
	public boolean onMouseInput(MapView mapview, MapMode mapmode) {
		return false;
	}

	@Override
	public int getOverlayGridSize() {   return 16;    }

}
