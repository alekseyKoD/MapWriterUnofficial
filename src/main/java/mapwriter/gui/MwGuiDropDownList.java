package mapwriter.gui;

import mapwriter.Mw;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

/*
Еhis class allows you to implement a dropdown text list that can be used as menu items, or as a selection from a list.
In the selection mode, the mode of adding to the list by typing on the keyboard with the subsequent
 saving of the typed text as an element of the list can be switched on.

 */
public class MwGuiDropDownList extends GuiScreen {

    private FontRenderer fontRenderer;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private List<String> elementList;
    private int elementIndex;
    private String menuItemName;
    private String menuID;
    private String displayedMenuItemName;
    private boolean showLabel;
    private boolean isElementMenuClickable;
    private int dropDownListPosX;
    private int dropDownListPosY;
    private int dropDownListWidth;
    private int dropDownListHeight;
    private int menuItemElementVspacing=1;
    private int menuFirstItemElementVshift=3;
    private int menuItemElementTextShift=5;
    private int menuItemElementHeight;


    public MwGuiDropDownList(FontRenderer fontRenderer,String menuID, String menuItemName, List<String> elementList,  boolean isElementMenuClickable) {
       // this.posX = posX;
       // this.posY = posY;
        this.fontRenderer=fontRenderer;
        this.width = 0;
        this.isElementMenuClickable=isElementMenuClickable;
        this.menuID=menuID;
        this.menuItemName = menuItemName;
        this.elementList = elementList;
        this.showLabel=false;
    }

    public MwGuiDropDownList(FontRenderer fontRenderer, String menuID, String menuItemName, List<String> elementList, boolean isElementMenuClickable,
                             boolean showLabel) {
        this.fontRenderer=fontRenderer;
        this.width = 0;
        this.isElementMenuClickable=isElementMenuClickable;
        this.showLabel=showLabel;
        this.menuID=menuID;
        this.menuItemName = menuItemName;
        this.elementList = elementList;
    }

    public String getMenuID(){return  this.menuID; }

    public void addMenuItem(String menuItemName) {
        this.elementList.add(menuItemName);
    }
    public boolean posWithin(int posX, int posY) {
        return (posX >= this.posX) && (posY >= this.posY) && (posX <= (this.posX + this.width)) && (posY <= (this.posY + this.height));
    }
    public boolean isDropDownElements() {
        if (this.elementList.size() > 0) {
            return true;
        } else return false;
    }

    public boolean isElementMenuClickable(){ return this.isElementMenuClickable; }

    public boolean isShowLabel(){ return this.showLabel; }

    public int getDropDownListElementsCount(){ return this.elementList.size(); }

    public int getDropDownActiveElementIndex(MwGuiDropDownList parentElement, int mouseX, int mouseY){

        return (mouseY-parentElement.dropDownListPosY)/(parentElement.fontRenderer.FONT_HEIGHT+1);

    }

    public String getDropDownActiveElement(int index){ return this.elementList.get(index); }

    public boolean isPosInsideDropdownList(MwGuiDropDownList parentElement,int mouseX, int mouseY) {

        int startXDetect;
        int endXDetect;
        int startYDetect;
        int endYDetect;


        if(parentElement.showLabel){
            startXDetect= parentElement.posX+fontRenderer.getStringWidth(parentElement.menuItemName+": ");
            endXDetect=startXDetect+this.dropDownListWidth;

        }else {
            startXDetect = parentElement.posX;
            endXDetect =startXDetect+ parentElement.dropDownListWidth;
        }



        startYDetect = parentElement.dropDownListPosY + parentElement.menuFirstItemElementVshift;
        endYDetect = startYDetect + parentElement.dropDownListHeight;
        if (mouseX > startXDetect && mouseX < endXDetect && mouseY > startYDetect && mouseY < endYDetect) {
            return true;
        } else return false;
    }

    public void init() {

        if(this.isShowLabel()){
            if(this.getDropDownListElementsCount()>0){
                this.displayedMenuItemName=this.menuItemName+": "+ this.elementList.get(0);
            }else this.displayedMenuItemName=this.menuItemName+":";
        }else displayedMenuItemName=this.menuItemName;

        this.menuItemElementHeight=this.fontRenderer.FONT_HEIGHT;
        this.height=this.fontRenderer.FONT_HEIGHT+4;

        if(isDropDownElements()){
            int maxWidthInPixel=this.fontRenderer.getStringWidth(this.elementList.get(0));
            for(int i=0;i<this.elementList.size(); i++) {
                maxWidthInPixel = Math.max( maxWidthInPixel,
                                            this.fontRenderer.getStringWidth(this.elementList.get(i)));
            }
            this.dropDownListWidth=maxWidthInPixel+this.menuItemElementTextShift+4;
        }

        this.dropDownListHeight=this.elementList.size()*(this.fontRenderer.FONT_HEIGHT+this.menuItemElementVspacing);


    }

    public String getMenuItemName() { return displayedMenuItemName; }

    public void setDropDownListPosY(int dropDownListPosY) { this.dropDownListPosY = dropDownListPosY; }


    public void setCurrentMenuItemName(String itemName) {
        if(this.isShowLabel()){
            if(this.getDropDownListElementsCount()>0){
                this.displayedMenuItemName=this.menuItemName+": "+ itemName;
            }else this.displayedMenuItemName=this.menuItemName+":";
        }

    }

    public void draw(int posX, int posY, String menuItemName){
        this.posX=posX;
        this.posY=posY;
        this.width=this.fontRenderer.getStringWidth(menuItemName)+4;

        drawRect(this.posX, this.posY, this.posX + this.width, this.posY + this.height, 0x80000000);
        drawString(this.fontRenderer, displayedMenuItemName, this.posX + 2, this.posY + 2, 0xffffff);


    }
    public void drawHighlightMainMenuElement(MwGuiDropDownList parentElement){
        //highlight active parent element menu
        drawRect(parentElement.posX, parentElement.posY,parentElement.posX+ parentElement.width,
                parentElement.posY+ parentElement.height, 0x80ffffff);

    }

    public void drawDropDownList(MwGuiDropDownList parentElement){
           int listPosX;
           int listPosY;
           int listWidth;
           this.dropDownListHeight=this.elementList.size()*(this.fontRenderer.FONT_HEIGHT+this.menuItemElementVspacing);

        if(parentElement.elementList.size()>0){

            //draw drop-down menu area
            if(parentElement.showLabel){

                 listPosX= parentElement.posX+fontRenderer.getStringWidth(parentElement.menuItemName+": ");
                 listWidth=this.dropDownListWidth;

            }else {
                listPosX=parentElement.posX;
                listWidth=this.dropDownListWidth>this.width ? this.dropDownListWidth : this.width;
            }
            listPosY=parentElement.posY+parentElement.height;
            this.dropDownListPosY=listPosY;

            drawRect(listPosX, listPosY, listPosX+listWidth,
                    listPosY+this.dropDownListHeight+this.menuFirstItemElementVshift,0x50000000);
            //draw drop-down menu element
            for(int i=0; i<this.elementList.size(); i++){
                drawString(this.fontRenderer,this.elementList.get(i),listPosX+this.menuItemElementTextShift,
                        listPosY+this.menuFirstItemElementVshift+i*((this.menuItemElementHeight)+this.menuItemElementVspacing),0xffffff);
            }

        }
   }

   public void drawHighlightDropDownListElement(MwGuiDropDownList parentElement,int posX, int posY){
        int highlightBoxposX;
        int highlightBoxWidth;
        int highlightBoxPosY;
        int activeElement;


        //detect active menu item

        activeElement=this.getDropDownActiveElementIndex(parentElement, posX, posY);

        highlightBoxPosY=activeElement*10+ parentElement.dropDownListPosY+
                                                parentElement.menuFirstItemElementVshift-
                                                parentElement.menuItemElementVspacing;

       if(parentElement.showLabel){
           highlightBoxposX= parentElement.posX+fontRenderer.getStringWidth(parentElement.menuItemName+": ");
           highlightBoxWidth=this.dropDownListWidth;

       }else {
            highlightBoxposX = parentElement.posX;
            highlightBoxWidth = this.dropDownListWidth > this.width ? this.dropDownListWidth : this.width;
       }
       if(isPosInsideDropdownList(parentElement, posX, posY) && activeElement<(parentElement.elementList.size())) {
            drawRect(highlightBoxposX, highlightBoxPosY, highlightBoxposX+highlightBoxWidth,
                 highlightBoxPosY + parentElement.menuItemElementHeight + parentElement.menuItemElementVspacing,
                 0x80ffffff);
        }
   }
}

