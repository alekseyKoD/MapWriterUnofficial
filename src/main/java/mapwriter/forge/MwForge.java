package mapwriter.forge;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import mapwriter.server.MwSavedData;
import mapwriter.server.MarkerStorage;
import mapwriter.server.networkPackets.ServerToClient.ServerCheckerPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mapwriter.Mw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;


@Mod(modid="MapWriter", name="MapWriter", version="2.2.Exp_ServerEdition", acceptableRemoteVersions = "*")

public class MwForge {

    private MwSavedData mwWorldData;
    private MarkerStorage mwMarkerStorage;

        private final HashMap<UUID,MwSavedData> mwDataStorage=new HashMap<UUID, MwSavedData>();


	@Instance("MapWriter")
	public static MwForge instance;

	
	@SidedProxy(clientSide="mapwriter.forge.ClientProxy", serverSide="mapwriter.forge.CommonProxy")
	public static CommonProxy proxy;


	public static Logger logger = LogManager.getLogger("MapWriter");

    public MarkerStorage getMwMarkerStorage(){ return mwMarkerStorage; }


	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit(event.getSuggestedConfigurationFile());

	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.load();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
	
    @SubscribeEvent
    public void renderMap(RenderGameOverlayEvent.Post event){
        if(event.type == RenderGameOverlayEvent.ElementType.ALL){
            Mw.instance.onTick();
        }
    }

    @SubscribeEvent
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event){
    	if (!event.isLocal) {
    		InetSocketAddress address = (InetSocketAddress) event.manager.getSocketAddress();
    		Mw.instance.setServerDetails(address.getHostName(), address.getPort());
                	}
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){

        if (event.phase == TickEvent.Phase.START){
        	// run the cleanup code when Mw is loaded and the player becomes null.
        	// a bit hacky, but simpler than checking if the connection has closed.
            if ((Mw.instance.ready) && (Minecraft.getMinecraft().thePlayer == null)) {
                Mw.instance.close();
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.entity;
        UUID playerUUID=entity.getUniqueID();



        if (!entity.worldObj.isRemote && entity instanceof EntityPlayerMP) {
            //set flag, what MapWriter mod install and works on Server
            new ServerCheckerPacket(true).sendToPlayer((EntityPlayerMP)event.entity);

            if(!this.mwDataStorage.containsKey(playerUUID)){

                    this.mwDataStorage.put(playerUUID, new MwSavedData("MapWriter."+playerUUID.toString()));
                }

           /*
            if(this.mwWorldData==null){

                this.mwWorldData=new MwSavedData("MapWriter."+entity.getUniqueID().toString());
            }
            */
            this.mwDataStorage.get(playerUUID).setMwData(MwSavedData.get(event.world).getMwData());
            //this.mwWorldData.setMwData(this.mwWorldData.get(event.world).getMwData());
            //int count=this.mwWorldData.getSavedMarkerCount();
            int count=this.mwDataStorage.get(playerUUID).getSavedMarkerCount();
            System.out.println("save data to NBT");
            String markerName="marker"+ (count + 1);

            this.mwDataStorage.get(playerUUID).addMarkerData(playerUUID,markerName,100,count+1,100,16,0);
            this.mwDataStorage.get(playerUUID).markDirty();
            event.world.mapStorage.setData(this.mwDataStorage.get(playerUUID).getTagName(),this.mwDataStorage.get(playerUUID));


            /*
            this.mwWorldData.addMarkerData(markerName,100,count+1,100,16,0);
            this.mwWorldData.markDirty();
            event.world.mapStorage.setData(mwWorldData.getTagName(),this.mwWorldData);
            */
  //          this.getMwDataStorage().createNBTStructure(entity.getUniqueID());


        }


    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event){


        if (!event.world.isRemote && event.world.provider.dimensionId==0){

            mwMarkerStorage =new MarkerStorage();
        }

    }


    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {

        
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT) {
            //save markers on client side(save to local storage -file mapwriter.cfg)
            if (Mw.instance.lastSavedTick != Mw.instance.tickCounter) {
                Mw.instance.lastSavedTick = Mw.instance.tickCounter;
                if(Mw.instance.saveMarkersOnServer==0){
                    Mw.instance.saveCfgAndMarkers();
                }
            }
        }else{
            //save markers on server side (only integrated server)
            /*
                UUID playerUUID=Minecraft.getMinecraft().thePlayer.getUniqueID();
                if(event.world.provider.dimensionId==0){
                    mwMarkerStorage.createNBTStructure(playerUUID);
                }
           */

        }

    }


    //save markers on server side work only dedicated server on server-side
    @SubscribeEvent
    public  void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event){
    String uuid=event.player.getUniqueID().toString();

    }



    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
// this most certainly WILL fire, even in single player, see for yourself:
        int serverTick=1;
    }



}
