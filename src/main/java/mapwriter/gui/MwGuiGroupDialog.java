package mapwriter.gui;

import mapwriter.Mw;
import mapwriter.map.Marker;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MwGuiGroupDialog extends MwGuiTextDialog {

    final Mw mw;


    public MwGuiGroupDialog(GuiScreen parentScreen, Mw mw) {
        super(parentScreen, I18n.format("mw.gui.mwguigroupdialog.title"),"",I18n.format("mw.gui.mwguigroupdialog.error"));
        this.mw = mw;

    }

    @Override
    public boolean submit() {
        boolean done = false;
        String oldGroupName=mw.markerManager.getVisibleGroupName();
        String newGroupName=this.getInputAsString();

        if (this.inputValid) {
            for (Marker marker : mw.markerManager.markerList) {
               if(marker.groupName.equals(oldGroupName)){
                    marker.setGroupName(newGroupName);
                    mw.markerManager.setVisibleGroupName(newGroupName);
               }
            }

            mw.markerManager.update();

            //save markers to file
            mw.markerManager.saveMarkersToFile();
            done = true;
        }
        return done;
    }
}
