package lbcai.entities;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import lbcai.util.Assets;
import lbcai.util.Constants;

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
	int tilesDimensionWidth;
	int tilesDimensionHeight;
	
	ArrayList<Tile> tiles = new ArrayList<Tile>();
	
	//used to link enemies to platforms
	String id;
	
	class Tile {
		Vector2 position;
		Vector2 origin;
		TextureRegion region;
		int statusCounter = 5;
		
		//constructor
		Tile(int tilesSize, Platform platform) {
			
			//FIRST: figure out which tile to display
			//
			//0-indexing in java. to find the edge tiles:
			//assuming we have a 6x5 platform,
			// 0 5 10 15 20 25
			// 1 6 11 16 21 26
			// 2 7 12 17 22 27
			// 3 8 13 18 23 28
			// 4 9 14 19 24 29
			// (width * height) - 1 = the largest index in the platform array
			// 0 = always will be a corner
			// (height - 1) = all the indices in the first row
			// x % height == 0 are all the indices in the top row
			// (x + 1) % height == 0 are all the indices in the bottom row
			// (width * height) - height up until the largest index in the platform array = all the indices in the last row

			if (tilesSize <= tilesDimensionHeight - 1) {
				//we are in the first edge column
				statusCounter = 4;
				region = Assets.instance.platformAssets.tileSet1_4;
				origin = new Vector2(Constants.cornerTileDimTrans.x, 0);
			}
			
			if (tilesSize >= ((tilesDimensionWidth * tilesDimensionHeight) - tilesDimensionHeight)) {
				//we are in the last edge column
				statusCounter = 6;
				region = Assets.instance.platformAssets.tileSet1_6;
				origin = new Vector2(0, 0);
			}
			
			// statusCounter key:
			// 1 2 2 2 3
			// 4 0 0 0 6
			// 4 0 0 0 6
			// 4 0 0 0 6
			// 7 8 8 8 9
			
			if (tilesSize % tilesDimensionHeight == 0) {
				//we are in the top edge row
				if (statusCounter == 5) {
					statusCounter = 2;
					region = Assets.instance.platformAssets.tileSet1_2;
					origin = new Vector2(0, 0);
				} else if (statusCounter == 4) {
					//we are top left corner
					statusCounter = 1;
					region = Assets.instance.platformAssets.tileSet1_1;
					origin = new Vector2(Constants.cornerTileDimTrans.x, 0);
				} else if (statusCounter == 6) {
					//we are top right corner
					statusCounter = 3;
					region = Assets.instance.platformAssets.tileSet1_3;
					origin = new Vector2(0, 0);
				}

			}
			
			if ((tilesSize + 1) % tilesDimensionHeight == 0) {
				//we are in the bottom edge row
				if (statusCounter == 5) {
					statusCounter = 8;
					region = Assets.instance.platformAssets.tileSet1_8;
					origin = new Vector2(0, Constants.cornerTileDimTrans.y);
				} else if (statusCounter == 4) {
					//we are bottom left corner
					statusCounter = 7;
					region = Assets.instance.platformAssets.tileSet1_7;
					origin = Constants.cornerTileDimTrans;
				} else if (statusCounter == 6) {
					//we are bottom right corner
					statusCounter = 9;
					region = Assets.instance.platformAssets.tileSet1_9;
					origin = new Vector2(0, Constants.cornerTileDimTrans.y);
				}
			}
			
			if (statusCounter == 5) {
				region = Assets.instance.platformAssets.tileSet1_5;
				origin = new Vector2(0, 0);
			}
			
			//SECOND: figure out where the tile must go
			//
			//requires knowing the position of previous tiles. first determine: am i the first tile in the list?
			//if the tilesSize is 0, i am the first tile in the list and i am a 1 tile. i will use the platform.top and 
			//platform.left plus my own dimensions (55x60px) to determine where my bottom left corner should be and where i 
			//should position myself.
			
			if (tilesSize == 0) {
				position = new Vector2(platform.left, platform.top - Constants.cornerTileDim.y);
			} else {
				//placeholder
				position = new Vector2(0, 0);
			}
			
			
		}
		
	}
	
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
		
		//get the number of middle tiles required for width and height:
		float middleWidth = (this.width - 60) / 90;
		float middleHeight = (this.height - 72) / 60;
		
		//array dimensions for width must be middleWidth + 2 and middleHeight + 2 because on each side of the middle stack are 
		//2 cap tiles
		tilesDimensionWidth = (int) middleWidth + 2;
		tilesDimensionHeight = (int) middleHeight + 2;
		
		for (int i = 0; i < (tilesDimensionWidth * tilesDimensionHeight); i++) {
			// if you System.out.println(i); here, the number that goes into the tile that is being made is this number.
			// this means that the tile being made gets the size of the arraylist BEFORE it is made...
			// you do not have to add 1 to the tile size for the new tile being added to get its own index because of java's
			// 0-indexing!
			tiles.add(new Tile(tiles.size(), this));
		}
		
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
		//Assets.instance.platformAssets.platformNinepatch.draw(batch, left - 1, bottom - 1, width + 2, height + 5);
		
		for (Tile tile : tiles) {
			batch.draw(tile.region.getTexture(), 
					tile.position.x, 
					tile.position.y, 
					tile.origin.x, 
					tile.origin.y, 
					tile.region.getRegionWidth(), 
					tile.region.getRegionHeight(), 
					1, 
					1, 
					0, 
					tile.region.getRegionX(), 
					tile.region.getRegionY(), 
					tile.region.getRegionWidth(), 
					tile.region.getRegionHeight(), 
					false, 
					false);
		}
		
		
		
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	
	
}
