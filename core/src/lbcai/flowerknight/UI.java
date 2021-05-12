package lbcai.flowerknight;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import lbcai.entities.Player;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;

public class UI {

	public final Viewport viewport;
	final BitmapFont font;
	private Player player;
	private Vector3 mousePosition;
	
	//graphics
	private List<AtlasRegion> livesRegions;
	private int lastLives;
	private ArrayList<livesPetal> livesPetals;
	
	//the rectangle where the player can click if they would like to see their health number (or they can just hover to glimpse)
	private Rectangle healthToggle;
	private Vector2 healthToggleCenter;
	private GlyphLayout healthToggleLayout; //for holding the text to be displayed and easy centering
	private int healthToggleState;
	
	final float worldHeight;
	final float cameraY;
	
	private class livesPetal {
		
		Vector2 position;
		Vector2 startPosition;
		TextureRegion region;
		float rotation;
		float originX;
		float originY;
		float scaleX;
		float scaleY;
		int spawnAnim;
		
		livesPetal(int i) {
			//take the index to know which life is being displayed
			region = livesRegions.get(i);
			position = new Vector2(healthToggleCenter.x - Constants.hpOrbCenter.x, 
					healthToggleCenter.y - Constants.hpOrbCenter.y);
			startPosition = new Vector2(healthToggleCenter.x - Constants.hpOrbCenter.x, 
					healthToggleCenter.y - Constants.hpOrbCenter.y);
			rotation = 0;
			originX = 0;
			originY = 0;
			scaleX = 0;
			scaleY = 0;
			spawnAnim = 0;
		}
		
		void render(SpriteBatch batch) {
			
			if (spawnAnim == 0) {
				spawn();
			}
			
			batch.draw(region.getTexture(), 
					position.x, 
					position.y, 
					originX, 
					originY, 
					region.getRegionWidth(), 
					region.getRegionHeight(), 
					scaleX, 
					scaleY, 
					rotation, 
					region.getRegionX(), 
					region.getRegionY(), 
					region.getRegionWidth(), 
					region.getRegionHeight(), 
					false, 
					false);
		}
		
		void fall() {
			//sets the origin of rotation to the "flower cover" point of the petal. as the petal falls, it will spin around
			//and be scaled around this point, otherwise it will use the bottom left corner and align itself with the
			//orb graphic.
			originX = Constants.livesFlowerCenter.x;
			originY = Constants.livesFlowerCenter.y;
			rotation += 20;
			
			//causes the life count petal to fly off the flower in a parabola shape
			position.x += 15;
			position.y -= (0.0003) * Math.pow(position.x - startPosition.x, 2);
			
			//make the petal look 2D when it flies off 
			scaleY = (float) Math.cos(position.x / 100);
			
		}
		
		void spawn() {
			//makes the petal do a little growing animation when a life is added :)
			if (scaleX < 1) {
				originX = Constants.livesFlowerCenter.x;
				originY = Constants.livesFlowerCenter.y;
				scaleX += 0.08;
				scaleY += 0.08;
			} else if (scaleX > 1) {
				scaleX -= 0.01;
				scaleY -= 0.01;
			} else if (scaleX == 1) {
				originX = 0;
				originY = 0;
				spawnAnim = 1;
			}
			
		}

	}
	
	
	public UI(Level level) {
		viewport = new ExtendViewport(Constants.WorldSize, Constants.WorldSize);
		
		//health orb
		font = new BitmapFont();
		font.getData().setScale(2f);
		healthToggle = new Rectangle();
		healthToggleCenter = new Vector2();
		healthToggleLayout = new GlyphLayout();
		healthToggleState = 0;
		mousePosition = new Vector3();
		
		player = level.player;
		lastLives = 0;
		
		livesRegions = Arrays.asList(
				Assets.instance.UIassets.livesFlower1, 
				Assets.instance.UIassets.livesFlower2,
				Assets.instance.UIassets.livesFlower3,
				Assets.instance.UIassets.livesFlower4,
				Assets.instance.UIassets.livesFlower5
				);

		livesPetals = new ArrayList<livesPetal>();
		worldHeight = viewport.getWorldHeight();
		cameraY = viewport.getCamera().position.y;
		
	}
	
	public void update(float delta) {

		//if the player's number of lives has changed:
		if (player.lives != lastLives) {
			if (player.lives > lastLives) {
				livesPetals.add(new livesPetal(livesPetals.size()));
				lastLives += 1;
				
			} else if (player.lives < lastLives) {
				if (player.lives > -1) {
					//path that the petal will take to fly off the screen when player loses a life
					//livesPetals.get(livesPetals.size() - 1).position.y -= 10;
					livesPetals.get(livesPetals.size() - 1).fall();
					//once the petal flies off the screen, remove it and update the lives value
					if (livesPetals.get(livesPetals.size() - 1).position.y < cameraY - worldHeight) {
						livesPetals.remove(livesPetals.size() - 1);
						lastLives -= 1;
				}
				
				}
				
			}
			
		}
	}
	
	public void render(SpriteBatch batch) {
		
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
	
		
		//ui "padding" is 1/20 width and 19/20 height (since count begins from bottom left corner) adjust once graphics in place
		//.set is probably more efficient than creating a new rectangle every update! this snippet positions the clickable area
		//of the health orb in the ui. cannot set on creation of ui because the viewport is not applied yet
		//also this way if the dimensions are changed it will update properly
		healthToggle.set(Constants.margin,
				viewport.getWorldHeight() - (Constants.margin + Constants.hpOrbDimensions.y + 10),
				Constants.hpOrbDimensions.x, 
				Constants.hpOrbDimensions.y);
		healthToggle.getCenter(healthToggleCenter);
		healthToggleLayout.setText(font, Integer.toString(player.health), Color.RED, healthToggle.getWidth(), Align.center, false);
		
		//draw base health orb graphic
		batch.draw(Assets.instance.UIassets.orb.getTexture(), 
				healthToggleCenter.x - Constants.hpOrbCenter.x, 
				healthToggleCenter.y - Constants.hpOrbCenter.y, 
				0, 
				0, 
				Assets.instance.UIassets.orb.getRegionWidth(), 
				Assets.instance.UIassets.orb.getRegionHeight(), 
				1, 
				1, 
				0, 
				Assets.instance.UIassets.orb.getRegionX(), 
				Assets.instance.UIassets.orb.getRegionY(), 
				Assets.instance.UIassets.orb.getRegionWidth(), 
				Assets.instance.UIassets.orb.getRegionHeight(), 
				false, 
				false);
		
		//draw the appropriate number of lives petals depending on player's current lives
		for (livesPetal petal : livesPetals) {
			petal.render(batch);
		}
		
		//draw flower cover center for lives petals on top of petals
		batch.draw(Assets.instance.UIassets.livesFlowerCover.getTexture(), 
				healthToggleCenter.x - Constants.hpOrbCenter.x, 
				healthToggleCenter.y - Constants.hpOrbCenter.y, 
				0, 
				0, 
				Assets.instance.UIassets.livesFlowerCover.getRegionWidth(), 
				Assets.instance.UIassets.livesFlowerCover.getRegionHeight(), 
				1, 
				1, 
				0, 
				Assets.instance.UIassets.livesFlowerCover.getRegionX(), 
				Assets.instance.UIassets.livesFlowerCover.getRegionY(), 
				Assets.instance.UIassets.livesFlowerCover.getRegionWidth(), 
				Assets.instance.UIassets.livesFlowerCover.getRegionHeight(), 
				false, 
				false);

		
		//unproject to make the click coordinates "click on the ui panel" and not on something in world
		//the Vector3 is only used to allow us to use unproject, the Z value doesn't matter
		mousePosition = viewport.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

		//draw hp in orb if applicable
		if (healthToggleState == 0) {
			if (healthToggle.contains(mousePosition.x, mousePosition.y)) {
				//if hovering orb, draw hp in orb
				font.draw(batch, 
						healthToggleLayout, 
						healthToggle.x, 
						healthToggleCenter.y + (healthToggleLayout.height / 2));
			}
			//default: do not draw hp in orb
		} else if (healthToggleState == 1) {
			//leave health displayed on orb (happens after you click the orb)
			font.draw(batch, 
					healthToggleLayout, 
					healthToggle.x, 
					healthToggleCenter.y + (healthToggleLayout.height / 2));
		}

		
		if (Gdx.input.justTouched()) {
			//if clicked on the health orb, then change the display setting:
			if (healthToggle.contains(mousePosition.x, mousePosition.y)) {
				if (healthToggleState == 0) {
					healthToggleState = 1;
				} else if (healthToggleState == 1) {
					healthToggleState = 0;
				}
			}
		}
		
		
	}
	
	
	public void debugRender(ShapeRenderer shape, SpriteBatch batch) {
		//keep in mind that from this point on in the shaperenderer, it is using the batch projection matrix. if you add something
		//for debug renderer to render AFTER the ui, it will probably be bugged and stuck on the screen. just add it before
		//the ui or reset the projection matrix.
		shape.setProjectionMatrix(batch.getProjectionMatrix());
		shape.rect(healthToggle.x, healthToggle.y, healthToggle.width, healthToggle.height);
	}
	
}
