package mapwriter.gui;

import mapwriter.Render;
import net.minecraft.client.gui.GuiScreen;
import java.util.ArrayList;
import java.util.List;

public class MwColorPallete extends GuiScreen {

    private int x;
    private int y;
    private int palleteWidth;
    private int palleteHeight;
    private String palleteLabel ="Colour";
    private int colorCellHSpacing=5;
    private int colorCellVSpacing=5;
    private int colorCellsRowCount;
    private int colorCellsColumnCount;
    private int parentScreenWidth;
    private int parentScreenStartXpos;


    private int[] colors;
    private int selectedColor;

    public List<MwColorCell> colorCells= new ArrayList <MwColorCell> ();



    public MwColorPallete(int x, int y,int palleteHeight, int[] colors, int selectedColor,
                          int parentScreenWidth,int parentScreenStartXpos ){

        this.x =x;
        this.y =y;
        this.palleteWidth=palleteHeight;
        this.palleteHeight=palleteHeight;
        this.colors=colors;
        this.selectedColor =selectedColor;
        this.parentScreenWidth=parentScreenWidth;
        this.parentScreenStartXpos=parentScreenStartXpos;
        this.colorCellsRowCount=0;
        this.colorCellsColumnCount=0;

        int colorCellXPos=this.x;
        int colorCellYPos=this.y;

        for (int i=0;i<this.colors.length; i++){


            if( (this.x+(this.colorCellsColumnCount+1)*this.palleteWidth+(this.colorCellsColumnCount+1)*colorCellHSpacing)>
                        (this.parentScreenStartXpos+this.parentScreenWidth) ){
                this.colorCellsColumnCount=0;
                this.colorCellsRowCount++;

                colorCellXPos=this.x + this.colorCellsColumnCount*(this.palleteHeight+colorCellHSpacing);
                colorCellYPos=this.y+this.colorCellsRowCount*(this.colorCellVSpacing+this.palleteHeight);

            } else {
                 colorCellXPos=this.x + this.colorCellsColumnCount*(this.palleteHeight+colorCellHSpacing);
                 colorCellYPos=this.y+this.colorCellsRowCount*(this.colorCellVSpacing+this.palleteHeight);
            }


            this.colorCells.add(new MwColorCell(i, colorCellXPos, colorCellYPos, this.palleteWidth, this.palleteHeight,
                                    this.colors[i],false));

            this.colorCellsColumnCount++;
        }

        this.setActiveCell(this.getIndexFromColor(selectedColor));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {



    }

    public int getX() { return x; }

    public int getY() { return y; }

    public String getpaletteLabel() { return palleteLabel; }

    public int getSelectedColor() { return selectedColor; }

    public int getIndexFromColor(int color){

        for(int i=0;i<this.colorCells.size();i++){
            if(this.colorCells.get(i).cellColor==color){
              return i;
            }
        }
        return 0;
    }

    public int getActiveCell() {
        for(int i=0; i<this.colorCells.size(); i++) {
            if (this.colorCells.get(i).cellActive) {
                return i ;
            }
        }
        return 0;
    }

    public void setActiveCell(int index){

        for (int i=0;i<this.colorCells.size();i++) {

            this.colorCells.get(i).setCellInacive();
        }

        this.colorCells.get(index).setCellActive();
        this.selectedColor=this.colorCells.get(index).cellColor;

    }

    public void setNextAcctiveCell(int index){

        index=index+1>this.colorCells.size()-1?0:index+1;
        setActiveCell(index);

    }

    public void setPrevAcctiveCell(int index){

        index=index-1<0?this.colorCells.size()-1:index-1;
        setActiveCell(index);

    }

    public int getColorCellsRowCount() { return colorCellsRowCount; }

    public int getColorCellsColumnCount() { return colorCellsColumnCount; }

    public int getPalleteWidth() { return palleteWidth; }

    public int getPalleteHeight() { return palleteHeight; }

    public int getColorCellVSpacing() { return colorCellVSpacing; }

    public void draw(){

        for (int i=0;i<this.colorCells.size(); i++){

            this.colorCells.get(i).draw();
        }
    }

    class MwColorCell {
        private int index;
        private int cellX;
        private int cellY;
        private int cellWidth;
        private int cellHeight;
        private int cellColor;
        private boolean cellActive;


        public MwColorCell(int index,int cellX,int cellY, int cellWidth, int cellHeight, int cellColor, boolean cellActive){
            this.index =index;
            this.cellX=cellX;
            this.cellY=cellY;
            this.cellWidth=cellWidth;
            this.cellHeight=cellHeight;
            this.cellColor=cellColor;
            this.cellActive=cellActive;
        }

        public void draw(){

          int colorCellRow=1;





            Render.setColour(this.cellColor);
            Render.drawRect(this.cellX,this.cellY, this.cellWidth, this.cellHeight);

            Render.setColour( 0xffa0a0a0);
            Render.drawRectBorder(this.cellX,this.cellY, this.cellWidth, this.cellHeight,1);

            Render.setColour( this.cellActive==true? 0xffffffff : 0x00a0a0a0);
            Render.drawRectBorder(this.cellX-1,this.cellY-1, this.cellWidth+2, this.cellHeight+2,1);
        }

        public void setCellActive(){ this.cellActive=true; }



        public void setCellInacive() { this.cellActive=false; }

        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

            if (mouseButton == 0){

                if ( mouseX>=this.cellX && mouseX<=this.cellX+this.cellWidth &&
                        mouseY>=this.cellY && mouseY<=this.cellY+this.cellWidth ) {

                   setActiveCell(this.index);
                }
            }

        }

        public void mouseDWheelScrolled(int mouseX, int mouseY, int direction) {

            if ( mouseX>=this.cellX && mouseX<=this.cellX+this.cellWidth &&
                    mouseY>=this.cellY && mouseY<=this.cellY+this.cellWidth ) {

                if (direction>0){
                    setNextAcctiveCell(getActiveCell());
                } else setPrevAcctiveCell(getActiveCell());

            }




        }

    }

}
