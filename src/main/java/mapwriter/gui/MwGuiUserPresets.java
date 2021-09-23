package mapwriter.gui;

import mapwriter.Mw;
import mapwriter.Render;
import mapwriter.map.Marker;
import mapwriter.map.UserPresetMarker;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MwGuiUserPresets extends GuiScreen {
    GuiScreen parentScreen;
    private Mw mw;
    private int posX;
    private int posY;
    private int windowWidth;
    private int windowHeight;
    private int presetMarkersCount=10;
    private int elementHSpacing=18;
    private List<Integer> colors;
    private MwGuiComboBox userPresetsGroup;
    private List<GuiTextField> userPresetsMarkers;
    private List<MwGuiColorComboBox> userPresetsMarkerColor;
    private boolean presetsMarkerSaved =true;
    private boolean presetsGroupSaved =true;
    private boolean inCorrectPresetGroupName =false;

    private HashMap<Integer, List<String> >presetMarkersList=new HashMap<Integer,List<String>>();
    private HashMap<Integer, List<Integer> >presetMarkersColorList=new HashMap<Integer, List<Integer>>();

    String presetGroupLabel=I18n.format("mw.gui.mwguiuserpreset.groupPreset");




    private GuiButton buttonClose;



    public MwGuiUserPresets(Mw mw, GuiScreen parentScreen) {
        this.mw=mw;
        this.parentScreen=parentScreen;
    }

    public boolean isPosInsideCloseButton(int mouseX, int mouseY){
        int startXDetect;
        int endXDetect;
        int startYDetect;
        int endYDetect;

        startXDetect= this.buttonClose.xPosition;
        endXDetect=startXDetect+this.buttonClose.width;

        startYDetect = this.buttonClose.yPosition;
        endYDetect = startYDetect +this.buttonClose.height;
        if (mouseX > startXDetect && mouseX < endXDetect && mouseY > startYDetect && mouseY < endYDetect) {
            return true;
        } else return false;
    }

    public void loadPresetMarker(){
        String activePresetGroupName=this.userPresetsGroup.getSelectionElementName();
        int activePresetGroupIndex=this.userPresetsGroup.getSelectionElementIndex();

        if(this.mw.markerManager.userPresetMarker.containsKey(activePresetGroupName)){
            HashMap<Integer, UserPresetMarker> tempPresetMarkerMap=
                                                    this.mw.markerManager.userPresetMarker.get(activePresetGroupName);

            for(int i=0; i<10; i++){
                if(tempPresetMarkerMap.containsKey(i)){
                    this.userPresetsMarkers.get(i).setText(tempPresetMarkerMap.get(i).getPresetMarkerName());
                    this.userPresetsMarkerColor.get(i).setActiveColor(tempPresetMarkerMap.get(i).getColor());
                }else{
                    this.userPresetsMarkers.get(i).setText("EmptySlot");
                    this.userPresetsMarkerColor.get(i).setActiveColor(0xffff0000);
                }
            }

        }else {
            for(int i=0; i<10; i++){
                this.userPresetsMarkers.get(i).setText("EmptySlot");
                this.userPresetsMarkerColor.get(i).setActiveColor(0xffff0000);

            }

        }




    }

    public void savePresetMarker() {

        HashMap<Integer,UserPresetMarker> tempPresetMarkerMap=new HashMap<Integer, UserPresetMarker>();
        String activePresetGroupName=this.userPresetsGroup.getSelectionElementName();
        int activePresetGroupIndex=this.userPresetsGroup.getSelectionElementIndex();



        for(int i=0; i<10; i++){
             if(!this.userPresetsMarkers.get(i).getText().equals("EmptySlot")){
                 tempPresetMarkerMap.put(i,new UserPresetMarker(activePresetGroupName,
                         this.userPresetsMarkers.get(i).getText(),
                         this.userPresetsMarkerColor.get(i).getActiveColor(),
                         activePresetGroupIndex,
                         i ));
              }
        }
        this.mw.markerManager.userPresetMarker.put(activePresetGroupName,tempPresetMarkerMap);
        this.mw.markerManager.userPresetGroup.put(this.userPresetsGroup.getSelectionElementIndex(),
                                                  this.userPresetsGroup.getSelectionElementName() );
    }

    public boolean activeGroupSaved(int indexActiveGroup){

        if(this.mw.markerManager.userPresetGroup.containsKey(indexActiveGroup)){
            if(this.mw.markerManager.userPresetGroup.get(indexActiveGroup).
                    equals(this.userPresetsGroup.getSelectionElementName()) ){
                return true;
            }else return false;

        }else if(!this.userPresetsGroup.getSelectionElementName().equals("EmptySlot")){
            return false;
        }else return true;
    }

    @Override
    public void initGui() {

        this.posX=30;
        this.posY=20;

        String buttonSaveName=I18n.format("mw.gui.mwguiuserpreset.buttonSave");
        String buttonCloseName=I18n.format("mw.gui.mwguiuserpreset.buttonClose");

        colors=new ArrayList<Integer>();
        for(int i=0; i<Marker.getColours().length ; i++) {
            colors.add(i,0xff000000 | Marker.getColours()[i]);
        }

        //load preset group
        List <String> elementGroup=new ArrayList<String>();
        for(int i=0; i<10; i++){
            if(this.mw.markerManager.userPresetGroup.containsKey(i)){
                elementGroup.add(this.mw.markerManager.userPresetGroup.get(i));
            }else elementGroup.add("EmptySlot");
        }

        this.userPresetsGroup=new MwGuiComboBox(this.fontRendererObj,
                this.posX+this.fontRendererObj.getStringWidth(this.presetGroupLabel+":   0.")-5,
                this.posY,"",150,elementGroup,true);


        this.buttonList.add(new GuiButton(200,this.posX+
                            this.fontRendererObj.getStringWidth(this.presetGroupLabel+":   0.")+
                            this.userPresetsGroup.getWidth(),this.posY,
                            this.fontRendererObj.getStringWidth(buttonSaveName+5),12,buttonSaveName));

        this.buttonList.add(buttonClose=new GuiButton(201,
                this.width-this.fontRendererObj.getStringWidth(buttonCloseName)-30,this.height-25,
                this.fontRendererObj.getStringWidth(buttonCloseName+5)+20,20,buttonCloseName));


        this.userPresetsMarkers=new ArrayList<GuiTextField>();
        this.userPresetsMarkerColor=new ArrayList<MwGuiColorComboBox>();


        for(int i=0; i<this.presetMarkersCount; i++){

            this.userPresetsMarkers.add(i,new GuiTextField(this.fontRendererObj,this.posX,
                                (this.posY+this.elementHSpacing)+i*this.elementHSpacing,100,12));
            this.userPresetsMarkers.get(i).setMaxStringLength(20);
            this.userPresetsMarkers.get(i).setText("EmptySlot");
            this.userPresetsMarkerColor.add(i,new MwGuiColorComboBox(this,
                    this.posX+10+userPresetsMarkers.get(i).width,(this.posY+this.elementHSpacing)+
                                                                                  i*this.elementHSpacing,this.colors));
        }

        this.loadPresetMarker();
   }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {

        drawBackground(1);

        drawCenteredString(this.fontRendererObj,I18n.format("mw.gui.mwguiuserpreset.title"),
                                                                this.width/2,10,0xffffffff);

        presetsMarkerSaved =true;
        presetsGroupSaved=true;

        for(int i=0; i<this.userPresetsMarkers.size(); i++){

            int k=this.userPresetsMarkers.size()-1-i;

            drawString(this.fontRendererObj,String.valueOf((k+1)==10 ? 0 : k+1)+".",this.posX-15,
                                                    (this.posY+4)+(k+1)*this.elementHSpacing,0xffffffff);

            userPresetsMarkers.get(k).drawTextBox();
            userPresetsMarkerColor.get(k).draw(mouseX, mouseY);

            //draw red border if preset marker not saved

            if(this.mw.markerManager.userPresetMarker.containsKey(this.userPresetsGroup.getSelectionElementName())){
               HashMap<Integer, UserPresetMarker> activePresetMarkerMap=this.mw.markerManager.userPresetMarker.
                            get(this.userPresetsGroup.getSelectionElementName());

               if(activePresetMarkerMap.containsKey(k)){


                   if(!activePresetMarkerMap.get(k).getPresetMarkerName().equals( userPresetsMarkers.get(k).getText()) ){
                       Render.setColour(0xffff0000);
                       Render.drawRectBorder(   this.userPresetsMarkers.get(k).xPosition,
                                                this.userPresetsMarkers.get(k).yPosition,
                                                this.userPresetsMarkers.get(k).width,
                                                this.userPresetsMarkers.get(k).height, 1);
                       this.presetsMarkerSaved=false;
                   }

               } else if(!this.userPresetsMarkers.get(k).getText().equals("EmptySlot")){
                       Render.setColour(0xffff0000);
                       Render.drawRectBorder(   this.userPresetsMarkers.get(k).xPosition,
                                                this.userPresetsMarkers.get(k).yPosition,
                                                this.userPresetsMarkers.get(k).width,
                                                this.userPresetsMarkers.get(k).height, 1);
                       this.presetsMarkerSaved=false;
               }

            }
        }

        String groupIndex=(this.userPresetsGroup.getSelectionElementIndex()+1)==10 ? String.valueOf(0) :
                                                    String.valueOf(this.userPresetsGroup.getSelectionElementIndex()+1);
        String groupLabel=presetGroupLabel+":   "+ groupIndex+".";
        drawString(this.fontRendererObj,groupLabel,this.posX,this.posY+3,0xffffffff);
        userPresetsGroup.draw(mouseX, mouseY);

        //draw border if group have incorrect name when saving or not saved
        if(inCorrectPresetGroupName || !this.activeGroupSaved(this.userPresetsGroup.getSelectionElementIndex())){

            Render.setColour(0xffff0000);
            Render.drawRectBorder(this.userPresetsGroup.getTextfieldPosX(),this.userPresetsGroup.getTextfieldPosY(),
                    this.userPresetsGroup.getTextFieldWidth(),this.userPresetsGroup.getTextFieldHeight(), 1);
            this.presetsGroupSaved=false;

        }

        //mouse hovered close button

        if(isPosInsideCloseButton(mouseX, mouseY) && (!this.presetsGroupSaved || !this.presetsMarkerSaved) ){
            int AlertTextWidth=this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguiuserpreset.notSavedText"));


            drawRect(this.width/2-AlertTextWidth/4,
                     this.height/3,
                    this.width/2+AlertTextWidth/3,
                    this.height/3+50,
                     0x80000000);
            this.fontRendererObj.drawSplitString(I18n.format("mw.gui.mwguiuserpreset.notSavedText"),
                                                this.width/2-AlertTextWidth/4+10,
                                                this.height/3+10,AlertTextWidth/2+20,0xffffff);

        }


        super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        if(p_146284_1_.id == 200) {

            if(!this.userPresetsGroup.getSelectionElementName().contains("EmptySlot") ||
                    this.mw.markerManager.userPresetGroup.containsKey(this.userPresetsGroup.getSelectionElementIndex())){
                if(this.userPresetsGroup.validateTextFieldData()){
                    this.userPresetsGroup.setNewElementName(this.userPresetsGroup.getSelectionElementName());
                    this.savePresetMarker();

                }else {
                    this.userPresetsGroup.setNewElementName(this.userPresetsGroup.getElementList().get(
                            this.userPresetsGroup.getSelectionElementIndex()));
                    this.userPresetsGroup.setActiveElementName(this.userPresetsGroup.getElementList().get(
                            this.userPresetsGroup.getSelectionElementIndex()));
                }
                this.inCorrectPresetGroupName =false;
            }else this.inCorrectPresetGroupName =true;

        }else if(p_146284_1_.id == 201){
            this.mw.markerManager.savePresetGroups(this.mw.worldConfig,this.mw.catUserPresetGroups);
            this.mw.markerManager.savePresetMarkers(this.mw.worldConfig,this.mw.catUserPresetMarkers);
            this.mc.displayGuiScreen(this.parentScreen);

        }

    }

    @Override
    protected void keyTyped(char c, int key) {

        boolean focusedTextfield=false;

        switch(key){


            case Keyboard.KEY_ESCAPE:
                this.mc.displayGuiScreen(this.parentScreen);
                break;
            case Keyboard.KEY_TAB:

                GuiTextField thisTextfield=null;
                GuiTextField prevTextfield=null;
                GuiTextField nextTextfield=null;

                for(int i=0;i<this.userPresetsMarkers.size(); i++){
                    if(this.userPresetsMarkers.get(i).isFocused()){


                        thisTextfield=this.userPresetsMarkers.get(i);
                        prevTextfield = ((i - 1) < 0) ? this.userPresetsMarkers.get(this.userPresetsMarkers.size() - 1)
                                                       : this.userPresetsMarkers.get(i - 1);

                        nextTextfield =((i+1)>this.userPresetsMarkers.size()-1) ? this.userPresetsMarkers.get(0)
                                                                              : this.userPresetsMarkers.get(i+1);
                        focusedTextfield=true;
                        break;

                    }
                }
                if(focusedTextfield){
                    if(thisTextfield.getText().length()==0){
                        thisTextfield.setText("EmptySlot");
                    }
                    thisTextfield.setFocused(false);
                    thisTextfield.setCursorPositionEnd();
                    if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)){

                        prevTextfield.setFocused(true);
                        thisTextfield.setCursorPositionEnd();

                    } else {
                        nextTextfield.setFocused(true);
                        thisTextfield.setCursorPositionEnd();

                    }
                    break;


                } else break;

            default:
                 this.userPresetsGroup.keyTyped(c,key);
                 for(int i=0; i<this.userPresetsMarkers.size(); i++){
                     this.userPresetsMarkers.get(i).textboxKeyTyped(c,key);
                }

        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {

     //detect active gui elmement so that the processing of the click was only on one gui element at a time,
        MwGuiComboBox activePresetGroup=null;
        MwGuiColorComboBox activePresetMarkerColor=null;

        for(int i=0; i<this.userPresetsMarkers.size(); i++){
            if(this.userPresetsMarkerColor.get(i).isPosInsideDropdownList(mouseX,mouseY) &&
                                                    this.userPresetsMarkerColor.get(i).isDropDownelementListActive() ){
                activePresetMarkerColor=this.userPresetsMarkerColor.get(i);
                break;
            } else activePresetMarkerColor=null;
        }

        if(this.userPresetsGroup.isPosInsideComboBox(mouseX,mouseY,"dropDownList") &&
                                                                  this.userPresetsGroup.isDropDownelementListActive()){

            this.userPresetsGroup.mouseClicked(mouseX,mouseY,button);
            this.loadPresetMarker();
            activePresetGroup=this.userPresetsGroup;

        }

        this.userPresetsGroup.mouseClicked(mouseX,mouseY,button);


        for(int i=0; i<this.userPresetsMarkers.size(); i++){
            if((activePresetMarkerColor==this.userPresetsMarkerColor.get(i) || activePresetMarkerColor==null) &&
                                                                                             activePresetGroup==null ){
                this.userPresetsMarkerColor.get(i).mouseClicked(mouseX,mouseY,button);
            }
            this.userPresetsMarkers.get(i).mouseClicked(mouseX,mouseY,button);
            if(this.userPresetsMarkers.get(i).getText().length()==0){
                this.userPresetsMarkers.get(i).setText("EmptySlot");
            }
        }

    super.mouseClicked(mouseX,mouseY,button);

    }
}
