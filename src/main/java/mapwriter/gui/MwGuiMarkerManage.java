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
    private ScrollableTextBox currentGroup=null;
    private ScrollableTextBox setSelectedName=null;
    private ScrollableTextBox setSelectedGroup=null;
    private MwColorPallete setSelectedColor=null;
    private int[] colours=null;
    boolean backToGameOnSubmit = false;
    private boolean selectAllFalg=false;
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


        int setSelectedNameStartPosY=20+this.markerManageSlot.bottom+2*this.fontRendererObj.FONT_HEIGHT+elementVSpacing;
        int setSelectedGroupStartPosY=setSelectedNameStartPosY+elementVSpacing;
        int setSelectedColorStartPosY=setSelectedGroupStartPosY+elementVSpacing;

        this.currentGroup=new ScrollableTextBox(20+ this.fontRendererObj.getStringWidth(currentGroupLabel),
                                this.markerManageSlot.top-this.fontRendererObj.FONT_HEIGHT-elementVSpacing,
                             this.width/100*20,currentGroupLabel, this.mw.markerManager.groupList);

        this.currentGroup.init();
        this.currentGroup.textField.setText(this.mw.markerManager.getVisibleGroupName());
        this.currentGroup.setDrawArrows(true);
        this.currentGroup.textField.setEnabled(false);


        this.textField = new MwGuiTextField(this.fontRendererObj,
                this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimarkermanage.markersCount"))+20,
                this.markerManageSlot.bottom+5, this.markerManageSlot.getMarkerNameFieldWidth(),16);
        this.textField.setMaxStringLength(20);
        this.textField.setText(previousSearchText);
        this.textField.setFocused(true);

        this.setSelectedName=new ScrollableTextBox(20+this.LeftScreenBorderShift, setSelectedNameStartPosY,
                5*(this.colours.length-1)+this.currentGroup.textFieldHeight*this.colours.length+14, //14 is 2*arrowsWidth
                      setSelectedNameLabel);
        this.setSelectedName.init();
        this.setSelectedName.setDrawArrows(false);

        this.setSelectedGroup=new ScrollableTextBox(20+this.LeftScreenBorderShift, setSelectedGroupStartPosY,
                5*(this.colours.length-1)+this.currentGroup.textFieldHeight*this.colours.length+14, //14 is 2*arrowsWidth
                      setSelectedGroupLabel,this.mw.markerManager.groupList);
        this.setSelectedGroup.init();
        this.setSelectedGroup.textField.setText(this.mw.markerManager.getVisibleGroupName());
        this.setSelectedGroup.setDrawArrows(true);

        this.setSelectedColor=new MwColorPallete(20+this.LeftScreenBorderShift+this.setSelectedGroup.arrowsWidth,
                                    setSelectedColorStartPosY,
                                    this.setSelectedGroup.textFieldHeight,this.colours,this.colours[0],
                                    this.width,0);

        // add a button which selected all visible marker in markerslot
        this.buttonList.add(new GuiButton(200, this.markerManageSlot.getStartPosX(), this.markerManageSlot.top-14,
                10,10,"x"));

        //add a button to assign a name to selected markers
        this.buttonList.add(new GuiButton(201,20+this.LeftScreenBorderShift+this.setSelectedName.width,
                setSelectedNameStartPosY-2*textfieldFrameThicknessInPx,this.assignButtonWidth,
                this.setSelectedName.textFieldHeight+2*textfieldFrameThicknessInPx,"OK"));

        //add a button to assign a group to selected markers
        this.buttonList.add(new GuiButton(202,20+this.LeftScreenBorderShift+this.setSelectedGroup.width,
                                setSelectedGroupStartPosY-2*textfieldFrameThicknessInPx,this.assignButtonWidth,
                        this.setSelectedGroup.textFieldHeight+2*textfieldFrameThicknessInPx,"OK"));
        //add a button to assign a color to selected markers
        this.buttonList.add(new GuiButton(203,20+this.LeftScreenBorderShift+this.setSelectedGroup.width,
                                setSelectedColorStartPosY-2*textfieldFrameThicknessInPx,this.assignButtonWidth,
                        this.setSelectedColor.getPalleteHeight()+2*textfieldFrameThicknessInPx,"OK"));
        //add a button for import markers from JourneyMap`s waypoints

        int importJMButtonWidth=10+this.fontRendererObj.
                                getStringWidth(I18n.format("mw.gui.mwguimarkermanage.importFtomJMButton"));
        this.buttonList.add(new GuiButton(204,this.width-importJMButtonWidth-2,
                    this.markerManageSlot.top-20, importJMButtonWidth,16,
                                I18n.format("mw.gui.mwguimarkermanage.importFtomJMButton")));





        this.markerManageSlot.updateMarkerList(this.textField.getText());


    }

    protected void keyTyped(char c, int k) {
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
        this.currentGroup.mouseDWheelScrolled(x, y, direction);
        this.setSelectedGroup.mouseDWheelScrolled(x, y, direction);
        for(int i=0;i<this.setSelectedColor.colorCells.size();i++){
            this.setSelectedColor.colorCells.get(i).mouseDWheelScrolled(x,y,direction);
        }

    }


    protected void mouseClicked(int x, int y, int button) {

        super.mouseClicked(x, y, button);
        this.textField.mouseClicked(x, y, button);
        this.currentGroup.mouseClicked(x, y, button);
        this.setSelectedName.mouseClicked(x, y, button);
        this.setSelectedGroup.mouseClicked(x, y, button);
        for(int i=0;i<this.setSelectedColor.colorCells.size();i++){
            this.setSelectedColor.colorCells.get(i).mouseClicked(x,y,button);
        }


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

        this.currentGroup.draw();

        this.drawString(this.fontRendererObj,
                (I18n.format("mw.gui.mwguimarkermanage.markersCount",
                                    String.valueOf(this.markerManageSlot.checkBoxes.size()))),
               10,this.textField.yPosition+(this.textField.height-this.fontRendererObj.FONT_HEIGHT+1),0xffffff);

        this.drawString(this.fontRendererObj, this.ActionsWithSelectedLabel,10,
                this.markerManageSlot.bottom+30,0xffffff);

        Render.setColour(0xffffffff);
        Render.drawRectBorder(10,30+this.markerManageSlot.bottom+this.fontRendererObj.FONT_HEIGHT+2,
                                            this.LeftScreenBorderShift+this.setSelectedGroup.width+50,
                (this.setSelectedColor.getY()+this.setSelectedColor.getPalleteHeight())-
                        (30+this.markerManageSlot.bottom+this.fontRendererObj.FONT_HEIGHT+2)+5,1f);

        this.setSelectedName.draw();
        this.setSelectedGroup.draw();
        this.setSelectedColor.draw();

        this.drawString(this.fontRendererObj,
                this.selectAllFalg ? I18n.format("mw.gui.mwguimarkermanage.selectAllButtonOff"):
                                     I18n.format("mw.gui.mwguimarkermanage.selectAllButtonOn"),
                this.markerManageSlot.getStartPosX()+15,this.markerManageSlot.top-12,0xffffff);

        this.drawString(this.fontRendererObj,this.setSelectedColorLabel,20,
                //this.markerManageSlot.bottom+2*this.fontRendererObj.FONT_HEIGHT+3*elementVSpacing,0xffffff);
                this.setSelectedColor.getY()+2,0xffffff);

        this.textField.drawTextBox();


        super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        if(p_146284_1_.id == 200){
            //select all markers
            int counter=0;
            for (Map.Entry<Integer,Boolean> pair: this.markerManageSlot.checkboxesEnabled.entrySet()) {

                this.markerManageSlot.checkBoxes.get(counter).setIsChecked(!this.selectAllFalg);
                pair.setValue(!this.selectAllFalg);
                counter++;

            }
            this.selectAllFalg=!this.selectAllFalg;

        }else if (p_146284_1_.id == 201) {
            // assign name to selected markers
            if (this.setSelectedName.validateTextFieldData()) {
                this.assignNameToSelectedMarkers(setSelectedName.textField.getText());
            }
        } else if(p_146284_1_.id == 202){
            // assign group to selected markers
            if (this.setSelectedGroup.validateTextFieldData()) {
                this.assignGroupToSelectedMarkers(setSelectedGroup.textField.getText());
            }


        }else if(p_146284_1_.id == 203){
            // assign color to selected markers
            assignColorToSelectedMarkers(this.setSelectedColor.getSelectedColor());

        }else if(p_146284_1_.id == 204){
        // Open import JM waypoints GUI

        //    MwUtil.chooseDirectory();

        this.mc.displayGuiScreen(new MwGuiImportMarkerFromJM(this,this.mw.markerManager));

    }


    }

    public void assignNameToSelectedMarkers(String markerName){

        String oldMarkerName="";
        int counter=0;
        for (Map.Entry<Integer,Boolean> mapPair: this.markerManageSlot.checkboxesEnabled.entrySet()){
            if (mapPair.getValue()){
                int Index=this.markerManageSlot.checkboxesId.get(mapPair.getKey());
                oldMarkerName=this.markerManageSlot.markerList.get(Index).name;
                this.markerManageSlot.markerList.get(Index).setMarkerName(markerName);
                counter++;
            }
        }

        this.saveMarkers();
        this.selectAllFalg=false;
        MwUtil.logInfo("Rename %d markers. Old name<%s>, new name<%s>",counter,oldMarkerName, markerName);
    }

    public void assignGroupToSelectedMarkers(String group){

        int counter=0;
        for (Map.Entry<Integer,Boolean> mapPair: this.markerManageSlot.checkboxesEnabled.entrySet()){

            if (mapPair.getValue()){
                int Index=this.markerManageSlot.checkboxesId.get(mapPair.getKey());
                this.markerManageSlot.markerList.get(Index).setGroupName(group);
                counter++;
            }
        }
        this.saveMarkers();
        this.selectAllFalg=false;
        MwUtil.logInfo("Move %d markers from group<%s> to group<%s>",
                                counter,this.currentGroup.textField.getText(), group);
    }

    public void assignColorToSelectedMarkers(int color){
        int counter=0;
        for (Map.Entry<Integer,Boolean> mapPair: this.markerManageSlot.checkboxesEnabled.entrySet()){
            if (mapPair.getValue()){
                int Index=this.markerManageSlot.checkboxesId.get(mapPair.getKey());
                this.markerManageSlot.markerList.get(Index).setColour(color);
                counter++;
            }
        }
        this.saveMarkers();
        this.selectAllFalg=false;
        MwUtil.logInfo("Changed color for  %d markers.", counter);
    }

    public void saveMarkers(){
        this.mw.markerManager.update();
        this.mw.markerManager.saveMarkersToFile();
        this.markerManageSlot.updateMarkerList(this.textField.getText());
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
                MwGuiMarkerManage.this.mw.markerManager.setVisibleGroupName(this.textField.getText());
                MwGuiMarkerManage.this.mw.markerManager.update();
                MwGuiMarkerManage.this.markerManageSlot.markerList=MwGuiMarkerManage.this.mw.markerManager.visibleMarkerList;
                MwGuiMarkerManage.this.markerManageSlot.updateMarkerList(MwGuiMarkerManage.this.textField.getText());
            }
        }
    }
}
