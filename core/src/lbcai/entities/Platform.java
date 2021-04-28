package lbcai.entities;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
		//corner pieces: 55x60px
		//long side pieces: 90x60px for horizontal, 55x60px for vertical
		//middle pieces: 90x60px
		//a platform must be 90x + 60 long and 60x + 72 tall. below we make sure input is correct
		//we draw the left edge pieces 25 pixels before the left edge of the platform to account for transparent parts of image
		//and the bottom edge pieces 24 pixels before the bottom edge of the platform for the same reason
		
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
		
		//start at the bottom left corner, place tile 7 25px to the left and 24px below the actual corner of the platform
		//this means only 36px vertically and 30px horizontally of the tile is actually overlapping the platform space
		//for a platform of the min height (36+36=72), draw tile 1 above tile 7. so go up 60px from the drawn corner or
		//36px from the platform's actual corner. this is where you will place tile 1
		//if the platform is greater in height than 72, subtract 72 off the height and divide this by 60 to find the number of
		//vertical tiles (tile 4) required to fill in the space, then draw these tiles keeping in mind 25px is transparent
		//buffer space on the left side and the tile is 60px tall
		//once finished, draw tile 1 on top of this
		
		//repeat process for tiles 2, 5, 7 but divide the width - 60 by 90 and get the number of middle tiles required
		//to fill the middle of the platform
		
		//finally, cap the platform with tiles 3, 6, 9 following same process as above
		
		//do the calculations in the constructor and simply input the position values in the render method.
		
		
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
