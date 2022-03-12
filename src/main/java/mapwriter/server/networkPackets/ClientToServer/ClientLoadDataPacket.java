package mapwriter.server.networkPackets.ClientToServer;


import hohserg.elegant.networking.api.ClientToServerPacket;
import hohserg.elegant.networking.api.ElegantPacket;
import mapwriter.forge.MwForge;
import mapwriter.map.Marker;
import mapwriter.Common;
import mapwriter.server.networkPackets.ServerToClient.ServerMarkerPackets;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.*;


@ElegantPacket
public class ClientLoadDataPacket implements ClientToServerPacket {
    final Common.EnumServerActionType actionType;



    public ClientLoadDataPacket(Common.EnumServerActionType actionType){
        this.actionType = actionType;
    }



    @Override

    public void onReceive(EntityPlayerMP player) {

        switch (this.actionType){
            case SYNC:
                //load marker`s info, group`s info and preset marker`s from server storage to client storage
                if(MwForge.instance.getMwDataStorage().getMarkerStorage().containsKey(player.getUniqueID()) &&
                        MwForge.instance.getMwDataStorage().getMarkerStorage().get(player.getUniqueID()).size()>0 ) {


                    HashMap<Integer, Marker> userMarkerStorage = new HashMap<Integer, Marker>
                            (MwForge.instance.getMwDataStorage().getMarkerStorage().get(player.getUniqueID()));
                    new ServerMarkerPackets(Common.EnumMarkerActionType.LOAD,
                            MwForge.instance.getMwDataStorage().getConvertedMarkerStorage(userMarkerStorage),
                            MwForge.instance.getMwDataStorage().getGroupStorage().get(player.getUniqueID()),
                            MwForge.instance.getMwDataStorage().getGroupOrderStorage().get(player.getUniqueID()),
                            MwForge.instance.getMwDataStorage().getVisibleGroupIndexStorage().get(player.getUniqueID()))
                            .sendToPlayer(player);
                }
                break;

        }
    }
}

