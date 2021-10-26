package mapwriter.overlay;

import mapwriter.Mw;
import mapwriter.api.IMwChunkOverlay;
import mapwriter.api.IMwDataProvider;
import mapwriter.map.MapView;
import mapwriter.map.Marker;
import mapwriter.map.mapmode.MapMode;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.ArrayList;

public  class OverlayRegionsGrid implements IMwDataProvider {

    public class ChunkOverlay implements IMwChunkOverlay {
        private Mw mw;
        Point coord;
        float borderWidth = 0.5F;
        boolean chunkHasBorder = true;
        int overlayGridSize = OverlayRegionsGrid.this.getOverlayGridSize();

        public ChunkOverlay(Mw mw, int x, int z) {
            this.mw = mw;
            this.coord = new Point(x, z);

        }

        @Override
        public Point getCoordinates() {
            return this.coord;
        }
        @Override
        public int getColor() {
            return 0x00ffffff;
        }

        @Override
        public int getColorFromXY(int x, int z) {
            String currentGroup = this.mw.markerManager.getVisibleGroupName();

            for (Marker marker : this.mw.markerManager.markerList) {
                if(marker.groupName.equals(currentGroup) &&
                            marker.x>x && marker.x<x+ overlayGridSize &&
                            marker.z>z && marker.z<z+ overlayGridSize) {
                    String color = String.format("%1$02X", marker.colour >> 16 & 255) +
                                    String.format("%1$02X", marker.colour >> 8 & 255) +
                                    String.format("%1$02X", marker.colour & 255);
                    return 0x80000000 | Integer.parseInt(color, 16);
                }
            }
            return 0x0;
        }

        @Override
        public boolean getPaintChunks() {   return this.mw.paintChunks;}

        @Override
        public float getFilling() {
            return 1.0F;
        }
        @Override
        public boolean hasBorder() {
            return this.chunkHasBorder;
        }
        @Override
        public float getBorderWidth() {
            return this.borderWidth;
        }
        @Override
        public int getBorderColor() {
            if (this.mw.backgroundTextureMode == 0) {
                return 0x8021140e; //0x6021140e
            } else {
                return 0xff000000;
            }
        }

        @Override
        public int getOverlayGridSize() {  return this.overlayGridSize;   }
    }

    @Override
    public ArrayList<IMwChunkOverlay> getChunksOverlay(Mw mw, int dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ, int gridSize) {
        int minChunkX = MathHelper.ceiling_double_int(minX) / gridSize - 1;
        int minChunkZ = MathHelper.ceiling_double_int(minZ) / gridSize - 1;
        int maxChunkX = MathHelper.ceiling_double_int(maxX) / gridSize + 1;
        int maxChunkZ = MathHelper.ceiling_double_int(maxZ) / gridSize + 1;
        int cX = MathHelper.ceiling_double_int(centerX) / gridSize + 1;
        int cZ = MathHelper.ceiling_double_int(centerZ) / gridSize + 1;
        int limitMinX = Math.max(minChunkX, cX - gridSize);
        int limitMaxX = Math.min(maxChunkX, cX + gridSize);
        int limitMinZ = Math.max(minChunkZ, cZ - gridSize);
        int limitMaxZ = Math.min(maxChunkZ, cZ + gridSize);
        ArrayList<IMwChunkOverlay> chunks = new ArrayList();

        for(int x = limitMinX; x <= limitMaxX; ++x) {
            for(int z = limitMinZ; z <= limitMaxZ; ++z) {
                chunks.add(new OverlayRegionsGrid.ChunkOverlay(mw, x, z));
            }
        }

        return chunks;
    }

    @Override
    public String getStatusString(int dim, int bX, int bY, int bZ) {
        return "";
    }

    @Override
    public void onMiddleClick(int dim, int bX, int bZ, MapView mapview) {
    }

    @Override
    public void onDimensionChanged(int dimension, MapView mapview) {
    }

    @Override
    public void onMapCenterChanged(double vX, double vZ, MapView mapview) {
    }

    @Override
    public void onZoomChanged(int level, MapView mapview) {
    }

    @Override
    public void onOverlayActivated(MapView mapview) {
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
    public int getOverlayGridSize() {   return 512;    }
}
