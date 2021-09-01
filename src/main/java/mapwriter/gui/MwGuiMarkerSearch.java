package mapwriter.gui;

import mapwriter.Mw;
import mapwriter.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MwGuiMarkerSearch extends GuiScreen {



    private final Mw mw;
    private final GuiScreen parentScreen;
    private MwGuiTextField textField = null;
    private MwGuiMarkerSlot markerSlot = null;

    public MwGuiMarkerSearch(GuiScreen parentScreen, Mw mw) {
        this.mw = mw;
        this.parentScreen = parentScreen;
    }

    public void initGui() {


        String previousSearchText=this.textField==null? "": this.textField.getText();

        this.markerSlot = new MwGuiMarkerSlot(this, this.mc, this.mw);
        this.textField = new MwGuiTextField(this.fontRendererObj, (this.width / 2) - 108, this.height - 28, 200, 20);
        this.textField.setMaxStringLength(20);
        this.textField.setText(previousSearchText);
        this.textField.setFocused(true);
        this.markerSlot.updateMarkerList(this.textField.getText());


    }

    protected void keyTyped(char c, int k) {
        if (k==Keyboard.KEY_ESCAPE){
            this.mc.displayGuiScreen(this.parentScreen);
        }else{
            super.keyTyped(c, k);
            this.textField.textboxKeyTyped(c, k);
            this.markerSlot.updateMarkerList(this.textField.getText());
        }
    }

    protected void mouseClicked(int x, int y, int btn) {

        super.mouseClicked(x, y, btn);
        this.textField.mouseClicked(x, y, btn);

    }

    public void updateScreen()
    {
        super.updateScreen();
        this.textField.updateCursorCounter();
    }

    public void drawScreen(int mouseX, int mouseY, float f) {

        this.drawDefaultBackground();
        this.markerSlot.drawScreen(mouseX, mouseY, f);

        this.drawCenteredString(this.fontRendererObj,
                EnumChatFormatting.UNDERLINE+(EnumChatFormatting.BOLD+I18n.format("mw.gui.mwgui.markers")),
                        this.width / 2, 10, 0xffffff);

        this.drawString(this.fontRendererObj,
                I18n.format("mw.gui.mwguimarkersearch.markersCount",String.valueOf(this.markerSlot.buttons.size())),
                this.markerSlot.getStartPosX(), this.markerSlot.bottom+2,0xffffff);


        this.textField.drawTextBox();

        int currentSlotIndex=this.markerSlot.func_148124_c(this.markerSlot.width/2,mouseY);

        //detect top border on Slot
        int currentSlotTopY=mouseY;

        if (this.markerSlot.isInsideMarkerSlots(mouseX,mouseY) && currentSlotIndex>=0){
                do {
                    currentSlotTopY--;
                } while (this.markerSlot.func_148124_c(
                                this.markerSlot.width/2,currentSlotTopY)==currentSlotIndex);

            int currentSlotBottomY=currentSlotTopY+this.markerSlot.getSlotHeight();

            //If the top border selected slot partially extends beyond the top border
            if (currentSlotTopY<=(this.markerSlot.top)){
                currentSlotBottomY=currentSlotTopY+this.markerSlot.getSlotHeight();
                currentSlotTopY=this.markerSlot.top;

            }

            //If the bottom border selected slot partially extends beyond the bottom border

            if (currentSlotBottomY>=(this.markerSlot.bottom)){
                currentSlotTopY=currentSlotBottomY-this.markerSlot.getSlotHeight();
                currentSlotBottomY=this.markerSlot.bottom;

            }

            int highlightBoxWidth=this.markerSlot.getDiffWidthSlotScrollBar()>0 ?
                                  this.markerSlot.getFullMarkerFieldWidth()+20 :
                                  this.markerSlot.getFullMarkerFieldWidth()+10+this.markerSlot.getDiffWidthSlotScrollBar();

           Render.setColour(0x30ffffff);
           Render.drawRect(this.markerSlot.getStartPosX()-5,currentSlotTopY,
                   highlightBoxWidth,currentSlotBottomY-currentSlotTopY);


        }




        super.drawScreen(mouseX, mouseY, f);
    }
}
