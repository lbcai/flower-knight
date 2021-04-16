package lbcai.flowerknight;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

public class UI {

	public final Viewport viewport;
	final BitmapFont font;
	private Player player;
	private Vector3 mousePosition;
	
	//graphics
	private List<AtlasRegion> UIAssetList;
	private List<AtlasRegion> petalList;
	
	//the rectangle where the player can click if they would like to see their health number (or they can just hover to glimpse)
	private Rectangle healthToggle;
	private Vector2 healthToggleCenter;
	private GlyphLayout healthToggleLayout; //for holding the text to be displayed and easy centering
	private int healthToggleState;
	private int livesCounter;
	
	
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
		livesCounter = player.lives;
		
		UIAssetList = Arrays.asList(Assets.instance.UIassets.orb, 
				Assets.instance.UIassets.livesFlower1, 
				Assets.instance.UIassets.livesFlower2,
				Assets.instance.UIassets.livesFlower3,
				Assets.instance.UIassets.livesFlower4,
				Assets.instance.UIassets.livesFlower5,
				Assets.instance.UIassets.livesFlowerCover);
		
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
		
		
		//test placeholder orb graphic
		for (AtlasRegion item : UIAssetList) {

			batch.draw(item.getTexture(), 
					healthToggleCenter.x - 75, 
					healthToggleCenter.y - 192, 
					0, 
					0, 
					item.getRegionWidth(), 
					item.getRegionHeight(), 
					1, 
					1, 
					0, 
					item.getRegionX(), 
					item.getRegionY(), 
					item.getRegionWidth(), 
					item.getRegionHeight(), 
					false, 
					false);
		}

		
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
