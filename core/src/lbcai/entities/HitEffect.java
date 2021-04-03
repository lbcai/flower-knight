package lbcai.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;
import lbcai.util.Utils;

public class HitEffect extends Effect {
	
	/**
	 * Class for different sparkles/slash/impact hit effects that come out when entities are damaged. Does not include
	 * swing effects for attacks.
	 */
	
	private final long startTime;
	private boolean flipx;
	private int type = 0;
	private int[] randomizer = {1, 2, 3};
	private int randomPicker;
	
	//may need to add an int counter or something to indicate what kind of hit effect: slash, explosion, impact, etc.
	public HitEffect(Vector2 position, Facing facing, int type) {
		this.position = position;
		this.facing = facing;
		this.type = type;
		flipx = false;
		startTime = TimeUtils.nanoTime();
		
		//plan to, when spawning hit effect, input the facing direction that the entity is being attacked from
		if (facing == Facing.LEFT) {
			flipx = false;
			position.x -= 20;
		} else {
			flipx = true;
			position.x += 20;
		}
		
		if (type == 0) {
			//impact
			 region = Assets.instance.hitAssets.hitImpactOneAnim.getKeyFrame(0);
		} else if (type == 1) {
			//slash
			region = Assets.instance.hitAssets.hitSlashOneAnim.getKeyFrame(0);
		} else if (type == 2) {
			//shatter projectile
			region = Assets.instance.hitAssets.hitShatterOneAnim.getKeyFrame(0);
		}
		
		randomPicker = (int) (Math.random() * randomizer.length);
		
	}
	
	public void render(SpriteBatch batch) {
		
		float animTime = Utils.secondsSince(startTime);
		if (type == 0) {
			//impact
			if (randomPicker == 0) {
				region = Assets.instance.hitAssets.hitImpactOneAnim.getKeyFrame(animTime);
			} else if (randomPicker == 1) {
				region = Assets.instance.hitAssets.hitImpactTwoAnim.getKeyFrame(animTime);
			} else if (randomPicker == 2) {
				region = Assets.instance.hitAssets.hitImpactThreeAnim.getKeyFrame(animTime);
			}
			 
		} else if (type == 1) {
			//slash
			region = Assets.instance.hitAssets.hitSlashOneAnim.getKeyFrame(animTime);
		} else if (type == 2) {
			//shatter projectile
			if (randomPicker == 0) {
				region = Assets.instance.hitAssets.hitShatterOneAnim.getKeyFrame(animTime);
			} else if (randomPicker == 1) {
				region = Assets.instance.hitAssets.hitShatterTwoAnim.getKeyFrame(animTime);
			} else if (randomPicker == 2) {
				region = Assets.instance.hitAssets.hitShatterThreeAnim.getKeyFrame(animTime);
			}
			
		}
		alpha -= 5f/255f;
		batch.setColor(1, 1, 1, alpha);
		batch.draw(region.getTexture(), 
				(position.x - Constants.bulletCenter.x), 
				(position.y - Constants.bulletCenter.y), 
				0, 
				0, 
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
	
	@Override
	public boolean isExpired() {
		final float elapsedTime = Utils.secondsSince(startTime);
		//placeholder, also need to make multiple types of effects and add a randomization aspect
		return Assets.instance.hitAssets.hitImpactOneAnim.isAnimationFinished(elapsedTime);
	}
	
}
