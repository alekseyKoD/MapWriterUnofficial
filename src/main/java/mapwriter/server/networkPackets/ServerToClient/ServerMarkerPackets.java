package mapwriter.server.networkPackets.ServerToClient;

import cpw.mods.fml.common.FMLCommonHandler;
import hohserg.elegant.networking.api.ElegantPacket;
import hohserg.elegant.networking.api.ServerToClientPacket;
import mapwriter.Mw;
import mapwriter.map.Marker;
import mapwriter.Common;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ElegantPacket
public class ServerMarkerPackets implements ServerToClientPacket {
    final Common.EnumMarkerActionType actionType;
    HashMap<Integer, List<String>> markerData=new HashMap<Integer, List<String>>();
    List<Integer> groupOrderData =new ArrayList<Integer>();
    HashMap<String,Integer> groupData= new HashMap<String,Integer>();
    int visibleGroupIndexData;

    public ServerMarkerPackets(Common.EnumMarkerActionType actionType) {
        this.actionType=actionType;

    }

    public ServerMarkerPackets(Common.EnumMarkerActionType actionType,
                               HashMap<Integer,List<String>> markerData,
                               HashMap<String,Integer> groupData,
                               List<Integer> groupOrderData,
                               int visibleGroupIndexData ) {
        this.actionType=actionType;
        this.markerData=markerData;
        this.groupData=groupData;
        this.groupOrderData=groupOrderData;
        this.visibleGroupIndexData=visibleGroupIndexData;

    }

    @Override
    public void onReceive(Minecraft mc) {
       FMLCommonHandler.instance().getEffectiveSide();
       Mw.instance.markerManager.setGroupOrder(this.groupOrderData);
       Mw.instance.markerManager.setVisibleGroupIndex(this.visibleGroupIndexData);
       Mw.instance.markerManager.setGroupList(this.groupData);
       Mw.instance.markerManager.updateOrderedGroupList();


       for(Map.Entry <Integer,List<String>> entry: markerData.entrySet()){
        Mw.instance.markerManager.getMarkerList().put(entry.getKey(),new Marker(entry.getValue()) );

       }
    Mw.instance.markerManager.update();

    }
}