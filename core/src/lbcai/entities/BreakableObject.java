package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import lbcai.util.Assets;
import lbcai.util.Constants;

/**
 * 
 * Class for breakable environment objects. Behaves like an enemy that cannot attack or move. Some objects should be able
 * to do touch damage (like piles of vines).
 * @author lbcai
 *
 */

public class BreakableObject extends Entity {
	
	final Platform platform;

	public BreakableObject(Platform platform) {
		this.platform = platform;
		

		region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(0);
		damage = 0;
		range = 0;
		maxHealth = Constants.breakableObjHealth;
		health = maxHealth;
		//spawn randomly on surface of platform
		position = new Vector2((MathUtils.random() * (platform.right - platform.left + 1) - platform.left), platform.top + Constants.itemCenter.y);
		//placeholder using item sprite
		hitBox =  new Rectangle(
				position.x - Constants.itemCenter.x,
				position.y - Constants.itemCenter.y,
				Constants.itemCenter.x * 2,
				Constants.itemCenter.y * 2);
		
	}
	
	public void update(float delta) {
		
	}
	
	public void render(SpriteBatch batch) {
		batch.draw(region.getTexture(), 
				(position.x - Constants.itemCenter.x), 
				(position.y - Constants.itemCenter.y), 
				Constants.itemCenter.x, 
				Constants.itemCenter.y, 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				1, 
				1, 
				0, 
				region.getRegionX(), 
				region.getRegionY(), 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				false, 
				false);
	}
	
	public boolean isExpired() {
		return false;
	}
	
}
