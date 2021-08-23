package mapwriter;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import mapwriter.forge.MwForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.StringUtils;

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

}

