package mapwriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mapwriter.forge.MwForge;
import mapwriter.map.Marker;
import mapwriter.map.MarkerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.StringUtils;
import cpw.mods.fml.common.FMLCommonHandler;

import javax.swing.*;


public class MwUtil {
	
	public final static Pattern patternInvalidChars = Pattern.compile("[^\\p{IsAlphabetic}\\p{Digit}_]");
	
	public static void logInfo(String s, Object...args) {
		MwForge.logger.info(String.format(s, args));
	}
	
	public static void logWarning(String s, Object...args) {
		MwForge.logger.warn(String.format(s, args));
	}
	
	public static void logError(String s, Object...args) {
		MwForge.logger.error(String.format(s, args));
	}
	
	public static void debug(String s, Object...args) {
		MwForge.logger.debug(String.format(s, args));
	}
	
	public static void log(String s, Object...args) {
		logInfo(String.format(s, args));
	}
	
	public static String mungeString(String s) {
		s = s.replace('.', '_');
		s = s.replace('-', '_');
		s = s.replace(' ',  '_');
		s = s.replace('/',  '_');
		s = s.replace('\\',  '_');
		return patternInvalidChars.matcher(s).replaceAll("");
	}
	
	public static File getFreeFilename(File dir, String baseName, String ext) {
		int i = 0;
		File outputFile;
		if (dir != null) {
			outputFile = new File(dir, baseName + "." + ext);
		} else {
			outputFile = new File(baseName + "." + ext);
		}
		while (outputFile.exists() && (i < 1000)) {
			if (dir != null) {
				outputFile = new File(dir, baseName + "." + i + "." + ext);
			} else {
				outputFile = new File(baseName + "." + i + "." + ext);
			}
			i++;
		}
		return (i < 1000) ? outputFile : null;
	}
	
	public static void printBoth(String msg) {
		EntityClientPlayerMP thePlayer = Minecraft.getMinecraft().thePlayer;
		if (thePlayer != null) {
			thePlayer.addChatMessage(new ChatComponentText(msg));
		}
		MwUtil.log("%s", msg);
	}
	
	public static File getDimensionDir(File worldDir, int dimension) {
		File dimDir;
		if (dimension != 0) {
			dimDir = new File(worldDir, "DIM" + dimension);
		} else {
			dimDir = worldDir;
		}
		return dimDir;
	}
	
	public static IntBuffer allocateDirectIntBuffer(int size) {
		return ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
	}
	
	// algorithm from http://graphics.stanford.edu/~seander/bithacks.html (Sean Anderson)
	// works by making sure all bits to the right of the highest set bit are 1, then
	// adding 1 to get the answer.
	public static int nextHighestPowerOf2(int v) {
		// decrement by 1 (to handle cases where v is already a power of two)
		v--;
		
		// set all bits to the right of the uppermost set bit.
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		// v |= v >> 32; // uncomment for 64 bit input values
		
		// add 1 to get the power of two result
		return v + 1;
	}
	
	public static String getCurrentDateString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		return dateFormat.format(new Date());
	}
	
	public static int distToChunkSq(int x, int z, Chunk chunk) {
		int dx = (chunk.xPosition << 4) + 8 - x;
		int dz = (chunk.zPosition << 4) + 8 - z;
		return (dx * dx) + (dz * dz);
	}

	public static class JmWorldList{
		private String worldName;
		private File jmWorldFolder;

		public JmWorldList(String worldName, File jmWorldFolder) {
			this.worldName = worldName;
			this.jmWorldFolder = jmWorldFolder;
		}

		public String getWorldName() {
			return this.worldName;
		}

		public File getJmWorldFolder() {
			return this.jmWorldFolder;
		}
	}

	public static List getTargetInfo(int sourceX, int sourceZ, int targetX, int targetZ){

		//Return List with elements:
		//		int straights distance beetwen marker & player
		//		String compass direction to marker

		List targetInfo= new ArrayList();
		String compassPoint;

		double diffX=sourceX-targetX;
		double diffZ =sourceZ-targetZ;
		int angle =(int)Math.toDegrees(Math.atan2(diffX, diffZ));
		angle=angle<0?angle+360:angle;


	//Determine marker`s the side of the world relative to the player.
		if(angle<=10 || angle >350){
			compassPoint="N";
		}else if (angle<=80 && angle >10){
			compassPoint="NW";
		}else if (angle<=100 && angle >80){
			compassPoint="W";
		}else if (angle<=170 && angle >100){
			compassPoint="SW";
		}else if (angle<=190 && angle >170){
			compassPoint="S";
		}else if (angle<=260 && angle >190){
			compassPoint="SE";
		}else if (angle<=280 && angle >260){
			compassPoint="E";
		}else compassPoint="NE";

		targetInfo.add(compassPoint);
	//Determine the direct distance from the player to the marker
		targetInfo.add ((int)Math.sqrt(Math.abs(diffX)*Math.abs(diffX) +
						   Math.abs(diffZ)*Math.abs(diffZ)));
	return targetInfo;
	}


	public static File getJMWaypointsFolder(){

		String minecraftRootFolders= null;
		try {
			minecraftRootFolders = Minecraft.getMinecraft().mcDataDir.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String jmDataFolder=minecraftRootFolders+"\\journeymap\\data\\";

		if (Minecraft.getMinecraft().isIntegratedServerRunning()) { //sp
			jmDataFolder+="sp\\"+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"\\waypoints";

		} else { //mp

			jmDataFolder+="mp\\"+ Minecraft.getMinecraft().func_147104_D().serverName +"\\waypoints";


		}
		File jmWaypointsFolder=new File(jmDataFolder);




		return jmWaypointsFolder;

	}

	public static String getGameMode(){
		if(Minecraft.getMinecraft().isIntegratedServerRunning()){
			return "sp";
		} else return "mp";

	}

	public static String getCurrentMcWorldName(){
		if (Minecraft.getMinecraft().isIntegratedServerRunning()) { //sp
			return Minecraft.getMinecraft().getIntegratedServer().getFolderName();
		} else { //mp
			return Minecraft.getMinecraft().func_147104_D().serverName;
		}
	}

	public static void importMarkersFromJourneymap(MarkerManager markerManager,File jmWaypountsFolder) {

		JsonParser parser = new JsonParser();
		HashMap <String, String > uniqueMarker= new HashMap<String, String>();
		//create HashMap to avoid duplication markers from import process
		//	HashMap`s key is a string from Name+coordX+coordY+coordZ+dimension

		for (Marker marker: markerManager.markerList){
			String hashString=marker.name+String.valueOf(marker.x)+String.valueOf(marker.y)+String.valueOf(marker.z)+String.valueOf(marker.dimension);
			if(!uniqueMarker.containsKey(hashString)){
				uniqueMarker.put(hashString,"");
			}
		}



		if(jmWaypountsFolder.isDirectory()) {


			for (final File fileEntry : jmWaypountsFolder.listFiles()) {
				if (!fileEntry.isDirectory() && fileEntry.getPath().contains(".json") && fileEntry.length()>0) {
					int counter=0;
					Object jmJsonMarkerObject = null;
					try {
						jmJsonMarkerObject = parser.parse(new FileReader(fileEntry));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					if(!jmJsonMarkerObject.equals(null)){
						JsonObject jmMarkerData = (JsonObject) jmJsonMarkerObject;
						JsonArray markerDimensions = jmMarkerData.getAsJsonArray("dimensions");

						int markerColour = 0xff00ff00;

						if (markerDimensions.size()>0) {
							String markerName;
							int markerX;
							int markerY;
							int markerZ;
							for (int i = 0; i < markerDimensions.size(); i++) {
								int dimension = markerDimensions.get(i).getAsInt();
								String markerGroup = "importJM";

								if(jmMarkerData.get("x")!=null) {
									markerX = jmMarkerData.get("x").getAsInt();
								}else {
									MwUtil.logInfo("%s is not contains information about X coordinates", fileEntry.getName());
									break;
								}
								if(jmMarkerData.get("y")!=null) {
									markerY = jmMarkerData.get("y").getAsInt();
								}else {
									MwUtil.logInfo("%s is not contains information about Y coordinates", fileEntry.getName());
									break;
								}
								if(jmMarkerData.get("z")!=null) {
									markerZ = jmMarkerData.get("z").getAsInt();
								}else {
									MwUtil.logInfo("%s is not contains information about Z coordinates", fileEntry.getName());
									break;
								}
								if(jmMarkerData.get("name")!=null && jmMarkerData.get("name").getAsString().length()>0) {
									 markerName = jmMarkerData.get("name").getAsString();
								}else {
									MwUtil.logInfo("%s is not contains information about name. Name" +
													" will be assign from coordinates", fileEntry.getName());
									markerName=String.valueOf(markerX)+";"+String.valueOf(markerY)+";"+String.valueOf(markerZ);
								}

								String hashString=markerName+String.valueOf(markerX)+String.valueOf(markerY)+
										String.valueOf(markerZ)+String.valueOf(dimension);

								if(!uniqueMarker.containsKey(hashString)){
									markerManager.addMarker(markerName, markerGroup,
																	markerX,markerY, markerZ, dimension, markerColour);
									counter++;
								}


							}
						} else MwUtil.logInfo("%s is not contains information about dimension", fileEntry.getName());
					}
				} else MwUtil.logInfo("%s is not JourneyMap`s waypoints file", fileEntry.getName());
			}
		}

		markerManager.update();
		markerManager.saveMarkersToFile();
	}


	public static File chooseDirectory(String startFolderPath){
		//File Chooser
		File selectedFile=null;

		JFrame frame = new JFrame("Select");

		File skinFile = new File(startFolderPath);

		JFileChooser chooser = new JFileChooser(startFolderPath);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		int result = chooser.showOpenDialog(frame);

		if (result == JFileChooser.APPROVE_OPTION) {

			selectedFile = chooser.getSelectedFile();

		}else selectedFile=new File(startFolderPath);

	return selectedFile;
	}

}

