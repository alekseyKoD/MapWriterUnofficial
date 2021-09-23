package mapwriter.gui;


import mapwriter.Render;
import mapwriter.map.Marker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class MwGuiColorComboBox extends GuiScreen {


    GuiScreen parentScreen;
    MwColorPallete colorPallete;
    private int posX;
    private int posY;

    private List <Integer> colorList;

    private int arrowIconX;
    private int arrowIconY;
    private int arrowIconWidth = 12;
    private int arrowIconHeight = 12;

    private int colorCellWidth=12;
    private int colorCellHeight=12;
    private int width=colorCellWidth+2;
    private int height=colorCellHeight+2;
    private int activeColor =0xffffffff;



    private int elementListSpasing=1;
    private boolean dropDownelementList=false;

    int dropDownListPosX;
    int dropDownListPosY;
    int dropDownListWidth;
    int dropDownListHeight;
    int colorCellRows=2;
    int colorCellCols;




    private ResourceLocation arrowIconTexture = new ResourceLocation(
            "mapwriter", "textures/map/arrow_combobox.png");


    public MwGuiColorComboBox(GuiScreen parentScreen, int posX, int posY, List <Integer> colorList ) {
        this.parentScreen=parentScreen;
        this.posX=posX;
        this.posY=posY;
        this.colorList=colorList;
        this.init();
    }

    public void init(){
        int colorCount=10;
        int colorRows=3;


        this.colorCellCols=this.colorList.size()/this.colorCellRows;
        if(this.colorCellCols*this.colorCellRows<this.colorList.size()){
            this.colorCellRows+=1;

        }

        this.activeColor =colorList.get(0);
        this.arrowIconX=this.posX+this.width;
        this.arrowIconY=this.posY;

        this.dropDownListPosX=this.posX-1;
        this.dropDownListPosY=this.posY+this.height;
        this.dropDownListWidth=this.colorCellCols*(this.colorCellWidth+this.elementListSpasing);
        this.dropDownListHeight=this.colorCellRows*(this.colorCellHeight+this.elementListSpasing);

   }

    private boolean isArrowClick(int mouseX,int mouseY){

        int startXDetect;
        int endXDetect;
        int startYDetect;
        int endYDetect;



        startXDetect=this.arrowIconX;
        endXDetect=startXDetect+this.arrowIconWidth;
        startYDetect = this.arrowIconY;
        endYDetect = startYDetect + this.arrowIconHeight;

        if (mouseX > startXDetect && mouseX < endXDetect && mouseY > startYDetect && mouseY < endYDetect) {
            return true;
        } else return false;


    }

    public boolean isPosInsideDropdownList(int mouseX, int mouseY) {

        int startXDetect;
        int endXDetect;
        int startYDetect;
        int endYDetect;

        startXDetect= this.dropDownListPosX;
        endXDetect=startXDetect+this.dropDownListWidth;

        startYDetect = this.dropDownListPosY;
        endYDetect = startYDetect +this.dropDownListHeight;
        if (mouseX > startXDetect && mouseX < endXDetect && mouseY > startYDetect && mouseY < endYDetect) {
            return true;
        } else return false;
    }

    public int getDropDownActiveElementIndex(int mouseX, int mouseY,String data){
        int col;
        int row;
        int index;
        col=(mouseX-this.dropDownListPosX)/(this.colorCellWidth+1);
        row=(mouseY-this.dropDownListPosY)/(this.colorCellHeight+1);
        index=this.colorList.size()/(this.colorCellRows)*row+col;

        if(data.equals("index")) {
            return index;
        }else if(data.equals("column")) {
            return col;

        }else if(data.equals("row")) {
            return row;

        }else return 0;
    }

    public int getActiveColor(){ return this.activeColor; }
    public void setActiveColor(int color){ this.activeColor=color; }

    public boolean isDropDownelementListActive(){ return this.dropDownelementList; }

    public int getWidth(){ return this.width; }

    public int getHeight(){ return this.height; }

    public int getPosX(){ return this.posX;}

    public int getPosY(){ return this.posY;}

    public List <Integer> getElementList(){ return this.colorList; }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {

        if(this.isArrowClick(mouseX,mouseY)){

            this.dropDownelementList=!this.dropDownelementList;

        }else if(this.dropDownelementList && isPosInsideDropdownList(mouseX,mouseY)){

            this.activeColor=this.colorList.get(this.getDropDownActiveElementIndex(mouseX,mouseY,"index"));
            this.dropDownelementList=false;

        }else this.dropDownelementList=false;

    }

    @Override
    protected void keyTyped(char c, int key) {


    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {


        super.drawScreen( mouseX,  mouseY,  f);
    }

    public void draw(int mouseX, int mouseY){



          drawRect(this.posX-2,this.posY-2,
                this.posX+this.colorCellWidth+this.arrowIconWidth+4,
                this.posY+this.height, 0x80000000);


        Minecraft.getMinecraft().renderEngine.bindTexture(this.arrowIconTexture);
        Render.setColour(0xffffffff);
        Render.drawTexturedRect(this.arrowIconX, this.arrowIconY,this.arrowIconWidth,
                this.arrowIconHeight, 0.0, 0.0, 1.0, 1.0);


        Render.setColour( 0xffffffff);
        Render.drawRectBorder(this.posX,this.posY, this.colorCellWidth, this.colorCellHeight,1);



        drawRect(this.posX,this.posY,this.posX+this.colorCellWidth,
                this.posY+this.colorCellHeight, this.activeColor);




        if(this.dropDownelementList){

            this.drawDropDownList(mouseX,mouseY);
            this.drawHighlightDropDownListElement(mouseX, mouseY);
        }


    }

    public void drawDropDownList(int mouseX, int mouseY){

        int posX=0;
        int posY=0;
        int countCols=0;
        int countRows=0;


        if(this.dropDownListPosY+this.dropDownListHeight>this.parentScreen.height){
            this.colorCellRows= (this.colorCellRows - 1) != 0 ? (this.colorCellRows - 1) : 1;
            this.colorCellCols= Math.max(this.colorList.size() / colorCellRows, this.colorList.size());
        }

        this.dropDownListWidth=this.colorCellCols*(this.colorCellWidth+this.elementListSpasing);
        this.dropDownListHeight=this.colorCellRows*(this.colorCellHeight+this.elementListSpasing);

        drawRect(this.dropDownListPosX,this.dropDownListPosY,this.dropDownListPosX+this.dropDownListWidth,
                this.dropDownListPosY+this.dropDownListHeight,0xff000000);
        //Render.setColour(0xffa0a0a0);
        Render.setColour(0xffffffff);
        Render.drawRectBorder(this.dropDownListPosX-1,this.dropDownListPosY-1,this.dropDownListWidth+1,this.dropDownListHeight+1,1);





        for(int i=0; i<this.colorList.size(); i++){



            if(countRows<this.colorCellRows){

                if(countCols<this.colorCellCols){
                    posX=this.dropDownListPosX+countCols*(this.colorCellWidth+this.elementListSpasing);
                    posY=this.dropDownListPosY+countRows*(this.colorCellHeight+this.elementListSpasing);
                //    drawRect(posX,posY,posX+this.colorCellWidth,posY+this.colorCellHeight, this.colorList.get(i));
                    countCols++;

                }else{
                    countRows++;
                    countCols=0;

                    posX=this.dropDownListPosX+countCols*(this.colorCellWidth+this.elementListSpasing);
                    posY=this.dropDownListPosY+countRows*(this.colorCellHeight+this.elementListSpasing);
                    drawRect(posX,posY,posX+this.colorCellWidth,posY+this.colorCellHeight, this.colorList.get(i));
                    countCols++;


                }
                drawRect(posX,posY,posX+this.colorCellWidth,posY+this.colorCellHeight, this.colorList.get(i));
           }


        }


    }

    public void drawHighlightDropDownListElement(int posX, int posY){

        int colPos=1;
        int rowPos=1;
        int highlightBoxposX;
        int highlightBoxPosY;
        int highlightBoxWidth;
        int highlightBoxHeight;

        //detect active menu item

        if (this.dropDownelementList && isPosInsideDropdownList(posX,posY)){

            colPos=this.getDropDownActiveElementIndex( posX, posY,"column");
            rowPos=this.getDropDownActiveElementIndex( posX, posY,"row");

            highlightBoxposX= this.dropDownListPosX+(colPos)*(this.colorCellWidth+this.elementListSpasing);
            highlightBoxPosY=this.dropDownListPosY+(rowPos)*(this.colorCellHeight+this.elementListSpasing);
            highlightBoxWidth=this.colorCellWidth;
            highlightBoxHeight=this.colorCellHeight;

            Render.setColour( 0xffffffff);
            Render.drawRectBorder(highlightBoxposX,highlightBoxPosY, highlightBoxWidth, highlightBoxHeight,1);

        }


    }



}
