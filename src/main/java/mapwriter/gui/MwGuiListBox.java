package mapwriter.gui;

import mapwriter.MwUtil;
import mapwriter.Render;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Collections;
import java.util.List;

public class MwGuiListBox extends GuiScreen{
    private FontRenderer fontRenderer;
    private GuiScreen parentElement;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private List<String> elementList;
    private int itemElementVspacing=3;
    private int itemElementTextShift=5;
    private int menuItemElementHeight;
    private boolean isSelectedElement=false;

    private int selectedElementIndex=0;

    public boolean isSelectedElement(){
        return isSelectedElement;
    }




    public MwGuiListBox(FontRenderer fontRenderer, List<String> elementList, int width) {

        this.fontRenderer=fontRenderer;
        this.elementList = elementList;
        this.width = width;
        this.init();

    }


    public boolean posWithin(int posX, int posY) {
        return (posX >= this.posX) && (posY >= this.posY) && (posX <= (this.posX + this.width)) && (posY <= (this.posY + this.height));
    }

    public int getElementsCount(){ return this.elementList.size(); }

    public int getSelectedElementIndex(){ return selectedElementIndex; }

    public void setSelectedElementIndex(int index){
        if(index>elementList.size()-1)
        {
            selectedElementIndex=elementList.size()-1;
        } else if(index<0){
            selectedElementIndex=0;
        }else selectedElementIndex=index;
    }

    public int getActiveElementIndex(int mouseX, int mouseY){

        return (mouseY-posY)/(fontRenderer.FONT_HEIGHT+itemElementVspacing);

    }

    public String getActiveElement(int index){
        if(index>this.elementList.size()-1){
            return this.elementList.get(this.elementList.size()-1);
        }
        return this.elementList.get(index);
    }

    public boolean isPosInsideList(int mouseX, int mouseY) {

        int startXDetect;
        int endXDetect;
        int startYDetect;
        int endYDetect;


        startXDetect = posX;
        endXDetect =startXDetect+ width;

        startYDetect = posY;
        endYDetect = startYDetect + height;
        return mouseX > startXDetect && mouseX < endXDetect && mouseY > startYDetect && mouseY < endYDetect;
    }

    public void init() {

        menuItemElementHeight=fontRenderer.FONT_HEIGHT;
        height=Math.max(elementList.size()*(fontRenderer.FONT_HEIGHT+itemElementVspacing),30);


    }

    private int getListWidth(){

        int maxWidthInPixel=fontRenderer.getStringWidth(elementList.get(0));
        for (String s : elementList) {
            maxWidthInPixel = Math.max(maxWidthInPixel,
                    fontRenderer.getStringWidth(s));
        }
        return maxWidthInPixel;

    }

    public void draw(int posX, int posY){
        this.posX=posX;
        this.posY=posY;

        Render.setColour(0xffffffff);
        Render.drawRectBorder(posX,posY,width,height,1);

        for(int i=0;i<elementList.size() ; i++){
            drawString(fontRenderer, MwUtil.truncatedString(elementList.get(i),this.width),
                    posX+itemElementTextShift,
                    posY+i*(menuItemElementHeight+itemElementVspacing),0xffffff);
        }
    }

    public void drawHighlightListElement(int posX, int posY){
        int highlightBoxposX;
        int highlightBoxPosY;
        int highlightBoxWidth;
        int highlightBoxHeight;
        int activeElement;


        //detect active menu item

        activeElement=this.getActiveElementIndex(posX, posY);

        highlightBoxPosY=this.posY-1+activeElement*(menuItemElementHeight+itemElementVspacing);

        highlightBoxposX =this.posX+1;
        highlightBoxWidth = this.width-2;
        highlightBoxHeight=menuItemElementHeight+itemElementVspacing-2;

        if(this.isSelectedElement){
            Render.setColour(0xffff0000);
            Render.drawRectBorder(highlightBoxposX,highlightBoxPosY,highlightBoxWidth,highlightBoxHeight,1);

        }

        drawRect(highlightBoxposX, highlightBoxPosY, highlightBoxposX+highlightBoxWidth,
                    highlightBoxPosY+highlightBoxHeight,
                    0x80ffffff);

    }

    public void drawHighlightSelectedElement(int posX, int posY){
        int highlightBoxposX;
        int highlightBoxPosY;
        int highlightBoxWidth;
        int highlightBoxHeight;


        highlightBoxPosY=this.posY-1+selectedElementIndex*(menuItemElementHeight+itemElementVspacing);

        highlightBoxposX =this.posX+1;
        highlightBoxWidth = this.width-2;
        highlightBoxHeight=menuItemElementHeight+itemElementVspacing-2;

        if(this.isSelectedElement){
            Render.setColour(0xffff0000);
            Render.drawRectBorder(highlightBoxposX,highlightBoxPosY,highlightBoxWidth,highlightBoxHeight,1);

        }

        drawRect(highlightBoxposX, highlightBoxPosY, highlightBoxposX+highlightBoxWidth,
                highlightBoxPosY+highlightBoxHeight,
                0x80ffffff);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button){

        if (isPosInsideList(mouseX, mouseY)) {
            isSelectedElement = !isSelectedElement;
            if (!isSelectedElement) {
                selectedElementIndex = 0;
            } else selectedElementIndex = getActiveElementIndex(mouseX, mouseY);
        }

    }
}
