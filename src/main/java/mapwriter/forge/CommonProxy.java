package mapwriter.forge;

import mapwriter.server.MwSavedData;
import mapwriter.server.MarkerStorage;

import java.io.File;

public class CommonProxy {

	private MwSavedData mwWorldData;
	private MwConfig config;

	public void preInit(File configFile) {
		this.config = new MwConfig(configFile);

	}
	public void load() {
		MarkerStorage serverMarkerStorage = new MarkerStorage();

	}
	public void postInit() {}


}
