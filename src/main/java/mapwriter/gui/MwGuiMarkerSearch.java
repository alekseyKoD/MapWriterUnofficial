package mapwriter.gui;

import mapwriter.Mw;
import mapwriter.Render;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

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
        this.markerSlot = new MwGuiMarkerSlot(this, this.mc, this.mw);

        this.textField = new MwGuiTextField(this.fontRendererObj, (this.width / 2) - 108, this.height - 28, 200, 20);
        this.textField.setMaxStringLength(20);
        this.textField.setFocused(true);
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
                EnumChatFormatting.UNDERLINE+(EnumChatFormatting.BOLD+I18n.format("mw.gui.mwgui.markers")), this.width / 2, 10, 0xffffff);

        this.textField.drawTextBox();

        int currentSlotIndex=this.markerSlot.getSlotIndexFromScreenCoords(this.markerSlot.width/2,mouseY);

        //detect top border on Slot
        int currentSlotTopY=mouseY;

        //Draw highlight active string
        if (mouseX>this.markerSlot.getStartPosX() && mouseY >= this.markerSlot.top &&
                mouseY <= this.markerSlot.bottom &&
                mouseX<=(this.markerSlot.getStartPosX()+this.markerSlot.getFullMarkerFieldWidth()) &&
                currentSlotIndex>=0){

                do {
                    currentSlotTopY--;
                } while (this.markerSlot.getSlotIndexFromScreenCoords(
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

           Render.setColour(0x30ffffff);
           Render.drawRect(this.markerSlot.getStartPosX()-5,currentSlotTopY,
                                this.markerSlot.getFullMarkerFieldWidth()+10,currentSlotBottomY-currentSlotTopY);


        }




        super.drawScreen(mouseX, mouseY, f);
    }
}
