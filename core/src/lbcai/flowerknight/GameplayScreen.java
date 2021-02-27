package lbcai.flowerknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import lbcai.util.Assets;
import lbcai.util.ChaseCam;
import lbcai.util.Constants;

/**
 * This screen will render everything in the level. Multiple screens can be made in a game, i.e. for main menu.
 * 
 * @author lbcai
 *
 */
public class GameplayScreen extends ScreenAdapter {
	/**
	 * Add the level, which is a container for everything. The screen will render the level, thus rendering everything inside the
	 * level.
	 */
	Level level;
	
	/**
	 * Extends viewport for resizing. Will scale to fit window in one direction, then stretch in the remaining direction.
	 */
	ExtendViewport viewport;
	
	/**
	 * Draws rectangles that will have textures (sprites) on them. Graphics are expensive and a batch data structure is more 
	 * efficient than doing everything one at a time.
	 */
	SpriteBatch batch;
	
	/**
	 * Add a camera that follows the player around.
	 */
	private ChaseCam chaseCam;
	
	/**
	 * show() is called when this screen becomes the current screen for a game. Basically the initialize method.
	 */
	@Override
	public void show() {
		//Make an AssetManager for this screen so we only load what we need and only load it once.
		AssetManager manager = new AssetManager();
		//Initialize our own Assets class that handles rendering for entities.
		Assets.instance.init(manager);
		viewport = new ExtendViewport(Constants.WorldSize, Constants.WorldSize);
		level = new Level(viewport);
		batch = new SpriteBatch();
		
		chaseCam = new ChaseCam(viewport.getCamera(), level.player);
		
		
	}
	
	/**
	 * Overrides render() from ScreenAdapter. This render method has time built in as delta (seconds) so we avoid problems with 
	 * frame rate causing time to speed up or slow down on different devices.
	 */
	@Override
	public void render(float delta) {
		//Update the camera.
		chaseCam.update(delta);
		//Check what happened; update the level so we can re-render it below and provide illusion of movement.
		level.update(delta);
		//Actually applies the view to our camera.
		viewport.apply();
		//Sets the background color using rgba values from BackgroundColor.
		Gdx.gl.glClearColor(
				Constants.BackgroundColor.r,
				Constants.BackgroundColor.g,
				Constants.BackgroundColor.b,
				Constants.BackgroundColor.a);
		//Clear frame buffer so sprites don't linger.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//A projection matrix basically says that things look smaller the farther they are from the camera. A view matrix
		//basically says where things should be rendered in relation to camera (so if camera moves left, the item moves right).
		//The camera.combined = both view and projection matrices. Describes where to render things on the screen.
		//We are now telling our SpriteBatch to use this combined matrix to render things. Call this whenever you do something to
		//the camera! Putting it in render() is also safe.
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		batch.begin();
		//Render the level! Renders everything in the level.
		level.render(batch);
		batch.end();
		
	}
	
	@Override
	public void resize(int width, int height) {
		/**
		 * Use ExtendViewport to adjust the things we see if the window is resized. Center camera set to true.
		 */
		viewport.update(width, height, true);
	}
	
	@Override
	public void dispose() {
		//Prevent memory leaks! Dispose is called when the screen is closed.
		Assets.instance.dispose();
	}
	
}
