package mapwriter.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import mapwriter.Mw;
import mapwriter.MwUtil;
import mapwriter.map.Marker;
import mapwriter.map.SearchMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;






public class MwGuiMarkerManageSlot extends GuiSlot {

    private Minecraft mc;
    private Mw mw;

    private int mouseX = 0;
    private int mouseY = 0;
    private final GuiScreen parentScreen;
    private boolean mouseButton1 = false;



    public List<Marker> markerList;
    public List<GuiCheckBox> checkBoxes = new ArrayList<GuiCheckBox>();
    public List<SearchMarker> searchMarkerList = new ArrayList<SearchMarker>();
    public List<Integer> checkboxesId = new ArrayList<Integer>();
    public HashMap <Integer, Boolean> checkboxesEnabled= new HashMap<Integer, Boolean>();





    public MwGuiMarkerManageSlot(GuiScreen parentScreen, Minecraft mc, Mw mw) {
        super(mc, parentScreen.width+60, parentScreen.height, 50, parentScreen.height - 140,16);
        //super(mc, parentScreen.width+200, parentScreen.height, 50, parentScreen.height - 40, 25);
        this.parentScreen = parentScreen;
        this.mw = mw;
        this.mc = mc;
        this.markerList = mw.markerManager.visibleMarkerList;
        updateMarkerList("");
    }

    public void updateMarkerList(String text) {
        int color;
        int slotIndex=0;
        this.checkBoxes.clear();
        this.checkboxesId.clear();
        this.searchMarkerList.clear();
        this.checkboxesEnabled.clear();


        int buttonSizeX=15;

        for (int i = 0; i < this.markerList.size(); i++) {

            String markerName=this.markerList.get(i).name;
            int markerColor=this.markerList.get(i).colour;
            String coordinates=this.markerList.get(i).x+": "+this.markerList.get(i).z;

            Marker marker = this.markerList.get(i);
            if ((text.equals("") || marker.name.toLowerCase().contains(text.toLowerCase())) &&
                    marker.dimension == this.mw.playerDimension) {

                this.searchMarkerList.add(new SearchMarker(markerName, coordinates, markerColor));

                this.checkBoxes.add(new GuiCheckBox(400 + i, 0, 0,"",false));
//                this.checkBoxes.add(new GuiButton(400 + i, 0, 0,15,15,""));
                this.checkboxesId.add(i);
                this.checkboxesEnabled.put(slotIndex,false);
                slotIndex++;
            }

        }

    }

    @Override
    protected int getSize() {
        return this.checkBoxes.size();
    }

    @Override
    protected void elementClicked(int i, boolean doubleClicked, int x, int y) {

    }

    protected void checkBoxClicked(int index){
        this.checkBoxes.get(index).setIsChecked(!this.checkBoxes.get(index).isChecked());
        checkboxesEnabled.put(index, !checkboxesEnabled.get(index));
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

    public int getStartPosX() { return Math.max(this.getScrollBarX() - this.getFullMarkerFieldWidth() - 15, 15); }

    public int getDiffWidthSlotScrollBar(){
        //  returns the difference between the end X coordinate full markersearch string a
        //  nd start X coordinates markerslot`s scrollbar.
        return this.getScrollBarX()-this.getStartPosX()-this.getFullMarkerFieldWidth()-5;
    }

    public boolean isInsideMarkerManageSlots(int mouseX, int mouseY){

        int startXDetect=this.getStartPosX();
        int endXDetect=  this.getStartPosX()+12;

        int startYDetect=this.top;
        int endYDetect=this.bottom;
        if (mouseX> startXDetect && mouseX< endXDetect && mouseY > startYDetect &&  mouseY < endYDetect){
            return true;
        } else return false;




    }

    public String getTrimRightString(String text,int overlaps){
        int count=0;
        String trimmedText;
        int fullTextPixelSize=mc.fontRenderer.getStringWidth(text);

        do{
            trimmedText=text.substring(0,text.length()-count);
            count++;

        }while (mc.fontRenderer.getStringWidth(text)-mc.fontRenderer.getStringWidth(trimmedText)<=overlaps);

        return  trimmedText;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        if (Mouse.isButtonDown(0) && !mouseButton1){

           int selectedIndex = this.func_148124_c(this.width/2,mouseY);

            if ( this.isInsideMarkerManageSlots(mouseX,mouseY) && selectedIndex>=0){

                this.checkBoxClicked(selectedIndex);
                //this.elementClicked(selectedIndex, false, mouseX, mouseY);

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

        mouseButton1 = Mouse.isButtonDown(0);
        int startPosX;
        int textShift;
        int textYShift=0;//mc.fontRenderer.FONT_HEIGHT/2;

        GuiButton button = checkBoxes.get(i);
        button.xPosition = getStartPosX();
        button.yPosition = y;

        String markerName=this.searchMarkerList.get(i).getMarkerName();
        String coordinates=this.searchMarkerList.get(i).getCoordinates();

        int markerNamePixelSize=mc.fontRenderer.getStringWidth(markerName);
        int coordinatesPixelSize=mc.fontRenderer.getStringWidth(coordinates);

        //Draw coordinates column, align at right border
        startPosX=this.getStartPosX()+this.getMarkerNameFieldWidth()+this.getDiffWidthSlotScrollBar();
        textShift=this.getCoordinatesFieldWidth()-coordinatesPixelSize;
        button.drawString(this.mc.fontRenderer,coordinates,startPosX+textShift,y+textYShift,
                            this.searchMarkerList.get(i).getStringColor());

        //draw Marker`s name Column, align at left border. If marker`s name string longer and overlaps
        // marker coordinate`s string, trim the marker`s name string

        int overlaps=this.getStartPosX()+markerNamePixelSize-startPosX-textShift;
        overlaps=overlaps>0 ? overlaps:0;

        markerName= overlaps<=0? markerName : this.getTrimRightString(markerName,overlaps);

        button.drawString(this.mc.fontRenderer,markerName ,this.getStartPosX()+15,y+textYShift,
                             this.searchMarkerList.get(i).getStringColor());


        button.drawButton(this.mc, this.mouseX, this.mouseY);
    }


}


