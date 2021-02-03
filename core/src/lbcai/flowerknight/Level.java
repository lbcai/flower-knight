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
		platforms.add(new Platform(100, 100, 100, 100));
	}
	
	public void update(float delta) {
		player.update(delta);
	}
	
	/**
	 * Render the level.
	 * 
	 * @param batch      Object that draws textures on a bunch of rectangles/squares in OpenGL. Disposable.
	 * @param renderer   Renders points, outlines, filled shapes, lines. Disposable.
	 */
	public void render(SpriteBatch batch, ShapeRenderer renderer) {
		
		//Shape type to render: here it renders a filled shape. The platform class itself says it should render a rectangle.
		//The result is a filled rectangle.
		renderer.begin(ShapeType.Filled);
		for (Platform platform : platforms) {
			platform.render(renderer);
		}
		renderer.end();
		
		batch.begin();
		player.render(batch);
		batch.end();
	}
}
