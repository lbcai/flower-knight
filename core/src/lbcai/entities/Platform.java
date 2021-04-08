package lbcai.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import lbcai.util.Assets;

public class Platform {
	
	/**
	 * Make the platform qualities.
	 */
	float left;
	float right;
	float top;
	float bottom;
	float centerX;

	//used to link enemies to platforms
	String id;
	
	/**
	 * Constructor for a platform. Defines the platform qualities based on input.
	 * 
	 * @param left    left edge of the platform
	 * @param top     top edge of the platform 
	 * @param width   difference between left and right edges of platform
	 * @param height  difference between top and bottom edges of platform
	 */
	public Platform(float left, float top, float width, float height) {
		this.top = top;
		this.bottom = top - height;
		this.left = left;
		this.right = left + width;
		centerX = left + (width / 2);

	}
	
	/**
	 * Render method for platforms.
	 * 
	 * @param batch is a disposable object that renders textures on quads, need for nine patch.
	 */
	public void render(SpriteBatch batch) {
		float width = right - left;
		float height = top - bottom;
		//position x, position y, width, height
		//no documentation. seems to slide platform 1 pixel to left and 1 down, add 2 to width and height
		//this is on top of what was defined for the platform when it was created, seems like these values affect
		//how far away from the actual texture entities will stand on the platform/count as touching platform, adjust as needed
		Assets.instance.platformAssets.platformNinepatch.draw(batch, left - 1, bottom - 1, width + 2, height + 5);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public float getCenterX() {
		return centerX;
	}
	
}
