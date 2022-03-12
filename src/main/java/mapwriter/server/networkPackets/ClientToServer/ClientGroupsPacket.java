package mapwriter.server.networkPackets.ClientToServer;


import hohserg.elegant.networking.api.ClientToServerPacket;
import hohserg.elegant.networking.api.ElegantPacket;
import mapwriter.Mw;
import mapwriter.MwUtil;
import mapwriter.forge.MwForge;
import mapwriter.Common;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.*;


@ElegantPacket
public class ClientGroupsPacket implements ClientToServerPacket {
    final Common.EnumGroupActionType actionType;
    HashMap<String,Integer> groupData=new HashMap<String, Integer>();
    List<Integer> groupOrderData=new ArrayList<Integer>();
    int visibleGroupIndex;


    public ClientGroupsPacket(Common.EnumGroupActionType actionType){
        this.actionType = actionType;
    }

    public ClientGroupsPacket(Common.EnumGroupActionType actionType, HashMap<String,Integer> groupData){
        this.actionType = actionType;
        this.groupData=groupData;
    }
    public ClientGroupsPacket(Common.EnumGroupActionType actionType, HashMap<String,Integer> groupData,
                                                                                List<Integer> groupOrderData){
        this.actionType = actionType;
        this.groupData=groupData;
        this.groupOrderData=groupOrderData;
    }

    public ClientGroupsPacket(Common.EnumGroupActionType actionType, HashMap<String,Integer> groupData,
                                                                List<Integer> groupOrderData, int visibleGroupIndex){
        this.actionType = actionType;
        this.groupData=groupData;
        this.groupOrderData=groupOrderData;
        this.visibleGroupIndex=visibleGroupIndex;
    }

    public ClientGroupsPacket(Common.EnumGroupActionType actionType, List<Integer> groupOrderData){
        this.actionType = actionType;
        this.groupOrderData=groupOrderData;
    }
    public ClientGroupsPacket(Common.EnumGroupActionType actionType, int visibleGroupIndex){
        this.actionType = actionType;
        this.visibleGroupIndex=visibleGroupIndex;
    }



    @Override

    public void onReceive(EntityPlayerMP player) {
        UUID playerUUID=player.getUniqueID();

        switch(this.actionType) {

            case ADD_SYSTEM_GROUP:
                //add system group (all & none) to server storage

                if (!MwForge.instance.getMwDataStorage().getGroupStorage().containsKey(playerUUID)) {
                    MwForge.instance.getMwDataStorage().getGroupStorage().put(playerUUID, new HashMap<String, Integer>());
                    MwForge.instance.getMwDataStorage().getGroupOrderStorage().put(playerUUID, new ArrayList<Integer>());

                }

                if(!MwForge.instance.getMwDataStorage().getGroupStorage().get(playerUUID).
                                                containsKey(Common.NONE_GROUP)){
                    MwForge.instance.getMwDataStorage().getGroupStorage().get(playerUUID).put(
                            Common.NONE_GROUP,
                            Common.NONE_INDEX);
                }
                if(!MwForge.instance.getMwDataStorage().getGroupStorage().get(playerUUID).
                        containsKey(Common.ALL_GROUP)){
                    MwForge.instance.getMwDataStorage().getGroupStorage().get(playerUUID).put(
                            Common.ALL_GROUP,
                            Common.ALL_INDEX);
                }
                break;

            case EDITGROUP:
                // add group list to server storage
                if (!MwForge.instance.getMwDataStorage().getGroupStorage().containsKey(playerUUID)) {
                    MwForge.instance.getMwDataStorage().getGroupStorage().put(playerUUID, new HashMap<String, Integer>());
                    MwForge.instance.getMwDataStorage().getGroupOrderStorage().put(playerUUID, new ArrayList<Integer>());

                }

                MwForge.instance.getMwDataStorage().getGroupStorage().get(playerUUID).clear();
                MwForge.instance.getMwDataStorage().getGroupStorage().get(playerUUID).putAll(this.groupData);

                // add order group list to server storage

                MwForge.instance.getMwDataStorage().getGroupOrderStorage().get(playerUUID).clear();
                MwForge.instance.getMwDataStorage().getGroupOrderStorage().get(playerUUID).addAll(this.groupOrderData);
                break;

            case CHANGEORDER:
                // add order group list to server storage
                if (!MwForge.instance.getMwDataStorage().getGroupOrderStorage().containsKey(playerUUID)) {
                    MwForge.instance.getMwDataStorage().getGroupOrderStorage().put(playerUUID, new ArrayList<Integer>());
                }
                MwForge.instance.getMwDataStorage().getGroupOrderStorage().get(playerUUID).clear();
                MwForge.instance.getMwDataStorage().getGroupOrderStorage().get(playerUUID).addAll(this.groupOrderData);
                break;

            case CHANGEVISIBLE:
                MwForge.instance.getMwDataStorage().getVisibleGroupIndexStorage().put(playerUUID, this.visibleGroupIndex);
                break;

            case DELETE:
                if (!MwForge.instance.getMwDataStorage().getGroupStorage().containsKey(playerUUID)) {
                    MwForge.instance.getMwDataStorage().getGroupStorage().put(playerUUID, new HashMap<String, Integer>());
                    MwForge.instance.getMwDataStorage().getGroupOrderStorage().put(playerUUID, new ArrayList<Integer>());
                }

                MwForge.instance.getMwDataStorage().getGroupStorage().get(playerUUID).clear();
                MwForge.instance.getMwDataStorage().getGroupStorage().get(playerUUID).putAll(this.groupData);
                MwForge.instance.getMwDataStorage().getGroupOrderStorage().get(playerUUID).clear();
                MwForge.instance.getMwDataStorage().getGroupOrderStorage().get(playerUUID).addAll(this.groupOrderData);
                MwForge.instance.getMwDataStorage().getVisibleGroupIndexStorage().put(playerUUID, this.visibleGroupIndex);
                break;
            default:
                break;
        }







    }
}

