package mapwriter.gui;

import mapwriter.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class MwGuiComboBox extends GuiScreen {

    private int posX;
    private int posY;
    private int width;
    private int height;
    private String label;
    private int labelPosX;
    private int labelPosY;

    private boolean editableTextField;
    private int textFieldPosX;
    private int textFieldPosY;
    private int textFieldWidth;
    private int textFieldHeight;
    private FontRenderer fontRenderer;
    private List <String> elementList;

    private int arrowIconX;
    private int arrowIconY;
    private int arrowIconWidth = 12;
    private int arrowIconHeight = 12;

    private int elementListSpasing=1;
    private boolean dropDownelementList=false;

    int dropDownListPosX;
    int dropDownListPosY;
    int dropDownListWidth;
    int dropDownListHeight;


    private GuiTextField textField=null;


    private ResourceLocation arrowIconTexture = new ResourceLocation(
            "mapwriter", "textures/map/arrow_combobox.png");


    public MwGuiComboBox(FontRenderer fontRenderer, int posX, int posY,String label, int width,
                                                            List <String> elementList ,boolean editableTextField) {
        this.posX=posX;
        this.posY=posY;
        this.fontRenderer = fontRenderer;
        this.label=label;
        this.width=width;
        this.elementList=elementList;
        this.editableTextField=editableTextField;
        this.init();
    }

    public void init(){

        this.labelPosX=this.posX+2;
        this.labelPosY=this.posY+2;

        this.textFieldPosX=this.labelPosX+this.fontRenderer.getStringWidth(this.label)+5;
        this.textFieldPosY=this.labelPosY;
        this.textFieldHeight=fontRenderer.FONT_HEIGHT;
        this.textFieldWidth=this.width-this.fontRenderer.getStringWidth(this.label)-5-arrowIconWidth-2-4;

        this.height=this.textFieldHeight+4;

        this.arrowIconX=this.textFieldPosX+this.textFieldWidth+2;
        this.arrowIconY=this.posY;

        this.textField=new GuiTextField(this.fontRenderer,this.textFieldPosX,this.textFieldPosY,
                                                                            this.textFieldWidth,this.textFieldHeight);
        this.textField.setEnabled(this.editableTextField);
        if(this.elementList.size()>0){
            this.textField.setText(this.elementList.get(0));
        }else this.textField.setText("");

        this.dropDownListPosX=this.textFieldPosX;
        this.dropDownListPosY=this.posY+this.height;
        this.dropDownListWidth=this.textFieldWidth+this.arrowIconWidth+4;
        this.dropDownListHeight=this.elementList.size()*(this.fontRenderer.FONT_HEIGHT+this.elementListSpasing);
        
        

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

    public int getDropDownActiveElementIndex(int mouseX, int mouseY){

        return (mouseY-this.dropDownListPosY)/
                (this.fontRenderer.FONT_HEIGHT+1);

    }

    public int getWidth(){ return this.width; }

    public int getHeight(){ return this.height; }

    public int getPosX(){ return this.posX;}

    public int getPosY(){ return this.posY;}

    public List <String> getElementList(){ return this.elementList; }

    public String getSelectionElementName(){ return this.textField.getText(); }

    public void setActiveElementName(String name){ this.textField.setText(name); }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {

        if(this.isArrowClick(mouseX,mouseY)){

            this.dropDownelementList=!this.dropDownelementList;

        }else if(this.dropDownelementList && isPosInsideDropdownList(mouseX,mouseY)){

            this.textField.setText(this.elementList.get(getDropDownActiveElementIndex(mouseX,mouseY)));
            this.dropDownelementList=false;

        }else this.dropDownelementList=false;

        this.textField.mouseClicked(mouseX,mouseY,button);

    }

    @Override
    protected void keyTyped(char c, int key) {
        if (this.textField.isFocused())
            this.textField.textboxKeyTyped(c, key);
    }

    public void draw(int mouseX,int mouseY){
        
        drawString(this.fontRenderer,this.label, this.labelPosX, this.labelPosY, 0xffffff);


        drawRect(this.textFieldPosX-2,this.textFieldPosY-2,
                this.textFieldPosX+this.textFieldWidth+this.arrowIconWidth+4,
                this.posY+this.height, 0x80000000);


        Minecraft.getMinecraft().renderEngine.bindTexture(this.arrowIconTexture);
        Render.setColour(0xffffffff);
        Render.drawTexturedRect(this.arrowIconX, this.arrowIconY,this.arrowIconWidth, 
                                                                this.arrowIconHeight, 0.0, 0.0, 1.0, 1.0);

        this.textField.drawTextBox();

        if(this.dropDownelementList){
            this.drawDropDownList();
            this.drawHighlightDropDownListElement(mouseX, mouseY);
        }


    }

    public void drawDropDownList(){

        this.dropDownListHeight=this.elementList.size()*(this.fontRenderer.FONT_HEIGHT+this.elementListSpasing);

        drawRect(this.dropDownListPosX,this.dropDownListPosY,this.dropDownListPosX+this.dropDownListWidth,
                                            this.dropDownListPosY+this.dropDownListHeight,0xff000000);

        for(int i=0; i<this.elementList.size(); i++){
            drawString(this.fontRenderer, this.elementList.get(i),this.dropDownListPosX, 
              this.dropDownListPosY+i*(this.fontRenderer.FONT_HEIGHT+this.elementListSpasing),0xffffff );
        }


    }

    public void drawHighlightDropDownListElement(int posX, int posY){


        int highlightBoxposX;
        int highlightBoxPosY;
        int highlightBoxWidth;
        int highlightBoxHeight;
        int activeElement;


        //detect active menu item

        activeElement=this.getDropDownActiveElementIndex( posX, posY);

        if(this.dropDownelementList && isPosInsideDropdownList(posX, posY) && activeElement<(this.elementList.size())) {

            highlightBoxPosY=this.dropDownListPosY+activeElement*(this.fontRenderer.FONT_HEIGHT+this.elementListSpasing);
            highlightBoxposX= this.dropDownListPosX;
            highlightBoxWidth=this.dropDownListWidth;
            highlightBoxHeight=this.fontRenderer.FONT_HEIGHT;

            drawRect(highlightBoxposX, highlightBoxPosY-this.elementListSpasing, highlightBoxposX+highlightBoxWidth,
                    highlightBoxPosY + highlightBoxHeight,
                    0x80ffffff);
        }
    }

}
