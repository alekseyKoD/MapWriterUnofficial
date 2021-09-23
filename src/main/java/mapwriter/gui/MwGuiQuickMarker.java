package mapwriter.gui;

import mapwriter.Mw;
import mapwriter.Render;
import mapwriter.map.UserPresetMarker;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import java.util.HashMap;
import java.util.Map;

public class MwGuiQuickMarker extends GuiScreen {
    private Mw mw;
    private boolean drawPresetMarker=false;
    private boolean createMarker=false;
    private int pressedKeyIndex=0;
    private int pressedGroupKeyIndex=0;
    private int maxNameWidthInPix=0;
    private String newMarkerKeyDesc;
    private String exitGuiKeyDesc="ESCAPE";
    private String backToGroupKeyDesc="BACKSPACE";

    private HashMap<Integer, String> userPresetGroup=new HashMap<Integer, String>();
    private HashMap<String, HashMap<Integer,UserPresetMarker> > userPresetMarkerInGroup=new HashMap<String, HashMap<Integer,UserPresetMarker>>();
    private  HashMap<Integer, UserPresetMarker> selectedPresetMarker = new HashMap<Integer, UserPresetMarker>();


    public MwGuiQuickMarker(Mw mw) {
        this.mw=mw;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {

        int buttonWidth=Math.max(this.fontRendererObj.getStringWidth(this.newMarkerKeyDesc),
                this.fontRendererObj.getStringWidth(this.exitGuiKeyDesc));

        buttonWidth=Math.max(buttonWidth,this.fontRendererObj.getStringWidth(this.backToGroupKeyDesc));

        int buttonHeight=12;
        int windowsWidth=this.maxNameWidthInPix+buttonWidth+15;
        String quickKeyDesc;
        String quickGroupDesc;
        String quickMarkerDesc;

        int posX=0;
        int posY=5;
        drawRect(0,0,windowsWidth,this.height,0x80000000);

        //draw title
        posX=windowsWidth/2-this.fontRendererObj.getStringWidth(I18n.format("mwgui.mwguiquickmarker.title"))/2;
        drawString(this.fontRendererObj,I18n.format("mwgui.mwguiquickmarker.title"),posX,posY,0xffffff);

        //draw horizontal line
        posY+=10;
        drawRect(4,posY,windowsWidth-5,posY+1,0xffffffff);

        posY+=5;

        Render.setColour(0xffffffff);
        Render.drawRectBorder(5,posY,buttonWidth+2,buttonHeight,1);
        drawString(this.fontRendererObj,exitGuiKeyDesc,
                (buttonWidth+10)/2-this.fontRendererObj.getStringWidth(exitGuiKeyDesc)/2+2,
                (posY+3),0xffffff);
        posY+=3;
        drawString(this.fontRendererObj,I18n.format("mwgui.mwguiquickmarker.ExitLabel"),
                buttonWidth+12,posY,0xffffff);

        posY+=buttonHeight;
        Render.setColour(0xffffffff);
        Render.drawRectBorder(5,posY,buttonWidth+2,buttonHeight,1);
        drawString(this.fontRendererObj,backToGroupKeyDesc,
                (buttonWidth+10)/2-this.fontRendererObj.getStringWidth(backToGroupKeyDesc)/2+2,
                (posY+3),0xffffff);

        if(this.drawPresetMarker){
            //draw Label "back to group"
            drawString(this.fontRendererObj,I18n.format("mwgui.mwguiquickmarker.prevLevel"),
                    buttonWidth+12,posY+3,0xffffff);
        }

        posY+=buttonHeight+3;
        Render.setColour(0xffffffff);
        Render.drawRectBorder(5,posY,buttonWidth+2,buttonHeight,1);
        drawString(this.fontRendererObj,newMarkerKeyDesc,
                (buttonWidth+10)/2-this.fontRendererObj.getStringWidth(newMarkerKeyDesc)/2+2,
                (posY+3),0xffffff);
        posY+=3;
        drawString(this.fontRendererObj,I18n.format("mwgui.mwguiquickmarker.markerdialog"),
                buttonWidth+12,posY,0xffffff);

        //draw horizontal line
        posY+=buttonHeight;
        drawRect(4,posY,windowsWidth-5,posY+1,0xffffffff);


        posY+=20;
        if(!this.drawPresetMarker){
            //draw group label
            posX=windowsWidth/2-this.fontRendererObj.getStringWidth(I18n.format("mwgui.mwguiquickmarker.grouptitle"))/2;

            drawString(this.fontRendererObj,I18n.format("mwgui.mwguiquickmarker.grouptitle"),posX,posY,0xffffff);

        }else {
            //draw marker label
            posX=windowsWidth/2-this.fontRendererObj.getStringWidth(I18n.format("mwgui.mwguiquickmarker.markertitle"))/2;

            drawString(this.fontRendererObj,I18n.format("mwgui.mwguiquickmarker.markertitle"),posX,posY,0xffffff);
        }


        //draw horizontal line
        posY+=10;
        drawRect(4,posY,windowsWidth-5,posY+1,0xffffffff);


        posY+=3;




        if(this.userPresetGroup.containsKey(this.pressedKeyIndex)){
            String keyMap=this.userPresetGroup.get(this.pressedKeyIndex);
            if(this.userPresetMarkerInGroup.containsKey(keyMap)){
                  this.selectedPresetMarker=this.userPresetMarkerInGroup.get(keyMap);
            }


        }




        for(int i=0; i<10; i++){
            Render.setColour(0xffffffff);
            Render.drawRectBorder(5,posY+i*(buttonHeight+3),buttonHeight,buttonHeight,1);

            //draw quick key group desc
            if(!this.drawPresetMarker){
                if(this.userPresetGroup.containsKey(i)){
                       quickGroupDesc=this.userPresetGroup.get(i);
                       quickKeyDesc=(i+1)!=10 ? String.valueOf(i+1) : String.valueOf(0);
                       drawString(this.fontRendererObj,quickKeyDesc,8,posY+i*(buttonHeight+3)+3,0xffffff);

                }else {
                       quickGroupDesc="EmptySlot";

                       }

                drawString(this.fontRendererObj,quickGroupDesc,buttonHeight+12,
                                                                posY+i*(buttonHeight+3)+3,0xffffff);
            }else{
                //draw quick key marker desc
                if(this.selectedPresetMarker.containsKey(i) ){
                    quickMarkerDesc=this.selectedPresetMarker.get(i).getPresetMarkerName();
                    quickKeyDesc=(i+1)!=10 ? String.valueOf(i+1) : String.valueOf(0);

                    drawRect(5,
                            posY+i*(buttonHeight+3),
                            5+buttonHeight,
                            posY+i*(buttonHeight+3)+buttonHeight,
                                      this.selectedPresetMarker.get(i).getColor());

                    drawString(this.fontRendererObj,quickKeyDesc,8,
                                                        posY+i*(buttonHeight+3)+3,0xffffff);

                }else {
                    quickMarkerDesc="EmptySlot";
                }
                drawString(this.fontRendererObj,quickMarkerDesc,buttonHeight+12,
                        posY+i*(buttonHeight+3)+3,0xffffff);
            }




        }
    super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    protected void keyTyped(char c, int key) {

        String markerName="";
        String activePresetGroupName="";
        int markerColor=0;

        switch(key){
            case Keyboard.KEY_ESCAPE:

                    this.mc.displayGuiScreen(null);
                    break;

            case Keyboard.KEY_BACK:
                    if(this.drawPresetMarker){
                        this.drawPresetMarker=false;
                        this.createMarker=false;
                    }
                    break;

            case Keyboard.KEY_INSERT:

                // open new marker dialog
                this.mc.displayGuiScreen(null);

                String group = this.mw.markerManager.getVisibleGroupName();
                if (group.equals("none")) {
                    group = "group";
                }
                if (this.mw.newMarkerDialog)
                {
                    this.mc.displayGuiScreen(
                            new MwGuiMarkerDialogNew(
                                    null,
                                    this.mw.markerManager,
                                    "",
                                    group,
                                    this.mw.playerXInt,
                                    this.mw.playerYInt,
                                    this.mw.playerZInt,
                                    this.mw.playerDimension
                            )
                    );
                }
                else
                {
                    this.mc.displayGuiScreen(
                            new MwGuiMarkerDialog(
                                    null,
                                    this.mw.markerManager,
                                    "",
                                    group,
                                    this.mw.playerXInt,
                                    this.mw.playerYInt,
                                    this.mw.playerZInt,
                                    this.mw.playerDimension
                            )
                    );
                }


            default:

                if(key>1 && key<12){
                    this.pressedKeyIndex=c!='0' ? Integer.parseInt(String.valueOf(c))-1 : 9;

                   if(!this.drawPresetMarker) {
                       this.drawPresetMarker = true;

                       if(this.userPresetGroup.containsKey(this.pressedKeyIndex) ){

                           this.pressedGroupKeyIndex=this.pressedKeyIndex;


                       }else {
                           this.drawPresetMarker = false;
                       }

                   }else{
                       if(this.userPresetGroup.containsKey(this.pressedGroupKeyIndex) ){
                           activePresetGroupName=this.userPresetGroup.get(this.pressedGroupKeyIndex);

                           if(this.userPresetMarkerInGroup.containsKey(activePresetGroupName)){
                               this.selectedPresetMarker=this.userPresetMarkerInGroup.get(activePresetGroupName);

                               if(this.selectedPresetMarker.containsKey(this.pressedKeyIndex)){
                                   markerName=this.selectedPresetMarker.get(this.pressedKeyIndex).getPresetMarkerName();
                                   markerColor=this.selectedPresetMarker.get(this.pressedKeyIndex).getColor();
                                   this.createMarker=true;
                               }else {
                                   this.createMarker = false;
                                   this.pressedKeyIndex=-1;

                                    }


                           }
                       }

                   }
               }

                break;
        }


        if(this.drawPresetMarker && this.createMarker){

            //create new marker

            this.mc.displayGuiScreen(null);
            this.createMarker=false;

            this.mw.markerManager.addMarker(markerName,
                                            activePresetGroupName,
                                            this.mw.playerXInt,
                                            this.mw.playerYInt,
                                            this.mw.playerZInt,
                                            this.mw.playerDimension,
                                            markerColor);
            this.mw.markerManager.update();
            //save markers to file
            this.mw.markerManager.saveMarkersToFile();



        }



        super.keyTyped(c, key);
    }

    @Override
    public void initGui() {

        this.userPresetGroup=this.mw.markerManager.userPresetGroup;
        this.userPresetMarkerInGroup=this.mw.markerManager.userPresetMarker;

        this.maxNameWidthInPix=this.fontRendererObj.getStringWidth("EmptySlot");
        String[] dialogText={
                I18n.format("mwgui.mwguiquickmarker.title"),
                I18n.format("mwgui.mwguiquickmarker.markertitle"),
                I18n.format("mwgui.mwguiquickmarker.grouptitle"),
                I18n.format("mwgui.mwguiquickmarker.ExitLabel"),
                I18n.format("mwgui.mwguiquickmarker.prevLevel"),
                I18n.format("mwgui.mwguiquickmarker.markerdialog")
        };

        for(int i=0; i< dialogText.length; i++){

            this.maxNameWidthInPix=Math.max(this.maxNameWidthInPix,this.fontRendererObj.getStringWidth(dialogText[i]));

        }

        //calc max width group name in pixels

        for(Map.Entry<Integer, String>  groupMapEntry : this.mw.markerManager.userPresetGroup.entrySet()){

            this.maxNameWidthInPix=Math.max(this.fontRendererObj.getStringWidth(groupMapEntry.getValue()),this.maxNameWidthInPix);
        }

        //calc max width marker name in pixels

        for(Map.Entry<String, HashMap<Integer,UserPresetMarker>> markerEntry : this.mw.markerManager.userPresetMarker.entrySet()){
            for(Map.Entry<Integer, UserPresetMarker>  markersMapEntry : markerEntry.getValue().entrySet()){
                this.maxNameWidthInPix=Math.max( this.fontRendererObj.getStringWidth(markersMapEntry.getValue().getPresetMarkerName()),
                                            this.maxNameWidthInPix);
            }
        }
        for (KeyBinding key: this.mc.gameSettings.keyBindings){

            if (key.getKeyDescription().equals("key.mw_new_marker")){
                newMarkerKeyDesc=Keyboard.getKeyName(key.getKeyCode());
                break;


            }
        }


    }
}
