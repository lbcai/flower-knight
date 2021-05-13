package lbcai.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import lbcai.entities.Player;
import lbcai.flowerknight.Level;

public class ChaseCam {
	
	private Camera camera;
	
	/**
	 * Make a player so we can target it with the camera.
	 */
	private Player target;
	
	/**
	 * Is the camera following player? (for debug mode)
	 */
	private Boolean following;
	private Level level;
	private Vector2 camera2DPosition;

	
	public ChaseCam(Camera camera, Player target) {
		this.camera = camera;
		this.target = target;
		this.level = target.getLevel();
		camera2DPosition = new Vector2();
		following = true;
		
	}
	
	public void update(float delta) {

		if (Gdx.input.isKeyJustPressed(Keys.F1)) {
			//Invert the boolean following. Use "isKeyJustPressed" instead of "isKeyPressed" if you want a toggle and not a
			//keydown.
			following = !following;
		}
		
		if (following) {
			//change the position to vector2 in order to lerp it for smooth transition movements
			//must change position back to vector3 in order to actually use the camera position
			camera2DPosition.x = camera.position.x;
			camera2DPosition.y = camera.position.y;
			camera2DPosition.lerp(target.position, 0.3f);
			camera.position.x = camera2DPosition.x;
			camera.position.y = camera2DPosition.y;
			
			final float worldWidth = level.getViewport().getWorldWidth();
			final float worldHeight = level.getViewport().getWorldHeight();
			
			//limit the position of the camera so it cannot move beyond half a screen width inside level bounds and half a screen
			//height inside level bounds. does not apply for debug mode, also vertical bounds expanded for bottom of map so
			//there will be room for graphics
			if (camera.position.x > level.levelBound.x + level.levelBound.width - (worldWidth / 2)) {
				camera.position.x = level.levelBound.x + level.levelBound.width - (worldWidth / 2);
			} else if (camera.position.x < level.levelBound.x + (worldWidth / 2)) {
				camera.position.x = level.levelBound.x + (worldWidth / 2);
			}
			
			if (camera.position.y > level.levelBound.y + level.levelBound.height - (worldHeight / 2)) {
				camera.position.y = level.levelBound.y + level.levelBound.height - (worldHeight / 2);
			} else if (camera.position.y < Constants.killPlane) {
				camera.position.y = Constants.killPlane;
			}
			
			System.out.println(camera.position.y);
			
		} else {
			if (Gdx.input.isKeyPressed(Keys.A)) {
				camera.position.x -= delta * Constants.cameraMoveSpeed;
			}
			
			if (Gdx.input.isKeyPressed(Keys.D)) {
				camera.position.x += delta * Constants.cameraMoveSpeed;
			}
			
			if (Gdx.input.isKeyPressed(Keys.W)) {
				camera.position.y += delta * Constants.cameraMoveSpeed;
			}
			
			if (Gdx.input.isKeyPressed(Keys.S)) {
				camera.position.y -= delta * Constants.cameraMoveSpeed;
			}
		}

	}
	
}
