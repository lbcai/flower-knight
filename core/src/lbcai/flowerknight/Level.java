package lbcai.flowerknight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import lbcai.entities.Platform;
import lbcai.entities.Player;

public class Level {
	
	/**
	 * Make the player.
	 */
	Player player;
	
	/**
	 * Make an array of platforms to hold all platforms in the level.
	 */
	Array<Platform> platforms;
	
	public Level() {

		//Initialize the array of platforms and add a test platform.
		platforms = new Array<Platform>();
		//Start up the Debug level.
		initDebugLevel();
	}
	
	public void update(float delta) {
		player.update(delta, platforms);
	}
	
	/**
	 * Render the level.
	 * 
	 * @param batch      Object that draws textures on a bunch of rectangles/squares in OpenGL. Disposable.
	 * @param renderer   Renders points, outlines, filled shapes, lines. Disposable.
	 */
	public void render(SpriteBatch batch) {

		batch.begin();
		for (Platform platform : platforms) {
			platform.render(batch);
		}
		player.render(batch);
		batch.end();
	}
	
	private void initDebugLevel() {
		//left, top, width, height
		platforms.add(new Platform(500, 75, 200, 50));
		platforms.add(new Platform(0, 0, 512, 50));
		//platform height bug (jump distance)
		//platforms.add(new Platform(100, 85, 300, 50));
		platforms.add(new Platform(100, 160, 500, 50));
		//Add player to the level. Add a start position for the level as input.
		player = new Player(new Vector2(100, 200));
	}
}
