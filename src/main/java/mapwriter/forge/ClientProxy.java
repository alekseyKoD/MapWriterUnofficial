package mapwriter.forge;

import java.io.File;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mapwriter.Mw;
import mapwriter.api.MwAPI;
import mapwriter.overlay.*;
import mapwriter.region.MwChunk;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.event.world.WorldEvent;

public class ClientProxy extends CommonProxy {

//	private MarkerStorage serverMarkerStorage;
	private MwConfig config;

	public void preInit(File configFile) {
		this.config = new MwConfig(configFile);
	}

	public void load() {
	//	MarkerStorage serverMarkerStorage = new MarkerStorage();
		Mw mw = new Mw(this.config);
		Object eventHandler = new EventHandler(mw);
		MinecraftForge.EVENT_BUS.register(eventHandler);
		FMLCommonHandler.instance().bus().register(eventHandler);
		//FMLCommonHandler.instance().bus().register(new MwServerHandler());

		Object eventKeyHandler = new MwKeyHandler();
		FMLCommonHandler.instance().bus().register(eventKeyHandler);
		MinecraftForge.EVENT_BUS.register(eventKeyHandler);

		// temporary workaround for user defined key bindings not being loaded
		// at game start. see https://github.com/MinecraftForge/FML/issues/378
		// for more info.
		Minecraft.getMinecraft().gameSettings.loadOptions();
	}

	public void postInit() {
		if (Loader.isModLoaded("CarpentersBlocks")) {
			MwChunk.carpenterdata();
		}
		if (Loader.isModLoaded("ForgeMultipart")) {
			MwChunk.FMPdata();
		}
		MwAPI.registerDataProvider("Slime", new OverlaySlime());
		MwAPI.registerDataProvider("ChunkGrid", new OverlayGrid());
		MwAPI.registerDataProvider("OreVeinGrid", new OverlayVeinsGrid());
		MwAPI.registerDataProvider("FluidsGrid", new OverlayFluidsGrid());
		MwAPI.registerDataProvider("RegionGrid", new OverlayRegionsGrid());
		// MwAPI.registerDataProvider("Checker", new OverlayChecker());
		// MwAPI.setCurrentDataProvider("Slime");
	}

	}
