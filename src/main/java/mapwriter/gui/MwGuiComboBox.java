package mapwriter.gui;

import mapwriter.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

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
    private int selectionElementIndex;

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
        this.selectionElementIndex=0;
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

    public boolean isPosInsideComboBox(int mouseX, int mouseY,String detectionElement){
        int startXDetect=0;
        int endXDetect=0;
        int startYDetect=0;
        int endYDetect=0;

        if(detectionElement.equals("arrow")){
            startXDetect=this.arrowIconX;
            endXDetect=startXDetect+this.arrowIconWidth;
            startYDetect = this.arrowIconY;
            endYDetect = startYDetect + this.arrowIconHeight;

        } else if(detectionElement.equals("dropDownList")){
            startXDetect= this.dropDownListPosX;
            endXDetect=startXDetect+this.dropDownListWidth;
            startYDetect = this.dropDownListPosY;
            endYDetect = startYDetect +this.dropDownListHeight;

        }else if(detectionElement.equals("textfield")){

            startXDetect= this.textFieldPosX;
            endXDetect=startXDetect+this.textFieldWidth;
            startYDetect = this.textFieldPosY;
            endYDetect = startYDetect +this.textFieldHeight;

        }else return false;

         if (mouseX > startXDetect && mouseX < endXDetect && mouseY > startYDetect && mouseY < endYDetect) {
            return true;
        } else return false;
    }

    public int getDropDownActiveElementIndex(int mouseX, int mouseY){

            return (mouseY-this.dropDownListPosY)/(this.fontRenderer.FONT_HEIGHT+1);

    }

    public boolean validateTextFieldData() {
        return this.textField.getText().length() > 0;
    }

    public int getTextfieldPosX(){ return this.textFieldPosX; }

    public int getTextfieldPosY(){ return this.textFieldPosY; }

    public int getTextFieldWidth(){ return this.textFieldWidth; }

    public int getTextFieldHeight(){ return this.textFieldHeight; }

    public int getWidth(){ return this.width; }

    public int getHeight(){ return this.height; }

    public int getPosX(){ return this.posX;}

    public int getPosY(){ return this.posY;}

    public List <String> getElementList(){ return this.elementList; }

    public String getSelectionElementName(){ return this.textField.getText(); }

    public int getSelectionElementIndex(){ return this.selectionElementIndex; }

    public boolean isDropDownelementListActive(){ return this.dropDownelementList; }

    public void setNewElementName(String name){ this.elementList.set(this.selectionElementIndex, name); }

    public void setLabel(String labelName){ this.label=labelName; }

    public void setActiveElementName(String name){ this.textField.setText(name); }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {

        int index;

        //detect click on arrow to open dropdown list
        if(this.isPosInsideComboBox(mouseX,mouseY,"arrow")){

            this.dropDownelementList=!this.dropDownelementList;

        //detect click om dropdown list to set selection elemen as active
        }else if(this.dropDownelementList && isPosInsideComboBox(mouseX,mouseY,"dropDownList")){

            index=this.getDropDownActiveElementIndex(mouseX,mouseY);
            this.textField.setText(this.elementList.get(index));
            this.selectionElementIndex=index;
            this.dropDownelementList=false;
            this.textField.setFocused(false);

        }else this.dropDownelementList=false;

        //detect click to textfield to focused
        if(this.isPosInsideComboBox(mouseX, mouseY,"textfield")){
            this.textField.mouseClicked(mouseX,mouseY,button);
        }

        if(this.textField.isFocused() && !this.isPosInsideComboBox(mouseX,mouseY,"textfield") &&
                                                !this.isPosInsideComboBox(mouseX,mouseY,"arrow") ){
            this.textField.setFocused(false);
        }




    }

    @Override
    protected void keyTyped(char c, int key) {
        if (this.textField.isFocused())
            this.textField.textboxKeyTyped(c, key);

        if (key==Keyboard.KEY_RETURN) {

            if(this.validateTextFieldData()){
                this.setNewElementName(this.getSelectionElementName());
                this.textField.setFocused(false);
            } else {
                this.setNewElementName(this.elementList.get(this.selectionElementIndex));
                this.setActiveElementName(this.elementList.get(this.selectionElementIndex));
                this.textField.setFocused(false);
            }




        }

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

        if(this.dropDownelementList && isPosInsideComboBox(posX,posY,"dropDownList") &&
                                                                            activeElement<(this.elementList.size())) {

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
