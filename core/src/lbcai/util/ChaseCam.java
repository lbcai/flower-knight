package lbcai.util;

import com.badlogic.gdx.graphics.Camera;

import lbcai.entities.Player;

public class ChaseCam {
	
	private Camera camera;
	
	/**
	 * Make a player so we can target it with the camera.
	 */
	private Player target;
	
	public ChaseCam(Camera camera, Player target) {
		this.camera = camera;
		this.target = target;
	}
	
	public void update() {
		camera.position.x = target.position.x;
		camera.position.y = target.position.y;
	}
	
}
