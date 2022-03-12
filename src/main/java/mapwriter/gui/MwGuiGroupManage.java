package mapwriter.gui;

import mapwriter.Common;
import mapwriter.Mw;
import mapwriter.Render;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

public class MwGuiGroupManage extends GuiScreen {

    private int posX;
    private int posY;
    private int windowWidth;
    private int windowHeight;
    String newGroupNameLabel=I18n.format("mw.gui.mwguimanagegroup.rename");
    String errorMessage="";
    boolean showErrorMessage=false;


    Mw mw;
    GuiScreen parentScreen;
    MwGuiComboBox groupComboBox=null;
    GuiTextField newGroupName=null;
    GuiButton renameButton;
    GuiButton moveGroupUp;
    GuiButton moveGroupDown;
    MwGuiListBox orderedGroupList=null;

    public MwGuiGroupManage(Mw mw, GuiScreen parentScreen, int posX, int posY) {

        this.mw=mw;
        this.parentScreen=parentScreen;
        this.posX=posX;
        this.posY=posY;

    }


    @Override
    public void initGui() {
        this.windowWidth=this.width*60/100;
        this.windowHeight=this.height*50/100;


         this.posX=(this.width-this.windowWidth);
         this.posX=(this.width-this.windowWidth)/2;
         this.posY=(this.height-this.windowHeight)/3;

        this.groupComboBox=new MwGuiComboBox(this.mw.mc.fontRenderer,this.posX+5,this.posY+30,
                                              I18n.format("mw.gui.mwguimanagegroup.group"),
                                        this.windowWidth/2, this.mw.markerManager.getOrderedGroupList(),false);

        int labelWigth=this.mw.mc.fontRenderer.getStringWidth(newGroupNameLabel);
        int newGroupNameWidth=this.windowWidth/2-labelWigth-7;
        int renameButtonWidth=this.mw.mc.fontRenderer.getStringWidth(
                                                     I18n.format("mw.gui.mwguimanagegroup.buttonRename"))+5;

        this.newGroupName=new GuiTextField(this.mw.mc.fontRenderer,posX+labelWigth+10,
                            this.posY+47,newGroupNameWidth,this.mw.mc.fontRenderer.FONT_HEIGHT+3);

        this.buttonList.add(renameButton=new GuiButton (200,this.posX+7,this.posY+65,
                        renameButtonWidth,20,I18n.format("mw.gui.mwguimanagegroup.buttonRename") ));

        int orderedGroupListWidth;

        if( (this.posX+this.windowWidth/2+20+75)>=(this.posX+this.windowWidth) ){

            orderedGroupListWidth=75-((this.posX+this.windowWidth/2+20+75)-(this.posX+this.windowWidth))-5;

        } else orderedGroupListWidth=75;

        this.orderedGroupList=new MwGuiListBox(this.mw.mc.fontRenderer,this.mw.markerManager.getOrderedGroupList(),
                orderedGroupListWidth );

        this.buttonList.add(moveGroupUp=new GuiButton(201,
                                        this.posX+this.windowWidth/2+20+orderedGroupListWidth,
                                        this.posY+30,
                                        15,15,"\u2191".toUpperCase()));
        this.buttonList.add(moveGroupDown=new GuiButton(202,
                this.posX+this.windowWidth/2+20+orderedGroupListWidth,
                this.posY+45,
                15,15,"\u2193"));


    }

    public boolean validateTextFieldData() {
        return this.newGroupName.getText().length() > 0;
    }

    public boolean isSystemGroup(){
        if(this.groupComboBox.getSelectionElementName().equals(Common.ALL_GROUP) ||
                this.groupComboBox.getSelectionElementName().equals(Common.NONE_GROUP) ){
            return true;
        }else return false;
    }

    public boolean isDuplicateNameInList(List<String> list, String name){
        boolean result=false;
        for (int i=0; i<list.size(); i++){
            if( list.get(i).equals(name) ){
                result=true;
                break;
            }else result=false;
        }
        return result;
    }


    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 200) {

            if (!this.validateTextFieldData()) {
                //Empty new name
                this.showErrorMessage=true;
                this.errorMessage=I18n.format("mw.gui.mwguimanagegroup.errorEmptyName");

            }else if (this.isDuplicateNameInList(this.groupComboBox.getElementList(),this.newGroupName.getText())) {
                //duplicate group`s name
                this.showErrorMessage=true;
                this.errorMessage=I18n.format("mw.gui.mwguimanagegroup.errorDuplicateName");

            }else if (this.isSystemGroup()) {
                //rename system group
                this.showErrorMessage=true;
                this.errorMessage=I18n.format("mw.gui.mwguimanagegroup.errorSystemGroupName");
            } else {
                //rename group`s name
                this.mw.markerManager.renameGroup(this.groupComboBox.getSelectionElementName(),this.newGroupName.getText());
                this.groupComboBox.setActiveElementName(this.newGroupName.getText());
            }
        }else if(button.id == 201){
            if(this.orderedGroupList.isSelectedElement()){
                //move selected group one level up
                this.mw.markerManager.changeGroupOrder(this.orderedGroupList.getSelectedElementIndex(),1);
                this.orderedGroupList.setSelectedElementIndex(this.orderedGroupList.getSelectedElementIndex()-1);
            }

        }else if(button.id == 202) {
            if(this.orderedGroupList.isSelectedElement()){
                //move selected group one level down
                this.mw.markerManager.changeGroupOrder(this.orderedGroupList.getSelectedElementIndex(),-1);
                this.orderedGroupList.setSelectedElementIndex(this.orderedGroupList.getSelectedElementIndex()+1);
            }

        }
    }

    @Override
    public void handleMouseInput(){

        if(Mouse.getEventDWheel()>0 && this.orderedGroupList.isSelectedElement()){
            //move selected group one level up
            this.mw.markerManager.changeGroupOrder(this.orderedGroupList.getSelectedElementIndex(),1);
            this.orderedGroupList.setSelectedElementIndex(this.orderedGroupList.getSelectedElementIndex()-1);

        }else if(Mouse.getEventDWheel()<0 && this.orderedGroupList.isSelectedElement()){
            //move selected group one level down
            this.mw.markerManager.changeGroupOrder(this.orderedGroupList.getSelectedElementIndex(),-1);
            this.orderedGroupList.setSelectedElementIndex(this.orderedGroupList.getSelectedElementIndex()+1);

        }

            super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.groupComboBox.mouseClicked(mouseX, mouseY, button);
        this.newGroupName.mouseClicked(mouseX, mouseY, button);
        this.orderedGroupList.mouseClicked(mouseX, mouseY, button);

    }

    @Override
    protected void keyTyped(char c, int k) {
        if (k == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else {

            super.keyTyped(c, k);
            this.groupComboBox.keyTyped(c, k);
            this.newGroupName.textboxKeyTyped(c, k);

        }

    }

    public void draw(){



    }

    @Override
    public void onGuiClosed(){
        this.mw.markerManager.updateOrderedGroupList();
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        if (this.parentScreen != null) {
             this.parentScreen.drawScreen(mouseX, mouseY, f);
        } else {
         this.drawDefaultBackground();

        }


        drawRect(this.posX,this.posY, this.posX+this.windowWidth+12,
                                                    this.posY+this.windowHeight, 0x80000000);
        drawCenteredString(this.mw.mc.fontRenderer, I18n.format("mw.gui.mwguimanagegroup.title"),
                                            this.posX+windowWidth/2,this.posY+5,0xffffff);

        this.drawString(this.mw.mc.fontRenderer,I18n.format("mw.gui.mwguimanagegroup.renameGroup"),
                this.posX+7,this.posY+17,0xffffff);
        this.drawString(this.mw.mc.fontRenderer,I18n.format("mw.gui.mwguimanagegroup.changeOrder"),
                this.posX+this.windowWidth/2+10,this.posY+17,0xffffff);

        //Rename group frame
        Render.setColour(0xffffffff);
        Render.drawRectBorder(this.posX+5,this.posY+15,this.windowWidth/2+2,12,1);
        Render.drawRectBorder(this.posX+5,this.posY+15,this.windowWidth/2+2,this.windowHeight-20,1);

        //Change order group frame
        Render.drawRectBorder(this.posX+this.windowWidth/2+8,this.posY+15,this.windowWidth/2-12,12,1);
        Render.drawRectBorder(this.posX+this.windowWidth/2+8,this.posY+15,this.windowWidth/2-12,this.windowHeight-20,1);

        //rename text

        this.drawString(this.mw.mc.fontRenderer,newGroupNameLabel,this.posX+7,
                this.groupComboBox.getPosY()+this.groupComboBox.getHeight()+7,0xffffff);


        if(this.showErrorMessage) {

      //      this.drawString(this.mw.mc.fontRenderer, errorMessage, this.posX + this.renameButton.width+10,
      //              this.renameButton.yPosition+(this.renameButton.height-this.mw.mc.fontRenderer.FONT_HEIGHT)/2+1, 0xff0000);

            this.mw.mc.fontRenderer.drawSplitString(errorMessage,this.posX + this.renameButton.width+10,
                    this.renameButton.yPosition+(this.renameButton.height-this.mw.mc.fontRenderer.FONT_HEIGHT)/2+1,
                    this.windowWidth/2-this.renameButton.width-10,0xff0000);



        }

        super.drawScreen(mouseX, mouseY, f);
        this.orderedGroupList.draw(this.posX+this.windowWidth/2+20,this.groupComboBox.getPosY());

        if(!this.orderedGroupList.isSelectedElement()){
            if(this.orderedGroupList.isPosInsideList(mouseX,mouseY)){
                this.orderedGroupList.drawHighlightListElement(mouseX,mouseY);
            }
        } else this.orderedGroupList.drawHighlightSelectedElement(mouseX,mouseY);


        this.newGroupName.drawTextBox();

        this.groupComboBox.draw(mouseX,mouseY);



    }
}
