package lbcai.flowerknight;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import lbcai.entities.Player;
import lbcai.util.Constants;

public class UI {

	public final Viewport viewport;
	final BitmapFont font;
	private Player player;
	
	//the rectangle where the player can click if they would like to see their health number (or they can just hover to glimpse)
	private Rectangle healthToggle;
	private Vector2 healthToggleCenter;
	private GlyphLayout healthToggleLayout; //for holding the text to be displayed and easy centering
	
	public UI(Level level) {
		viewport = new ExtendViewport(Constants.WorldSize, Constants.WorldSize);
		font = new BitmapFont();
		player = level.player;
		healthToggle = new Rectangle();
		healthToggleCenter = new Vector2();
		healthToggleLayout = new GlyphLayout();
		
	}
	
	public void render(SpriteBatch batch) {
		
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		//ui "padding" is 1/20 width and 19/20 height (since count begins from bottom left corner) adjust once graphics in place
		//.set is probably more efficient than creating a new rectangle every update! this snippet positions the clickable area
		//of the health orb in the ui. cannot set on creation of ui because the viewport is not applied yet
		//also this way if the dimensions are changed it will update properly
		healthToggle.set(viewport.getWorldWidth() / 20,
				viewport.getWorldHeight() - ((viewport.getWorldHeight() / 20) + Constants.hpOrbDimensions.y),
				Constants.hpOrbDimensions.x, 
				Constants.hpOrbDimensions.y);
		healthToggle.getCenter(healthToggleCenter);
		healthToggleLayout.setText(font, Integer.toString(player.health), Color.RED, healthToggle.getWidth(), Align.center, false);
		font.draw(batch, 
				healthToggleLayout, 
				healthToggleCenter.x / 2, 
				healthToggleCenter.y + (healthToggleLayout.height / 2));
	}
	
	public void debugRender(ShapeRenderer shape, SpriteBatch batch) {
		//keep in mind that from this point on in the shaperenderer, it is using the batch projection matrix. if you add something
		//for debug renderer to render AFTER the ui, it will probably be bugged and stuck on the screen. just add it before
		//the ui or reset the projection matrix.
		shape.setProjectionMatrix(batch.getProjectionMatrix());
		shape.rect(healthToggle.x, healthToggle.y, healthToggle.width, healthToggle.height);
	}
	
}
