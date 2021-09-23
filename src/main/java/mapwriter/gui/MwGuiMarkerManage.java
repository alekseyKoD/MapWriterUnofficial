package mapwriter.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import mapwriter.Mw;
import mapwriter.MwUtil;
import mapwriter.Render;
import mapwriter.map.Marker;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Map;


public class MwGuiMarkerManage  extends GuiScreen {

    static final int elementVSpacing = 20;

    private final Mw mw;
    private final GuiScreen parentScreen;

    private MwGuiTextField textField = null;
    private MwGuiMarkerManageSlot markerManageSlot = null;
    private ScrollableTextBox setSelectedName=null;
    private ScrollableTextBox setSelectedGroup=null;
    private MwColorPallete setSelectedColor=null;
    private MwGuiComboBox currentGroup=null;

    private GuiCheckBox setSelectionName=null;
    private GuiCheckBox setSelectionGroup=null;
    private GuiCheckBox setSelectionColor=null;
    private GuiCheckBox setSelectAllAction=null;
    private GuiCheckBox setSelectAllMarkers=null;


    private GuiButton selectAllActions=null;

    private int[] colours=null;
    boolean backToGameOnSubmit = false;

    private boolean flagSelectAllMarkers=false;
    private boolean flagSelectAllActions=false;
    private boolean flagSetSelectionName=false;
    private boolean flagSetSelectionGroup=false;
    private boolean flagSetSelectionColor=false;

    int LeftScreenBorderShift=0;
    int assignButtonWidth=30;

    private String currentGroupLabel= I18n.format("mw.gui.mwguimarkermanage.currentGroup");
    private String ActionsWithSelectedLabel=I18n.format("mw.gui.mwguimarkermanage.actionWithSelectedTitle");
    private String setSelectedNameLabel=I18n.format("mw.gui.mwguimarkermanage.setSelectedNameLabel");
    private String setSelectedGroupLabel=I18n.format("mw.gui.mwguimarkermanage.setSelectedGroupLabel");
    private String setSelectedColorLabel=I18n.format("mw.gui.mwguimarkermanage.setSelectedColorLabel");

    private ResourceLocation leftArrowTexture = new ResourceLocation(
            "mapwriter", "textures/map/arrow_text_left.png");
    private ResourceLocation rightArrowTexture = new ResourceLocation(
            "mapwriter", "textures/map/arrow_text_right.png");


    public MwGuiMarkerManage(GuiScreen parentScreen, Mw mw) {
        this.mw = mw;
        this.parentScreen = parentScreen;

        this.colours= new int[Marker.getColours().length];
        for(int i=0; i<Marker.getColours().length ; i++) {
            this.colours[i] = 0xff000000 | Marker.getColours()[i];
        }
    }

    public void initGui() {

        // Choosing a large label length in pixels for aligning elements
        this.LeftScreenBorderShift= Math.max(this.fontRendererObj.getStringWidth(setSelectedGroupLabel),
                                            this.fontRendererObj.getStringWidth(setSelectedNameLabel));
        this.LeftScreenBorderShift= Math.max(this.LeftScreenBorderShift,
                                            this.fontRendererObj.getStringWidth(setSelectedColorLabel));

        String previousSearchText=this.textField==null? "": this.textField.getText();
        this.markerManageSlot = new MwGuiMarkerManageSlot(this, this.mc, this.mw);

        int textfieldFrameThicknessInPx=1;


        int setSelectedNameStartPosY=30+this.markerManageSlot.bottom+2*this.fontRendererObj.FONT_HEIGHT+elementVSpacing;
        int setSelectedGroupStartPosY=setSelectedNameStartPosY+elementVSpacing;
        int setSelectedColorStartPosY=setSelectedGroupStartPosY+elementVSpacing;

        this.currentGroup=new MwGuiComboBox(this.fontRendererObj,20,
                               this.markerManageSlot.top-this.fontRendererObj.FONT_HEIGHT-elementVSpacing,
                                currentGroupLabel,this.fontRendererObj.getStringWidth(this.currentGroupLabel)+120,
                                this.mw.markerManager.groupList,false);
        this.currentGroup.setActiveElementName(this.mw.markerManager.getVisibleGroupName());

        this.textField = new MwGuiTextField(this.fontRendererObj,
                this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimarkermanage.markersCount"))+20,
                this.markerManageSlot.bottom+5, this.markerManageSlot.getMarkerNameFieldWidth(),16);
        this.textField.setMaxStringLength(20);
        this.textField.setText(previousSearchText);
        this.textField.setFocused(true);

        this.setSelectedName=new ScrollableTextBox(20+this.LeftScreenBorderShift, setSelectedNameStartPosY,
                5*(this.colours.length-1)+this.currentGroup.getHeight()*this.colours.length+14, //14 is 2*arrowsWidth
                      setSelectedNameLabel);
        this.setSelectedName.init();
        this.setSelectedName.setDrawArrows(false);

        this.setSelectedGroup=new ScrollableTextBox(20+this.LeftScreenBorderShift, setSelectedGroupStartPosY,
                5*(this.colours.length-1)+this.currentGroup.getHeight()*this.colours.length+14, //14 is 2*arrowsWidth
                      setSelectedGroupLabel,this.mw.markerManager.groupList);
        this.setSelectedGroup.init();
        this.setSelectedGroup.textField.setText(this.mw.markerManager.getVisibleGroupName());
        this.setSelectedGroup.setDrawArrows(true);

        this.setSelectedColor=new MwColorPallete(20+this.LeftScreenBorderShift+this.setSelectedGroup.arrowsWidth,
                                    setSelectedColorStartPosY,
                                    this.setSelectedGroup.textFieldHeight,this.colours,this.colours[0],
                                    this.width,0);

        //add a checkbox to select/deselect all visible marker in markerslot
        this.setSelectAllMarkers=new GuiCheckBox(1,this.markerManageSlot.getStartPosX(),
                this.markerManageSlot.top-14,I18n.format("mw.gui.mwguimarkermanage.selectAllButtonOn"),false);


        //add a checkbox to select for assign a name to selected markers
        this.setSelectionName=new GuiCheckBox(2,22+this.LeftScreenBorderShift+this.setSelectedName.width,
                                                                setSelectedNameStartPosY,"",false);

        //add a checkbox to select for assign a group to selected markers
        this.setSelectionGroup=new GuiCheckBox(3,22+this.LeftScreenBorderShift+this.setSelectedName.width,
                                                                setSelectedGroupStartPosY,"",false);

        //add a checkbox to select  for assign a color to selected markers
        this.setSelectionColor=new GuiCheckBox(4,22+this.LeftScreenBorderShift+this.setSelectedName.width,
                                                                setSelectedColorStartPosY,"",false);

        //add a checkbox to select/deselect all actions

        this.setSelectAllAction=new GuiCheckBox(5,22+this.LeftScreenBorderShift+this.setSelectedName.width,
                setSelectedNameStartPosY-16,I18n.format("mw.gui.mwguimarkermanage.selectAllButtonOn"),false);

        //add a guibutton to apply selection action
        this.buttonList.add(new GuiButton(200,this.setSelectionColor.xPosition+
                                this.setSelectionColor.width+10, setSelectedColorStartPosY,
                this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimarkermanage.applybutton"))+20,
                16,I18n.format("mw.gui.mwguimarkermanage.applybutton")));

        this.markerManageSlot.updateMarkerList(this.textField.getText());


    }

    protected void keyTyped(char c, int k) {

        String oldName=this.setSelectedName.textField.getText();
        String oldGroup=this.setSelectedGroup.textField.getText();


        if (k==Keyboard.KEY_ESCAPE){
            this.mc.displayGuiScreen(this.parentScreen);
        }else{
            super.keyTyped(c, k);
            this.setSelectedGroup.textField.textboxKeyTyped(c, k);
            this.setSelectedName.textField.textboxKeyTyped(c, k);
            this.textField.textboxKeyTyped(c, k);
            if(!this.setSelectedGroup.textField.isFocused() && !this.setSelectedName.textField.isFocused()){
                this.markerManageSlot.updateMarkerList(this.textField.getText());
            }

            //set action checkboxes if the value of the new Name, group or color  has changed
            if(!this.setSelectedName.textField.getText().equals(oldName)){
                this.flagSetSelectionName=true;
                this.setSelectionName.setIsChecked(true);
            }
            if(!this.setSelectedGroup.textField.getText().equals(oldGroup)){
                this.flagSetSelectionGroup=true;
                this.setSelectionGroup.setIsChecked(true);
            }

        }
    }

    @Override
    public void handleMouseInput() {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height
                / this.mc.displayHeight - 1;
        int direction = Mouse.getEventDWheel();
        if (direction != 0) {
            this.mouseDWheelScrolled(x, y, direction);
        }
        super.handleMouseInput();
    }

    public void mouseDWheelScrolled(int x, int y, int direction) {

        String oldGroup=this.setSelectedGroup.textField.getText();
        int oldColor=this.setSelectedColor.getSelectedColor();



        this.setSelectedGroup.mouseDWheelScrolled(x, y, direction);

        for(int i=0;i<this.setSelectedColor.colorCells.size();i++){
            this.setSelectedColor.colorCells.get(i).mouseDWheelScrolled(x,y,direction);
        }

        //set action checkboxes if the value of the new Name, group or color  has changed
        if(this.setSelectedColor.getSelectedColor()!=oldColor){
            this.flagSetSelectionColor=true;
            this.setSelectionColor.setIsChecked(true);
        }
        if(!this.setSelectedGroup.textField.getText().equals(oldGroup)){
            this.flagSetSelectionGroup=true;
            this.setSelectionGroup.setIsChecked(true);
        }

    }


    private void setCurrentGroup(){
        MwGuiMarkerManage.this.mw.markerManager.setVisibleGroupName(this.currentGroup.getSelectionElementName());
        MwGuiMarkerManage.this.mw.markerManager.update();
        MwGuiMarkerManage.this.markerManageSlot.markerList=MwGuiMarkerManage.this.mw.markerManager.visibleMarkerList;
        MwGuiMarkerManage.this.markerManageSlot.updateMarkerList(this.textField.getText());
    }

    protected void mouseClicked(int x, int y, int button) {

        String oldCurrentGroup=this.currentGroup.getSelectionElementName();

        String oldName=this.setSelectedName.textField.getText();
        String oldGroup=this.setSelectedGroup.textField.getText();
        int oldColor=this.setSelectedColor.getSelectedColor();

        //set checkbox to assign selection`s markers name
        if(this.setSelectionName.mousePressed(this.mc, x, y)){
            this.flagSetSelectionName=!this.flagSetSelectionName;
            this.setSelectionName.setIsChecked(flagSetSelectionName);
        }
        //set checkbox to assign selection`s markers group
        if(this.setSelectionGroup.mousePressed(this.mc, x, y)){
            this.flagSetSelectionGroup=!this.flagSetSelectionGroup;
            this.setSelectionGroup.setIsChecked(flagSetSelectionGroup);
        }
        //set checkbox to assign selection`s markers color
        if(this.setSelectionColor.mousePressed(this.mc, x, y)){
            this.flagSetSelectionColor=!this.flagSetSelectionColor;
            this.setSelectionColor.setIsChecked(flagSetSelectionColor);
        }
        //select/deselect all checkboxes set actions
        if(this.setSelectAllAction.mousePressed(this.mc, x, y)) {
            this.flagSelectAllActions = !this.flagSelectAllActions;
            if(this.flagSelectAllActions){
                this.setSelectAllAction.displayString=I18n.format("mw.gui.mwguimarkermanage.selectAllButtonOff");
                this.flagSetSelectionName=true;
                this.flagSetSelectionGroup=true;
                this.flagSetSelectionColor=true;
                this.setSelectionName.setIsChecked(flagSetSelectionName);
                this.setSelectionGroup.setIsChecked(flagSetSelectionGroup);
                this.setSelectionColor.setIsChecked(flagSetSelectionColor);
            }else{
                this.setSelectAllAction.displayString=I18n.format("mw.gui.mwguimarkermanage.selectAllButtonOn");
                this.flagSetSelectionName=false;
                this.flagSetSelectionGroup=false;
                this.flagSetSelectionColor=false;
                this.setSelectionName.setIsChecked(flagSetSelectionName);
                this.setSelectionGroup.setIsChecked(flagSetSelectionGroup);
                this.setSelectionColor.setIsChecked(flagSetSelectionColor);
            }
        }
        // select/deselect all markers in list
        if(this.setSelectAllMarkers.mousePressed(this.mc, x, y)) {

            //select all markers in list
            int counter = 0;
            for (Map.Entry<Integer, Boolean> pair : this.markerManageSlot.checkboxesEnabled.entrySet()) {

                this.markerManageSlot.checkBoxes.get(counter).setIsChecked(!this.flagSelectAllMarkers);
                pair.setValue(!this.flagSelectAllMarkers);
                counter++;

                }
            this.flagSelectAllMarkers=!this.flagSelectAllMarkers;
            if(this.flagSelectAllMarkers){
                this.setSelectAllMarkers.displayString=I18n.format("mw.gui.mwguimarkermanage.selectAllButtonOff");
            }else this.setSelectAllMarkers.displayString=I18n.format("mw.gui.mwguimarkermanage.selectAllButtonOn");

        }
        this.textField.mouseClicked(x, y, button);
        this.currentGroup.mouseClicked(x, y, button);
        this.setSelectedName.mouseClicked(x, y, button);
        this.setSelectedGroup.mouseClicked(x, y, button);
        for(int i=0;i<this.setSelectedColor.colorCells.size();i++){
            this.setSelectedColor.colorCells.get(i).mouseClicked(x,y,button);
        }
        if (!this.currentGroup.getSelectionElementName().equals(oldCurrentGroup)){
            this.setCurrentGroup();
        }

        //set action checkboxes if the value of the new Name, group or color  has changed
        if(this.setSelectedColor.getSelectedColor()!=oldColor){
            this.flagSetSelectionColor=true;
            this.setSelectionColor.setIsChecked(true);
        }
        if(!this.setSelectedGroup.textField.getText().equals(oldGroup)){
            this.flagSetSelectionGroup=true;
            this.setSelectionGroup.setIsChecked(true);
        }
        super.mouseClicked(x, y, button);
    }

    public void updateScreen()
    {
        super.updateScreen();
        this.textField.updateCursorCounter();
    }

    public void drawScreen(int mouseX, int mouseY, float f) {

        this.drawDefaultBackground();
        this.markerManageSlot.drawScreen(mouseX, mouseY, f);

        this.drawCenteredString(this.fontRendererObj,
                 EnumChatFormatting.UNDERLINE+(EnumChatFormatting.BOLD+
                          I18n.format("mw.gui.mwguimarkermanage.title")),
                this.width / 2, 10, 0xffffff);

        this.currentGroup.draw(mouseX,mouseY);

        this.drawString(this.fontRendererObj,
                (I18n.format("mw.gui.mwguimarkermanage.markersCount",
                                    String.valueOf(this.markerManageSlot.checkBoxes.size()))),
               10,this.textField.yPosition+(this.textField.height-this.fontRendererObj.FONT_HEIGHT+1),0xffffff);

        this.drawString(this.fontRendererObj, this.ActionsWithSelectedLabel,10,
                this.markerManageSlot.bottom+30,0xffffff);

        Render.setColour(0xffffffff);
        Render.drawRectBorder(10,this.markerManageSlot.bottom+40,
                                            this.LeftScreenBorderShift+this.setSelectedGroup.width+100,
                (this.setSelectedColor.getY()+this.setSelectedColor.getPalleteHeight())-this.markerManageSlot.bottom-30,1f);

        this.setSelectedName.draw();
        this.setSelectedGroup.draw();
        this.setSelectedColor.draw();

        this.setSelectionName.drawButton(this.mc,mouseX, mouseY);
        this.setSelectionGroup.drawButton(this.mc,mouseX, mouseY);
        this.setSelectionColor.drawButton(this.mc,mouseX, mouseY);
        this.setSelectAllAction.drawButton(this.mc,mouseX, mouseY);
        this.setSelectAllMarkers.drawButton(this.mc,mouseX, mouseY);

        this.drawString(this.fontRendererObj,this.setSelectedColorLabel,20,
                                                        this.setSelectedColor.getY()+2,0xffffff);

        this.textField.drawTextBox();


        super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        if(p_146284_1_.id == 200) {
            //Apply selected action to selected markers

            String oldMarkerName="";
            int counterName=0;
            int counterGroup=0;
            int counterColor=0;
            for (Map.Entry<Integer,Boolean> mapPair: this.markerManageSlot.checkboxesEnabled.entrySet()){
                if (mapPair.getValue()){
                    int Index=this.markerManageSlot.checkboxesId.get(mapPair.getKey());

                    if(flagSetSelectionName && this.setSelectedName.validateTextFieldData()){
                        oldMarkerName=this.markerManageSlot.markerList.get(Index).name;
                        this.markerManageSlot.markerList.get(Index).setMarkerName(this.setSelectedName.textField.getText());
                        counterName++;
                    }
                    if (flagSetSelectionGroup && this.setSelectedGroup.validateTextFieldData()) {
                        this.markerManageSlot.markerList.get(Index).setGroupName(this.setSelectedGroup.textField.getText());
                        counterGroup++;
                    }
                    if(flagSetSelectionColor){
                        this.markerManageSlot.markerList.get(Index).setColour(this.setSelectedColor.getSelectedColor());
                        counterColor++;
                    }
                }
            }
            if(flagSetSelectionName){
                MwUtil.logInfo("Rename %d markers. Old name<%s>, new name<%s>",counterName,oldMarkerName,
                                                                        this.setSelectedName.textField.getText());
                flagSetSelectionName=!flagSetSelectionName;
                this.setSelectionName.setIsChecked(false);
            }
            if(flagSetSelectionGroup){
                MwUtil.logInfo("Move %d markers from group<%s> to group<%s>",
                                                        counterGroup,this.currentGroup.getSelectionElementName(),
                                                        this.setSelectedGroup.textField.getText());

                flagSetSelectionGroup=false;
                this.setSelectionGroup.setIsChecked(false);
            }
            if(flagSetSelectionColor){
                MwUtil.logInfo("Changed color for  %d markers.", counterColor);
                flagSetSelectionColor=false;
                this.setSelectionColor.setIsChecked(false);
            }
            this.mw.markerManager.update();
            this.mw.markerManager.saveMarkersToFile();
            this.markerManageSlot.updateMarkerList(this.textField.getText());
        }

    }
   class ScrollableTextBox {
        public int x;
        public int y;
        public int width;
        // private int height;
        public int labelX;
        public int labelY;
        public int labelWidth;
        public int labelHeight;
        public String label;
        public boolean drawArrows = false;
        public int leftArrowX;
        public int rightArrowX;
        public int arrowsY;
        public int arrowsWidth = 7;
        public int arrowsHeight = 12;
        public int textFieldX;
        public int textFieldY;
        public int textFieldWidth;
        public int textFieldHeight = 12;
        public List<String> scrollableElements;
        public GuiTextField textField = null;

        ScrollableTextBox(int x, int y, int width, String label) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.label = label;
        }

        ScrollableTextBox(int x, int y, int width, String label,
                          List<String> scrollableElements) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.label = label;
            this.scrollableElements = scrollableElements;
        }

        public void init() {
            this.textFieldX = this.x + this.arrowsWidth;
            this.textFieldY = this.y;
            this.textFieldWidth = this.width - this.arrowsWidth * 2;
            this.labelWidth = MwGuiMarkerManage.this.fontRendererObj.getStringWidth(this.label);
            this.labelHeight = MwGuiMarkerManage.this.fontRendererObj.FONT_HEIGHT;
            this.labelX = this.x - this.labelWidth - 4;
            this.labelY = this.y + this.labelHeight / 2 - 2;
            this.leftArrowX = this.x - 1;
            this.rightArrowX = this.textFieldX + this.textFieldWidth + 1;
            this.arrowsY = this.y;
            this.textField = new GuiTextField(
                    MwGuiMarkerManage.this.fontRendererObj, this.textFieldX,
                    this.textFieldY, this.textFieldWidth, this.textFieldHeight);
            this.textField.setMaxStringLength(32);
        }

        public void draw() {
            MwGuiMarkerManage screen = MwGuiMarkerManage.this;
            screen.drawString(screen.fontRendererObj, this.label, this.labelX,
                    this.labelY, 0xffffff);
            if (this.drawArrows) {
                screen.mc.renderEngine.bindTexture(screen.leftArrowTexture);
                Render.drawTexturedRect(this.leftArrowX, this.arrowsY,
                        this.arrowsWidth, this.arrowsHeight, 0.0, 0.0, 1.0, 1.0);
                MwGuiMarkerManage.this.mc.renderEngine
                        .bindTexture(screen.rightArrowTexture);
                Render.drawTexturedRect(this.rightArrowX, this.arrowsY,
                        this.arrowsWidth, this.arrowsHeight, 0.0, 0.0, 1.0, 1.0);
            }
            this.textField.drawTextBox();
            if (!this.validateTextFieldData()) {
                drawRect(this.textFieldX - 1, this.textFieldY - 1,
                        this.textFieldX + this.textFieldWidth + 1,
                        this.textFieldY,
                        0xff900000);
                drawRect(this.textFieldX - 1, this.textFieldY - 1,
                        this.textFieldX, this.textFieldY + this.textFieldHeight	+ 1,
                        0xff900000);
                drawRect(this.textFieldX + this.textFieldWidth + 1,
                        this.textFieldY + this.textFieldHeight + 1,
                        this.textFieldX,
                        this.textFieldY + this.textFieldHeight,
                        0xff900000);
                drawRect(this.textFieldX + this.textFieldWidth + 1,
                        this.textFieldY + this.textFieldHeight + 1,
                        this.textFieldX + this.textFieldWidth, this.textFieldY,
                        0xff900000);
            }
        }

        public void mouseClicked(int x, int y, int button) {
            int direction = this.posWithinArrows(x, y);
            if (direction != 0)
                this.textFieldScroll(direction);
            this.textField.mouseClicked(x, y, button);
        }

        public void setDrawArrows(boolean value) {
            this.drawArrows = value;
        }

        public void mouseDWheelScrolled(int x, int y, int direction) {
            if (posWithinTextField(x, y))
                textFieldScroll(-direction);
        }

        public boolean validateTextFieldData() {
            return this.textField.getText().length() > 0;
        }

        /**
         *
         * @return Returns clicked arrow: 1 for right and -1 for left
         */
        public int posWithinArrows(int x, int y) {
            if ((x >= this.leftArrowX) && (y >= this.arrowsY)
                    && (x <= this.arrowsWidth + this.leftArrowX)
                    && (y <= this.arrowsHeight + this.arrowsY))
                return -1;
            else if ((x >= this.rightArrowX) && (y >= this.arrowsY)
                    && (x <= this.arrowsWidth + this.rightArrowX)
                    && (y <= this.arrowsHeight + this.arrowsY))
                return 1;
            else
                return 0;
        }

        public boolean posWithinTextField(int x, int y) {
            return (x >= this.textFieldX) && (y >= this.textFieldY)
                    && (x <= this.textFieldWidth + this.textFieldX)
                    && (y <= this.textFieldHeight + this.textFieldY);
        }

        public void textFieldScroll(int direction) {
            if (this.scrollableElements != null) {
                int index = this.scrollableElements.indexOf(this.textField
                        .getText().trim());
                if (direction > 0) {
                    if (index == -1
                            || index == this.scrollableElements.size() - 1)
                        index = 0;
                    else
                        index++;
                } else if (direction < 0) {
                    if (index == -1 || index == 0)
                        index = this.scrollableElements.size() - 1;
                    else
                        index--;
                }
                this.textField.setText(this.scrollableElements.get(index));
            }

            if (this.equals(MwGuiMarkerManage.this.currentGroup)){

            }
        }
    }
}
