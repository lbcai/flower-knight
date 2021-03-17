package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.util.Assets;
import lbcai.util.Utils;

public class HitEffect {
	
	/**
	 * Class for different sparkles/slash/impact hit effects that come out when entities are damaged. Does not include
	 * swing effects for attacks.
	 */
	
	private final Vector2 position;
	private final long startTime;
	
	//may need to add an int counter or something to indicate what kind of hit effect: slash, explosion, impact, etc.
	public HitEffect(Vector2 position) {
		this.position = position;
		startTime = TimeUtils.nanoTime();
	}
	
	public void render(SpriteBatch batch) {
		
	}
	
	public boolean isExpired() {
		final float elapsedTime = Utils.secondsSince(startTime);
		//placeholder, also need to make multiple types of effects and add a randomization aspect
		return Assets.instance.lifeAssets.lifeAnim.isAnimationFinished(elapsedTime);
	}
	
}
