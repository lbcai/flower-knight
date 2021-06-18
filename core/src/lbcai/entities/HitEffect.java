package lbcai.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
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
	//for impact effect on other types of hiteffects, such as slash (stacking effects!)
	private int randomPicker2;
	private TextureRegion region2;
	
	public HitEffect(Vector2 position, Facing facing, int type, Level level) {
		this.position = position;
		//if not pierce effect, make it random direction facing
		if (type == 2) {
			this.facing = facing;
		} else {
			int facingRandom = (int) (Math.random() * 2);
			if (facingRandom == 0) {
				this.facing = Facing.RIGHT;
			} else if (facingRandom == 1) {
				this.facing = Facing.LEFT;
			}
		}
		
		this.type = type;
		flipx = false;
		startTime = TimeUtils.nanoTime();
		
		//when spawning hit effect, input the facing direction that the entity is being attacked from
		if (this.facing == Facing.LEFT) {
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
			randomPicker2 = (int) (Math.random() * randomizer.length);
			region2 = Assets.instance.hitAssets.hitImpactThreeAnim.getKeyFrame(0);
			region = Assets.instance.hitAssets.hitSlashOneAnim.getKeyFrame(0);
		} else if (type == 2) {
			//shatter projectile
			region = Assets.instance.hitAssets.hitPierceOneAnim.getKeyFrame(0);
		}
		
		randomPicker = (int) (Math.random() * randomizer.length);
		level.getRenderables().add(this);
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
			if (randomPicker == 0) {
				region = Assets.instance.hitAssets.hitSlashOneAnim.getKeyFrame(animTime);
			} else if (randomPicker == 1) {
				region = Assets.instance.hitAssets.hitSlashTwoAnim.getKeyFrame(animTime);
			} else if (randomPicker == 2) {
				region = Assets.instance.hitAssets.hitSlashThreeAnim.getKeyFrame(animTime);
			}
			
			if (randomPicker2 == 0) {
				//add a cycle time to skip 1st frame
				region2 = Assets.instance.hitAssets.hitImpactOneAnim.getKeyFrame(animTime + Constants.hitEffectCycleTime);
			} else if (randomPicker2 == 1) {
				region2 = Assets.instance.hitAssets.hitImpactTwoAnim.getKeyFrame(animTime + Constants.hitEffectCycleTime);
			} else if (randomPicker2 == 2) {
				region2 = Assets.instance.hitAssets.hitImpactThreeAnim.getKeyFrame(animTime + Constants.hitEffectCycleTime);
			}
			
			
		} else if (type == 2) {
			//shatter projectile
			if (randomPicker == 0) {
				region = Assets.instance.hitAssets.hitPierceOneAnim.getKeyFrame(animTime);
			} else if (randomPicker == 1) {
				region = Assets.instance.hitAssets.hitPierceTwoAnim.getKeyFrame(animTime);
			} else if (randomPicker == 2) {
				region = Assets.instance.hitAssets.hitPierceThreeAnim.getKeyFrame(animTime);
			}
			
		}
		alpha -= 5f/255f;
		batch.setColor(1, 1, 1, alpha);
		batch.draw(region.getTexture(), 
				(position.x - Constants.hitEffectCenter.x), 
				(position.y - Constants.hitEffectCenter.y), 
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
		
		//impact of slash
		if (type == 1) {
			if (Assets.instance.hitAssets.hitImpactThreeAnim.isAnimationFinished(animTime + Constants.hitEffectCycleTime) == false) {
				batch.setColor(1, 1, 1, alpha);
			} else {
				batch.setColor(1, 1, 1, 0f/255f);
			}
				batch.draw(region2.getTexture(), 
						(position.x - Constants.hitEffectCenter.x), 
						(position.y - Constants.hitEffectCenter.y), 
						0, 
						0, 
						region2.getRegionWidth(), 
						region2.getRegionHeight(), 
						1, 
						1, 
						0, 
						region2.getRegionX(), 
						region2.getRegionY(), 
						region2.getRegionWidth(), 
						region2.getRegionHeight(), 
						flipx, 
						false);
			
		}
		
		batch.setColor(1, 1, 1, 1);
	}
	
	@Override
	public boolean isExpired() {
		final float elapsedTime = Utils.secondsSince(startTime);
		return Assets.instance.hitAssets.hitImpactOneAnim.isAnimationFinished(elapsedTime);
	}
	
}
