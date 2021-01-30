package lbcai.flowerknight;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePackerHelper {
	 public static void main (String[] args) throws Exception {
	        TexturePacker.process("assets/playerSprites", "assets", "playerSpritesheet");
	    }
}
