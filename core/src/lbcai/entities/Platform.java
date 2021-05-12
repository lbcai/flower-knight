package lbcai.entities;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	ArrayList<Tile> tilesBack = new ArrayList<Tile>();
	
	//used to link enemies to platforms
	String id;
	
	class Tile {
		Vector2 position;
		TextureRegion region;
		int statusCounter = 5;
		
		//constructor
		Tile(int tilesSize, Platform platform, int type) {
			
			if (type == 0) {
				//if tile is for foreground:
				
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
				}
				
				if (tilesSize >= ((tilesDimensionWidth * tilesDimensionHeight) - tilesDimensionHeight)) {
					//we are in the last edge column
					statusCounter = 6;
					region = Assets.instance.platformAssets.tileSet1_6;
				}
				
				// statusCounter key:
				// 1 2 2 2 3
				// 4 5 5 5 6
				// 4 5 5 5 6
				// 4 5 5 5 6
				// 7 8 8 8 9
				
				if (tilesSize % tilesDimensionHeight == 0) {
					//we are in the top edge row
					if (statusCounter == 5) {
						statusCounter = 2;
						region = Assets.instance.platformAssets.tileSet1_2;
					} else if (statusCounter == 4) {
						//we are top left corner
						statusCounter = 1;
						region = Assets.instance.platformAssets.tileSet1_1;
					} else if (statusCounter == 6) {
						//we are top right corner
						statusCounter = 3;
						region = Assets.instance.platformAssets.tileSet1_3;
					}

				}
				
				if ((tilesSize + 1) % tilesDimensionHeight == 0) {
					//we are in the bottom edge row
					if (statusCounter == 5) {
						statusCounter = 8;
						region = Assets.instance.platformAssets.tileSet1_8;
					} else if (statusCounter == 4) {
						//we are bottom left corner
						statusCounter = 7;
						region = Assets.instance.platformAssets.tileSet1_7;
					} else if (statusCounter == 6) {
						//we are bottom right corner
						statusCounter = 9;
						region = Assets.instance.platformAssets.tileSet1_9;
					}
				}
				
				if (statusCounter == 5) {
					region = Assets.instance.platformAssets.tileSet1_5;
				}
				
				//SECOND: figure out where the tile must go
				//
				//requires knowing the position of previous tiles. first determine: am i the first tile in the list?
				//if the tilesSize is 0, i am the first tile in the list and i am a 1 tile. i will use the platform.top and 
				//platform.left plus my own dimensions (30x36px) to determine where my bottom left corner should be and where i 
				//should position myself.
				
				if (statusCounter == 5) {
					//non edge piece (probably a bit complicated) placed first because most pieces will not be edge pieces
					position = new Vector2((platform.left + 30) + ((((tilesSize) / tilesDimensionHeight) - 1) * 
							Constants.midTileDim.x), (platform.top - Constants.cornerTileDim.y) - 
							(((tilesSize) % tilesDimensionHeight) * Constants.midTileDim.y));
				} else if (statusCounter == 2) {
					//middle top row
					//this means y position is always going to be platform.top - Constants.cornerTileDim.y
					//determine the x position:
					//cannot be platform.left because 2 tiles never go on the left column
					//(platform.left + 30) + ((X) * Constants.midUpTileDim.x)
					//find X by determining which column we are on using tilesSize, remember that tilesSize = current tile despite
					//the fact that we are pulling the size of the array before adding the current tile. this is because of the 0-index
					//so it works in our favor to give us the index of the current tile even though we are pulling the size of the array
					//X is the column number and cannot be the first column or the last column.
					//subtract 1 from column number to get to the bottom left corner of that column
					position = new Vector2((platform.left + 30) + ((((tilesSize) / tilesDimensionHeight) - 1) * 
							Constants.midUpTileDim.x), platform.top - Constants.cornerTileDim.y);
				} else if (statusCounter == 6) {
					//middle right column
					//x is always going to be (platform.left + 30) + ((tilesDimensionWidth - 2) * Constants.midUpTileDim.x)
					//y must be determined based on row we are in
					//(platform.top - Constants.cornerTileDim.y) - (X * Constants.midSideTileDim.y)
					// - (Constants.cornerTileDim.y)
					//X is the row. % tilesDimensionHeight cannot = 0 because this is row 1. also cannot equal height - 1 because this
					//is the final row.
					position = new Vector2((platform.left + 30) + ((tilesDimensionWidth - 2) * Constants.midUpTileDim.x), 
							(platform.top - Constants.cornerTileDim.y) - (((tilesSize) % tilesDimensionHeight) * 
									Constants.midSideTileDim.y));
				} else if (statusCounter == 4) {
					//middle left column
					//x is always going to be platform.left - Constants.cornerTileDimTrans.x
					position = new Vector2(platform.left - Constants.cornerTileDimTrans.x, (platform.top - Constants.cornerTileDim.y) - 
							(((tilesSize) % tilesDimensionHeight) * Constants.midSideTileDim.y));
				} else if (statusCounter == 8) {
					//middle bottom row
					//compare to middle top row (tile 2) x positions.
					//from top of platform, subtract height of first row to get top of middle tiles. subtract height of 
					//a middle tile to get bottom corner since we are rendering from bottom corner.
					//subtract # of middle tiles * height of mid tiles to make us end up on bottom row
					position = new Vector2((platform.left + 30) + ((((tilesSize) / tilesDimensionHeight) - 1) * Constants.midUpTileDim.x), 
							(platform.top - Constants.cornerTileDim.y) - ((tilesDimensionHeight - 2) * Constants.midSideTileDim.y)
							 - (Constants.midSideTileDim.y));
				} else if (statusCounter == 1) {
					//upper left corner
					position = new Vector2(platform.left - Constants.cornerTileDimTrans.x, platform.top - Constants.cornerTileDim.y);
				} else if (statusCounter == 3) {
					//upper right corner
					//this means y position is always going to be platform.top - Constants.cornerTileDim.y
					//determine the x position:
					//always will start with platform.left + 30 because the 1 tile is 30px horizontally
					//subtract 2 from tilesDimensionWidth to get the number of tiles in the middle (non corner pieces)
					//multiply the non corner pieces by the width of non corner pieces in px
					//this x value puts you at the end of the top platform row of tiles just before the final corner piece.
					position = new Vector2((platform.left + 30) + ((tilesDimensionWidth - 2) * Constants.midUpTileDim.x), 
							platform.top - Constants.cornerTileDim.y);
				} else if (statusCounter == 7) {
					//bottom left corner
					//this means x position is always platform.left - Constants.cornerTileDimTrans.x
					position = new Vector2(platform.left - Constants.cornerTileDimTrans.x, 
							(platform.top - Constants.cornerTileDim.y) - ((tilesDimensionHeight - 2) * Constants.midSideTileDim.y)
							 - (Constants.midSideTileDim.y));
				} else if (statusCounter == 9) {
					//bottom right corner
					position = new Vector2(platform.right - Constants.cornerTileDim.x, 
							(platform.top - Constants.cornerTileDim.y) - ((tilesDimensionHeight - 2) * Constants.midSideTileDim.y)
							 - (Constants.midSideTileDim.y));
				}
				
			} else if (type == 1) {
				//PLACEHOLDER! these should not use the same tiles as the above, these are background tiles.
				
				//if tile is for background grass, only do the first uppermost row of tiles
				if (tilesSize == 0) {
					//if there is nothing in the array, we are on the first tile of the row
					region = Assets.instance.platformAssets.tileSet1_1;
					position = new Vector2(platform.left - Constants.cornerTileDimTrans.x, platform.top - Constants.cornerTileDim.y + 10);
				
				} else if (tilesSize == (tilesDimensionWidth - 1)) {
					//if the size of the array is the total width - 1, we are on the last tile of the row
					region = Assets.instance.platformAssets.tileSet1_9;
					position = new Vector2(platform.right - Constants.cornerTileDim.x, 
							platform.top - Constants.cornerTileDim.y - Constants.cornerTileDimTrans.y + 10);
				
				} else {
					//we are in the middle of the row
					region = Assets.instance.platformAssets.tileSet1_2;
					position = new Vector2((platform.left + 30) + ((tilesSize - 1) * 
							Constants.midUpTileDim.x), platform.top - Constants.cornerTileDim.y + 10);
				}
				
				
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
				} else if ((width - 60) % 90 < 45) {
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
			tiles.add(new Tile(tiles.size(), this, 0));
		}
		
		//adjusting the top of platform so player stands above the foreground platform covering
		this.top = top + 5;
		
		for (int j = 0; j < tilesDimensionWidth; j++) {
			// for the background grass tiles
			tilesBack.add(new Tile(tilesBack.size(), this, 1));
		}
		
		
	}
	
	/**
	 * Render method for platforms.
	 * 
	 * @param batch is a disposable object that renders textures on quads, need for nine patch.
	 */
	public void render(SpriteBatch batch) {
		
		//render background tiles first
		for (Tile tile : tilesBack) {
			batch.draw(tile.region.getTexture(), 
					tile.position.x, 
					tile.position.y, 
					0, 
					0, 
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
		
		//render foreground covering tiles last
		for (Tile tile : tiles) {
			batch.draw(tile.region.getTexture(), 
					tile.position.x, 
					tile.position.y, 
					0, 
					0, 
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

	public float getTop() {
		return this.top;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public void debugRender(ShapeRenderer shape) {
		shape.rect(this.left, this.bottom, this.width, this.height + 5);
	}
	
}
