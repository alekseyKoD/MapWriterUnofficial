package mapwriter.gui;

import mapwriter.Mw;
import mapwriter.MwUtil;
import mapwriter.map.Marker;
import mapwriter.map.SearchMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;






public class MwGuiMarkerSlot extends GuiSlot {

    private Minecraft mc;
    private Mw mw;

    private int mouseX = 0;
    private int mouseY = 0;


    private final GuiScreen parentScreen;

    public List<Marker> markerList;
    public List<GuiButton> buttons = new ArrayList<GuiButton>();
    public List<SearchMarker> searchMarkerList = new ArrayList<SearchMarker>();
    public List<Integer> buttonId = new ArrayList<Integer>();



    public MwGuiMarkerSlot(GuiScreen parentScreen, Minecraft mc, Mw mw) {
        super(mc, parentScreen.width+60, parentScreen.height, 50, parentScreen.height - 40,24);
        //super(mc, parentScreen.width+200, parentScreen.height, 50, parentScreen.height - 40, 25);
        this.parentScreen = parentScreen;
        this.mw = mw;
        this.mc = mc;
        this.markerList = mw.markerManager.visibleMarkerList;
        updateMarkerList("");
    }

    public void updateMarkerList(String text) {
        int color;
        this.buttons.clear();
        this.buttonId.clear();
        this.searchMarkerList.clear();
        this.setShowSelectionBox(true);

        int buttonSizeX=100;

        for (int i = 0; i < this.markerList.size(); i++) {

            List targetInfo=MwUtil.getTargetInfo(
                    this.mw.playerXInt,
                    this.mw.playerZInt,
                    this.markerList.get(i).x,
                    this.markerList.get(i).z);

            String markerName=this.markerList.get(i).name;
            int markerColor=this.markerList.get(i).colour;
            String coordinates=this.markerList.get(i).x+": "+this.markerList.get(i).z;
            String compassPoint=targetInfo.get(0).toString();
            int distance2Target= Integer.parseInt(targetInfo.get(1).toString());


            if(distance2Target<=200){
                color=0xff00ff00;
            }else if(distance2Target>200 && distance2Target<=500){
                color=0xffffff00;
            }else color=0xffff0000;

            Marker marker = this.markerList.get(i);
            if ((text.equals("") || marker.name.toLowerCase().contains(text.toLowerCase())) &&
                    marker.dimension == this.mw.playerDimension) {

                this.searchMarkerList.add(new SearchMarker(markerName,
                                                            coordinates,
                                                            distance2Target,
                                                            color,
                                                            compassPoint,
                                                            markerColor ));


                this.buttons.add(new GuiButton(400 + i, 0, 0,buttonSizeX,20,""));

                this.buttonId.add(i);
            }

        }
    }

    @Override
    protected int getSize() {
        return this.buttons.size();
    }

    @Override
    protected void elementClicked(int i, boolean doubleClicked, int x, int y) {


        this.mw.markerManager.selectedMarker = this.markerList.get(this.buttonId.get(i));
        this.mw.mwGui.mapView.setViewCentreScaled(
                this.mw.markerManager.selectedMarker.x,
                this.mw.markerManager.selectedMarker.z,
                0
        );

        this.mw.mwGui.backFromMarkerSearch = true;
        this.mw.setTextureSize();
        this.mc.displayGuiScreen(this.mw.mwGui);


    }

    public int getMarkerNameFieldWidth() { return 200; }

    public int getCoordinatesFieldWidth() { return 120; }

    public int getDistanceFieldWidth() { return 50; }

    public int getCompassPointFieldWidth() { return 20;}

    public int getFullMarkerFieldWidth(){
        return  this.getMarkerNameFieldWidth()+
                this.getCoordinatesFieldWidth()+
                this.getDistanceFieldWidth()+
                this.getCompassPointFieldWidth();

    }

    public int getStartPosX() { return Math.max(this.getScrollBarX() - this.getFullMarkerFieldWidth() - 15, 0); }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        if (Mouse.isButtonDown(0) && this.getEnabled()){

            int selectedIndex = this.getSlotIndexFromScreenCoords(this.width/2,mouseY);

            if (mouseX>this.getStartPosX() && mouseX<=(this.getStartPosX()+this.getFullMarkerFieldWidth()) &&
                    mouseY >= this.top && mouseY <= this.bottom && selectedIndex>=0){

                this.elementClicked(selectedIndex, false, mouseX, mouseY);

           }


        }


        super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    protected boolean isSelected(int p_148131_1_) {
        return false;
    }

    @Override
    protected void drawBackground() {

    }

    @Override
    protected void drawSlot(int i, int x, int y, int i4, Tessellator tessellator, int i5, int i6) {

        int startPosX;
        int textShift;
        int textYShift=mc.fontRendererObj.FONT_HEIGHT/2;

        GuiButton button = buttons.get(i);
        button.xPosition = getStartPosX();
        button.yPosition = y;


        String markerName=this.searchMarkerList.get(i).getMarkerName();
        String coordinates=this.searchMarkerList.get(i).getCoordinates();
        String distance=String.valueOf(this.searchMarkerList.get(i).getDistance())+"m";
        String compassPoint=this.searchMarkerList.get(i).getCompassPoint();

        int coordinatesPixelSize=mc.fontRendererObj.getStringWidth(coordinates);
        int distancePixelsSize=mc.fontRendererObj.getStringWidth(distance);
        int compassPointPixelsSize=mc.fontRendererObj.getStringWidth(compassPoint);


        //draw Marker`s name Column, align at left border
        startPosX=this.getStartPosX();
        button.drawString(this.mc.fontRendererObj,markerName ,startPosX,y+textYShift,
                    this.mw.colorMarkerNameSearchMode==1 ? this.searchMarkerList.get(i).getStringColor() : 0xffffffff );


        //Draw coordinates column, align at right border
        startPosX+=this.getMarkerNameFieldWidth();
        textShift=this.getCoordinatesFieldWidth()-coordinatesPixelSize;
        button.drawString(this.mc.fontRendererObj,coordinates,startPosX+textShift,y+textYShift,0xffffffff );

        //Draw distance column, align at right border
        startPosX+=this.getCoordinatesFieldWidth();
        textShift=this.getDistanceFieldWidth()-distancePixelsSize;
        button.drawString(this.mc.fontRendererObj,distance,startPosX+textShift,y+textYShift,
                this.mw.colorMarkerDistanceSearchMode==1 ? this.searchMarkerList.get(i).getDistanceColor() : 0xffffffff );

        //Draw CompassPoint column, align at right border
        startPosX+=this.getDistanceFieldWidth();
        textShift=this.getCompassPointFieldWidth()-compassPointPixelsSize;
        button.drawString(this.mc.fontRendererObj,compassPoint,startPosX+textShift,y+textYShift,0xffffffff );

        //button.drawButton(this.mc, this.mouseX, this.mouseY);


    }

}


