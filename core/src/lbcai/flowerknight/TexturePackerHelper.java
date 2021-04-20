package lbcai.flowerknight;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePackerHelper {

	
	 public static void main (String[] args) throws Exception {
		 	//pack the textures and generate the atlas
	        TexturePacker.process("assets/playerSprites", "assets", "playerSpritesheet");
	        
	        //read the old atlas which has 0,0 offsets
	        File playerAtlas = new File("assets/playerSpritesheet.atlas");
	        FileReader fileReader = new FileReader(playerAtlas);
	        LineNumberReader buffReader = new LineNumberReader(fileReader);
	        
	        //make a new atlas that will hold the corrected values and write to it
	        File tempFile = new File("assets/playerSpritesheet2.atlas");
	        FileWriter fileWriter = new FileWriter(tempFile);
	        BufferedWriter buffWriter = new BufferedWriter(fileWriter);
	        
	        //use lists to associate animation names with frames, and frames with appropriate x and y offset values
	        String[] animNames = {"Attack1left", "Attack1right", "Boosttoplatleft", "Boosttoplatright", "Squatright", 
	        		"Squatleft", "Jumpattack1left", "Jumpattack1right", "Landcombatleft", "Landcombatright", "Knockdownleft", 
	        		"Knockdownright"};
	        String[][] animFrames0 = {{"0", "0"}, {"0", "0"}, {"-10", "0"}, {"-55", "0"}, {"-55", "0"}, {"-55", "0"}, {"-20", "0"}, {"-4", "0"}, {"20", "0"}};
	        String[][] animFrames1 = {{"0", "0"}, {"0", "0"}, {"0", "0"}, {"70", "0"}, {"70", "0"}, {"70", "0"}, {"-17", "0"}, {"-17", "0"}, {"-17", "0"}};
	        String[][] animFrames2 = {{"0", "-62"}, {"-15", "-70"}, {"-15", "-70"}, {"-11", "0"}, {"-11", "0"}, {"-11", "0"}, {"-11", "0"}, {"-11", "0"}};
	        String[][] animFrames3 = {{"0", "-62"}, {"0", "-70"}, {"0", "-70"}, {"14", "0"}, {"14", "0"}, {"14", "0"}, {"14", "0"}, {"14", "0"}};
	        String[][] animFrames4 = {{"15", "0"}, {"15", "0"}};
	        String[][] animFrames5 = {{"-13", "0"}, {"-15", "0"}};
	        String[][] animFrames6 = {{"45", "0"}, {"45", "0"}, {"0", "0"}, {"-95", "-40"}, {"-95", "-40"}, {"-90", "0"}, {"-5", "-3"}};
	        String[][] animFrames7 = {{"-40", "0"}, {"-40", "0"}, {"-40", "0"}, {"75", "-30"}, {"75", "-30"}, {"75", "-30"}, {"55", "7"}};
	        String[][] animFrames8 = {{"0", "0"}, {"0", "0"}, {"10", "0"}, {"13", "0"}, {"13", "0"}, {"13", "0"}};
	        String[][] animFrames9 = {{"0", "0"}, {"0", "0"}, {"10", "0"}, {"1", "0"}, {"1", "0"}, {"1", "0"}};
	        String[][] animFrames10 = {{"0", "0"}, {"0", "0"}, {"0", "0"}, {"0", "0"}, {"30", "-10"}, {"30", "-10"}, 
	        		{"30", "-10"}, {"30", "-15"}, {"30", "-15"}, {"30", "-15"}, {"30", "-15"}, {"30", "-15"}, {"30", "-15"}, 
	        		{"30", "-15"}, {"30", "-15"}, {"30", "-15"}, {"30", "-15"}, {"30", "-15"}, {"30", "-15"}, {"30", "-15"}};
	        String[][] animFrames11 = {{"0", "0"}, {"0", "0"}, {"0", "0"}, {"0", "0"}, {"0", "0"}, {"0", "-10"}, 
	        		{"0", "-10"}, {"0", "-10"}, {"0", "-10"}, {"0", "-10"}, {"0", "-10"}, {"0", "-25"}, {"0", "-25"}, 
	        		{"0", "-25"}, {"0", "-25"}, {"0", "-25"}, {"0", "-25"}, {"0", "-25"}, {"0", "-25"}, {"0", "-25"}};
	        
	        //pair the information properly in a map
	        Map<String, String[][]> map = new HashMap<>();
	        map.put(animNames[0], animFrames0);
	        map.put(animNames[1], animFrames1);
	        map.put(animNames[2], animFrames2);
	        map.put(animNames[3], animFrames3);
	        map.put(animNames[4], animFrames4);
	        map.put(animNames[5], animFrames5);
	        map.put(animNames[6], animFrames6);
	        map.put(animNames[7], animFrames7);
	        map.put(animNames[8], animFrames8);
	        map.put(animNames[9], animFrames9);
	        map.put(animNames[10], animFrames10);
	        map.put(animNames[11], animFrames11);
	        
	        //fix the offset values in the appropriate place before putting the line into the new atlas
	        //for all the lines in the file that aren't null, 
	        for (String atlasLine = null; (atlasLine = buffReader.readLine()) != null;) {
	        	//if our map has a key that matches the line, we are on a line corresponding to the animation name of an
	        	//animation that requires offset adjustment
	        	if (map.containsKey(atlasLine)) {
	        		buffWriter.write(atlasLine + "\n");
	        		int currentLine = buffReader.getLineNumber();
	        		
	        		//pass over lines between the animation name and the relevant info
	        		for (int n = 0; n < 6; n++) {
	        			if (n < 3) {
	        				//we will not revisit these lines so we can write them in the new file
	        				buffWriter.write(buffReader.readLine() + "\n");
	        			} else if (n == 4) {
	        				//mark the offset line so we can return here
	        				buffReader.mark(1000);
	        			} else {
	        				//move lines without writing into the new file so the lines will be written later once the reader
	        				//passes over them after making edits, this keeps lines in the correct order
	        				buffReader.readLine();
	        			}
	        		}
	        		//get the line that has the animation index number (the animation/sprite frame)
	        		String indexLine = buffReader.readLine();
	        		String indexNum = null;
	        		
	        		//go backwards from the end of the index line in case the index is a multi-digit number, once you hit space
	        		//the index number is over. add 1 to the space's index and this number to the end of the string = the 
	        		//complete index number
	        		for (int i = indexLine.length() - 1; i > 0; --i) {
	        			if (Character.isWhitespace(indexLine.charAt(i))) {
	        				indexNum = indexLine.substring(i + 1);
	        				break;
	        			}
	        		}
	        		
	        		if (indexNum != null) {
	        			//return to offset line
	        			buffReader.reset();
	        			StringBuilder offsetLine = new StringBuilder(buffReader.readLine());
	        			String[][] appropriateArray = map.get(atlasLine);
	        			String x = appropriateArray[Integer.parseInt(indexNum) - 1][0];
	        			String y = appropriateArray[Integer.parseInt(indexNum) - 1][1];
	        			
        				offsetLine.delete(10, offsetLine.length());
        				
        				offsetLine.append(x + ", " + y);
        				
        				buffWriter.write(offsetLine.toString() + "\n");
        				
	        		}	
	        	} else {
	        		//if the line is not a relevant animation name, just put it in the new file and move on with life
	        		buffWriter.write(atlasLine + "\n");
	        	}
	        }
	        
	        //close streams, save memory
	        fileReader.close();
	        buffReader.close();
	        buffWriter.close();
	        fileWriter.close();
	        
	        
	        //remove the original atlas with 0,0 offsets
	        Files.delete(Paths.get("assets/playerSpritesheet.atlas"));
	        
	        //rename the new atlas to have the name of the old atlas
	        File newPlayerAtlas = new File("assets/playerSpritesheet.atlas");
	        tempFile.renameTo(newPlayerAtlas);
	        
	    }
}
