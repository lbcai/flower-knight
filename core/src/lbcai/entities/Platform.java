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
	float width;
	float height;

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
		
		//for tiled platforms, dimensions are as follows:
		//corner pieces: 30x36px
		//long side pieces: 90x36px for horizontal, 30x60px for vertical
		//middle pieces: 90x60px
		//so a platform must be 90x + 60 long and 60x + 72 tall. below we make sure input is correct
		
		if (width < 60) {
			this.width = 60;
		} else {
			if ((width - 60) % 90 == 0) {
				this.width = width;
			} else {
				if ((width - 60) % 90 >= 45) {
					this.width = width + (90 - ((width - 60) % 90));
				} else if (width % 90 < 45) {
					this.width = width - ((width - 60) % 90);
				}
			}
		}
		
		
		if (height < 72) {
			this.height = 72;
		} else {
			if ((height - 72) % 60 == 0) {
				this.height = height;
			} else {
				if ((height - 72) % 60 >= 30) {
					this.height = height + (60 - ((height - 72) % 60));
				} else if ((height - 72) % 60 < 350) {
					this.height = height - ((height - 72) % 60);
				}
			}
		}
		
		
		this.top = top;
		this.bottom = top - this.height;
		this.left = left;
		this.right = left + this.width;
		centerX = left + (this.width / 2);
		
	}
	
	/**
	 * Render method for platforms.
	 * 
	 * @param batch is a disposable object that renders textures on quads, need for nine patch.
	 */
	public void render(SpriteBatch batch) {
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

	
	
}
