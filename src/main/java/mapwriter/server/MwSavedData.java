package mapwriter.server;

import mapwriter.Common;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.HashMap;
import java.util.UUID;

public class MwSavedData extends WorldSavedData {
    //private static  String TAG_NAME = "MapWriterData";
    private static String TAG_NAME;
    private NBTTagCompound mwData = new NBTTagCompound();

  //  NBTTagCompound userMarkerList=new NBTTagCompound();
  //  NBTTagList userStorageList=new NBTTagList();
  //  NBTTagList groupOrderList=new NBTTagList();
  //  NBTTagCompound userGroupOrder=new NBTTagCompound();
  //  NBTTagCompound groupName=new NBTTagCompound();


    public MwSavedData() {
        super(TAG_NAME);
    }

   public MwSavedData(String tagName) {
       super(tagName);
        this.TAG_NAME=tagName;
   }

    @Override
    public void readFromNBT(NBTTagCompound mwNbt) {

        mwData=mwNbt.getCompoundTag(TAG_NAME);
    }

    @Override
    public void writeToNBT(NBTTagCompound mwNbt) {
        mwNbt.setTag(TAG_NAME,mwData);
    }

    public void addMarkerData(UUID playerUUID, String markerName,int posX, int posY,int posZ, int color, int markerGroupIndex){
        NBTTagList tempOrderGroupList=new NBTTagList();
        NBTTagList tempMarkerList=new NBTTagList();

        NBTTagCompound userMarkerList=new NBTTagCompound();
        NBTTagList userStorageList=new NBTTagList();

        NBTTagCompound userGroupOrder=new NBTTagCompound();
        NBTTagList groupOrderList=new NBTTagList();




        tempMarkerList=this.mwData.getTagList("mwData",10).getCompoundTagAt(0).
                                                                        getTagList("markers",10);
        tempOrderGroupList=this.mwData.getTagList("mwData",10).getCompoundTagAt(1).
                                                                        getTagList("groups",10);

        /*
        if(this.mwData.getTagList("mwData",10).tagCount()!=0){
         this.mwData.getTagList("mwData",10).removeTag(0); // //remove NBT List "markers"
        }
        */
   //     userGroupOrder.setTag("groupsOrder",groupName);
   //     groupOrderList.appendTag(userGroupOrder);

        NBTTagCompound markerTagName=new NBTTagCompound();
        markerTagName.setString("name",markerName);
        markerTagName.setInteger("posX",posX);
        markerTagName.setInteger("posY",posY);
        markerTagName.setInteger("posZ",posZ);
        markerTagName.setInteger("color",color);
        markerTagName.setInteger("group",markerGroupIndex);

        tempMarkerList.appendTag(markerTagName);
        userMarkerList.setTag("markers",tempMarkerList);

        userStorageList.appendTag(userMarkerList);


        NBTTagCompound groupTag=new NBTTagCompound();
        groupTag.setString("groupName", Common.ALL_GROUP);
        groupTag.setInteger("groupIndex", Common.ALL_INDEX);
        groupTag.setInteger("groupOrder",0);
        tempOrderGroupList.appendTag(groupTag);

        groupTag=new NBTTagCompound();
        groupTag.setString("groupName", Common.NONE_GROUP);
        groupTag.setInteger("groupIndex", Common.NONE_INDEX);
        groupTag.setInteger("groupOrder",1);
        tempOrderGroupList.appendTag(groupTag);

        groupTag=new NBTTagCompound();
        groupTag.setString("groupName", "group2");
        groupTag.setInteger("groupIndex", 2);
        groupTag.setInteger("groupOrder",3);
        tempOrderGroupList.appendTag(groupTag);

        userGroupOrder.setTag("groups",tempOrderGroupList);


        userStorageList.appendTag(userGroupOrder);




    //    userStorageList.appendTag(userGroupOrder);




       // userMarkerStorage.setTag(playerUUID.toString(),userStorageList);
       // this.mwData.setTag(markerName,markerTagName);
        this.mwData.setTag("mwData",userStorageList);
    }

    public String getTagName(){return this.TAG_NAME ;}

    public int getSavedMarkerCount(){
        return this.mwData.getTagList("mwData",10).getCompoundTagAt(0).
                getTagList("markers",10).tagCount();

    }

    public NBTTagCompound getMwData(){ return mwData; }

    public void setMwData(NBTTagCompound data){
        this.mwData=data;
    }

    public static MwSavedData get(World world){
        MapStorage markerStorage= world.mapStorage;
        MwSavedData instance =(MwSavedData) markerStorage.loadData(MwSavedData.class,TAG_NAME);
        if(instance==null){
            instance= new MwSavedData();
            markerStorage.setData(TAG_NAME,instance);
        }
        return  instance;
    }
}
