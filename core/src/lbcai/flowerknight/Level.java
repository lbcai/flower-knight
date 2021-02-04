package lbcai.flowerknight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
		//Add player to the level.
		player = new Player();
		//Initialize the array of platforms and add a test platform.
		platforms = new Array<Platform>();
		//left, top, width, height
		platforms.add(new Platform(50, 50, 512, 50));
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
}
