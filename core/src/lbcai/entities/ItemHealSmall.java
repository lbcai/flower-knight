package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;

public class ItemHealSmall extends Item {

	public ItemHealSmall(Vector2 position, Level level) {
		super(position, level);
		region = Assets.instance.lifeAssets.healSmallAnim.getKeyFrame(startTime);
	}
	
	@Override
	public void use() {
		//this is a 20% of max heal
		player.health += player.maxHealth * 0.2;
		if (player.health > player.maxHealth) {
			player.health = player.maxHealth;
		}
		expire = 1;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		float animTime = Utils.secondsSince(startTime);
		region = Assets.instance.lifeAssets.healSmallAnim.getKeyFrame(animTime);
		if (expire != 0) {
			region = Assets.instance.lifeAssets.healSmallAnim.getKeyFrame(animTime);
			alpha -= 15f/255f;
			if (alpha <= 0f/255f) {
				expire = 3;
			}
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
				rotation, 
				region.getRegionX(), 
				region.getRegionY(), 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				false, 
				false);
		batch.setColor(1, 1, 1, 1);
	}
}
