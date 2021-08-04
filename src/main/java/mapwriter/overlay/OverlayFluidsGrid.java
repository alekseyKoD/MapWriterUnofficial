package mapwriter.overlay;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import mapwriter.Mw;
import mapwriter.api.IMwChunkOverlay;
import mapwriter.api.IMwDataProvider;
import mapwriter.map.MapView;
import mapwriter.map.Marker;
import mapwriter.map.mapmode.MapMode;
import net.minecraft.util.MathHelper;

public class OverlayFluidsGrid implements IMwDataProvider {
    public OverlayFluidsGrid() {
    }

    public ArrayList<IMwChunkOverlay> getChunksOverlay(Mw mw, int dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ) {
        int minChunkX = MathHelper.ceiling_double_int(minX) / 96 - 1;
        int minChunkZ = MathHelper.ceiling_double_int(minZ) / 96 - 1;
        int maxChunkX = MathHelper.ceiling_double_int(maxX) / 96 + 1;
        int maxChunkZ = MathHelper.ceiling_double_int(maxZ) / 96 + 1;
        int cX = MathHelper.ceiling_double_int(centerX) / 96 + 1;
        int cZ = MathHelper.ceiling_double_int(centerZ) / 96 + 1;
        int limitMinX = Math.max(minChunkX, cX - 96);
        int limitMaxX = Math.min(maxChunkX, cX + 96);
        int limitMinZ = Math.max(minChunkZ, cZ - 96);
        int limitMaxZ = Math.min(maxChunkZ, cZ + 96);
        ArrayList<IMwChunkOverlay> chunks = new ArrayList();

        for(int x = limitMinX; x <= limitMaxX; ++x) {
            for(int z = limitMinZ; z <= limitMaxZ; ++z) {
                chunks.add(new OverlayFluidsGrid.ChunkOverlay(mw, x, z));
            }
        }

        return chunks;
    }

    public String getStatusString(int dim, int bX, int bY, int bZ) {
        return "";
    }

    public void onMiddleClick(int dim, int bX, int bZ, MapView mapview) {
    }

    public void onDimensionChanged(int dimension, MapView mapview) {
    }

    public void onMapCenterChanged(double vX, double vZ, MapView mapview) {
    }

    public void onZoomChanged(int level, MapView mapview) {
    }

    public void onOverlayActivated(MapView mapview) {
    }

    public void onOverlayDeactivated(MapView mapview) {
    }

    public void onDraw(MapView mapview, MapMode mapmode) {
    }

    public boolean onMouseInput(MapView mapview, MapMode mapmode) {
        return false;
    }

    public class ChunkOverlay implements IMwChunkOverlay {
        private Mw mw;
        Point coord;
        float borderWidth = 0.5F;
        boolean ChunkHasBorder = true;

        public ChunkOverlay(Mw mw, int x, int z) {
            this.mw = mw;
            this.coord = new Point(x, z);
        }

        public Point getCoordinates() {
            return this.coord;
        }

        public int getColor() {
            return 16777215;
        }

        public int getColorFromXY(int X, int Z) {
            String CurrentGroup = this.mw.markerManager.getVisibleGroupName();
            Iterator var4 = this.mw.markerManager.markerList.iterator();

            Marker marker;
            do {
                if (!var4.hasNext()) {
                    return 0;
                }

                marker = (Marker)var4.next();
            } while(marker.x <= X || marker.x >= X + 96 || !marker.groupName.equals(CurrentGroup) || marker.z <= Z || marker.z >= Z + 96);

            String color = String.format("%1$02X", marker.colour >> 16 & 255) + String.format("%1$02X", marker.colour >> 8 & 255) + String.format("%1$02X", marker.colour & 255);
            return -2147483648 | Integer.parseInt(color, 16);
        }

        public boolean getPaintChunks() {   return this.mw.paintChunks;}

        public float getFilling() {
            return 1.0F;
        }

        public boolean hasBorder() {
            return this.ChunkHasBorder;
        }

        public float getBorderWidth() {
            return this.borderWidth;
        }

        public int getBorderColor() {
            return this.mw.backgroundTextureMode == 0 ? -2145315826 : -16777216;
        }
    }
}
