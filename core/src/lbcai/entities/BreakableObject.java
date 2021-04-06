package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;

/**
 * 
 * Class for breakable environment objects. Behaves like an enemy that cannot attack or move. Some objects should be able
 * to do touch damage (like piles of vines).
 * @author lbcai
 *
 */

public class BreakableObject extends Enemy {
	
	public BreakableObject(Platform platform, Level level, Facing facing) {
		super(platform, level);
		touchDmg = false;
		this.facing = facing;

		region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(0);
		damage = 0;
		range = 0;
		maxHealth = Constants.breakableObjHealth;
		health = maxHealth;
		eyeHeight = Constants.itemCenter;
		//spawn randomly on surface of platform
		position = new Vector2((MathUtils.random() * (platform.right - platform.left + 1) + platform.left), platform.top + eyeHeight.y);
		//placeholder using item sprite
		hitBox =  new Rectangle(
				position.x - Constants.itemCenter.x,
				position.y - Constants.itemCenter.y,
				Constants.itemCenter.x * 2,
				Constants.itemCenter.y * 2);
		aggroRange = new Rectangle(
				position.x,
				position.y,
				Constants.itemCenter.x,
				Constants.itemCenter.y);
		
	}
	
	@Override
	public void update(float delta) {

		if (inactive == false) {

			if (health <= 0) {
				level.dropItem(this);
				inactive = true;
				inactiveTimer = TimeUtils.nanoTime();
			}

		} else {
			if (Utils.secondsSince(inactiveTimer) < Constants.respawnTime) {
				if (alpha > 0f/255f) {
					alpha -= 5f/255f;
				} else {
					alpha = 0f/255f;
				}
			}
			//respawn if time is up
			else if (Utils.secondsSince(inactiveTimer) >= Constants.respawnTime) {
				if (alpha == 0f/255f) {
					position = new Vector2((MathUtils.random() * (platform.right - platform.left + 1) + platform.left), platform.top + eyeHeight.y);
				}
				alpha += 15f/255f;
				if (alpha >= 255f/255f) {
					health = maxHealth;
					inactive = false;
					alpha = 255f/255f;
				}
				
			}
		}
	}
	
	public void render(SpriteBatch batch) {
		Boolean flipx = false;
		if (facing == Facing.LEFT) {
			flipx = true;
		} else {
			flipx = false;
		}
		
		batch.setColor(1, 1, 1, alpha);
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
				flipx, 
				false);
		batch.setColor(1, 1, 1, 1);
	}
	
	public boolean isExpired() {
		//do i want objects to respawn?
		return false;
	}
	
}
