package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;
import lbcai.util.Utils;

public class HitEffect {
	
	/**
	 * Class for different sparkles/slash/impact hit effects that come out when entities are damaged. Does not include
	 * swing effects for attacks.
	 */
	
	private final Vector2 position;
	private final long startTime;
	private Facing facing;
	private boolean flipx;
	TextureRegion region = Assets.instance.hitAssets.hitOneAnim.getKeyFrame(0);
	
	//may need to add an int counter or something to indicate what kind of hit effect: slash, explosion, impact, etc.
	public HitEffect(Vector2 position, Facing facing) {
		this.position = position;
		this.facing = facing;
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
		
	}
	
	public void render(SpriteBatch batch) {
		
		float animTime = Utils.secondsSince(startTime);
		region = Assets.instance.hitAssets.hitOneAnim.getKeyFrame(animTime);
		
		
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
	}
	
	public boolean isExpired() {
		final float elapsedTime = Utils.secondsSince(startTime);
		//placeholder, also need to make multiple types of effects and add a randomization aspect
		return Assets.instance.hitAssets.hitOneAnim.isAnimationFinished(elapsedTime);
	}
	
}
