package lbcai.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;

import lbcai.entities.Player;

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
	
	public ChaseCam(Camera camera, Player target) {
		this.camera = camera;
		this.target = target;
		following = true;
	}
	
	public void update(float delta) {
		
		if (Gdx.input.isKeyJustPressed(Keys.F1)) {
			//Invert the boolean following. Use "isKeyJustPressed" instead of "isKeyPressed" if you want a toggle and not a
			//keydown.
			following = !following;
		}
		
		if (following) {
			camera.position.x = target.position.x;
			camera.position.y = target.position.y;
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
