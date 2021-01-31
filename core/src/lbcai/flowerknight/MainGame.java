package lbcai.flowerknight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The game class. Some extend ApplicationAdapter instead of Game if they plan to handle screen management without using the Screen
 * class. This game uses the Screen class to handle multiple screens and rendering objects. If you want to make your own render/draw
 * methods, use ApplicationAdapter since Screen already has a render method.
 * @author Bird
 *
 */
public class MainGame extends Game {

	
	@Override
	public void create () {
		setScreen(new GameplayScreen());
	}
	
	@Override
	public void pause() {
		
	}
	

}
