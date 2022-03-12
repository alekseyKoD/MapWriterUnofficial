package mapwriter.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class MwSavedData extends WorldSavedData {
    //private static  String TAG_NAME = "MapWriterMarkers";
    private static String TAG_NAME;
    private NBTTagCompound mwData = new NBTTagCompound();

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

    public void addMarkerData(String markerName,int posX, int posY,int posZ, int color, int markerGroupIndex){

        NBTTagList mwMarkersList=new NBTTagList();
        NBTTagCompound markerTagName=new NBTTagCompound();
        markerTagName.setInteger("posX",posX);
        markerTagName.setInteger("posY",posY);
        markerTagName.setInteger("posZ",posZ);
        markerTagName.setInteger("color",color);
        markerTagName.setInteger("group",markerGroupIndex);
        this.mwData.setTag(markerName,markerTagName);
    }

    public String getTagName(){return this.TAG_NAME ;}

    public int getSavedMarkerCount(){
        NBTTagCompound nbt=new NBTTagCompound();
        nbt=this.getMwData();

        return nbt.func_150296_c().size();

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
