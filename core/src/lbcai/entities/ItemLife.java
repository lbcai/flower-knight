package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;

public class ItemLife extends Item {

	public ItemLife(Vector2 position, Level level) {
		super(position, level);
		region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(0);
	}
	
	@Override
	public void use() {
		//this adds a life if player has slots available
		if (expire != 1) {
			expire = 1;
			if (player.lives < 5) {
				player.lives += 1;
			}
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		float animTime = Utils.secondsSince(startTime);
		region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(animTime);
		if (expire != 0) {
			region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(animTime);
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
