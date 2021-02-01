package lbcai.flowerknight;

import com.badlogic.gdx.Game;

/**
 * The game class. Some extend ApplicationAdapter instead of Game if they plan to handle screen management without using the Screen
 * class. This game uses the Screen class to handle multiple screens and rendering objects. If you want to make your own render/draw
 * methods, use ApplicationAdapter since Screen already has a render method.
 * 
 * This program actually uses ScreenAdapter, an implementation of Screen which simplifies things so we don't need to override
 * extra things. This still counts as using Screen. If we extend ApplicationAdapter instead of Game, we can't use the setScreen 
 * method.
 * 
 * @author lbcai
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
