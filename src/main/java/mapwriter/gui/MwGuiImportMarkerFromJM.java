package mapwriter.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mapwriter.MwUtil;
import mapwriter.Render;
import mapwriter.map.Marker;
import mapwriter.map.MarkerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;


@SideOnly(Side.CLIENT)
public class MwGuiImportMarkerFromJM extends GuiScreen {
    private final GuiScreen parentScreen;
    private MarkerManager markerManager;
    private int x;
    private int y;
    private int elementYSpacing=10;
    private int textYSpacing=2;
    private File jmWaypointsCurrentFolder;


    private ResourceLocation leftArrowTexture = new ResourceLocation(
            "mapwriter", "textures/map/arrow_text_left.png");
    private ResourceLocation rightArrowTexture = new ResourceLocation(
            "mapwriter", "textures/map/arrow_text_right.png");


    public MwGuiImportMarkerFromJM(GuiScreen parentScreen, MarkerManager markerManager) {
        this.parentScreen = parentScreen;
        this.markerManager= markerManager;

    }

    @Override
    protected void keyTyped(char c, int key) {

        switch (key) {
            case Keyboard.KEY_ESCAPE:
                this.mc.displayGuiScreen(this.parentScreen);
                break;

        }
    }

    @Override
    public void initGui() {

        List worldNameList=new ArrayList();

        jmWaypointsCurrentFolder=MwUtil.getJMWaypointsFolder();

        int buttonWidth=10+this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.importButton"));
        int buttonHeight=20;
        int buttonChooseWidth=10+this.fontRendererObj.getStringWidth(
                                            I18n.format("mw.gui.mwguimportmarkerfromjm.chooseFolderButton"));



        int w=20+this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.title"));
        int h=5*this.elementYSpacing+5*this.textYSpacing+8*this.fontRendererObj.FONT_HEIGHT+35;
        int xPos=(this.width - w) / 2;
        int yPos=(this.height-h)/2;

        this.buttonList.add(new GuiButton(200,xPos , yPos+h-buttonHeight,
                buttonWidth,buttonHeight,I18n.format("mw.gui.mwguimportmarkerfromjm.importButton")));
        this.buttonList.add(new GuiButton(201,xPos+buttonWidth+5 , yPos+h-buttonHeight,
                buttonWidth,buttonHeight,I18n.format("mw.gui.mwguimportmarkerfromjm.cancelButton")));
        this.buttonList.add(new GuiButton(202,xPos+w-buttonChooseWidth , yPos+h-buttonHeight,
                buttonChooseWidth,buttonHeight, I18n.format("mw.gui.mwguimportmarkerfromjm.chooseFolderButton")));

    }

    public int getWidthMenu(){
        int[] maxWidth={
        this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.title")),
        this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.currentWorldTitle")),
        this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.jmWaypointsCount")),
        this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.text1")),
        this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.text2")),
        this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.text3")),
        this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.text4")),
        this.fontRendererObj.getStringWidth(I18n.format("mw.gui.mwguimportmarkerfromjm.text5"))
        };
        int max=maxWidth[0];
        for(int i=0;i<maxWidth.length; i++){
            max=Math.max(max,maxWidth[i]);
        }


        return max;
    }

    public File getFolderNameFromJmWorldName( List<MwUtil.JmWorldList> folderList,String worldName){
            File folderName=null;
       for(int i=0; i<folderList.size(); i++) {

           if (folderList.get(i).getWorldName().equals(worldName)) {
               folderName = folderList.get(i).getJmWorldFolder();
               break;
           } else folderName = null;
       }
       return folderName;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        int waypointCount;
        this.parentScreen.drawScreen(mouseX, mouseY, f);
        drawBackground(0);


        int w=20+getWidthMenu();

        int h=5*this.elementYSpacing+5*this.textYSpacing+8*this.fontRendererObj.FONT_HEIGHT+40;

        int xPos=(this.width - w) / 2;
        int yPos=(this.height-h)/2;


        if(this.jmWaypointsCurrentFolder.isDirectory() && this.jmWaypointsCurrentFolder.getName().equals("waypoints")
                                                        && this.jmWaypointsCurrentFolder.list().length>0){
            waypointCount=this.jmWaypointsCurrentFolder.list().length;
        }else waypointCount=0;



        Render.drawRectBorder(xPos,yPos, w, h,2);
        this.drawCenteredString(this.fontRendererObj,
                    EnumChatFormatting.UNDERLINE+ I18n.format("mw.gui.mwguimportmarkerfromjm.title"),
                        this.width/2,yPos+this.elementYSpacing,0xffffffff);

        this.drawString(this.fontRendererObj,
                    I18n.format("mw.gui.mwguimportmarkerfromjm.currentWorldTitle",
                            EnumChatFormatting.GREEN+MwUtil.getCurrentMcWorldName(),MwUtil.getGameMode()),
                xPos+5,yPos+2*this.elementYSpacing+this.fontRendererObj.FONT_HEIGHT,0xffffffff);

        this.drawString(this.fontRendererObj,
                    I18n.format("mw.gui.mwguimportmarkerfromjm.jmWaypointsCount",
                            EnumChatFormatting.GREEN+String.valueOf(waypointCount)),
                xPos+5,yPos+3*this.elementYSpacing+3*this.fontRendererObj.FONT_HEIGHT,0xffffffff);

       yPos+=5;
        this.drawString(this.fontRendererObj,
                I18n.format("mw.gui.mwguimportmarkerfromjm.text1"),
                xPos+5,yPos+4*this.elementYSpacing+3*this.fontRendererObj.FONT_HEIGHT,0xffffffff);
        this.drawString(this.fontRendererObj,
                I18n.format("mw.gui.mwguimportmarkerfromjm.text2"),
                xPos+5,yPos+4*this.elementYSpacing+this.textYSpacing+
                                                        4*this.fontRendererObj.FONT_HEIGHT,0xffffffff);
        this.drawString(this.fontRendererObj,
                I18n.format("mw.gui.mwguimportmarkerfromjm.text3"),
                xPos+5,yPos+4*this.elementYSpacing+2*this.textYSpacing+
                                                        5*this.fontRendererObj.FONT_HEIGHT,0xffffffff);
        this.drawString(this.fontRendererObj,
                I18n.format("mw.gui.mwguimportmarkerfromjm.text4"),
                xPos+5,yPos+4*this.elementYSpacing+3*this.textYSpacing+
                                                        6*this.fontRendererObj.FONT_HEIGHT,0xffffffff);
        this.drawString(this.fontRendererObj,
                I18n.format("mw.gui.mwguimportmarkerfromjm.text5"),
                xPos+5,yPos+4*this.elementYSpacing+4*this.textYSpacing+
                                                        7*this.fontRendererObj.FONT_HEIGHT,0xffffffff);


        super.drawScreen(mouseX, mouseY, f);
    }

    public void drawHelp(int mouseX,int mouseY) {
        drawRect(10, 20, this.width - 20, this.height - 30, 0x80000000);

    }




    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {

        if(p_146284_1_.id == 200){
            //import waypoints
            if (jmWaypointsCurrentFolder.isDirectory() && jmWaypointsCurrentFolder.list().length>0){
                   //clear all markers from import jM
                List<Marker> markedDeletedList = new ArrayList<Marker>();

                for( int i=0;i<this.markerManager.markerList.size();i++){
                  if (this.markerManager.markerList.get(i).groupName.equals("importJM")){
                    markedDeletedList.add(this.markerManager.markerList.get(i));
                  }
                }
                this.markerManager.markerList.removeAll(markedDeletedList);
                this.markerManager.update();

                MwUtil.importMarkersFromJourneymap(this.markerManager,jmWaypointsCurrentFolder);
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }else if(p_146284_1_.id == 201){
            //cancel import and close
            this.mc.displayGuiScreen(this.parentScreen);

        }else if(p_146284_1_.id == 202) {
            //Manual choose World folder with JFileChooswer
            String minecraftRootFolders= null;
            try {
                minecraftRootFolders = Minecraft.getMinecraft().mcDataDir.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jmDataFolder=minecraftRootFolders+"\\journeymap\\data\\";

            if (MwUtil.getGameMode().equals("sp")) { //sp
                jmDataFolder+="sp\\";

            } else { //mp

                jmDataFolder+="mp\\";
             }

            this.jmWaypointsCurrentFolder=MwUtil.chooseDirectory(jmDataFolder);


        }



    }

}
