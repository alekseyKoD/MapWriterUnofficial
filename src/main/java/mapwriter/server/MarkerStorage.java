package mapwriter.server;


import mapwriter.map.Marker;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

public class MarkerStorage {
  public static MarkerStorage instance;

  private HashMap<UUID,HashMap<Integer,Marker> >markerStorage= new HashMap<UUID, HashMap<Integer, Marker>>();

  private HashMap<UUID,HashMap<String,Integer> >groupStorage = new HashMap<UUID, HashMap<String, Integer>>();
  private HashMap<UUID,List<Integer> > groupOrderStorage=new HashMap<UUID, List<Integer>>();
  private HashMap<UUID,Integer> visibleGroupIndexStorage = new HashMap<UUID, Integer>();



  public MarkerStorage() {
        instance=this;
    }

  public HashMap<UUID, HashMap<Integer,Marker> > getMarkerStorage(){ return markerStorage; }

  public HashMap<UUID, HashMap<String, Integer>> getGroupStorage(){ return groupStorage; }

  public HashMap<UUID, List<Integer>> getGroupOrderStorage(){ return groupOrderStorage; }

  public HashMap<UUID, Integer> getVisibleGroupIndexStorage(){ return visibleGroupIndexStorage; }

  public HashMap<Integer, List<String>>getConvertedMarkerStorage(HashMap<Integer, Marker> markerStorage){

    HashMap<Integer,List<String>> convertedMarkerStorage=new HashMap<Integer, List<String>>();

    for(Map.Entry<Integer, Marker> marker: markerStorage.entrySet()){
      convertedMarkerStorage.put(marker.getKey(),marker.getValue().convertMarkerToArray(marker.getValue()));
    }

    return convertedMarkerStorage;
  }

  public void createNBTStructure(UUID playerUUID) {

    String[] groupOrder = new String[] {"all","none"};

    NBTTagCompound userMarkerStorage=new NBTTagCompound();
    NBTTagCompound userMarkerList=new NBTTagCompound();
    NBTTagList userStorageList=new NBTTagList();
    NBTTagList markerList=new NBTTagList();
    NBTTagList groupOrderList=new NBTTagList();
    NBTTagCompound userGroupOrder=new NBTTagCompound();
    NBTTagCompound groupName=new NBTTagCompound();


    for(int i=0; i<groupOrder.length; i++) {

      groupName.setTag(groupOrder[i], new NBTTagInt(i));
    }
    userGroupOrder.setTag("group order",groupName);
    groupOrderList.appendTag(userGroupOrder);

    userMarkerList.setTag("markers",markerList);

    userStorageList.appendTag(userGroupOrder);
    userStorageList.appendTag(userMarkerList);

    userMarkerStorage.setTag(playerUUID.toString(),userStorageList);

    NBTTagList testList=userMarkerStorage.getTagList(playerUUID.toString(),10);

    // userMarkerStorage.getTagList(playerUUID.toString(),10).getCompoundTagAt(0).getCompoundTag("group order")


  }

  public void saveMarkerOnServer(UUID playerUUID){

  }
/*
  код для формирование NBT
        private NBTTagCompound userMarkerStorage;

        String[] groupOrder = new String[] {"all","none","test_group","Common"};

		this.userMarkerStorage=new NBTTagCompound();

		NBTTagCompound userMarkerList=new NBTTagCompound();

		NBTTagList userStorageList=new NBTTagList();
		NBTTagList markerList=new NBTTagList();
		NBTTagList groupOrderList=new NBTTagList();
		NBTTagCompound userGroupOrder=new NBTTagCompound();
		NBTTagCompound groupName=new NBTTagCompound();


		for(int i=0; i<groupOrder.length; i++) {

			groupName.setTag(groupOrder[i], new NBTTagInt(i));
		}
		userGroupOrder.setTag("group order",groupName);
		groupOrderList.appendTag(userGroupOrder);

		userMarkerList.setTag("markers",markerList);

		userStorageList.appendTag(userGroupOrder);
		userStorageList.appendTag(userMarkerList);

		userMarkerStorage.setTag(this.mw.mc.thePlayer.getUniqueID().toString(),userStorageList);

		NBTTagList testList=this.userMarkerStorage.getTagList(this.mw.mc.thePlayer.getUniqueID().toString(),10);




  */


}
