package mapwriter.server.networkPackets.ClientToServer;


import hohserg.elegant.networking.api.ClientToServerPacket;
import hohserg.elegant.networking.api.ElegantPacket;
import mapwriter.forge.MwForge;
import mapwriter.map.Marker;
import mapwriter.Common;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.*;


@ElegantPacket
public class ClientMarkersPacket implements ClientToServerPacket {
    final Common.EnumMarkerActionType actionType;
    int markerHash;
    int oldMarkerHash;
    List <String> markerData =new ArrayList<String>();
    HashMap<Integer ,List<String>> allMarkersData = new HashMap<Integer, List<String>>();

    public ClientMarkersPacket(Common.EnumMarkerActionType actionType){
        this.actionType = actionType;
    }

    public ClientMarkersPacket(Common.EnumMarkerActionType actionType,int hash){
        this.markerHash = hash;
        this.actionType = actionType;
    }


    public ClientMarkersPacket(Common.EnumMarkerActionType actionType, int hash, List<String> marker){
        this.markerHash = hash;
        this.actionType = actionType;
        this.markerData = marker;
    }
    public ClientMarkersPacket(Common.EnumMarkerActionType actionType,int oldHash,int hash, List<String> marker){
        this.markerHash = hash;
        this.oldMarkerHash=oldHash;
        this.actionType = actionType;
        this.markerData = marker;
    }
    public ClientMarkersPacket(Common.EnumMarkerActionType actionType,HashMap<Integer,List<String>> allMarkersData){
        this.actionType = actionType;
        this.allMarkersData = allMarkersData;
    }


    @Override

    public void onReceive(EntityPlayerMP player) {
        UUID playerUUID=player.getUniqueID();
        switch(this.actionType) {
            case ADD:
                if (!MwForge.instance.getMwDataStorage().getMarkerStorage().containsKey(playerUUID)) {
                    HashMap<Integer, Marker> markerMap = new HashMap<Integer, Marker>();
                    markerMap.put(this.markerHash, new Marker(this.markerData));
                    MwForge.instance.getMwDataStorage().getMarkerStorage().put(playerUUID, markerMap);
                }
                MwForge.instance.getMwDataStorage().getMarkerStorage().get(playerUUID).put(this.markerHash,
                                                                                            new Marker(this.markerData));
                break;
            case EDIT:
                MwForge.instance.getMwDataStorage().getMarkerStorage().get(playerUUID).remove(this.oldMarkerHash);
                MwForge.instance.getMwDataStorage().getMarkerStorage().get(playerUUID).put(this.markerHash,
                                                                                            new Marker(this.markerData));
                break;
            case DELETE:
                MwForge.instance.getMwDataStorage().getMarkerStorage().get(playerUUID).remove(this.markerHash);
                break;
            case SEND:
                if (!MwForge.instance.getMwDataStorage().getMarkerStorage().containsKey(playerUUID)) {
                    HashMap<Integer, Marker> markerMap = new HashMap<Integer, Marker>();
                    MwForge.instance.getMwDataStorage().getMarkerStorage().put(playerUUID, markerMap);
                } else MwForge.instance.getMwDataStorage().getMarkerStorage().get(playerUUID).clear();


                for(Map.Entry<Integer, List<String>> allMarkersMap: this.allMarkersData.entrySet() ){
                    MwForge.instance.getMwDataStorage().getMarkerStorage().get(playerUUID).put(allMarkersMap.getKey(),
                                                                                    new Marker(allMarkersMap.getValue()));

                }
        }








    }
}

