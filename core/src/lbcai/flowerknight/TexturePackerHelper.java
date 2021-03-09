package lbcai.flowerknight;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.*;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePackerHelper {

	
	 public static void main (String[] args) throws Exception {
	        TexturePacker.process("assets/playerSprites", "assets", "playerSpritesheet");
	        
	        File playerAtlas = new File("assets/playerSpritesheet.atlas");
	        FileReader fileReader = new FileReader(playerAtlas);
	        LineNumberReader buffReader = new LineNumberReader(fileReader);
	        
	        FileWriter fileWriter = new FileWriter(new File("assets/playerSpritesheet2.atlas"));
	        BufferedWriter buffWriter = new BufferedWriter(fileWriter);
	        
	        String[] animNames = {"Attack1left", "Attack1right", "Boosttoplatleft", "Boosttoplatright"};
	        String[][] animFrames0 = {{"0", "0"}, {"0", "0"}, {"0", "0"}, {"-40", "0"}, {"-40", "0"}, {"-40", "0"}, {"-20", "0"}, {"14", "0"}, {"20", "0"}};
	        String[][] animFrames1 = {{"0", "0"}, {"0", "0"}, {"0", "0"}, {"80", "0"}, {"80", "0"}, {"80", "0"}, {"-17", "0"}, {"-17", "0"}, {"-17", "0"}};
	        String[][] animFrames2 = {{"0", "-62"}, {"-15", "-70"}, {"-15", "-70"}, {"-11", "0"}, {"-11", "0"}, {"-11", "0"}, {"-11", "0"}, {"-11", "0"}};
	        String[][] animFrames3 = {{"0", "-62"}, {"0", "-70"}, {"0", "-70"}, {"14", "0"}, {"14", "0"}, {"14", "0"}, {"14", "0"}, {"14", "0"}};
	        
	        Map<String, String[][]> map = new HashMap<>();
	        map.put(animNames[0], animFrames0);
	        map.put(animNames[1], animFrames1);
	        map.put(animNames[2], animFrames2);
	        map.put(animNames[3], animFrames3);
	        
	        for (String atlasLine = null; (atlasLine = buffReader.readLine()) != null;) {
	        	
	        }
	        
	        fileReader.close();
	        buffReader.close();
	        
	        
	    }
}
