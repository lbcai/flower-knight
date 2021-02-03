package lbcai.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Platform {
	
	/**
	 * Make the platform qualities.
	 */
	float left;
	float right;
	float top;
	float bottom;
	
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
	}
	
	/**
	 * Render method for platforms. Handled by libGDX.
	 * 
	 * @param renderer  is a ShapeRenderer, an object that renders points, lines, shape outlines, and filled shapes. Is disposable.
	 */
	public void render(ShapeRenderer renderer) {
		float width = right - left;
		float height = top - bottom;
		//ShapeRenderer method to draw a rectangle and color it green.
		renderer.setColor(Color.GREEN);
		renderer.rect(left, bottom, width, height);
	}
	
}
